package com.example.skillswap.chat;

import static android.content.Intent.getIntent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.skillswap.ChooseSignInUp;
import com.example.skillswap.MainActivity;
import com.example.skillswap.MatchedAdapter;
import com.example.skillswap.MatchesActivity;
import com.example.skillswap.NewthingMain;
import com.example.skillswap.R;
import com.example.skillswap.Thing;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatFragment extends Fragment {

    private FirebaseAuth mAuth;
    private Uri resultUri;

    private DatabaseReference mThingDatabase;
    private DatabaseReference mUserDatabase, mDatabaseChat;
    Uri downladUrl;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;
    private EditText message ;
    private Button send;
    private String CurrentUser, OtherUser, ThingId;
    private String chatId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        //Fragment will receive callback from Fragment manager
        setHasOptionsMenu(true); //called onCreate
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);

        Toolbar courseToolbar = (Toolbar) v.findViewById(R.id.ToolBar4);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(courseToolbar);

        mAuth = FirebaseAuth.getInstance();

        ThingId = getActivity().getIntent().getExtras().getString("ThingID");
        CurrentUser = mAuth.getCurrentUser().getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUser);
        mThingDatabase = FirebaseDatabase.getInstance().getReference().child("Things");
        mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");

        message = (EditText) v.findViewById(R.id.message);
        send = (Button) v.findViewById(R.id.send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(chatId);
            }
        });

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mChatLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new ChatAdapter(getDataSetChat(), getActivity(), mAuth.getCurrentUser().getUid());
        mRecyclerView.setAdapter(mChatAdapter);

        getOwnerAndCreateChatId(ThingId);

        return v;
    }

    private void setupChat(String chatId) {
        mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat").child(chatId);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(chatId);
            }
        });

        // Load existing messages
        loadMessages(chatId);
    }

    private void sendMessage(String chatId) {
        String sendMessageText = message.getText().toString();

        if (!sendMessageText.isEmpty()) {
            DatabaseReference newMessageDb = mDatabaseChat.push();
            Map<String, String> newMessage = new HashMap<>();
            newMessage.put("CreatedByUser", CurrentUser);
            newMessage.put("text", sendMessageText);
            newMessageDb.setValue(newMessage);
            resultChat.add(new Chat(CurrentUser, sendMessageText));
        }
        message.setText(null);
    }

    private void loadMessages(String chatId) {
        mDatabaseChat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                resultChat.clear(); // Clear the list to avoid duplicates

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat message = snapshot.getValue(Chat.class);
                    resultChat.add(message);
                }
                // Update your RecyclerView adapter with the messages
                mChatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }



    private void getOwnerAndCreateChatId(String thingId) {
        DatabaseReference mThingDatabase = FirebaseDatabase.getInstance().getReference().child("Things").child(thingId);
        mThingDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Thing thing = dataSnapshot.getValue(Thing.class);
                if (thing != null) {
                    String otherUser = thing.getOwner();
                    createChatId(CurrentUser, otherUser);
                } else {
                    Log.e("Error", "Thing not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
                Log.e("Error", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void createChatId(String currentUser, String otherUser) {

        if (currentUser.compareTo(otherUser) < 0) {
            chatId = currentUser + "_" + otherUser;
        } else {
            chatId = otherUser + "_" + currentUser;
        }
        // Now you have a unique chatId, you can use it to store and retrieve messages
        setupChat(chatId);
    }


    private ArrayList<Chat> resultChat = new ArrayList<Chat>();
    private List<Chat> getDataSetChat() {
        return resultChat;
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