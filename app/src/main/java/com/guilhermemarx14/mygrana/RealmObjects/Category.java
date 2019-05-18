package com.guilhermemarx14.mygrana.RealmObjects;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Guilherme Marx on 2019-05-13
 */
public class Category extends RealmObject {
    @PrimaryKey
    String name;
    int type;

    public RealmList<Subcategory> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(RealmList<Subcategory> subcategories) {
        this.subcategories = subcategories;
    }

    RealmList<Subcategory> subcategories;

    public Category() {
    }

    public Category(String name, int type) {
        this.name = name;
        this.type = type;
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
