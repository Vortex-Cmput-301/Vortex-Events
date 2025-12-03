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
                List.class    // 这里不用改，泛型会被擦除
        );
        method.setAccessible(true);

        return (File) method.invoke(null, context, eventName, eventId, rows);
    }


    /**
     * 正常情况：有两条用户数据时，CSV 里应该是：
     *   第 1 行：表头
     *   第 2 行：第一个用户
     *   第 3 行：第二个用户
     */

    @Test
    public void writeCsvFile_writesHeaderAndRows() throws Exception {
        Context context = InstrumentationRegistry
                .getInstrumentation()
                .getTargetContext();

        // 这里改成 List<String[]>
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

        // 1 行表头 + 2 行数据
        assertEquals(3, lines.size());
        assertEquals("UserID,Username,UserEmail,UserPhone", lines.get(0));
        assertEquals("\"uid123\",\"SunFlower\",\"sunny@gardon.com\",\"1234567890\"", lines.get(1));
        assertEquals("\"uid456\",\"Peashooter\",\"pea@gardon.com\",\"0987654321\"", lines.get(2));

    }


    /**
     * 边界情况：rows 为空时，CSV 里至少要有表头一行。
     */
    @Test
    public void writeCsvFile_handlesEmptyRows() throws Exception {
        Context context = InstrumentationRegistry
                .getInstrumentation()
                .getTargetContext();

        List<String[]> rows = new ArrayList<>();   // 空列表

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

        // 只有表头
        assertEquals(1, lines.size());
        assertEquals("UserID,Username,UserEmail,UserPhone", lines.get(0));
    }

}
