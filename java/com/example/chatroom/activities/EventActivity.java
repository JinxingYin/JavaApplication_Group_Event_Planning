package com.example.chatroom.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import com.example.chatroom.databinding.ActivityEventBinding;
import com.example.chatroom.models.Event;
import com.example.chatroom.utilities.Constants;

// a place holder for all activities relate to an event
public class EventActivity extends AppCompatActivity {
    private ActivityEventBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.groupchat.setOnClickListener(view -> {
            Event event = (Event)getIntent().getSerializableExtra(Constants.KEY_EVENT);
            Intent intent = new Intent(getApplicationContext(),GroupChatActivity.class);
            intent.putExtra(Constants.KEY_EVENT,event);
            startActivity(intent);
            finish();
        });
        binding.button3.setOnClickListener(view->{
            Event event = (Event)getIntent().getSerializableExtra(Constants.KEY_EVENT);
            startActivity(new Intent(getApplicationContext(),Polling.class).putExtra(Constants.KEY_EVENT,event));

            finish();
        });
        binding.button.setOnClickListener(view-> {
            startActivity(new Intent(getApplicationContext(), Itinerary.class));
        });
    }
}