package com.guilhermemarx14.mygrana.RealmObjects;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import static com.guilhermemarx14.mygrana.Utils.Constants.getSubcategoryId;

/**
 * Created by Guilherme Marx on 2019-05-13
 */
public class Subcategory extends RealmObject implements Serializable {
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @PrimaryKey
    private long id;
    private String subcategoryName;
    private String categoryName;

    public Subcategory() {
        id =0;
        subcategoryName = "";
        categoryName = "";
    }

    public Subcategory(String nameSubcategory, String category) {
        this.id = getSubcategoryId();
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
