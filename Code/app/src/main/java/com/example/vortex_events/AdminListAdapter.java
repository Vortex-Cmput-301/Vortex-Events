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

    public void setUsers(List<RegisteredUser> newUsers) {
        currentTab = AdminTabType.USERS;
        users.clear();
        if (newUsers != null) {
            users.addAll(newUsers);
        }
        notifyDataSetChanged();
    }

    public void setImages(List<String> newImages) {
        currentTab = AdminTabType.IMAGES;
        images.clear();
        if (newImages != null) {
            images.addAll(newImages);
        }
        notifyDataSetChanged();
    }

    public void setNotifications(List<AppNotification> newNotifications) {
        currentTab = AdminTabType.NOTIFICATIONS;
        notifications.clear();
        if (newNotifications != null) {
            notifications.addAll(newNotifications);
        }
        notifyDataSetChanged();
    }

    public AdminTabType getCurrentTab() {
        return currentTab;
    }

    // ========= RecyclerView.Adapter =========

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

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
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

    static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarImageView;
        TextView nameTextView;
        TextView timeTextView;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.imgAvatar);
            nameTextView = itemView.findViewById(R.id.txtName);
            timeTextView = itemView.findViewById(R.id.txtTime);
        }
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_admin_event);
        }
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView messageTextView;
        TextView timeTextView;

        NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.text_notification_title);
            messageTextView = itemView.findViewById(R.id.text_notification_message);
            timeTextView = itemView.findViewById(R.id.text_notification_time);
        }
    }
}
