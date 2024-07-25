package com.example.skillswap;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skillswap.chat.ChatActivity;

public class MatchesViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView ThingMatchedId, ThingOwner;
    public ImageView MatchImage;
    public String ThingId;
    public MatchesViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        ThingMatchedId = (TextView) itemView.findViewById(R.id.ThingMatchedId);
        ThingOwner = (TextView) itemView.findViewById(R.id.ThingOwner);
        MatchImage = (ImageView) itemView.findViewById(R.id.MatchImage);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), ChatActivity.class);
        Bundle d = new Bundle();
        d.putString("ThingID", ThingId);
        intent.putExtras(d);
        v.getContext().startActivity(intent);
    }
}
