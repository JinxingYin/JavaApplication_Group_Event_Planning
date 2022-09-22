package com.example.chatroom.activities;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.chatroom.adapters.ChatAdapter;
import com.example.chatroom.databinding.ActivityChatBinding;
import com.example.chatroom.models.ChatMessage;
import com.example.chatroom.models.User;
import com.example.chatroom.network.ApiClient;
import com.example.chatroom.network.ApiService;
import com.example.chatroom.utilities.Constants;
import com.example.chatroom.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// activity when user clicks the one to one chat
public class ChatActivity extends BaseActivity {
    private ActivityChatBinding binding;
    private User receiverUser;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private String conversionId = null;
    private Boolean isReceiverAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();
        loadReceiverDetails();
        init();
        listenMessage();
    }

    //set button listeners
    private void setListener(){
        binding.imageBack.setOnClickListener(v-> onBackPressed());
        binding.layoutSend.setOnClickListener(v-> sendMessage());
    }

    //load receiver details
    private void loadReceiverDetails(){
        receiverUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textName.setText(receiverUser.name);
    }

    //initialize the messages
     private void init(){
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(
                chatMessages,getBitmapFromEncodedString(receiverUser.image),
                preferenceManager.getString(Constants.KEY_USER_ID)
        );
        binding.chatRecyclerView.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();

     }

     //handles the sender sends messages
     private void sendMessage(){
        //add message to FireBaseFireStore
         HashMap<String, Object> message = new HashMap<>();
         message.put(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID));
         message.put(Constants.KEY_IS_GROUP_MESSAGE,"false");
         message.put(Constants.KEY_RECEIVER_ID,receiverUser.id);
         message.put(Constants.KEY_MESSAGE,binding.inputMessage.getText().toString());
         message.put(Constants.KEY_TIMESTAMP,new Date());
         database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
         if(conversionId!= null){
             updateConversion(binding.inputMessage.getText().toString());
         }
         else{
             //get the conversionId
             HashMap<String,Object> conversion = new HashMap<>();
             conversion.put(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID));
             conversion.put(Constants.KEY_SENDER_NAME,preferenceManager.getString(Constants.KEY_NAME));
             conversion.put(Constants.KEY_SENDER_IMAGE,preferenceManager.getString(Constants.KEY_IMAGE));
             conversion.put(Constants.KEY_IS_GROUP_MESSAGE,"false");
             conversion.put(Constants.KEY_RECEIVER_ID,receiverUser.id);
             conversion.put(Constants.KEY_RECEIVER_NAME,receiverUser.name);
             conversion.put(Constants.KEY_RECEIVER_IMAGE,receiverUser.image);
             conversion.put(Constants.KEY_LAST_MESSAGE,binding.inputMessage.getText().toString());
             conversion.put(Constants.KEY_TIMESTAMP, new Date());
             addConversion(conversion);
         }
         //check the receiver is offline or not and invoke notification function
         if(!isReceiverAvailable){
             try{
                 JSONArray tokens = new JSONArray();
                 tokens.put(receiverUser.token);
                 JSONObject data = new JSONObject();
                 data.put(Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
                 data.put(Constants.KEY_NAME,preferenceManager.getString(Constants.KEY_NAME));
                 data.put(Constants.KEY_FCM_TOKEN,preferenceManager.getString(Constants.KEY_FCM_TOKEN));
                 data.put(Constants.KEY_MESSAGE,binding.inputMessage.getText().toString());
                 JSONObject body = new JSONObject();
                 body.put(Constants.REMOTE_MSG_DATA, data);
                 body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);
                 sendNotification(body.toString());
             }catch (Exception exception){
                 showToast(exception.getMessage());
             }
         }
         binding.inputMessage.setText(null);
     }

     private void showToast(String message){
         Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
     }

    //send the notification to the receiver
     private void sendNotification(String messageBody){
         ApiClient.getClient().create(ApiService.class).sendMessage(
                 Constants.getRemoteMsgHeaders(), messageBody)
                 .enqueue(new Callback<String>() {
                     @Override
                     public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            if(response.isSuccessful()){
                                try{
                                    if(response.body()!= null){
                                        JSONObject responseJson = new JSONObject(response.body());
                                        JSONArray results = responseJson.getJSONArray("results");
                                        if(responseJson.getInt("failure") == 1){
                                            JSONObject error = (JSONObject) results.get(0);
                                            showToast(error.getString("error"));
                                            return;
                                        }
                                    }
                                }catch(JSONException e){
                                    e.printStackTrace();
                                }
                                showToast("Notification sent successfully");
                                Log.d("Notification" , "Notification sent successfully");
                            }else{
                                showToast("Error "+ response.code());
                                Log.d("Error" ,""+ response.code());
                            }
                     }
                     @Override
                     public void onFailure(@NonNull Call <String> call, @NonNull Throwable t) {
                        showToast("Error came from here" + t.getMessage());
                        Log.d("error" , "" + t.getMessage());
                     }
                 });
     }

     //check the receiver is offline or not
     private void listenAvailabilityOfReceiver(){
        database.collection(Constants.KEY_COLLECTION_USERS).document(
            receiverUser.id).
            addSnapshotListener(ChatActivity.this, (value, error)->{
                if(error != null){
                    return;
                }
                if(value != null){
                    if(value.getLong(Constants.KEY_AVAILABILITY)!= null){
                        int availability = Objects.requireNonNull(
                                value.getLong(Constants.KEY_AVAILABILITY)
                        ).intValue();
                        isReceiverAvailable = availability ==1;
                    }
                    receiverUser.token = value.getString(Constants.KEY_FCM_TOKEN);
                    if(receiverUser.image == null){
                        receiverUser.image = value.getString(Constants.KEY_IMAGE);
                        chatAdapter.setReceiverProfileImage(getBitmapFromEncodedString(receiverUser.image));
                    chatAdapter.notifyItemRangeChanged(0,chatMessages.size());
                    }
                }
                if(isReceiverAvailable){
                    binding.textAvailability.setVisibility(View.VISIBLE);
                }
                else{
                    binding.textAvailability.setVisibility(View.GONE);
                }
            });
     }

     //listen to incoming message whether current user is sender or receiver
     private void listenMessage(){
        database.collection(Constants.KEY_COLLECTION_CHAT).
                whereEqualTo(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVER_ID,receiverUser.id)
                .addSnapshotListener(eventListener);
         database.collection(Constants.KEY_COLLECTION_CHAT)
                 .whereEqualTo(Constants.KEY_SENDER_ID,receiverUser.id)
                 .whereEqualTo(Constants.KEY_RECEIVER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
                 .addSnapshotListener(eventListener);
     }

     //update the chat message with the listen message event
     private final EventListener<QuerySnapshot> eventListener =(value, error) -> {
         if(error!= null){
             return ;
         }
         if(value != null){
             int count = chatMessages.size();
             for(DocumentChange documentChange : value.getDocumentChanges()){
                 if(documentChange.getType() == DocumentChange.Type.ADDED) {
                     ChatMessage chatMessage = new ChatMessage();
                     chatMessage.senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                     chatMessage.receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                     chatMessage.message = documentChange.getDocument().getString(Constants.KEY_MESSAGE);
                     chatMessage.dateTime = getDateTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                     chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                     chatMessages.add(chatMessage);
                 }
             }
             Collections.sort(chatMessages, (object1, object2) -> object1.dateObject.compareTo(object2.dateObject));
             if(count ==0){
                 chatAdapter.notifyDataSetChanged();
             }else{
                 chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                 binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size()-1);
             }
             binding.chatRecyclerView.setVisibility(View.VISIBLE);
         }
         binding.progressBar.setVisibility(View.GONE);
         if(conversionId == null){
             checkForConversion();
         }
     };

    //convert the encoded Image string to image
    private Bitmap getBitmapFromEncodedString(String encodedImage){
        if(encodedImage != null) {
            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        else {
            return null;
        }
    }

    //return the current time
    private String getDateTime(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    //get the conversionId
   private void addConversion(HashMap<String, Object> conversion){
           database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                   .add(conversion)
                .addOnSuccessListener(documentReference ->  conversionId = documentReference.getId());
    }

    //update a conversion id bc of a new chat message
    private void updateConversion(String message){
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(conversionId);
        documentReference.update(Constants.KEY_LAST_MESSAGE,message,Constants.KEY_TIMESTAMP, new Date());
    }

    //find out the conversion by calling the checkConversionRemotely function
    private void checkForConversion(){
        if(chatMessages.size()!= 0){
            checkForConversionRemotely(
                    preferenceManager.getString(Constants.KEY_USER_ID),
                    receiverUser.id
            );
            checkForConversionRemotely(
                    receiverUser.id,
                    preferenceManager.getString(Constants.KEY_USER_ID)
            );
        }
    }

    //search for the conversion id from the fire store data base
    private void checkForConversionRemotely(String senderId,String receiverId){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID,senderId)
                .whereEqualTo(Constants.KEY_RECEIVER_ID,receiverId)
                .get()
                .addOnCompleteListener(conversionOnCompleterListener);
    }

    // listener for the conversion id
    private final OnCompleteListener<QuerySnapshot> conversionOnCompleterListener = task->
    {if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size()>0){
        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
        conversionId = documentSnapshot.getId();
    }
    };

    @Override
    protected void onResume() {
        super.onResume();
        listenAvailabilityOfReceiver();
    }
}