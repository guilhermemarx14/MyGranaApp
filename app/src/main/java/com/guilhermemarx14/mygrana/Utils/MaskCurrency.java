package com.guilhermemarx14.mygrana.Utils;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.util.Locale;
/**
 * Created by Guilherme Marx on 2019-05-18
 */
public class MaskCurrency implements TextWatcher {
    TextInputEditText field;
    Boolean isUpdating = false;
    NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));

    public MaskCurrency(TextInputEditText field){
        this.field = field;
        field.setText("");
        nf.setMaximumIntegerDigits(6);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(isUpdating){
            isUpdating = false;
            return;
        }
        isUpdating = true;
        String str = s.toString();
        str = str.replace(",", "").replace("R$","").replace(".","");

        try{
            str = nf.format((double) (Float.parseFloat(str)/100));
            field.setText(str);
            field.setSelection(field.getText().length());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void afterTextChanged(Editable e) {    }

}
