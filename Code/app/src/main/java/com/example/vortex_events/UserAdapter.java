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
 * ArrayAdapter used to display RegisteredUser objects
 *
 */
public class UserAdapter extends ArrayAdapter<RegisteredUser> {

    /**
     * Creates a new UserAdapter.
     *
     * @param context the Activity or Application context
     * @param users   the list of RegisteredUser objects to display
     */
    public UserAdapter(Context context, ArrayList<RegisteredUser> users) {
        super(context, 0, users);
    }

    /**
     * Provides the view used to display each RegisteredUser in the list.
     *
     * @param position     the index of the current item in the list
     * @param convertView  a recycled view that can be reused
     * @param parent       the parent ViewGroup that this view is attached to
     * @return the completed list item view for display
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
