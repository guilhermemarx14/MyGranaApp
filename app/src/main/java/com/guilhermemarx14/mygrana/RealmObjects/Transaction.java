package com.guilhermemarx14.mygrana.RealmObjects;


import android.util.Pair;

import java.io.Serializable;
import java.util.HashMap;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import static com.guilhermemarx14.mygrana.Utils.Constants.getTransactionId;

/**
 * Created by Guilherme Marx on 2019-05-18
 */

public class Transaction extends RealmObject implements Serializable, Comparable<Transaction> {
    @PrimaryKey
    private long id;
    private float value;
    private String categoryName;
    private String subcategory = null;
    private String description;
    private String date;
    private boolean payd;

    public Transaction(Pair<String,Transaction> map) {
        this.id = Integer.parseInt(map.first);
        this.value =  map.second.value;
        this.categoryName =  map.second.categoryName;
        this.subcategory =  map.second.subcategory;
        this.description =  map.second.description;
        this.date =  map.second.date;
        this.payd = map.second.payd;

    }
    public Transaction(long i, float value, String categoryName, String subcategory, String description, String date, boolean payd) {
        if (i == 0)
            this.id = getTransactionId();
        else this.id = i;
        this.value = value;
        this.categoryName = categoryName;
        this.subcategory = subcategory;
        this.description = description;
        this.date = date;
        this.payd = payd;
    }

    public Transaction(long i, float value, String categoryName, String description, String date, boolean payd) {
        if (i == 0)
            this.id = getTransactionId();
        else this.id = i;
        this.value = value;
        this.categoryName = categoryName;
        this.description = description;
        this.date = date;
        this.payd = payd;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public boolean isPayd() {
        return payd;
    }

    public void setPayd(boolean payd) {
        this.payd = payd;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Transaction() {
    }

    public float getValue() {
        return value;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getCategory() {
        return categoryName;
    }

    public void setCategory(String category) {
        this.categoryName = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public int compareTo(Transaction o) {
        return -this.date.compareTo(o.getDate());
    }
}
