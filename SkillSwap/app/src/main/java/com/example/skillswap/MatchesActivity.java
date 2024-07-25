package com.example.skillswap;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MatchesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_matches);

        //Use FragmentManager to add CourseFragment to fragment_container of MainActivity
        FragmentManager fn = getSupportFragmentManager();
        Fragment fragment = fn.findFragmentById(R.id.framgment_container);

        if(fragment == null)
        {
            //Always important to commit
            fragment = new MatchesFragment();
            fn.beginTransaction().add(R.id.framgment_container, fragment).commit();

            //use .replace instead of .add to add new fragment
        }


    }
}