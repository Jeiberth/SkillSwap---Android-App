package com.example.skillswap.chat;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillswap.R;
import com.example.skillswap.Thing;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolders> {

    private List<Chat> chatList;
    private String CurrentUser = "";
    private Context context;

    public ChatAdapter(List<Chat> chatList, Context context, String CurrentUser){
        this.chatList = chatList;
        this.context = context;
        this.CurrentUser = CurrentUser;
    }

    @NonNull
    @Override
    public ChatViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ChatViewHolders rcv = new ChatViewHolders((layoutView));
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolders holder, int position) {
        Chat chat = chatList.get(position);
        holder.message.setText(chat.getText());

        if (chat.getCreatedByUser() != null && chat.getCreatedByUser().equals(CurrentUser)) {
            holder.message.setGravity(Gravity.END);
            holder.message.setTextColor(Color.parseColor("#404040"));
            holder.message.setBackgroundColor(Color.parseColor("#e0d5ed"));
        } else {
            holder.message.setGravity(Gravity.START);
            holder.message.setTextColor(Color.parseColor("#404040"));
            holder.message.setBackgroundColor(Color.parseColor("#edd5dc"));
        }
    }


    @Override
    public int getItemCount() {
        return chatList.size(); // Return the size of the matchesList
    }
}

