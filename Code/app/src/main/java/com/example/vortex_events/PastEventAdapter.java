package com.example.vortex_events;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PastEventAdapter extends RecyclerView.Adapter<PastEventAdapter.PastEventViewHolder> {

    private final List<Event> pastEventList;
    private final Profile activity; // A reference to the activity that created it.

    /**
     * Constructor that takes the data list and a reference to the calling Activity.
     * @param pastEventList The list of events to display.
     * @param activity The ProfileActivity instance, used for handling clicks.
     */
    public PastEventAdapter(List<Event> pastEventList, Profile activity) {
        this.pastEventList = pastEventList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public PastEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout (item_past_event.xml)
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_past_event, parent, false);
        return new PastEventViewHolder(itemView);
    }

    public static class PastEventViewHolder extends RecyclerView.ViewHolder {
        // Declare the views from your item_past_event.xml
        ImageView thumbnail;
        TextView title, date;
        Button detailsButton;

        public PastEventViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the views using findViewById
            thumbnail = itemView.findViewById(R.id.past_event_thumbnail);
            title = itemView.findViewById(R.id.past_event_title);
            date = itemView.findViewById(R.id.past_event_date);
            detailsButton = itemView.findViewById(R.id.past_event_details_button);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull PastEventViewHolder holder, int position) {
        // Get the data model for this position
        Event currentEvent = pastEventList.get(position);

        // Bind the data to the views in the ViewHolder
        holder.title.setText(currentEvent.getName());

        // Format and set the date
        Date eventStartTime = currentEvent.getStart_time();
        if (eventStartTime != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault());
            holder.date.setText(formatter.format(eventStartTime));
        } else {
            holder.date.setText("No date");
        }

        // Set the click listener to call the Activity's public method directly
        holder.detailsButton.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            // Check for a valid position, as it can be NO_POSITION during layout changes
            if (currentPosition != RecyclerView.NO_POSITION) {
                // Directly call the public method on the ProfileActivity instance
//                activity.onPastEventDetailsClick(currentPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pastEventList.size();
    }

}
