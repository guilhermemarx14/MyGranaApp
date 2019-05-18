package com.guilhermemarx14.mygrana.RealmObjects;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Guilherme Marx on 2019-05-13
 */
public class Subcategory extends RealmObject {
    @PrimaryKey
    String subcategoryName;
    Category category;

    public Subcategory(){}

    public Subcategory(String nameSubcategory, Category category){
        this.subcategoryName = nameSubcategory;
        this.category = category;
    }

    public String getSubcategoryName() {
        return subcategoryName;
    }

    public void setSubcategoryName(String subcategoryName) {
        this.subcategoryName = subcategoryName;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
