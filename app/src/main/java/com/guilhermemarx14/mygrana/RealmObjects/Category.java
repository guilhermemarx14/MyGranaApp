package com.guilhermemarx14.mygrana.RealmObjects;

import java.io.Serializable;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Guilherme Marx on 2019-05-13
 */
public class Category extends RealmObject implements Serializable {
    @PrimaryKey
    String name;
    int type;
    RealmList<Subcategory> subcategories;


    public Category() {
        subcategories = new RealmList<>();
    }

    public Category(String name, int type) {
        this.name = name;
        this.type = type;
        subcategories = new RealmList<>();
    }

    public RealmList<Subcategory> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(RealmList<Subcategory> subcategories) {
        this.subcategories = subcategories;
    }

    public int getNumberOfCategories(Realm realm){
        return realm.where(Category.class).findAll().size();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
