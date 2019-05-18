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
import com.guilhermemarx14.mygrana.RealmObjects.Transaction;
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
    Category selected;
    Subcategory selected2;
    public AddTransactionDialog(@NonNull Context context) {
        super(context);
        act =(Activity) context;
        selected=null;
        selected2=null;
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
                selected = realm.where(Category.class).equalTo("name",categorySelected).findFirst();
                RealmList<Subcategory> list= realm.where(Category.class).equalTo("name",categorySelected).findFirst().getSubcategories();

                if (list.size()>0)
                {
                    findViewById(R.id.textSubcategoryName).setVisibility(View.VISIBLE);
                    findViewById(R.id.spinnerSubcategory).setVisibility(View.VISIBLE);
                    final Spinner subcategory = findViewById(R.id.spinnerSubcategory);
                    final ArrayAdapter<String> arrayAdapterSubcategory = new ArrayAdapter<>(act,R.layout.spinners,getListSubcategories(categorySelected));
                    subcategory.setAdapter(arrayAdapterSubcategory);

                    subcategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String subcategorySelected = arrayAdapterSubcategory.getItem(position);
                            selected2 = realm.where(Subcategory.class).equalTo("subcategoryName",subcategorySelected).findFirst();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                }else{
                    findViewById(R.id.textSubcategoryName).setVisibility(View.INVISIBLE);
                    findViewById(R.id.spinnerSubcategory).setVisibility(View.INVISIBLE);
                    selected2 = null;

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
                int num = Integer.parseInt(et.getText().toString().replace(".","").replace(",","").
                        replace(" ","").replace("R$",""));
                String desc = ((EditText) findViewById(R.id.inpDescription)).getText().toString();
                float value = (float) num/100;
                Transaction t;
                if(selected2 == null)
                    t = new Transaction(value,selected,desc);
                else t = new Transaction(value,selected,selected2,desc);
                realm.beginTransaction();
                    realm.copyToRealm(t);
                realm.commitTransaction();
                dismiss();
            }
        });
    }
}
