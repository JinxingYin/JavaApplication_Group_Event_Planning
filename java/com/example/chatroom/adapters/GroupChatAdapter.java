package com.example.chatroom.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatroom.databinding.ItemContainerReceivedGroupMessageBinding;
import com.example.chatroom.databinding.ItemContainerReceivedMessageBinding;
import com.example.chatroom.databinding.ItemContainerSentMessageBinding;
import com.example.chatroom.models.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class GroupChatAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final List<ChatMessage> chatMessages;
    private final String senderId;
    private final List<String> receiverNames;
    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVE =2;


    public GroupChatAdapter(List<ChatMessage> chatMessages, List<String> receiverNames,  String senderId) {
        this.chatMessages = chatMessages;
        this.senderId = senderId;
        this.receiverNames = receiverNames;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_SENT){
            return new GroupChatAdapter.SentMessageViewHolder(
                    ItemContainerSentMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),parent,false
                    )
            );
        }else{
            return new GroupChatAdapter.ReceivedMessageViewHolder(
                    ItemContainerReceivedGroupMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,false
                    )
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == VIEW_TYPE_SENT){
            ((GroupChatAdapter.SentMessageViewHolder)holder).setData(chatMessages.get(position));
        }else {
            //for(int i=0; i<receiverNames.size(); i++) {
                ((GroupChatAdapter.ReceivedMessageViewHolder) holder).setData((chatMessages).get(position), chatMessages.get(position).senderName/*receiverNames.get(i)*/);
           // }
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position){
        if(chatMessages.get(position).senderId.equals(senderId)){
            return VIEW_TYPE_SENT;
        }
        else
            return VIEW_TYPE_RECEIVE;
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder{
        private final ItemContainerSentMessageBinding binding;
        SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding){
            super(itemContainerSentMessageBinding.getRoot());
            binding= itemContainerSentMessageBinding;
        }
        void setData(ChatMessage chatMessage){
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
        }
    }
    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder{
        private final ItemContainerReceivedGroupMessageBinding binding;
        ReceivedMessageViewHolder(ItemContainerReceivedGroupMessageBinding itemContainerReceivedGroupMessageBinding){
            super(itemContainerReceivedGroupMessageBinding.getRoot());
            binding = itemContainerReceivedGroupMessageBinding;
        }

        void setData(ChatMessage chatMessage, String receiverName){
            binding.textMessage.setText(chatMessage.message);
            binding.textName.setText(receiverName);
            binding.textDateTime.setText(chatMessage.dateTime);
        }
    }
}
