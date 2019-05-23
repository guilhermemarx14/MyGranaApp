package com.guilhermemarx14.mygrana.RealmObjects;

import java.util.HashMap;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import static com.guilhermemarx14.mygrana.Utils.Constants.getUniversityId;

/**
 * Created by Guilherme Marx on 2019-05-20
 */
public class University extends RealmObject {
    @PrimaryKey
    long id;
    String name;
    public University(String name) {
        this.id = getUniversityId();
        this.name = name;
    }
    public University(long id, String name) {
        this.id = id;
        this.name = name;
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
