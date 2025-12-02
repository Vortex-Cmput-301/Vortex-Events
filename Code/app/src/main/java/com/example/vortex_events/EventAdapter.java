package com.example.vortex_events;// adapters/EventAdapter.java
import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private final List<Event> eventList;
    private final ExplorePage activity;

    public EventAdapter(List<Event> eventList, ExplorePage activity) {
        this.eventList = eventList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout (item_event.xml)
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event2, parent, false);
        return new EventViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        // Declare the views from your item_event.xml
        ImageView eventThumbnail;
        TextView eventTitle, eventLocation, eventDate;
        Button detailsButton;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the views
            eventThumbnail = itemView.findViewById(R.id.imageView_event_thumbnail);
            eventTitle = itemView.findViewById(R.id.textView_event_title);
            eventLocation = itemView.findViewById(R.id.textView_event_location);
            eventDate = itemView.findViewById(R.id.textView_event_date);
            detailsButton = itemView.findViewById(R.id.button_details);
        }

        // A helper method to bind data and listeners
    }
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        // Get the data model for this position
        Event currentEvent = eventList.get(position);

        // Bind the data to the views in the ViewHolder
        holder.eventTitle.setText(currentEvent.getName());
        // FIX: Add event location display
        if (currentEvent.getLocation() != null && !currentEvent.getLocation().isEmpty()) {
            holder.eventLocation.setText(currentEvent.getLocation());
        } else {
            holder.eventLocation.setText("Unknown location");
        }
        Date eventStartTime = currentEvent.getStart_time();
        if (eventStartTime != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            holder.eventDate.setText(formatter.format(eventStartTime));
        } else {
            holder.eventDate.setText("No date");
        }

        String imageString = currentEvent.getImage();


        if (imageString != null && !imageString.isEmpty()) {
            try {

                byte[] decodedString = android.util.Base64.decode(imageString, android.util.Base64.DEFAULT);


                com.bumptech.glide.Glide.with(holder.itemView.getContext())
                        .load(decodedString)
                        .fitCenter()
                        .into(holder.eventThumbnail);

            } catch (Exception e) {
                holder.eventThumbnail.setImageResource(R.drawable.app_icon);
            }
        } else {
            holder.eventThumbnail.setImageResource(R.drawable.app_icon);
        }

        // Set the click listener to call the Activity's public method directly
        holder.detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO COMPLETED: Implement event details functionality in ExplorePage
                // Call the public method in ExplorePage to handle details click
                activity.onEventDetailsClick(currentEvent);
            }
        });
    }
}


//        OnClick listener for eventDetails button - Saleh
        // todo completed
//        holder.detailsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(activity, EventDetails.class);
//                intent.putExtra("EventID", currentEvent.eventID);
//                activity.startActivity(intent);
//
//            }
//        });
//    }
//}
