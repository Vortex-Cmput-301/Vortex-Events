package com.example.vortex_events;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashSet;

public class UserAdapter extends ArrayAdapter<RegisteredUser> {

    // Tracks the Device IDs of the selected users
    private HashSet<String> selectedItems = new HashSet<>();
    private boolean selectionMode = false;

    public UserAdapter(Context context, ArrayList<RegisteredUser> users) {
        super(context, 0, users);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }

        RegisteredUser currentUser = getItem(position);
        TextView nameTextView = listItemView.findViewById(R.id.user_name);

        if (currentUser != null) {
            nameTextView.setText(currentUser.getName());

            // If this user is selected, turn the row GREY. Otherwise, White/Transparent.
            if (selectedItems.contains(currentUser.getDeviceID())) {
                listItemView.setBackgroundColor(Color.LTGRAY);
            } else {
                listItemView.setBackgroundColor(Color.TRANSPARENT);
            }
        }

        return listItemView;
    }



    public void toggleSelection(String deviceID) {
        if (selectedItems.contains(deviceID)) {
            selectedItems.remove(deviceID);
        } else {
            selectedItems.add(deviceID);
        }

        // Turn selection mode on if at least 1 person is selected
        selectionMode = !selectedItems.isEmpty();

        notifyDataSetChanged(); // Refreshes the list to show/hide grey color
    }

    public boolean isSelectionMode() {
        return selectionMode;
    }

    public ArrayList<String> getSelectedIds() {
        return new ArrayList<>(selectedItems);
    }

    public void clearSelection() {
        selectedItems.clear();
        selectionMode = false;
        notifyDataSetChanged();
    }
}
