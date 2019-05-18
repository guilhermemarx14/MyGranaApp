package com.guilhermemarx14.mygrana;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import static com.guilhermemarx14.mygrana.Utils.Constants.getListCategories;

public class AddTransaction extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUpFloatingActionButton();

        Spinner spinnerCategory = findViewById(R.id.spinnerCategory);
        ArrayAdapter<String> arrayAdapterCategory = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,getListCategories());
        spinnerCategory.setAdapter(arrayAdapterCategory);
    }

    private void setUpFloatingActionButton() {
        FloatingActionButton fab = findViewById(R.id.fabAddTransaction);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
