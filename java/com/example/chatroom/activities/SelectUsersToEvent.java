package com.example.chatroom.activities;

import androidx.annotation.NonNull;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.example.chatroom.adapters.UsersAdapter;
import com.example.chatroom.databinding.ActivitySelectUsersToEventBinding;
import com.example.chatroom.listener.UserListener;
import com.example.chatroom.models.Event;
import com.example.chatroom.models.User;
import com.example.chatroom.utilities.Constants;
import com.example.chatroom.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

// add users to the event
public class SelectUsersToEvent extends BaseActivity implements UserListener {
    private ActivitySelectUsersToEventBinding binding;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    List <User> group = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectUsersToEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        preferenceManager.putBoolean(Constants.KEY_SELECT_TO_EVENT_OR_NOT, true);
        database = FirebaseFirestore.getInstance();
        setListener();
        getUsers();
    }

    //set up the listener
    private void setListener(){
        binding.imageBack.setOnClickListener(v-> onBackPressed());
        binding.nextPage.setOnClickListener(v->{
            //add the current user to the list
           // group = new ArrayList<>();
            User user = new User();
            user.name = preferenceManager.getString(Constants.KEY_NAME);
            user.email = preferenceManager.getString(Constants.KEY_USER_EMAIL);
            user.image = preferenceManager.getString(Constants.KEY_IMAGE);
            user.id = preferenceManager.getString(Constants.KEY_USER_ID);
            user.addToGroup = preferenceManager.getBoolean(Constants.KEY_ADDED_TO_GROUP);
            group.add(user);
            for(int i =0; i<group.size(); i++){
                if(group.get(i).groups == null) {
                    group.get(i).groups = new ArrayList<>();
                }
                group.get(i).groups.add(preferenceManager.getString(Constants.KEY_EVENT));
            }
            //retrieve the event list users previously had and update it
            database.collection(Constants.KEY_COLLECTION_USERS).get().addOnCompleteListener(task -> {
                if(task.isSuccessful() && task.getResult()!=null){
                    for(int index =0; index<group.size(); index++){
                        for(int i=0; i<task.getResult().getDocuments().size(); i++){
                            DocumentReference df = database.collection(Constants.KEY_COLLECTION_USERS)
                                    .document(task.getResult().getDocuments().get(i).getId());
                            if(task.getResult().getDocuments().get(i).getString(Constants.KEY_NAME).equals(group.get(index).name)) {
                                List<String> old_group_list = convertObjectToList(task.getResult().getDocuments().get(i).get(Constants.KEY_GROUP_LIST));
                                old_group_list.add(preferenceManager.getString(Constants.KEY_EVENT));
                                df.update(Constants.KEY_GROUP_LIST, old_group_list);
                            }
                        }
                    }
                }
            });
            //update the event list collection in firebase firestore
            HashMap<String,Object> event = new HashMap<>();
            event.put(Constants.KEY_EVENT_NAME, preferenceManager.getString(Constants.KEY_EVENT));
            event.put("Event Description",preferenceManager.getString(Constants.KEY_EVENT_DESCRIPTION));
            event.put("Date",preferenceManager.getString(Constants.KEY_EVENT_DATE));
            event.put("Time", preferenceManager.getString(Constants.KEY_EVENT_TIME));
            List<String> members = new ArrayList<>();
            for(int i=0; i<group.size(); i++){
                members.add(group.get(i).name);
            }
            event.put("Members",members);
            database.collection(Constants.KEY_EVENT_LIST).add(event).addOnSuccessListener(
                    new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("Document add success", "Document Snapshot written with ID: "+ documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Document add error","Error adding document" ,e);
                        }
                    });
            //jump directly to the group chat room
            Intent intent = new Intent(getApplicationContext(),GroupChatActivity.class);
            Event eventInfo = new Event();
            eventInfo.members = convertObjectToList(event.get("Members"));
            eventInfo.name = event.get(Constants.KEY_EVENT_NAME).toString();
            eventInfo.description = event.get("Event Description").toString();
            eventInfo.date = event.get("Date").toString();
            eventInfo.time = event.get("Time").toString();
            intent.putExtra(Constants.KEY_EVENT,eventInfo);
            startActivity(intent);
            //finish();
        });
    }

    //display the list of users and wait for selection
    private void getUsers(){
        loading(true);
        //FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task->{
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if(task.isSuccessful() && task.getResult()!=null){
                        List<User> users = new ArrayList<>();
                        for(QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                            if(currentUserId.equals(queryDocumentSnapshot.getId())){
                                continue;
                            }
                            User user = new User();
                            user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                            user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                            user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            user.id = queryDocumentSnapshot.getId();
                            user.groups = convertObjectToList(queryDocumentSnapshot.get(Constants.KEY_GROUP_LIST));
                            users.add(user);
                        }
                        if(users.size() >0){
                            UsersAdapter usersAdapter = new UsersAdapter(users,this);
                            binding.usersRecyclerView.setAdapter(usersAdapter);
                            binding.usersRecyclerView.setVisibility(View.VISIBLE);
                        }else{
                            showErrorMessage();
                        }
                    }
                    else {
                        showErrorMessage();
                    }
                });
    }

    //error message printing
    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s","No User Available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    //loading the user list
    private void loading(Boolean isLoading){
        if(isLoading)
            binding.progressBar.setVisibility(View.VISIBLE);
        else
            binding.progressBar.setVisibility(View.INVISIBLE);
    }

    //depend on whether the user is in event or not add to it
    @Override
    public void onUserClicked(User user){
        if(preferenceManager.getBoolean(Constants.KEY_SELECT_TO_EVENT_OR_NOT)) {
            if(!user.addToGroup){
                group.add(user);
                user.addToGroup = true;
            }
            else {
                group.remove(user);
                user.addToGroup = false;
            }
        }
        for(int i =0; i<group.size(); i++){
            Log.d("Group Member ", "On Click!! group member has: " + group.get(i).name);
        }
    }

    //convert helper function which convert the object read from firebase firestore to array list
    public static List<String> convertObjectToList(Object obj) {
        List<String> list = new ArrayList<>();
         if (obj instanceof Collection) {
            list = new ArrayList<>((Collection<String>)obj);
        }
        return list;
    }
}