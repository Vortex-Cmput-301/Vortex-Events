package com.example.vortex_events.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vortex_events.R;
import com.example.vortex_events.model.EnrolledUser;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.*;

import java.util.*;

public class OrganizerParticipantsActivity extends AppCompatActivity {

    public static final String EXTRA_EVENT_ID = "event_id";

    private String eventId;
    private ProgressBar progress;
    private TextView txtEmpty;
    private EnrolledAdapter adapter;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListenerRegistration reg;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_participants);

        progress = findViewById(R.id.progress);
        txtEmpty = findViewById(R.id.txtEmpty);
        RecyclerView rv = findViewById(R.id.rvEnrolled);

        adapter = new EnrolledAdapter();
        rv.setAdapter(adapter);

        Intent i = getIntent();
        eventId = i != null ? i.getStringExtra(EXTRA_EVENT_ID) : null;
        if (TextUtils.isEmpty(eventId)) {
            toast("Missing eventId");
            finish();
            return;
        }

        startListeningAcceptedOnly();
    }

    private void startListeningAcceptedOnly() {
        if (reg != null) reg.remove();
        progress.setVisibility(View.VISIBLE);

        reg = db.collection("Events").document(eventId)
                .addSnapshotListener((snap, e) -> {
                    progress.setVisibility(View.GONE);
                    if (e != null) { toast("Load failed: " + e.getMessage()); return; }
                    if (snap == null || !snap.exists()) { toast("Event not found"); finish(); return; }

                    // 组织者校验（客户端校验，避免误看）
                    @SuppressLint("HardwareIds")
                    String myId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                    String organizer = snap.getString("organizer");
                    if (organizer == null || !organizer.equals(myId)) {
                        toast("You are not the organizer of this event");
                        finish();
                        return;
                    }

                    // 只取最终名单：acceptedIds
                    List<String> accepted = (List<String>) snap.get("acceptedIds");
                    if (accepted == null) accepted = new ArrayList<>();

                    // 可选的加入时间 map：acceptedAt { deviceId: Timestamp }
                    Map<String, Timestamp> acceptedAt = null;
                    try { acceptedAt = (Map<String, Timestamp>) (Map) snap.get("acceptedAt"); } catch (Exception ignore) {}

                    loadNamesAndShow(accepted, acceptedAt);
                });
    }

    private void loadNamesAndShow(List<String> ids, @Nullable Map<String, Timestamp> acceptedAt) {
        if (ids.isEmpty()) {
            showList(Collections.emptyList());
            return;
        }
        progress.setVisibility(View.VISIBLE);
        List<Task<DocumentSnapshot>> ts = new ArrayList<>();
        for (String id : ids) ts.add(db.collection("Users").document(id).get());

        Tasks.whenAllComplete(ts).addOnCompleteListener(done -> {
            progress.setVisibility(View.GONE);

            List<EnrolledUser> list = new ArrayList<>();
            for (int idx = 0; idx < ids.size(); idx++) {
                String uid = ids.get(idx);
                String name = null;

                // 某些任务可能失败，这里做下兜底
                try {
                    Task<?> t = done.getResult().get(idx);
                    if (t.isSuccessful()) {
                        DocumentSnapshot u = (DocumentSnapshot) t.getResult();
                        if (u != null && u.exists()) name = u.getString("name");
                    }
                } catch (Exception ignore) {}

                Timestamp t = acceptedAt != null ? acceptedAt.get(uid) : null;
                list.add(new EnrolledUser(uid, name, t));
            }

            // 时间降序（没有时间的排后）
            list.sort((a,b) -> {
                long at = a.enrolledAt==null?0:a.enrolledAt.toDate().getTime();
                long bt = b.enrolledAt==null?0:b.enrolledAt.toDate().getTime();
                return Long.compare(bt, at);
            });

            showList(list);
        });
    }

    private void showList(List<EnrolledUser> list) {
        txtEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.submitList(list);
    }

    private void toast(String s) { Toast.makeText(this, s, Toast.LENGTH_SHORT).show(); }

    @Override protected void onDestroy() {
        if (reg != null) reg.remove();
        super.onDestroy();
    }
}
