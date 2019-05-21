package com.guilhermemarx14.mygrana;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.guilhermemarx14.mygrana.RealmObjects.Category;
import com.guilhermemarx14.mygrana.RealmObjects.Subcategory;
import com.guilhermemarx14.mygrana.RealmObjects.Transaction;
import com.guilhermemarx14.mygrana.Utils.MaskCurrency;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static com.guilhermemarx14.mygrana.Utils.Constants.getListCategories;
import static com.guilhermemarx14.mygrana.Utils.Constants.getListSubcategories;

public class StatementDetailActivity extends AppCompatActivity {
    Button confirm;
    Category selected;
    Subcategory selected2;
    TextInputEditText value;
    Context context;
    CheckBox payd;
    Transaction chosen;
    Realm realm;
    int position;
    DatabaseReference myRef;
    RealmResults<Transaction> result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statement_detail);


        realm = Realm.getDefaultInstance();
        Bundle extras = getIntent().getExtras();
        position = (int) extras.get("position");
        context = this;
        result = realm.where(Transaction.class).findAll();
        ArrayList<Transaction> mylist = new ArrayList<>();
        mylist.addAll(result);
        chosen = mylist.get(position);

        findViewById(R.id.textSubcategoryName).setVisibility(View.INVISIBLE);
        findViewById(R.id.spinnerSubcategory).setVisibility(View.INVISIBLE);
        final Spinner category = findViewById(R.id.spinnerCategory);
        final ArrayAdapter<String> arrayAdapterCategory = new ArrayAdapter<>(this, R.layout.spinners, getListCategories());
        category.setAdapter(arrayAdapterCategory);
        category.setSelection(getListCategories().indexOf(chosen.getCategoryName()));

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
                    final ArrayAdapter<String> arrayAdapterSubcategory = new ArrayAdapter<>(context, R.layout.spinners, getListSubcategories(categorySelected));
                    subcategory.setAdapter(arrayAdapterSubcategory);
                    if (getListSubcategories(categorySelected) != null)
                        subcategory.setSelection(getListSubcategories(categorySelected).indexOf(chosen.getSubcategory()));
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

        value = findViewById(R.id.inpValue);


        int mvalue =(int) (chosen.getValue()*100);

        value.addTextChangedListener(new MaskCurrency(value));
        if (chosen.getValue() > 0)
            value.setText("" + mvalue);
        else value.setText("" + (-mvalue));

        final EditText inpDate = findViewById(R.id.inpDateEnd);
        inpDate.setFocusable(false);
        String mDay = chosen.getDate().split("-")[2];
        String mMonth = chosen.getDate().split("-")[1];
        String mYear = chosen.getDate().split("-")[0];


        inpDate.setText(String.format("%s/%s/%s", mDay, mMonth, mYear));

        inpDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
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
        ((EditText) findViewById(R.id.inpDescription)).setText(chosen.getDescription());
        payd = findViewById(R.id.cbPayd);
        payd.setChecked(chosen.isPayd());

        confirm = findViewById(R.id.buttonConfirmFilter);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (value.getText().toString().trim().isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(context.getResources().getString(R.string.error));
                    builder.setMessage(context.getResources().getString(R.string.text_no_value));
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
                numero = numero.replaceAll(" ", "");
                int num = Integer.parseInt(numero);

                String desc = ((EditText) findViewById(R.id.inpDescription)).getText().toString();
                float value = (float) num / 100;


                if (!selected.getName().equals("Pensão") && !selected.getName().equals("Salário"))
                    value = -value;
                if (selected2 == null)
                    chosen = new Transaction(chosen.getId(), value, selected.getName(), desc, dateConvert(inpDate.getText().toString()), payd.isChecked());
                else
                    chosen = new Transaction(chosen.getId(), value, selected.getName(), selected2.getSubcategoryName(), desc, dateConvert(inpDate.getText().toString()), payd.isChecked());
                realm.beginTransaction();
                result.get(position).deleteFromRealm();
                realm.commitTransaction();
                realm.beginTransaction();
                realm.copyToRealm(chosen);
                realm.commitTransaction();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                myRef = database.getReference(user.getUid());
                HashMap<String, Object> map = new HashMap<>();
                map.put("" + chosen.getId(), chosen);
                myRef.child("transactions").updateChildren(map);


                Intent it = new Intent(context, StatementsActivity.class);
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(it);
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
