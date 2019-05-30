package com.guilhermemarx14.mygrana;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

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
    ArrayList<String> universities;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_user);
        TextView tv = findViewById(R.id.tvUniversityNotInList);
        tv.setText(HtmlCompat.fromHtml(getString(R.string.university_not_in_list1) + " <b>" + getString(R.string.university_not_in_list2) + "</b> "
                + getString(R.string.university_not_in_list3),HtmlCompat.FROM_HTML_MODE_LEGACY));

        Realm realm = Realm.getDefaultInstance();
        context = this;
        disableButton();
        final Spinner spinner = findViewById(R.id.spinnerUniversity);
        spinner.setBackground(ContextCompat.getDrawable(this, R.drawable.button_grey_shape));
       final  RealmResults<University> result = realm.where(University.class).findAll();
        universities = new ArrayList<>();
        for (University u : result)
            universities.add(u.getName());
        universities.add(0,"");
        Collections.sort(universities);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinners, universities);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0)
                    enableButton();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Button confirm = findViewById(R.id.buttonConfirmUniversity);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinner.getSelectedItem().equals(""))
                    return;
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(user.getUid());
                HashMap<String, Object> map = new HashMap<>();
                for (University u : result)
                    if(u.getName().equals(spinner.getSelectedItem())) {
                        map.put("" + u.getId(), spinner.getSelectedItem());
                        break;
                    }
                myRef.child("university").updateChildren(map);

                Intent it = new Intent(context, MenuActivity.class);
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(it);
            }
        });

    }

    private void disableButton() {
        Button confirm = findViewById(R.id.buttonConfirmUniversity);
        confirm.setEnabled(false);
        confirm.setBackground(ContextCompat.getDrawable(this,R.drawable.button_grey_shape));
    }

    private void enableButton(){
        Button confirm = findViewById(R.id.buttonConfirmUniversity);
        confirm.setEnabled(true);
        confirm.setBackground(ContextCompat.getDrawable(this,R.drawable.button_orange_shape));
    }
    @Override
    public void onBackPressed() {

    }
}
