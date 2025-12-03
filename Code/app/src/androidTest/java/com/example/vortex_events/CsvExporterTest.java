package com.example.vortex_events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests for CSV exporting logic in CsvExporter.
 * We use reflection to call the private writeCsvFile(...) method
 * so that we don't need to modify the production code.
 */
@RunWith(AndroidJUnit4.class)
public class CsvExporterTest {

    /**
     * Helper: call CsvExporter.writeCsvFile(...) via reflection.
     */
    @SuppressWarnings({"unchecked", "unchecked"})
    private File invokeWriteCsvFile(Context context,
                                    String eventName,
                                    String eventId,
                                    List<String[]> rows) throws Exception {

        Method method = CsvExporter.class.getDeclaredMethod(
                "writeCsvFile",
                Context.class,
                String.class,
                String.class,
                List.class
        );
        method.setAccessible(true);

        return (File) method.invoke(null, context, eventName, eventId, rows);
    }



    @Test
    public void writeCsvFile_writesHeaderAndRows() throws Exception {
        Context context = InstrumentationRegistry
                .getInstrumentation()
                .getTargetContext();


        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"uid123", "SunFlower", "sunny@gardon.com", "1234567890"});
        rows.add(new String[]{"uid456", "Peashooter", "pea@gardon.com", "0987654321"});

        File csvFile = invokeWriteCsvFile(
                context,
                "Protect the House",
                "fesuoH eht tcetorP",
                rows
        );

        assertNotNull(csvFile);
        assertTrue(csvFile.exists());

        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }


        assertEquals(3, lines.size());
        assertEquals("UserID,Username,UserEmail,UserPhone", lines.get(0));
        assertEquals("\"uid123\",\"SunFlower\",\"sunny@gardon.com\",\"1234567890\"", lines.get(1));
        assertEquals("\"uid456\",\"Peashooter\",\"pea@gardon.com\",\"0987654321\"", lines.get(2));

    }



    @Test
    public void writeCsvFile_handlesEmptyRows() throws Exception {
        Context context = InstrumentationRegistry
                .getInstrumentation()
                .getTargetContext();

        List<String[]> rows = new ArrayList<>();

        File csvFile = invokeWriteCsvFile(
                context,
                "Empty Event",
                "empty123",
                rows
        );

        assertNotNull(csvFile);
        assertTrue(csvFile.exists());

        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }


        assertEquals(1, lines.size());
        assertEquals("UserID,Username,UserEmail,UserPhone", lines.get(0));
    }

}
