package com.guilhermemarx14.mygrana.RealmObjects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import io.fabric.sdk.android.services.concurrency.AsyncTask;
import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by Guilherme Marx on 2019-05-13
 */
public class UserProfilePhoto extends RealmObject {
    private byte[] userPhoto;
    public UserProfilePhoto(){}

    public UserProfilePhoto(Bitmap bimage){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bimage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        this.userPhoto = stream.toByteArray();
    }

    public void setUserPhoto(byte[] photo){
        this.userPhoto = photo;
    }

    public Bitmap getUserPhoto(){
        return BitmapFactory.decodeByteArray(this.userPhoto, 0, this.userPhoto.length);
    }

}
