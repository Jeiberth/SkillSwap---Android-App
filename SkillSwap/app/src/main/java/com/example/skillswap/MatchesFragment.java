package com.example.skillswap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MatchesFragment extends Fragment {


    private FirebaseAuth mAuth;
    private Uri resultUri;
    private String userId;

    private DatabaseReference mThingDatabase;
    private DatabaseReference mUserDatabase;
    Uri downladUrl;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mMatchesAdapter;
    private RecyclerView.LayoutManager mMatchesLayoutManager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        //Fragment will receive callback from Fragment manager
        setHasOptionsMenu(true); //called onCreate
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_matches, container, false);

        Toolbar courseToolbar = (Toolbar) v.findViewById(R.id.ToolBar3);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(courseToolbar);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        mThingDatabase = FirebaseDatabase.getInstance().getReference().child("Things");

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mMatchesLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mMatchesLayoutManager);
        mMatchesAdapter = new MatchedAdapter(getDataSetMatches(), getActivity());
        mRecyclerView.setAdapter(mMatchesAdapter);

        mUserDatabase.child("Likes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> userLikes = (List<String>) dataSnapshot.getValue();
                if (userLikes == null) {
                    userLikes = new ArrayList<>();
                }
                findMatchingThings(userLikes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });


        mMatchesAdapter.notifyDataSetChanged(); // Notify adapter of data changes

        return v;
    }

    private ArrayList<Thing> resultMatches = new ArrayList<Thing>();
    private List<Thing> getDataSetMatches() {
        return resultMatches;
    }

    private void findMatchingThings(List<String> userLikes) {
        for (String thingId : userLikes) {
            mThingDatabase.child(thingId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Thing likedThing = dataSnapshot.getValue(Thing.class);
                    if (likedThing != null) {
                        String ownerId = likedThing.getOwner();
                        checkOwnerLikes(ownerId, likedThing);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle error
                }
            });
        }
    }

    private void checkOwnerLikes(String ownerId, Thing likedThing) {
        DatabaseReference ownerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(ownerId);
        ownerDatabase.child("Likes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> ownerLikes = (List<String>) dataSnapshot.getValue();
                if (ownerLikes == null) {
                    ownerLikes = new ArrayList<>();
                }
                for (String ownerLikedThingId : ownerLikes) {
                    mThingDatabase.child(ownerLikedThingId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Thing ownerLikedThing = dataSnapshot.getValue(Thing.class);
                            if (ownerLikedThing != null && ownerLikedThing.getOwner().equals(userId)) {
                                resultMatches.add(likedThing);
                                mMatchesAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle error
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }



    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        //Inflate course_menu
        inflater.inflate(R.menu.menu_acv, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Intent intent;

        if (item.getItemId() == R.id.LogOut) {
            try {
                mAuth.signOut();
                intent = new Intent(getActivity(), ChooseSignInUp.class);
                startActivity(intent);
                Toast.makeText(getActivity(), "Sign Out", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        if (item.getItemId() == R.id.AddNewThing) {
            try {
                intent = new Intent(getActivity(), NewthingMain.class);
                startActivity(intent);
                Toast.makeText(getActivity(), "New Thing Page", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        if (item.getItemId() == R.id.MatchPage) {
            try {
                intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                Toast.makeText(getActivity(), "Match Page", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        if (item.getItemId() == R.id.MyMatchs) {
            try {
                intent = new Intent(getActivity(), MatchesActivity.class);
                startActivity(intent);
                Toast.makeText(getActivity(), "Matches Page", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }



}