package com.example.chatroom.activities;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.example.chatroom.adapters.GroupChatAdapter;
import com.example.chatroom.databinding.ActivityGroupChatBinding;
import com.example.chatroom.models.ChatMessage;
import com.example.chatroom.models.Event;
import com.example.chatroom.utilities.Constants;
import com.example.chatroom.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class GroupChatActivity extends BaseActivity {
    private ActivityGroupChatBinding binding;
    private List<ChatMessage> chatMessages;
    private GroupChatAdapter groupChatAdapter;
    private FirebaseFirestore database;
    private Event event;
    private String conversionId =null;
    private PreferenceManager preferenceManager;
    private List<String> receivers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        database = FirebaseFirestore.getInstance();
        setListener();
        loadEventDetail();
        init();
        listenMessage();
    }

    //button click listener
    private void setListener(){
        binding.imageBack.setOnClickListener(v-> onBackPressed());
        binding.layoutSend.setOnClickListener(v-> sendMessage());
    }

    //gather all the info about the event
    private void loadEventDetail(){
        event = (Event)getIntent().getSerializableExtra(Constants.KEY_EVENT);
        binding.textName.setText(event.name);
        receivers = event.members;
        for(int i=0; i<receivers.size(); i++){
            if(receivers.get(i).equals(preferenceManager.getString(Constants.KEY_NAME))){
                Log.d("LoadEventDetail",""+receivers.get(i));
                receivers.remove(i);
            }
        }
    }

    //initialize the chat room
    private void init(){
        chatMessages = new ArrayList<>();
        groupChatAdapter = new GroupChatAdapter(
                chatMessages,receivers,
                preferenceManager.getString(Constants.KEY_USER_ID)
        );
        binding.chatRecyclerView.setAdapter(groupChatAdapter);

    }

    //retrieve the conversionId from fire store
    private void addConversion(HashMap<String, Object> conversion){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .add(conversion)
                .addOnSuccessListener(documentReference ->  conversionId = documentReference.getId());
    }

    //update the conversionId
    private void updateConversion(String message){
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(conversionId);
        documentReference.update(Constants.KEY_LAST_MESSAGE,message,Constants.KEY_TIMESTAMP, new Date());
    }

    //check for the conversion id and invoke the checkConversion remotely function
    private void checkForConversion(){
        if(chatMessages.size()!= 0){
            checkForConversionRemotely(
                    preferenceManager.getString(Constants.KEY_USER_ID),
                    event.name
            );
            checkForConversionRemotely(
                    event.name,
                    preferenceManager.getString(Constants.KEY_USER_ID)
            );
        }
    }

    //query the conversion by the sender id and event name
    private void checkForConversionRemotely(String senderId,String eventName){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID,senderId)
                .whereEqualTo(Constants.KEY_EVENT_NAME,eventName)
                .get()
                .addOnCompleteListener(conversionOnCompleterListener);
    }

    //if the checking conversion is successful a conversion id will be returned
    private final OnCompleteListener<QuerySnapshot> conversionOnCompleterListener = task->
    {if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size()>0){
        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
        conversionId = documentSnapshot.getId();
    }
    };

    //sending the message
    private void sendMessage(){
        //Hash map the message
        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_NAME,preferenceManager.getString(Constants.KEY_NAME));
        message.put(Constants.KEY_IS_GROUP_MESSAGE,"True");
        message.put(Constants.KEY_EVENT_NAME,event.name);
        message.put("Receiver List",receivers);
        message.put(Constants.KEY_MESSAGE,binding.inputMessage.getText().toString());
        message.put(Constants.KEY_TIMESTAMP,new Date());
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        if(conversionId!= null){
            updateConversion(binding.inputMessage.getText().toString());
        }
        else{
            //gather info for the conversion id if it's null and create one by calling addConversion function
            HashMap<String,Object> conversion = new HashMap<>();
            conversion.put(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID));
            conversion.put(Constants.KEY_SENDER_NAME,preferenceManager.getString(Constants.KEY_NAME));
            conversion.put(Constants.KEY_SENDER_IMAGE,preferenceManager.getString(Constants.KEY_IMAGE));
            conversion.put(Constants.KEY_IS_GROUP_MESSAGE,"true");
            conversion.put(Constants.KEY_EVENT_NAME,event.name);
            conversion.put("Receiver List",receivers);
            conversion.put(Constants.KEY_LAST_MESSAGE,binding.inputMessage.getText().toString());
            conversion.put(Constants.KEY_TIMESTAMP, new Date());
            addConversion(conversion);
        }
        binding.inputMessage.setText(null);
    }

    //return the current date time
    private String getDateTime(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    //event listener for the listen message event
    private final EventListener<QuerySnapshot> eventListener =(value, error) -> {
        if(error!= null){
            return ;
        }
        if(value != null){
            int count = chatMessages.size();
            for(DocumentChange documentChange : value.getDocumentChanges()){
                if(documentChange.getType() == DocumentChange.Type.ADDED) {
                    // if a document with related info was added in database retrieve this message and add to the chatroom
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderName = documentChange.getDocument().getString(Constants.KEY_NAME);
                    chatMessage.senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    chatMessage.eventName = documentChange.getDocument().getString(Constants.KEY_EVENT_NAME);
                    chatMessage.receivers = convertObjectToList(documentChange.getDocument().get("Receiver List"));
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_MESSAGE);
                    chatMessage.dateTime = getDateTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    chatMessages.add(chatMessage);
                }
            }
            Collections.sort(chatMessages, (object1, object2) -> object1.dateObject.compareTo(object2.dateObject));
            if(count ==0){
                groupChatAdapter.notifyDataSetChanged();
            }else{
                groupChatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size()-1);
            }
            binding.chatRecyclerView.setVisibility(View.VISIBLE);
        }
        binding.progressBar.setVisibility(View.GONE);
        if(conversionId == null){
            checkForConversion();
        }
    };

    //check for message related to the event & user
    private void listenMessage(){
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_EVENT_NAME,event.name)
               .addSnapshotListener(eventListener);
    }

    //convert the return object retrieve from fire base fire store to array list
    public static List<String> convertObjectToList(Object obj) {
        List<String> list = new ArrayList<>();
        if (obj instanceof Collection) {
            list = new ArrayList<>((Collection<String>)obj);
        }
        return list;
    }

}