package com.example.vortex_events;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * AdminActivity: administration panel with three switchable lists:
 * 1) Users
 * 2) All event images
 * 3) Notifications
 */
public class AdminActivity extends AppCompatActivity implements AdminListAdapter.AdminItemListener {

    private RecyclerView recyclerView;
    private MaterialButton buttonTabUsers;
    private MaterialButton buttonTabImages;
    private MaterialButton buttonTabNotifications;

    private DatabaseWorker databaseWorker;
    private AdminListAdapter adapter;
    private DialogHelper dialogHelper;

    private List<RegisteredUser> users = new ArrayList<>();
    private List<Event> events = new ArrayList<>();
    private List<String> imageUrls = new ArrayList<>();
    private List<AppNotification> notifications = new ArrayList<>();

    private AdminListAdapter.AdminTabType currentTab = AdminListAdapter.AdminTabType.USERS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin);

        databaseWorker = new DatabaseWorker();
        dialogHelper = new DialogHelper(this);

        recyclerView = findViewById(R.id.recycler_admin_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        buttonTabUsers = findViewById(R.id.button_tab_users);
        buttonTabImages = findViewById(R.id.button_tab_images);
        buttonTabNotifications = findViewById(R.id.button_tab_notifications);

        ImageView backButton = findViewById(R.id.button_admin_back);
        backButton.setOnClickListener(v -> finish());

        adapter = new AdminListAdapter(this);
        recyclerView.setAdapter(adapter);

        setupTabButtons();

        // Default tab: users
        switchToTab(AdminListAdapter.AdminTabType.USERS);
    }

    private void setupTabButtons() {
        buttonTabUsers.setOnClickListener(v -> switchToTab(AdminListAdapter.AdminTabType.USERS));
        buttonTabImages.setOnClickListener(v -> switchToTab(AdminListAdapter.AdminTabType.IMAGES));
        buttonTabNotifications.setOnClickListener(v -> switchToTab(AdminListAdapter.AdminTabType.NOTIFICATIONS));
    }

    private void switchToTab(AdminListAdapter.AdminTabType tabType) {
        currentTab = tabType;
        highlightTab(tabType);

        if (tabType == AdminListAdapter.AdminTabType.USERS) {
            loadUsers();
        } else if (tabType == AdminListAdapter.AdminTabType.IMAGES) {
            loadEventsAndImages();
        } else if (tabType == AdminListAdapter.AdminTabType.NOTIFICATIONS) {
            loadNotifications();
        }
    }

    private void highlightTab(AdminListAdapter.AdminTabType type) {
        // Reset all tabs to default style first
        resetTabStyles();

        int selectedTextColor = getColor(android.R.color.white);
        int selectedBgColor = getColor(android.R.color.black);

        if (type == AdminListAdapter.AdminTabType.USERS) {
            buttonTabUsers.setBackgroundTintList(ColorStateList.valueOf(selectedBgColor));
            buttonTabUsers.setTextColor(selectedTextColor);
        } else if (type == AdminListAdapter.AdminTabType.IMAGES) {
            buttonTabImages.setBackgroundTintList(ColorStateList.valueOf(selectedBgColor));
            buttonTabImages.setTextColor(selectedTextColor);
        } else if (type == AdminListAdapter.AdminTabType.NOTIFICATIONS) {
            buttonTabNotifications.setBackgroundTintList(ColorStateList.valueOf(selectedBgColor));
            buttonTabNotifications.setTextColor(selectedTextColor);
        }
    }

    private void resetTabStyles() {
        int strokeColor = getColor(android.R.color.black);
        int textColor = getColor(android.R.color.black);

        resetSingleTab(buttonTabUsers, strokeColor, textColor);
        resetSingleTab(buttonTabImages, strokeColor, textColor);
        resetSingleTab(buttonTabNotifications, strokeColor, textColor);
    }

    private void resetSingleTab(MaterialButton button, int strokeColor, int textColor) {
        // Transparent background
        int transparent = getColor(android.R.color.transparent);
        button.setBackgroundTintList(ColorStateList.valueOf(transparent));

        // StrokeColor is already a color int, wrap it as ColorStateList
        button.setStrokeColor(ColorStateList.valueOf(strokeColor));

        button.setTextColor(textColor);
    }

    // ========================
    // Data loading
    // ========================

    /**
     * Load all users for the Users tab.
     */
    private void loadUsers() {
        Task<QuerySnapshot> task = databaseWorker.getAllUsers();
        task.addOnCompleteListener(t -> {
            users.clear();
            if (t.isSuccessful() && t.getResult() != null) {
                for (QueryDocumentSnapshot document : t.getResult()) {
                    Double lat = document.getDouble("latitude");
                    Double lng = document.getDouble("longitude");

                    RegisteredUser user = new RegisteredUser(
                            document.getId(),
                            document.getString("phone_number"),
                            document.getString("email"),
                            document.getString("name"),
                            lat != null ? lat : 0.0,
                            lng != null ? lng : 0.0
                    );
                    users.add(user);
                }
            } else {
                Toast.makeText(this, "Failed to load users.", Toast.LENGTH_SHORT).show();
            }
            adapter.setUsers(users);
        });
    }

    /**
     * Load all events and their images for the Images tab.
     * We keep full Event list so we can later map image URL back to eventID.
     */
    private void loadEventsAndImages() {
        Task<QuerySnapshot> task = databaseWorker.getAllEvents();
        task.addOnCompleteListener(t -> {
            events.clear();
            imageUrls.clear();

            if (t.isSuccessful() && t.getResult() != null) {
                for (QueryDocumentSnapshot document : t.getResult()) {
                    Event event = DatabaseWorker.convertDocumentToEvent(document);
                    if (event != null) {
                        events.add(event);
                        String imageUrl = event.getImage();
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            imageUrls.add(imageUrl);
                        }
                    }
                }
            }
            adapter.setImages(imageUrls);

        });
    }

    /**
     * Load all notifications from all users for the Notifications tab.
     * Aggregates notifications from every user document.
     */
    private void loadNotifications() {
        Task<QuerySnapshot> task = databaseWorker.getAllUsers();
        task.addOnCompleteListener(t -> {
            notifications.clear();
            if (t.isSuccessful() && t.getResult() != null) {
                for (QueryDocumentSnapshot document : t.getResult()) {
                    Object raw = document.get("notifications");
                    if (raw instanceof ArrayList) {
                        ArrayList<?> list = (ArrayList<?>) raw;
                        for (Object item : list) {
                            if (item instanceof Map) {
                                Map<?, ?> map = (Map<?, ?>) item;
                                String authorID = map.get("authorID") != null
                                        ? map.get("authorID").toString()
                                        : "";
                                String title = map.get("title") != null
                                        ? map.get("title").toString()
                                        : "";
                                String description = map.get("description") != null
                                        ? map.get("description").toString()
                                        : "";
                                Date timeCreated = null;
                                Object timeObj = map.get("time_created");
                                if (timeObj instanceof com.google.firebase.Timestamp) {
                                    com.google.firebase.Timestamp ts =
                                            (com.google.firebase.Timestamp) timeObj;
                                    timeCreated = ts.toDate();
                                } else if (timeObj instanceof Date) {
                                    timeCreated = (Date) timeObj;
                                }
                                String notificationID = map.get("notificationID") != null
                                        ? map.get("notificationID").toString()
                                        : "";

                                AppNotification notification = new AppNotification();
                                notification.setAuthorID(authorID);
                                notification.setTitle(title);
                                notification.setDescription(description);
                                notification.setTime_created(timeCreated);
                                notification.setNotificationID(notificationID);
                                notifications.add(notification);
                            } else if (item instanceof AppNotification) {
                                notifications.add((AppNotification) item);
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Failed to load notifications.", Toast.LENGTH_SHORT).show();
            }

            adapter.setNotifications(notifications);
        });
    }

    // ========================
    // Click handlers (via Adapter callback)
    // ========================

    @Override
    public void onUserClicked(@NonNull RegisteredUser user) {
        // Open Profile activity for given deviceID.
        Intent intent = new Intent(this, Profile.class);
        intent.putExtra(Profile.EXTRA_DEVICE_ID, user.getDeviceID());
        startActivity(intent);
    }

    @Override
    public void onImageClicked(@NonNull String imageUrl) {
        dialogHelper.showSimpleConfirmationDialog(
                "Delete Image",
                "Are you sure you want to delete this image?",
                () -> performDeleteImage(imageUrl)
        );
    }

    @Override
    public void onNotificationClicked(@NonNull AppNotification notification) {
        dialogHelper.showSimpleConfirmationDialog(
                "Delete Notification",
                "Are you sure you want to delete this notification?",
                () -> performDeleteNotification(notification)
        );
    }

    // ========================
    // Actual delete operations
    // ========================

    /**
     * Clear image field of the event which has this imageUrl.
     */
    private void performDeleteImage(@NonNull String imageUrl) {
        // Find the event that owns this image
        Event targetEvent = null;
        for (Event event : events) {
            if (imageUrl.equals(event.getImage())) {
                targetEvent = event;
                break;
            }
        }

        if (targetEvent == null) {
            Toast.makeText(this, "Event not found for this image.", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseWorker.clearEventImage(targetEvent.getEventID())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Image deleted.", Toast.LENGTH_SHORT).show();
                        // Refresh images
                        loadEventsAndImages();
                    } else {
                        Toast.makeText(this, "Failed to delete image.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Delete a notification identified by notificationID from all users.
     */
    private void performDeleteNotification(@NonNull AppNotification notification) {
        String targetNotificationID = notification.getNotificationID();
        if (targetNotificationID == null || targetNotificationID.isEmpty()) {
            Toast.makeText(this, "Notification ID is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Load all users again, remove this notification from each user's notifications list
        databaseWorker.getAllUsers().addOnCompleteListener(task -> {
            if (!task.isSuccessful() || task.getResult() == null) {
                Toast.makeText(this, "Failed to delete notification.", Toast.LENGTH_SHORT).show();
                return;
            }

            QuerySnapshot snapshot = task.getResult();
            // For each user, filter out the notification with matching ID
            for (QueryDocumentSnapshot document : snapshot) {
                Object raw = document.get("notifications");
                if (!(raw instanceof ArrayList)) {
                    continue;
                }

                ArrayList<?> list = (ArrayList<?>) raw;
                List<AppNotification> newList = new ArrayList<>();
                boolean changed = false;

                for (Object item : list) {
                    AppNotification n = null;
                    if (item instanceof Map) {
                        Map<?, ?> map = (Map<?, ?>) item;
                        String nid = map.get("notificationID") != null
                                ? map.get("notificationID").toString()
                                : "";
                        String authorID = map.get("authorID") != null
                                ? map.get("authorID").toString()
                                : "";
                        String title = map.get("title") != null
                                ? map.get("title").toString()
                                : "";
                        String description = map.get("description") != null
                                ? map.get("description").toString()
                                : "";
                        Date timeCreated = null;
                        Object timeObj = map.get("time_created");
                        if (timeObj instanceof com.google.firebase.Timestamp) {
                            com.google.firebase.Timestamp ts =
                                    (com.google.firebase.Timestamp) timeObj;
                            timeCreated = ts.toDate();
                        } else if (timeObj instanceof Date) {
                            timeCreated = (Date) timeObj;
                        }

                        n = new AppNotification();
                        n.setNotificationID(nid);
                        n.setAuthorID(authorID);
                        n.setTitle(title);
                        n.setDescription(description);
                        n.setTime_created(timeCreated);
                    } else if (item instanceof AppNotification) {
                        n = (AppNotification) item;
                    }

                    if (n != null) {
                        if (targetNotificationID.equals(n.getNotificationID())) {
                            changed = true;
                            // skip this one (delete)
                        } else {
                            newList.add(n);
                        }
                    }
                }

                if (changed) {
                    String deviceID = document.getId();
                    databaseWorker.updateUserNotifications(deviceID, newList);
                }
            }

            Toast.makeText(this, "Notification deleted.", Toast.LENGTH_SHORT).show();
            // Refresh aggregated notifications list
            loadNotifications();
        });
    }
}
