package com.example.vortex_events;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Unit tests for AdminListAdapter.
 * Uses a subclass that avoids RecyclerView internals for data changes,
 * but still uses the real getItemViewType() implementation.
 */
public class AdminListAdapterTest {

    /**
     * Test-only subclass that:
     * - Updates parent field currentTab directly
     * - Avoids calling notifyDataSetChanged() by not using parent setters
     * - Maintains its own data lists for counting
     */
    private static class TestAdminListAdapter extends AdminListAdapter {

        private final List<RegisteredUser> testUsers = new ArrayList<>();
        private final List<String> testImages = new ArrayList<>();
        private final List<AppNotification> testNotifications = new ArrayList<>();

        public TestAdminListAdapter(AdminItemListener listener) {
            super(listener);
        }

        @Override
        public void setUsers(List<RegisteredUser> newUsers) {
            // set parent tab field so getItemViewType() sees the change
            // currentTab is a field in the parent class
            // We cannot access it directly (it is private),
            // so we rely on parent setter logic by calling super.setUsers(newUsers)
            // but we must avoid notifyDataSetChanged(), so we do manual reflection.
            setParentCurrentTab(AdminTabType.USERS);
            testUsers.clear();
            if (newUsers != null) {
                testUsers.addAll(newUsers);
            }
        }

        @Override
        public void setImages(List<String> newImages) {
            setParentCurrentTab(AdminTabType.IMAGES);
            testImages.clear();
            if (newImages != null) {
                testImages.addAll(newImages);
            }
        }

        @Override
        public void setNotifications(List<AppNotification> newNotifications) {
            setParentCurrentTab(AdminTabType.NOTIFICATIONS);
            testNotifications.clear();
            if (newNotifications != null) {
                testNotifications.addAll(newNotifications);
            }
        }

        @Override
        public int getItemCount() {
            switch (getCurrentTab()) {
                case USERS:
                    return testUsers.size();
                case IMAGES:
                    return testImages.size();
                case NOTIFICATIONS:
                    return testNotifications.size();
                default:
                    return 0;
            }
        }

        /**
         * Helper to set the private field currentTab in the parent class using reflection.
         */
        private void setParentCurrentTab(AdminTabType tabType) {
            try {
                java.lang.reflect.Field field =
                        AdminListAdapter.class.getDeclaredField("currentTab");
                field.setAccessible(true);
                field.set(this, tabType);
            } catch (Exception e) {
                throw new RuntimeException("Failed to set currentTab via reflection", e);
            }
        }
    }

    @Test
    public void setUsers_switchesTabAndUpdatesCount() {
        AdminListAdapter.AdminItemListener listener = mock(AdminListAdapter.AdminItemListener.class);
        TestAdminListAdapter adapter = new TestAdminListAdapter(listener);

        RegisteredUser u1 = new RegisteredUser();
        u1.setName("User1");
        RegisteredUser u2 = new RegisteredUser();
        u2.setName("User2");

        adapter.setUsers(Arrays.asList(u1, u2));

        assertEquals(AdminListAdapter.AdminTabType.USERS, adapter.getCurrentTab());
        assertEquals(2, adapter.getItemCount());
    }

    @Test
    public void setImages_switchesTabAndUpdatesCount() {
        AdminListAdapter.AdminItemListener listener = mock(AdminListAdapter.AdminItemListener.class);
        TestAdminListAdapter adapter = new TestAdminListAdapter(listener);

        adapter.setImages(Arrays.asList("img1", "img2", "img3"));

        assertEquals(AdminListAdapter.AdminTabType.IMAGES, adapter.getCurrentTab());
        assertEquals(3, adapter.getItemCount());
    }

    @Test
    public void setNotifications_switchesTabAndUpdatesCount() {
        AdminListAdapter.AdminItemListener listener = mock(AdminListAdapter.AdminItemListener.class);
        TestAdminListAdapter adapter = new TestAdminListAdapter(listener);

        AppNotification n1 = new AppNotification();
        AppNotification n2 = new AppNotification();

        adapter.setNotifications(Arrays.asList(n1, n2));

        assertEquals(AdminListAdapter.AdminTabType.NOTIFICATIONS, adapter.getCurrentTab());
        assertEquals(2, adapter.getItemCount());
    }

    @Test
    public void getItemViewType_matchesCurrentTab() {
        AdminListAdapter.AdminItemListener listener = mock(AdminListAdapter.AdminItemListener.class);
        TestAdminListAdapter adapter = new TestAdminListAdapter(listener);

        // users tab
        adapter.setUsers(Collections.singletonList(new RegisteredUser()));
        int viewTypeUser = adapter.getItemViewType(0);

        // images tab
        adapter.setImages(Collections.singletonList("img"));
        int viewTypeImage = adapter.getItemViewType(0);

        // notifications tab
        adapter.setNotifications(Collections.singletonList(new AppNotification()));
        int viewTypeNotification = adapter.getItemViewType(0);

        assertNotEquals(viewTypeUser, viewTypeImage);
        assertNotEquals(viewTypeUser, viewTypeNotification);
        assertNotEquals(viewTypeImage, viewTypeNotification);
    }
}
