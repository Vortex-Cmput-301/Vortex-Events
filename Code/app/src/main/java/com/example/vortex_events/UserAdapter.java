package com.example.vortex_events;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * Simple ArrayAdapter to show RegisteredUser items in a ListView.
 */
public class UserAdapter extends ArrayAdapter<RegisteredUser> {

    /**
     * Create a new UserAdapter.
     */
    public UserAdapter(Context context, ArrayList<RegisteredUser> users) {
        super(context, 0, users);
    }

    /**
     * Bind a RegisteredUser to the list item view.
     */
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
        }

        return listItemView;
    }
}
