package com.example.vortex_events;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.w3c.dom.Text;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class notificationsAdapter extends ArrayAdapter<AppNotification> {


    private Context context;
    private List<AppNotification> items;
    public notificationsAdapter(@NonNull Context context, List<AppNotification> notifications) {
        super(context, 0, notifications);
        this.context = context;
        this.items = notifications;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.notification_thumb, parent, false);
        }

        AppNotification currentItem = items.get(position);

        TextView notification_text = convertView.findViewById(R.id.notiication_text);


        String sampleText = currentItem.title + ": " + currentItem.description;

        notification_text.setText(sampleText);

        return convertView;
    }


}
