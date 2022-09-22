package com.example.chatroom.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.example.chatroom.adapters.RecentEventAdapter;
import com.example.chatroom.databinding.ActivityListEventBinding;
import com.example.chatroom.listener.EventListener;
import com.example.chatroom.models.Event;
import com.example.chatroom.utilities.Constants;
import com.example.chatroom.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//activity for user to see the list of event he joined and select one to play with
public class ListEventActivity extends BaseActivity implements EventListener {
    private ActivityListEventBinding binding;
    private PreferenceManager preferenceManager;
    private List<String> event_name_list = new ArrayList<>();
    private List<Event> events = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListener();
        getEvents();
    }

    //button listener for back a page
    private void setListener(){
        binding.imageBack.setOnClickListener(v-> onBackPressed());
    }

    //retrieve the list of events the user joined
    private void getEvents() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null) {
                        for(int i=0; i<task.getResult().getDocuments().size(); i++) {
                            //find the list of event
                            if(task.getResult().getDocuments().get(i).getId().equals(currentUserId)) {
                                event_name_list = convertObjectToList(task.getResult().getDocuments().get(i).get(Constants.KEY_GROUP_LIST));
                            }
                            }
                        database.collection(Constants.KEY_EVENT_LIST).get().addOnCompleteListener(task1->{
                            //retrieve the detail of that event and set up the binding between the event object with the view
                            if(task1.isSuccessful() && task1.getResult()!=null) {
                                for (int j = 0; j < event_name_list.size(); j++) {
                                    for(int k=0; k<task1.getResult().getDocuments().size(); k++){
                                        if(task1.getResult().getDocuments().get(k).getString(Constants.KEY_EVENT_NAME).equals(event_name_list.get(j))){
                                            Event event1 = new Event();
                                            event1.name = task1.getResult().getDocuments().get(k).getString(Constants.KEY_EVENT_NAME);
                                            event1.description = task1.getResult().getDocuments().get(k).getString("Event Description");
                                            event1.date = task1.getResult().getDocuments().get(k).getString("Date");
                                            event1.time = task1.getResult().getDocuments().get(k).getString("Time");
                                            event1.members = convertObjectToList(task1.getResult().getDocuments().get(k).get("Members"));
                                            events.add(event1);
                                            if (events.size() > 0) {
                                                RecentEventAdapter recentEventAdapter= new RecentEventAdapter(events,this);
                                                binding.eventsRecyclerView.setAdapter(recentEventAdapter);
                                                binding.eventsRecyclerView.setVisibility(View.VISIBLE);
                                            } else {
                                                showErrorMessage();
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    } else {
                        showErrorMessage();
                    }
                });
    }

    //error message
    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s","No Events Available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);

    }

    //loading the list
    private void loading(Boolean isLoading){
        if(isLoading)
            binding.progressBar.setVisibility(View.VISIBLE);
        else
            binding.progressBar.setVisibility(View.INVISIBLE);
    }

    //convert return object from firebase fire store to array list
    public static List<String> convertObjectToList(Object obj) {
        List<String> list = new ArrayList<>();
        if (obj instanceof Collection) {
            list = new ArrayList<>((Collection<String>)obj);
        }
        return list;
    }

    //when an event is clicked go to the detail of the event page
    @Override
    public void onEventClicked(Event event){
            Intent intent = new Intent(getApplicationContext(), EventActivity.class);
            intent.putExtra(Constants.KEY_EVENT,event);
            startActivity(intent);
    }
}