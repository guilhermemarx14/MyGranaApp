package com.guilhermemarx14.mygrana.RealmObjects;

import android.util.Pair;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


/**
 * Created by Guilherme Marx on 2019-05-20
 */
public class University extends RealmObject {
    @PrimaryKey
    long id;
    String name;

    public University(Pair<String,String> map) {
        this.id = Integer.parseInt(map.first);
        this.name = map.second;
    }

    public University() {


    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
