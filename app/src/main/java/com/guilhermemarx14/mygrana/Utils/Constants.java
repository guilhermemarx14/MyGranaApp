package com.guilhermemarx14.mygrana.Utils;

import com.guilhermemarx14.mygrana.RealmObjects.Category;
import com.guilhermemarx14.mygrana.RealmObjects.Subcategory;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Guilherme Marx on 2019-05-13
 */
public class Constants {
    public static final int GASTO = 0;
    public static final int RENDA = 1;
    public static ArrayList<String> getListCategories(){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Category> results = realm.where(Category.class).findAll();
        ArrayList<String> aux = new ArrayList<>();
        for(Category a: results)
            aux.add(a.getName());
        return aux;
    }

    public static ArrayList<String> getListSubcategories(String category){
        Realm realm = Realm.getDefaultInstance();

        RealmList<Subcategory> list = realm.where(Category.class).equalTo("name",category).findFirst().getSubcategories();
        ArrayList<String> aux = new ArrayList<>();
        for(Subcategory a: list)
            aux.add(a.getSubcategoryName());
        return aux;
    }

    public static String convertDateForExibition(String date){
        String mDate = "";
        mDate = date.split("-")[2];
        mDate+= " de ";
        int month = Integer.parseInt(date.split("-")[1]);
        switch(month){

            case 1:mDate+="Janeiro";break;
            case 2:mDate+="Fevereiro";break;
            case 3:mDate+="Março";break;
            case 4:mDate+="Abril";break;
            case 5:mDate+="Maio";break;
            case 6:mDate+="Junho";break;
            case 7:mDate+="Julho";break;
            case 8:mDate+="Agosto";break;
            case 9:mDate+="Setembro";break;
            case 10:mDate+="Outubro";break;
            case 11:mDate+="Novembro";break;
            case 12:mDate+="Dezembro";break;
            default: break;
        }
        mDate+= " de ";
        mDate+= date.split("-")[0];

        return mDate;
    }

    public static boolean isBefore(String date1, String date2){
        int day1,day2,month1,month2,year1,year2;
        year1 = Integer.parseInt(date1.split("/")[2]);
        year2 = Integer.parseInt(date2.split("/")[2]);
        if(year2>year1)
            return true;
        month1 = Integer.parseInt(date1.split("/")[1]);
        month2 = Integer.parseInt(date2.split("/")[1]);
        if(month2>month1 && year2==year1)
            return true;
        day1 = Integer.parseInt(date1.split("/")[0]);
        day2 = Integer.parseInt(date2.split("/")[0]);
        if(day2>day1 && month1==month2 && year1==year2)
            return true;

        return false;
    }
}
