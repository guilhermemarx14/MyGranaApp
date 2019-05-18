package com.guilhermemarx14.mygrana.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;
import com.guilhermemarx14.mygrana.R;
import com.guilhermemarx14.mygrana.RealmObjects.Category;
import com.guilhermemarx14.mygrana.RealmObjects.Subcategory;
import com.guilhermemarx14.mygrana.Utils.MaskCurrency;

import io.realm.Realm;
import io.realm.RealmList;

import static com.guilhermemarx14.mygrana.Utils.Constants.getListCategories;
import static com.guilhermemarx14.mygrana.Utils.Constants.getListSubcategories;

/**
 * Created by Guilherme Marx on 2019-05-14
 */
public class AddTransactionDialog extends Dialog{


    Activity act;
    Button confirm;

    public AddTransactionDialog(@NonNull Context context) {
        super(context);
        act =(Activity) context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Realm realm = Realm.getDefaultInstance();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_new_transaction);

        findViewById(R.id.textSubcategoryName).setVisibility(View.INVISIBLE);
        findViewById(R.id.spinnerSubcategory).setVisibility(View.INVISIBLE);
        final Spinner category = findViewById(R.id.spinnerCategory);
        final ArrayAdapter<String> arrayAdapterCategory = new ArrayAdapter<>(act,R.layout.spinners,getListCategories());
        category.setAdapter(arrayAdapterCategory);

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {//seta os spinners
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String categorySelected = arrayAdapterCategory.getItem(i);
                RealmList<Subcategory> list= realm.where(Category.class).equalTo("name",categorySelected).findFirst().getSubcategories();

                if (list.size()>0)
                {
                    findViewById(R.id.textSubcategoryName).setVisibility(View.VISIBLE);
                    findViewById(R.id.spinnerSubcategory).setVisibility(View.VISIBLE);
                    final Spinner subcategory = findViewById(R.id.spinnerSubcategory);
                    final ArrayAdapter<String> arrayAdapterSubcategory = new ArrayAdapter<>(act,R.layout.spinners,getListSubcategories(categorySelected));
                    subcategory.setAdapter(arrayAdapterSubcategory);

                }else{
                    findViewById(R.id.textSubcategoryName).setVisibility(View.INVISIBLE);
                    findViewById(R.id.spinnerSubcategory).setVisibility(View.INVISIBLE);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //setar a mascara de dinheiro
        TextInputEditText value = findViewById(R.id.inpValue);

        value.addTextChangedListener(new MaskCurrency(value));



//
        confirm = findViewById(R.id.buttonAddTransaction);
//
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                EditText et = findViewById(R.id.inpValue);
                Toast.makeText(act,"" + ((EditText)findViewById(R.id.inpValue)).getText().toString(), Toast.LENGTH_LONG).show();
//                realm.beginTransaction();
//                Category category = realm.where(Category.class).equalTo("name",(String) categoryAdd.getSelectedItem()).findFirst();
//                realm.copyToRealm(new Subcategory(et.getText().toString(),category));
//                category.getSubcategories().add(new Subcategory(et.getText().toString(),category));
//                realm.insertOrUpdate(category);
//                realm.commitTransaction();
//                dismiss();
            }
        });
    }
}
