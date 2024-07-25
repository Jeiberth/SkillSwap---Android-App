package com.example.skillswap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

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
import com.google.firebase.auth.AuthResult;
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
import java.util.Map;
import java.util.Random;

public class NewThingFragment extends Fragment {

    private EditText NameThing;
    private EditText DescriptionThing;
    private ImageView ImageThing;
    private Button AddNewThingButton;
    private FirebaseAuth mAuth;
    private String userId;
    private String ThingId;
    private Uri resultUri;
    private int NumThingsPosted = 0;
    private ArrayList<String> T = new ArrayList<>();
    private DatabaseReference mThingDatabase;
    private DatabaseReference mUserDatabase;
    Uri downladUrl;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        //Fragment will receive callback from Fragment manager
        setHasOptionsMenu(true); //called onCreate
    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedIntanceState) {

        View v = inflater.inflate(R.layout.fragment_new_thing, container, false);

        Toolbar courseToolbar = (Toolbar) v.findViewById(R.id.ToolBar2);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(courseToolbar);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        userId = mAuth.getCurrentUser().getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);



        NameThing = (EditText) v.findViewById(R.id.NameThing);
        DescriptionThing = (EditText) v.findViewById(R.id.DescriptionThing);
        ImageThing = (ImageView) v.findViewById(R.id.ImageThing);
        AddNewThingButton = (Button) v.findViewById(R.id.AddNewThingButton);

        ImageThing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });



        AddNewThingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(resultUri == null){
                    Toast.makeText(getActivity(), "Add an image", Toast.LENGTH_SHORT).show();
                    return;
                }

                ThingId = getThingNewID(userId);
                DatabaseReference mThingDatabase = FirebaseDatabase.getInstance().getReference().child("Things").child(ThingId);
                mThingDatabase.child("ThingId").setValue(ThingId);
                mThingDatabase.child("NameThing").setValue( NameThing.getText().toString());
                mThingDatabase.child("DescriptionThing").setValue(DescriptionThing.getText().toString());
                mThingDatabase.child("Owner").setValue(userId);
                mThingDatabase.child("Available").setValue(true);

                if(resultUri != null){
                    try{
                    StorageReference filepath = FirebaseStorage.getInstance().getReference().child("ImageThing").child(ThingId);
                    Bitmap bitmap = null;

                    try{
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplication().getContentResolver(), resultUri);
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                    byte[] data= baos.toByteArray();
                    UploadTask uploadTask = filepath.putBytes(data);
                    uploadTask.addOnFailureListener(e -> {
                        activity.finish();});
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            Uri downloadUrl = task.getResult();
                                            mThingDatabase.child("ImageThing").setValue(downloadUrl.toString());
                                        } else {
                                            // Manejar el error aquí
                                            Log.e("MiEtiqueta", "Error al obtener la URL de descarga", task.getException());
                                        }
                                    }
                                });
                            }
                        });
                    }
                    catch (Exception e){
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
                else{
                    mThingDatabase.child("ImageThing").setValue("default");
                }


                try {
                    Map userInfo = new HashMap<>();
                    T.add(ThingId);
                    userInfo.put("Things", T);
                    mUserDatabase.updateChildren(userInfo);
                    Toast.makeText(getActivity(), "Thing Added", Toast.LENGTH_SHORT).show();
                }
                catch(Exception e){
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                NameThing.setText("Name");
                DescriptionThing.setText("Description");
                ImageThing.setImageResource(R.drawable.addimage);

            };
        });


        return v;

    }

    private String getThingNewID(String userId){
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if(map.get("Things")!=null){
                        Log.d("MiEtiqueta", "T exits");
                        T = (ArrayList<String>) map.get("Things");
                        NumThingsPosted = T.size();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("MiEtiqueta", "Problem with T");
            }

        });

        Random rand = new Random();
        String randomChars = "";
        for (int i = 0; i < 3; i++) { // Generar 3 caracteres aleatorios
            char c = (char) (rand.nextInt(26) + 'a'); // Esto generará una letra aleatoria entre 'a' y 'z'
            randomChars += c;
        }

        return userId + (NumThingsPosted)+ randomChars;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == AppCompatActivity.RESULT_OK){

            final Uri imageUri = data.getData();
            resultUri = imageUri;
            ImageThing.setImageURI(resultUri);

        }
    }
}