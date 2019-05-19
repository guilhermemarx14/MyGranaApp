package com.guilhermemarx14.mygrana.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.guilhermemarx14.mygrana.Adapters.TransactionsAdapter;
import com.guilhermemarx14.mygrana.R;
import com.guilhermemarx14.mygrana.RealmObjects.Transaction;
import com.guilhermemarx14.mygrana.StatementsActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.guilhermemarx14.mygrana.Utils.Constants.isBefore;

/**
 * Created by Guilherme Marx on 2019-05-19
 */
public class SelectDateFilterStatements extends Dialog {
    Activity act;
    Realm realm;
    public SelectDateFilterStatements(@NonNull Activity context) {
        super(context);
        act = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         realm = Realm.getDefaultInstance();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_date_filter_statements);


        final EditText inpDateBegin = findViewById(R.id.inpDateBegin);
        final EditText inpDateEnd = findViewById(R.id.inpDateEnd);
        inpDateBegin.setFocusable(false);
        Calendar c = Calendar.getInstance();
        String mDay;
        String mMonth;
        if(c.get(Calendar.DAY_OF_MONTH)<=9)
            mDay = "0"+c.get(Calendar.DAY_OF_MONTH);
        else mDay = "" + c.get(Calendar.DAY_OF_MONTH);

        int month = c.get(Calendar.MONTH)+1;
        if(month<=9)
            mMonth = "0"+month;
        else mMonth = "" + month;

        inpDateBegin.setText(String.format("%s/%s/%d",mDay,mMonth,c.get(Calendar.YEAR)));
        inpDateEnd.setText(String.format("%s/%s/%d",mDay,mMonth,c.get(Calendar.YEAR)));
        inpDateBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog((Context) act, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String mDay;
                        String mMonth;
                        if(dayOfMonth<=9)
                            mDay = "0"+dayOfMonth;
                        else mDay = "" + dayOfMonth;

                        month = month+1;
                        if(month<=9)
                            mMonth = "0"+month;
                        else mMonth = "" + month;

                        inpDateBegin.setText(String.format("%s/%s/%d",mDay,mMonth,year));
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });

        inpDateEnd.setFocusable(false);

        inpDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog((Context) act, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String mDay;
                        String mMonth;
                        if(dayOfMonth<=9)
                            mDay = "0"+dayOfMonth;
                        else mDay = "" + dayOfMonth;

                        month = month+1;
                        if(month<=9)
                            mMonth = "0"+month;
                        else mMonth = "" + month;

                        inpDateEnd.setText(String.format("%s/%s/%d",mDay,mMonth,year));
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });

        Button confirm = findViewById(R.id.buttonConfirmFilter);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isBefore(inpDateBegin.getText().toString(), inpDateEnd.getText().toString()))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                    builder.setTitle(act.getResources().getString(R.string.error));
                    builder.setMessage(act.getResources().getString(R.string.error_data_filter));
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });
                    builder.create().show();

                    return;
                }

                RealmResults<Transaction> result = realm.where(Transaction.class).findAll();
                ArrayList<Transaction> myList = new ArrayList<>();
                String begin = inpDateBegin.getText().toString();
                begin = begin.split("/")[2] + "-"+begin.split("/")[1] + "-"+begin.split("/")[0];
                String end = inpDateEnd.getText().toString();
                end = end.split("/")[2] + "-"+end.split("/")[1] + "-"+end.split("/")[0];
                for(Transaction t: result){
                    if(t.getDate().compareTo(end)==-1 && t.getDate().compareTo(begin)==1)
                        myList.add(t);
                }
                Collections.sort(myList);
                ((StatementsActivity) act).adapter = new TransactionsAdapter(act,myList);
                ((StatementsActivity) act).rv.setAdapter(((StatementsActivity) act).adapter);
                ((StatementsActivity) act).rv.setLayoutManager(new LinearLayoutManager(act));
                dismiss();
            }
        });
    }
}
