package com.guilhermemarx14.mygrana.RealmObjects;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by Guilherme Marx on 2019-05-13
 */
public class Subcategory extends RealmObject implements Serializable {
    private String subcategoryName;
    private String categoryName;

    public Subcategory() {
    }

    public Subcategory(String nameSubcategory, String category) {
        this.subcategoryName = nameSubcategory;
        this.categoryName = category;
    }

    public String getSubcategoryName() {
        return subcategoryName;
    }

    public void setSubcategoryName(String subcategoryName) {
        this.subcategoryName = subcategoryName;
    }

    public String getCategory() {
        return categoryName;
    }

    public void setCategory(String category) {
        this.categoryName = category;
    }
}
