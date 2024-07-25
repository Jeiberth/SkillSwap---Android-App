package com.example.skillswap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MatchedAdapter extends RecyclerView.Adapter<MatchesViewHolders> {

    private List<Thing> matchesList;
    private Context context;

    public MatchedAdapter(List<Thing> matchesList, Context context){
        this.matchesList = matchesList;
        this.context = context;
    }

    @NonNull
    @Override
    public MatchesViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_matched, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        MatchesViewHolders rcv = new MatchesViewHolders((layoutView));
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull MatchesViewHolders holder, int position) {
        holder.ThingMatchedId.setText(matchesList.get(position).getNameThing());
        holder.ThingOwner.setText(matchesList.get(position).getDescriptionThing());
        Glide.with(context).load(matchesList.get(position).getImageThing()).into(holder.MatchImage);
        holder.ThingId = matchesList.get(position).getThingId();
    }

    @Override
    public int getItemCount() {
        return matchesList.size(); // Return the size of the matchesList
    }
}

