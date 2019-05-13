package com.guilhermemarx14.mygrana.RealmObjects;

import io.realm.RealmObject;

/**
 * Created by Guilherme Marx on 2019-05-13
 */
public class Subcategory extends RealmObject {
    String name;
    Category category;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
