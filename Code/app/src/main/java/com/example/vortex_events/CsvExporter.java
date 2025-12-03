package com.example.vortex_events;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Simplest version: press a button -> export accepted list to CSV.
 * No callback to the caller. All feedback is done via Toast + Log.
 *
 * Columns: UserID, Username, UserEmail, UserPhone
 */
public class CsvExporter {

    private static final String TAG = "CsvExporter";

    /**
     * Export the accepted list of a given event to a CSV file.
     * @param context The context.
     * @param eventId The ID of the event.
     */
    public static void exportAcceptedEntrants(
            @NonNull Context context,
            @NonNull String eventId
    ) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Events").document(eventId).get()
                .addOnSuccessListener(eventDoc -> {
                    if (!eventDoc.exists()) {
                        Toast.makeText(context, "Event not found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String tmpName = eventDoc.getString("name");
                    if (tmpName == null || tmpName.isEmpty()) {
                        tmpName = eventId;
                    }
                    final String eventName = tmpName;


                    List<String> acceptedIds = (List<String>) eventDoc.get("accepted");
                    if (acceptedIds == null) {
                        acceptedIds = new ArrayList<>();
                    }

                    List<Task<DocumentSnapshot>> userTasks = new ArrayList<>();
                    for (String uid : acceptedIds) {
                        userTasks.add(db.collection("Users").document(uid).get());
                    }

                    Tasks.whenAllSuccess(userTasks)
                            .addOnSuccessListener(results -> {
                                List<String[]> rows = new ArrayList<>();

                                for (Object obj : results) {
                                    DocumentSnapshot snap = (DocumentSnapshot) obj;
                                    String uid = snap.getId();
                                    String name = snap.getString("name");
                                    String email = snap.getString("email");
                                    String phone = snap.getString("phone_number");

                                    rows.add(new String[]{
                                            safe(uid),
                                            safe(name),
                                            safe(email),
                                            safe(phone)
                                    });
                                }

                                try {
                                    File file = writeCsvFile(context, eventName, eventId, rows);
                                    Toast.makeText(
                                            context,
                                            "CSV saved:\n" + file.getAbsolutePath(),
                                            Toast.LENGTH_LONG
                                    ).show();
                                } catch (IOException e) {
                                    Log.e(TAG, "Error writing CSV", e);
                                    Toast.makeText(
                                            context,
                                            "Failed to write CSV: " + e.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error loading users", e);
                                Toast.makeText(
                                        context,
                                        "Failed to load user data",
                                        Toast.LENGTH_LONG
                                ).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading event", e);
                    Toast.makeText(
                            context,
                            "Failed to load event",
                            Toast.LENGTH_LONG
                    ).show();
                });
    }


    /**
     * Writes a CSV file to the documents directory.
     * @param context The context.
     * @param eventName The name of the event.
     * @param eventId The ID of the event.
     * @param rows The rows of the CSV.
     * @return The file.
     * @throws IOException If the file cannot be written.
     */
    private static File writeCsvFile(
            Context context,
            String eventName,
            String eventId,
            List<String[]> rows) throws IOException {

        // csv name：EventName (eventId).csv
        String baseName = eventName + " (" + eventId + ").csv";
        String fileName = baseName.replaceAll("[\\\\/:*?\"<>|]", "_");

        File dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        if (dir == null) {
            dir = context.getFilesDir();
        }
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Could not create export directory");
        }

        File outFile = new File(dir, fileName);
        Log.d(TAG, "Writing CSV to " + outFile.getAbsolutePath());

        try (FileWriter writer = new FileWriter(outFile, false)) {
            // head
            writer.write("UserID,Username,UserEmail,UserPhone\n");

            // content
            for (String[] row : rows) {
                writer.write(
                        csvCell(row[0]) + "," +
                                csvCell(row[1]) + "," +
                                csvCell(row[2]) + "," +
                                csvCell(row[3]) + "\n"
                );
            }
        }
        return outFile;
    }

    /**
     * check if a string is not null
     * @param s the string to check
     * @return the string if not null, empty string otherwise
     * */
    private static String safe(String s) {
        return s == null ? "" : s;
    }

    /**
     * escape a string for csv
     * @param value the string to escape
     * @return the escaped string
     * */
    private static String csvCell(String value) {
        String v = safe(value);
        v = v.replace("\"", "\"\"");
        return "\"" + v + "\"";
    }
}
