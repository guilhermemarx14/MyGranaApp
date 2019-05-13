package com.guilhermemarx14.mygrana.RealmObjects;

import io.realm.RealmObject;

/**
 * Created by Guilherme Marx on 2019-05-13
 */
public class Category extends RealmObject {
    String name;
    int type;

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
