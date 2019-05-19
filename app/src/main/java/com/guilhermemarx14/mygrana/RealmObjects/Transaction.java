package com.guilhermemarx14.mygrana.RealmObjects;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by Guilherme Marx on 2019-05-18
 */
public class Transaction extends RealmObject implements Serializable {
    private float value;
    private String categoryName;
    private String subcategory;
    private String description;
    private String date;
    private boolean payd;

    public Transaction(float value, String categoryName, String subcategory, String description, String date, boolean payd) {
        this.value = value;
        this.categoryName = categoryName;
        this.subcategory = subcategory;
        this.description = description;
        this.date = date;
        this.payd = payd;
    }

    public Transaction(float value, String categoryName, String description, String date, boolean payd) {
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
}
