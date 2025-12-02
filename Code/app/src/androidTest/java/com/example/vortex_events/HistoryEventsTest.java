package com.example.vortex_events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Instrumented tests for HistoryEvents activity.
 * Focus on testing the formatEventDate method and basic activity startup.
 */
@RunWith(AndroidJUnit4.class)
public class HistoryEventsTest {

    /**
     * Helper to invoke the private formatEventDate method via reflection.
     */
    private String callFormatEventDate(HistoryEvents activity, Date start, Date end) throws Exception {
        Method m = HistoryEvents.class
                .getDeclaredMethod("formatEventDate", Date.class, Date.class);
        m.setAccessible(true);
        return (String) m.invoke(activity, start, end);
    }

    private Date buildDate(int year, int month, int day, int hour, int minute) {
        Calendar c = Calendar.getInstance(Locale.getDefault());
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    @Test
    public void formatEventDate_fullRange() {
        ActivityScenario<HistoryEvents> scenario =
                ActivityScenario.launch(new Intent(
                        androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().getTargetContext(),
                        HistoryEvents.class
                ));

        scenario.onActivity(activity -> {
            try {
                Date start = buildDate(2025, Calendar.OCTOBER, 10, 9, 30);
                Date end = buildDate(2025, Calendar.OCTOBER, 10, 11, 0);

                String result = callFormatEventDate(activity, start, end);

                // We do not assert exact text because of locale differences,
                // but we check key parts are present.
                String lower = result.toLowerCase(Locale.getDefault());
                // year
                assertFalse("Result should contain year", !lower.contains("2025"));
                // hour fragments
                // we just check it is not empty
                assertFalse("Result should not be empty", result.isEmpty());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        scenario.close();
    }

    @Test
    public void formatEventDate_onlyStart() {
        ActivityScenario<HistoryEvents> scenario =
                ActivityScenario.launch(new Intent(
                        androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().getTargetContext(),
                        HistoryEvents.class
                ));

        scenario.onActivity(activity -> {
            try {
                Date start = buildDate(2025, Calendar.DECEMBER, 1, 15, 0);

                String result = callFormatEventDate(activity, start, null);

                // Should not contain a dash for range
                // but should contain year 2025
                String lower = result.toLowerCase(Locale.getDefault());
                assertFalse("Result should not contain range dash", lower.contains("-"));
                assertFalse("Result should contain year", !lower.contains("2025"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        scenario.close();
    }

    @Test
    public void formatEventDate_bothNull() {
        ActivityScenario<HistoryEvents> scenario =
                ActivityScenario.launch(new Intent(
                        androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().getTargetContext(),
                        HistoryEvents.class
                ));

        scenario.onActivity(activity -> {
            try {
                String result = callFormatEventDate(activity, null, null);
                assertEquals("Date not specified", result);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        scenario.close();
    }
}
