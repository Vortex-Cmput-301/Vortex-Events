package com.example.vortex_events;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Locale;

public class ParticipantAdapter extends ArrayAdapter<ParticipantEntry> {

    public ParticipantAdapter(@NonNull Context context, @NonNull List<ParticipantEntry> data) {
        super(context, 0, data);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_enrolled_user, parent, false);
        }

        ParticipantEntry item = getItem(position);
        if (item == null) return convertView;

        ImageView avatar = convertView.findViewById(R.id.imgAvatar);
        TextView txtName = convertView.findViewById(R.id.txtName);
        TextView txtTime = convertView.findViewById(R.id.txtTime);

        // Name（Email）
        String name = item.getName();
        String email = item.getEmail();
        if (!TextUtils.isEmpty(email)) {
            txtName.setText(name + "  (" + email + ")");
        } else {
            txtName.setText(name);
        }

        // Second line：Status + time
        String statusLabel;
        switch (item.getStatus()) {
            case ACCEPT: statusLabel = "Accepted"; break;
            case WAITING: statusLabel = "Waiting list"; break;
            default: statusLabel = "Cancelled";
        }

        String timePart = "";
        if (item.getEnrolledAt() > 0) {
            String formatted = DateFormat.format("yyyy-MM-dd HH:mm", item.getEnrolledAt()).toString();
            timePart = " • " + formatted;
        }
        txtTime.setText(String.format(Locale.getDefault(), "%s%s", statusLabel, timePart));



        return convertView;
    }
}
