package com.guilhermemarx14.mygrana.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.guilhermemarx14.mygrana.R;

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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_new_subcategory);

        Spinner categoryAdd = findViewById(R.id.spinnerCategoryAdd);
        ArrayAdapter<String> arrayAdapterCategory = new ArrayAdapter<>(act,android.R.layout.simple_list_item_1,getListCategories());
        categoryAdd.setAdapter(arrayAdapterCategory);
        confirm = findViewById(R.id.buttonAddSubcategory);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = findViewById(R.id.inpSubcategoryName);
                Toast.makeText(act, et.getText().toString(),  Toast.LENGTH_LONG).show();
                dismiss();
            }
        });
    }
}
