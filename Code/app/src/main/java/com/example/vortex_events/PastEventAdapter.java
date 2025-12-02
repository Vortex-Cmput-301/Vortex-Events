package com.example.vortex_events;

import android.content.Intent;
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
    private final androidx.appcompat.app.AppCompatActivity activity; // can be Profile or MainActivity

    /**
     * Constructor that takes the data list and a reference to the calling Activity.
     * @param pastEventList The list of events to display.
     * @param activity The Activity instance, used for handling clicks.
     */
    public PastEventAdapter(List<Event> pastEventList, androidx.appcompat.app.AppCompatActivity activity) {
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
        ImageView thumbnail;
        TextView title, date;
        Button detailsButton;
        TextView eventLocation;

        public PastEventViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.past_event_thumbnail);
            title = itemView.findViewById(R.id.past_event_title);
            date = itemView.findViewById(R.id.past_event_date);
            detailsButton = itemView.findViewById(R.id.past_event_details_button);
            eventLocation = itemView.findViewById(R.id.textView_event_location);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull PastEventViewHolder holder, int position) {
        Event currentEvent = pastEventList.get(position);

        holder.title.setText(currentEvent.getName());

        String imageString = currentEvent.getImage();


        if (imageString != null && !imageString.isEmpty()) {
            try {

                byte[] decodedString = android.util.Base64.decode(imageString, android.util.Base64.DEFAULT);


                com.bumptech.glide.Glide.with(holder.itemView.getContext())
                        .load(decodedString)
                        .centerCrop()
                        .into(holder.thumbnail);

            } catch (Exception e) {

                holder.thumbnail.setImageResource(R.drawable.app_icon);
            }
        } else {
            holder.thumbnail.setImageResource(R.drawable.app_icon);
        }

        if (currentEvent.getLocation() != null && !currentEvent.getLocation().isEmpty()) {
            holder.eventLocation.setText(currentEvent.getLocation());
        } else {
            holder.eventLocation.setText("Unknown location");
        }

        Date eventStartTime = currentEvent.getStart_time();
        if (eventStartTime != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault());
            holder.date.setText(formatter.format(eventStartTime));
        } else {
            holder.date.setText("No date");
        }

        holder.detailsButton.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION && activity != null) {
                Event clickedEvent = pastEventList.get(currentPosition);
                Intent intent = new Intent(activity, EventDetails.class);
                intent.putExtra("EventID", clickedEvent.getEventID());
                intent.putExtra("prev_activity", "home"); // can be overridden by caller if needed
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pastEventList.size();
    }

}
