package com.guilhermemarx14.mygrana.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.guilhermemarx14.mygrana.MenuActivity;
import com.guilhermemarx14.mygrana.R;
import com.guilhermemarx14.mygrana.RealmObjects.Category;
import com.guilhermemarx14.mygrana.RealmObjects.Subcategory;
import com.guilhermemarx14.mygrana.RealmObjects.Transaction;
import com.guilhermemarx14.mygrana.Utils.MaskCurrency;

import java.util.Calendar;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmList;

import static com.guilhermemarx14.mygrana.Utils.Constants.getListCategories;
import static com.guilhermemarx14.mygrana.Utils.Constants.getListSubcategories;

/**
 * Created by Guilherme Marx on 2019-05-14
 */
public class AddTransactionDialog extends Dialog {


    Activity act;
    Button confirm;
    Category selected;
    Subcategory selected2;
    TextInputEditText value;

    public AddTransactionDialog(@NonNull Context context) {
        super(context);
        act = (Activity) context;
        selected = null;
        selected2 = null;
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
        final ArrayAdapter<String> arrayAdapterCategory = new ArrayAdapter<>(act, R.layout.spinners, getListCategories());
        category.setAdapter(arrayAdapterCategory);

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {//seta os spinners
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String categorySelected = arrayAdapterCategory.getItem(i);
                selected = realm.where(Category.class).equalTo("name", categorySelected).findFirst();
                RealmList<Subcategory> list = realm.where(Category.class).equalTo("name", categorySelected).findFirst().getSubcategories();

                if (list.size() > 0) {
                    findViewById(R.id.textSubcategoryName).setVisibility(View.VISIBLE);
                    findViewById(R.id.spinnerSubcategory).setVisibility(View.VISIBLE);
                    final Spinner subcategory = findViewById(R.id.spinnerSubcategory);
                    final ArrayAdapter<String> arrayAdapterSubcategory = new ArrayAdapter<>(act, R.layout.spinners, getListSubcategories(categorySelected));
                    subcategory.setAdapter(arrayAdapterSubcategory);

                    subcategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String subcategorySelected = arrayAdapterSubcategory.getItem(position);
                            selected2 = realm.where(Subcategory.class).equalTo("subcategoryName", subcategorySelected).findFirst();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                } else {
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
        value = findViewById(R.id.inpValue);

        value.addTextChangedListener(new MaskCurrency(value));


        final EditText inpDate = findViewById(R.id.inpDateEnd);
        inpDate.setFocusable(false);
        Calendar c = Calendar.getInstance();
        String mDay;
        String mMonth;
        if (c.get(Calendar.DAY_OF_MONTH) <= 9)
            mDay = "0" + c.get(Calendar.DAY_OF_MONTH);
        else mDay = "" + c.get(Calendar.DAY_OF_MONTH);

        int month = c.get(Calendar.MONTH) + 1;
        if (month <= 9)
            mMonth = "0" + month;
        else mMonth = "" + month;

        inpDate.setText(String.format("%s/%s/%d", mDay, mMonth, c.get(Calendar.YEAR)));
        inpDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog((Context) act, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String mDay;
                        String mMonth;
                        if (dayOfMonth <= 9)
                            mDay = "0" + dayOfMonth;
                        else mDay = "" + dayOfMonth;

                        month = month + 1;
                        if (month <= 9)
                            mMonth = "0" + month;
                        else mMonth = "" + month;

                        inpDate.setText(String.format("%s/%s/%d", mDay, mMonth, year));
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });


//
        confirm = findViewById(R.id.buttonDelete);
//
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (value.getText().toString().trim().isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                    builder.setTitle(act.getResources().getString(R.string.error));
                    builder.setMessage(act.getResources().getString(R.string.text_no_value));
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });
                    builder.create().show();

                    return;
                }
                EditText et = findViewById(R.id.inpValue);
                String numero = et.getText().toString().substring(2);
                numero = numero.replaceAll("[.]", "");
                numero = numero.replaceAll(",", "");
                numero = numero.replaceAll("\\s", "");
                int num = Integer.parseInt(numero);
                String desc = ((EditText) findViewById(R.id.inpDescription)).getText().toString();
                float value = (float) num / 100;
                Transaction t;
                CheckBox payd = findViewById(R.id.cbPayd);
                if (!selected.getName().equals("Pensão") && !selected.getName().equals("Salário"))
                    value = -value;
                if (selected2 == null)
                    t = new Transaction(0, value, selected.getName(), desc, dateConvert(inpDate.getText().toString()), payd.isChecked());
                else
                    t = new Transaction(0, value, selected.getName(), selected2.getSubcategoryName(), desc, dateConvert(inpDate.getText().toString()), payd.isChecked());
                realm.beginTransaction();
                realm.copyToRealm(t);
                realm.commitTransaction();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(user.getUid());
                HashMap<String, Object> map = new HashMap<>();
                map.put("" + t.getId(), t);
                myRef.child("transactions").updateChildren(map);

                Intent it = new Intent(act, MenuActivity.class);
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                act.startActivity(it);
                dismiss();
            }
        });
    }

    public String dateConvert(String date) {
        String day = date.split("/")[0];
        String month = date.split("/")[1];
        String year = date.split("/")[2];

        return year + "-" + month + "-" + day;
    }

}
