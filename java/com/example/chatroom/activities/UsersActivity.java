package com.example.chatroom.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.example.chatroom.adapters.UsersAdapter;
import com.example.chatroom.databinding.ActivityUsersBinding;
import com.example.chatroom.listener.UserListener;
import com.example.chatroom.models.User;
import com.example.chatroom.utilities.Constants;
import com.example.chatroom.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

// activity which select a user to talk with
public class UsersActivity extends BaseActivity implements UserListener {
private ActivityUsersBinding binding;
private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        preferenceManager.putBoolean(Constants.KEY_SELECT_TO_EVENT_OR_NOT, false);
        setListener();
        getUsers();
    }

    //go back to previous page
    private void setListener(){
        binding.imageBack.setOnClickListener(v-> onBackPressed());
    }

    //load all the users who download the app and display them as a user list
    private void getUsers(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        // return all the users
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
                            users.add(user);
                        }
                        //bind the user object with the view
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

    //error message showing
    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s","No User Available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    //initial loading the page
    private void loading(Boolean isLoading){
        if(isLoading)
            binding.progressBar.setVisibility(View.VISIBLE);
        else
            binding.progressBar.setVisibility(View.INVISIBLE);
    }

    //handles the clicks to the user to 1 to 1 chat
    @Override
    public void onUserClicked(User user){
        if(!preferenceManager.getBoolean(Constants.KEY_SELECT_TO_EVENT_OR_NOT)) {
            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
            intent.putExtra(Constants.KEY_USER, user);
            startActivity(intent);
            finish();
        }
    }
}