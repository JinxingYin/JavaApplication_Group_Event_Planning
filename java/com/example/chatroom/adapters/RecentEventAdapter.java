package com.example.chatroom.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chatroom.listener.EventListener;
import com.example.chatroom.models.Event;
import com.example.chatroom.databinding.ItemContainerRecentEventBinding;
import java.util.List;


public class RecentEventAdapter extends RecyclerView.Adapter<RecentEventAdapter.EventViewHolder>{

    private final List<Event> events;
    private final EventListener eventListener;

    public RecentEventAdapter(List<Event> events, EventListener eventListener) {
        this.events = events;
        this.eventListener= eventListener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerRecentEventBinding itemContainerRecentEventBinding = ItemContainerRecentEventBinding
                .inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new EventViewHolder(itemContainerRecentEventBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentEventAdapter.EventViewHolder holder, int position) {
        holder.setData(events.get(position));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }


    class EventViewHolder extends RecyclerView.ViewHolder{
        ItemContainerRecentEventBinding binding;
        EventViewHolder(ItemContainerRecentEventBinding itemContainerRecentEventBinding){
            super(itemContainerRecentEventBinding.getRoot());
            binding = itemContainerRecentEventBinding;
        }
        void setData(Event event){
            binding.textName.setText(event.name);
            binding.textRecentMessage.setText(event.message);
            binding.getRoot().setOnClickListener(
                    v-> {
                        eventListener.onEventClicked(event);
                    }
            );
        }
    }

}
