package com.guilhermemarx14.mygrana.RealmObjects;

import io.realm.RealmObject;

/**
 * Created by Guilherme Marx on 2019-05-18
 */
public class Transaction extends RealmObject {
    private float value;
    private Category category;
    private Subcategory subcategory;
    private String description;
    private String date;

    public Transaction(float value, Category category, String description, String date) {
        this.value = value;
        this.category = category;
        this.description = description;
        this.date = date;
    }

    public Transaction(float value, Category category, Subcategory subcategory, String description, String date) {
        this.value = value;
        this.category = category;
        this.subcategory = subcategory;
        this.description = description;
        this.date = date;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Subcategory getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(Subcategory subcategory) {
        this.subcategory = subcategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
