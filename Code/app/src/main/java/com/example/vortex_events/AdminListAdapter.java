package com.example.vortex_events;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Single adapter for AdminActivity.
 * Supports three modes: USERS, IMAGES, NOTIFICATIONS.
 */
public class AdminListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public enum AdminTabType {
        USERS,
        IMAGES,
        NOTIFICATIONS
    }

    public interface AdminItemListener {
        void onUserClicked(@NonNull RegisteredUser user);
        void onImageClicked(@NonNull String imageUrl);
        void onNotificationClicked(@NonNull AppNotification notification);
    }

    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_IMAGE = 2;
    private static final int VIEW_TYPE_NOTIFICATION = 3;

    private AdminTabType currentTab = AdminTabType.USERS;

    // Data lists
    private List<RegisteredUser> users = new ArrayList<>();
    private List<String> images = new ArrayList<>();
    private List<AppNotification> notifications = new ArrayList<>();

    private final AdminItemListener listener;

    public AdminListAdapter(@NonNull AdminItemListener listener) {
        this.listener = listener;
    }

    // ========= Public setters =========

    /**
     * Sets the data for the given tab.
     * @param newUsers The new list of users.
     * */
    public void setUsers(List<RegisteredUser> newUsers) {
        currentTab = AdminTabType.USERS;
        users.clear();
        if (newUsers != null) {
            users.addAll(newUsers);
        }
        notifyDataSetChanged();
    }

    /**
     * Sets the data for the given tab.
     * @param newImages The new list of images.
     * */
    public void setImages(List<String> newImages) {
        currentTab = AdminTabType.IMAGES;
        images.clear();
        if (newImages != null) {
            images.addAll(newImages);
        }
        notifyDataSetChanged();
    }

    /**
     * Sets the data for the given tab.
     * @param newNotifications The new list of notifications.
     * */
    public void setNotifications(List<AppNotification> newNotifications) {
        currentTab = AdminTabType.NOTIFICATIONS;
        notifications.clear();
        if (newNotifications != null) {
            notifications.addAll(newNotifications);
        }
        notifyDataSetChanged();
    }

    /**
     * Returns the current tab.
     * @return The current tab.
     * */
    public AdminTabType getCurrentTab() {
        return currentTab;
    }

    /**
     * Get the view type for the given position.
     * @param position The position of the tab.
     * @return The current tab.
     **/
    @Override
    public int getItemViewType(int position) {
        switch (currentTab) {
            case USERS:
                return VIEW_TYPE_USER;
            case IMAGES:
                return VIEW_TYPE_IMAGE;
            case NOTIFICATIONS:
                return VIEW_TYPE_NOTIFICATION;
            default:
                return VIEW_TYPE_USER;
        }
    }


    /**
     * Create a new ViewHolder for the given view type.
     * @param parent The parent ViewGroup.
     * @param viewType The type of view to create.
     * @return The created ViewHolder.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    )
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_USER) {
            View view = inflater.inflate(R.layout.item_enrolled_user, parent, false);
            return new UserViewHolder(view);
        } else if (viewType == VIEW_TYPE_IMAGE) {
            View view = inflater.inflate(R.layout.item_admin_image, parent, false);
            return new ImageViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_admin_notification, parent, false);
            return new NotificationViewHolder(view);
        }
    }

    /**
     * Bind the data for the given position to the ViewHolder.
     * @param holder The ViewHolder to bind data to.
     * @param position The position of the data.
     */
    @Override
    public void onBindViewHolder(
            @NonNull RecyclerView.ViewHolder holder,
            int position
    ) {
        if (holder instanceof UserViewHolder) {
            bindUser((UserViewHolder) holder, position);
        } else if (holder instanceof ImageViewHolder) {
            bindImage((ImageViewHolder) holder, position);
        } else if (holder instanceof NotificationViewHolder) {
            bindNotification((NotificationViewHolder) holder, position);
        }
    }

    /**
     * Get the number of items for the current tab.
     * @return The number of items.
     */
    @Override
    public int getItemCount() {
        switch (currentTab) {
            case USERS:
                return users.size();
            case IMAGES:
                return images.size();
            case NOTIFICATIONS:
                return notifications.size();
            default:
                return 0;
        }
    }

    // ========= Bind methods =========

    /**
     * Bind user data to the given ViewHolder.
     * @param holder The ViewHolder to bind data to.
     * @param position The position of the data.
     */
    private void bindUser(@NonNull UserViewHolder holder, int position) {
        RegisteredUser user = users.get(position);
        String name = user.getName() != null ? user.getName() : "Unknown user";
        holder.nameTextView.setText(name);

        String subtitle = "ID: " + (user.getDeviceID() != null ? user.getDeviceID() : "Unknown");
        holder.timeTextView.setText(subtitle);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUserClicked(user);
            }
        });
    }

    /**
     * Bind image data to the given ViewHolder.
     * @param holder The ViewHolder to bind data to.
     * @param position The position of the data.
     */
    private void bindImage(@NonNull ImageViewHolder holder, int position) {
        String base64 = images.get(position);

        if (base64 == null || base64.isEmpty()) {
            holder.imageView.setImageDrawable(null);
        } else {
            try {
                byte[] imageBytes = Base64.decode(base64, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                holder.imageView.setImageBitmap(bitmap);
            } catch (IllegalArgumentException e) {
                // Base64 decode failed
                holder.imageView.setImageDrawable(null);
            }
        }

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onImageClicked(base64);
            }
            return true; // consume the long click
        });
    }

    /**
     * Bind notification data to the given ViewHolder.
     * @param holder The ViewHolder to bind data to.
     * @param position The position of the data.
     */
    private void bindNotification(@NonNull NotificationViewHolder holder, int position) {
        AppNotification notification = notifications.get(position);

        String title = notification.getTitle() != null ? notification.getTitle() : "Untitled";
        String description = notification.getDescription() != null
                ? notification.getDescription()
                : "";

        holder.titleTextView.setText(title);
        holder.messageTextView.setText(description);

        Date time = notification.getTime_created();
        if (time != null) {
            String formatted = DateFormat.getDateTimeInstance().format(time);
            holder.timeTextView.setText(formatted);
        } else {
            holder.timeTextView.setText("");
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNotificationClicked(notification);
            }
        });
    }

    // ========= ViewHolders =========

    /**
     * ViewHolder for users.
     */
    static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarImageView;
        TextView nameTextView;
        TextView timeTextView;

        /**
         * Constructor for UserViewHolder.
         * @param itemView The view for this ViewHolder.
         */
        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.imgAvatar);
            nameTextView = itemView.findViewById(R.id.txtName);
            timeTextView = itemView.findViewById(R.id.txtTime);
        }
    }

    /**
     * ViewHolder for images.
     */
    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        /**
         * Constructor for ImageViewHolder.
         * @param itemView The view for this ViewHolder.
         */
        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_admin_event);
        }
    }

    /**
     * ViewHolder for notifications.
     */
    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView messageTextView;
        TextView timeTextView;

        /**
         * Constructor for NotificationViewHolder.
         * @param itemView The view for this ViewHolder.
         */
        NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.text_notification_title);
            messageTextView = itemView.findViewById(R.id.text_notification_message);
            timeTextView = itemView.findViewById(R.id.text_notification_time);
        }
    }
}
