package com.example.skillswap.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillswap.R;

import org.w3c.dom.Text;

public class ChatViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView message;
    public ChatViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        message = (TextView) itemView.findViewById(R.id.message);
    }

    @Override
    public void onClick(View v) {

    }
}
