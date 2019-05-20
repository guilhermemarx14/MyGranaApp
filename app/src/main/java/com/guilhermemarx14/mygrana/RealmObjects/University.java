package com.guilhermemarx14.mygrana.RealmObjects;

import io.realm.RealmObject;

/**
 * Created by Guilherme Marx on 2019-05-20
 */
public class University extends RealmObject {
    String name;

    public University(String name) {
        this.name = name;
    }

    public University() {


    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
