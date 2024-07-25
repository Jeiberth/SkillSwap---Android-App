package com.example.skillswap;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.yalantis.library.Koloda;
import com.yalantis.library.KolodaListener;

import java.util.ArrayList;
import java.util.List;

public class MatchPage extends Fragment {

    private SwipeAdapter adapter;
    private List<Thing> list;
    private Koloda koloda;

    private List<String> userThings;
    private List<String> userLikes;
    private List<String> userDislikes;
    private FirebaseAuth mAuth;

    private DatabaseReference mThingDatabase;
    private DatabaseReference mUserDatabase;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        mThingDatabase = FirebaseDatabase.getInstance().getReference().child("Things");
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_match_page, container, false);

        Toolbar courseToolbar = v.findViewById(R.id.ToolBar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(courseToolbar);


        koloda = v.findViewById(R.id.koloda);
        list = new ArrayList<>();
        adapter = new SwipeAdapter(getActivity(), list);
        koloda.setAdapter(adapter);

        // Retrieve current user's data
// Retrieve current user's data
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userThings = (List<String>) dataSnapshot.child("Things").getValue();
                if (userThings == null) {
                    userThings = new ArrayList<>();
                }

                userLikes = (List<String>) dataSnapshot.child("Likes").getValue();
                if (userLikes == null) {
                    userLikes = new ArrayList<>();
                }

                userDislikes = (List<String>) dataSnapshot.child("Dislikes").getValue();
                if (userDislikes == null) {
                    userDislikes = new ArrayList<>();
                }

                // Load data from Firebase and filter
                mThingDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            list.clear();
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                String thingId = postSnapshot.getKey();
                                Thing thing = postSnapshot.getValue(Thing.class);
                                if (thing != null && thingId != null && !userThings.contains(thingId) &&
                                        !userLikes.contains(thingId) && !userDislikes.contains(thingId)) {
                                    list.add(thing);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            System.out.print(e.getMessage());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle error
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });

        koloda.setKolodaListener(new KolodaListener() {
            @Override
            public void onClickRight(int i) {
                handleSwipe(i, "Likes");
            }

            @Override
            public void onClickLeft(int i) {
                handleSwipe(i, "Dislikes");
            }

            @Override
            public void onCardSwipedLeft(int position) {
                handleSwipe(position, "Dislikes");
            }

            @Override
            public void onCardSwipedRight(int position) {
                handleSwipe(position, "Likes");
            }

            @Override
            public void onNewTopCard(int position) {
                // Do something when a new card becomes the top card
            }

            @Override
            public void onCardDrag(int position, @NonNull View cardView, float progress) {
                // Do something when a card is being dragged
            }

            @Override
            public void onCardDoubleTap(int position) {
                // Do something on double tap
            }

            @Override
            public void onCardSingleTap(int position) {
                // Do something on single tap
            }

            @Override
            public void onCardLongPress(int position) {
                // Do something on long press
            }

            @Override
            public void onEmptyDeck() {
                // Do something when the deck is empty
            }
        });




        return v;
    }


    private void handleSwipe(int position, String listName) {
        position++;
        if (position >= 0 && position < list.size()) {
            String thingId = list.get(position).getThingId();
            addToUserList(listName, thingId);
        } else {
            Toast.makeText(getActivity(), "Invalid position: " + position, Toast.LENGTH_SHORT).show();
            System.out.print("Invalid position: " + position);
        }
    }

    private void addToUserList(String listName, String thingId) {
        mUserDatabase.child(listName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> list = (List<String>) dataSnapshot.getValue();
                if (list == null) {
                    list = new ArrayList<>();
                }
                if (!list.contains(thingId)) {
                    list.add(thingId);
                    mUserDatabase.child(listName).setValue(list);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
