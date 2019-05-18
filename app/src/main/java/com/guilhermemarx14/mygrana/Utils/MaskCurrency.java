package com.guilhermemarx14.mygrana.Utils;

import android.text.Editable;
import android.text.TextWatcher;

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
        nf.setMaximumIntegerDigits(7);

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(isUpdating){
            isUpdating = false;
            return;
        }
        if(s.toString().length() >= 13) {
            String str = s.toString();
            str = str.substring(0,str.length()-1);
            str = str.replace("$","$ ");
            field.setText(str);
            return;
        }
        isUpdating = true;
        String str = s.toString();
        str = str.replace(",", "").replace("R$","").replace(".","");

        try{
            str = nf.format((double) (Float.parseFloat(str)/100));
            str = str.replace("$","$ ");
            field.setText(str);
            field.setSelection(field.getText().length());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void afterTextChanged(Editable e) {    }

}
