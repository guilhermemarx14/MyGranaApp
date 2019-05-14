package com.guilhermemarx14.mygrana.Utils;

import com.guilhermemarx14.mygrana.RealmObjects.Category;

import java.util.ArrayList;

import io.realm.Realm;
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
}
