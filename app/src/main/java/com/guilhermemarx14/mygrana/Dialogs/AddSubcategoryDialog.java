package com.guilhermemarx14.mygrana.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.guilhermemarx14.mygrana.MenuActivity;
import com.guilhermemarx14.mygrana.R;
import com.guilhermemarx14.mygrana.RealmObjects.Category;
import com.guilhermemarx14.mygrana.RealmObjects.Subcategory;

import java.util.HashMap;

import io.realm.Realm;

import static com.guilhermemarx14.mygrana.Utils.Constants.getListCategories;

/**
 * Created by Guilherme Marx on 2019-05-14
 */
public class AddSubcategoryDialog extends Dialog{


    Activity act;
    Button confirm;

    public AddSubcategoryDialog(@NonNull Activity context) {
        super(context);
        act = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Realm realm = Realm.getDefaultInstance();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_new_subcategory);
        final Spinner categoryAdd = findViewById(R.id.spinnerCategoryAdd);
        final ArrayAdapter<String> arrayAdapterCategory = new ArrayAdapter<>(act,android.R.layout.simple_list_item_1,getListCategories());
        categoryAdd.setAdapter(arrayAdapterCategory);

        confirm = findViewById(R.id.buttonAddSubcategory);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = findViewById(R.id.inpSubcategoryName);
                realm.beginTransaction();
                Category category = realm.where(Category.class).equalTo("name",(String) categoryAdd.getSelectedItem()).findFirst();
                realm.copyToRealm(new Subcategory(et.getText().toString(),category.getName()));
                category.getSubcategories().add(new Subcategory(et.getText().toString(),category.getName()));
                realm.insertOrUpdate(category);
                realm.commitTransaction();

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(user.getUid());
                myRef.child("subcategories").push().setValue(new Subcategory(et.getText().toString(),category.getName()));

                Intent it = new Intent(act, MenuActivity.class);
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                act.startActivity(it);
                dismiss();
            }
        });
    }
}
