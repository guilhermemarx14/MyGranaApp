package com.guilhermemarx14.mygrana;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.guilhermemarx14.mygrana.RealmObjects.University;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmResults;


public class FirstUserActivity extends AppCompatActivity {
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_user);
        Realm realm = Realm.getDefaultInstance();
        context = this;
        final Spinner spinner = findViewById(R.id.spinnerUniversity);
        RealmResults<University> result = realm.where(University.class).findAll();
        ArrayList<String> universities = new ArrayList<>();
        for (University u : result)
            universities.add(u.getName());

        Collections.sort(universities);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinners, universities);
        spinner.setAdapter(adapter);

        Button confirm = findViewById(R.id.buttonConfirmUniversity);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(user.getUid());
                HashMap<String, Object> map = new HashMap<>();
                map.put("0", spinner.getSelectedItem());
                myRef.child("university").updateChildren(map);

                Intent it = new Intent(context, MenuActivity.class);
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(it);
            }
        });

    }

    @Override
    public void onBackPressed() {

    }
}
