package com.guilhermemarx14.mygrana;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.guilhermemarx14.mygrana.RealmObjects.Category;
import com.guilhermemarx14.mygrana.RealmObjects.FirstTime;
import com.guilhermemarx14.mygrana.RealmObjects.Subcategory;
import com.guilhermemarx14.mygrana.RealmObjects.Transaction;
import com.guilhermemarx14.mygrana.RealmObjects.University;
import com.guilhermemarx14.mygrana.RealmObjects.UserProfilePhoto;

import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static com.guilhermemarx14.mygrana.Utils.Constants.GASTO;
import static com.guilhermemarx14.mygrana.Utils.Constants.RENDA;
import static com.guilhermemarx14.mygrana.Utils.Constants.setSubcategoryId;
import static com.guilhermemarx14.mygrana.Utils.Constants.setTransactionId;

public class SplashActivity extends AppCompatActivity {
    Context context = this;
    FirebaseUser user;
    UserProfilePhoto upp;
    Realm realm;
    boolean firstTime;
    DatabaseReference queryUni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        Realm.getInstance(config);
        getSupportActionBar().hide();
        user = getFirebaseUser();
        realm = Realm.getDefaultInstance();
        final Animation animRotate = AnimationUtils.loadAnimation(this, R.anim.rotate);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        queryUni = rootRef.child(user.getUid()).child("university");
        if (realm.where(FirstTime.class).findAll().size() > 0)
            firstTime = false;
        else {
            realm.beginTransaction();
            realm.insertOrUpdate(new FirstTime(true));
            realm.commitTransaction();
            rootRef.child(user.getUid()).child("university").removeValue();
            firstTime = true;
        }


        realm.beginTransaction();
        realm.insertOrUpdate(new Category("Salário", RENDA));
        realm.insertOrUpdate(new Category("Pensão", RENDA));
        realm.insertOrUpdate(new Category("Moradia", GASTO));
        realm.insertOrUpdate(new Category("Alimentação", GASTO));
        realm.insertOrUpdate(new Category("Lazer", GASTO));
        realm.insertOrUpdate(new Category("Vestimenta", GASTO));
        realm.insertOrUpdate(new Category("Transporte", GASTO));
        realm.insertOrUpdate(new Category("Investimentos", GASTO));
        realm.insertOrUpdate(new Category("Saúde", GASTO));
        realm.insertOrUpdate(new Category("Esportes", GASTO));
        realm.commitTransaction();
        (new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(2000);
                SystemClock.sleep(2000);
                if (!firstTime) {
                    Intent it = new Intent(context, MenuActivity.class);
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(it);
                } else {
                    Intent it = new Intent(context, FirstUserActivity.class);
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(it);
                }
            }
        })).start();
        ImageView progressView = findViewById(R.id.image_progress);
        progressView.startAnimation(animRotate);


        upp = realm.where(UserProfilePhoto.class).findFirst();
        setUpFirstTimeUser();

    }

    private void setUpFirstTimeUser() {

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myquery = mDatabase.child("Universidades");
        ValueEventListener eventListener3 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (realm.where(University.class).findAll().size() != dataSnapshot.getChildrenCount())
                    for (int i = realm.where(University.class).findAll().size(); i <= dataSnapshot.getChildrenCount(); i++) {
                        University university = new University();
                        university.setName((String) dataSnapshot.child("" + i).getValue());
                        university.setId(i);

                        realm.beginTransaction();
                        realm.insertOrUpdate(university);
                        realm.commitTransaction();

                    }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        myquery.addListenerForSingleValueEvent(eventListener3);
        myquery.orderByKey();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        myquery = mDatabase.child(user.getUid()).child("transactions");
        ValueEventListener eventListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (realm.where(Transaction.class).findAll().size() != dataSnapshot.getChildrenCount())
                    for (int i = 1, j = 1; i <= dataSnapshot.getChildrenCount(); i++, j++) {
                        try {
                            Transaction transaction = new Transaction();
                            transaction.setCategory(dataSnapshot.child("" + j).getValue(Transaction.class).getCategory());
                            transaction.setId(j);
                            transaction.setSubcategory(dataSnapshot.child("" + j).getValue(Transaction.class).getSubcategory());
                            transaction.setDescription(dataSnapshot.child("" + j).getValue(Transaction.class).getDescription());
                            transaction.setDate(dataSnapshot.child("" + j).getValue(Transaction.class).getDate());
                            transaction.setPayd(dataSnapshot.child("" + j).getValue(Transaction.class).isPayd());
                            transaction.setCategoryName(dataSnapshot.child("" + j).getValue(Transaction.class).getCategoryName());
                            transaction.setValue(dataSnapshot.child("" + j).getValue(Transaction.class).getValue());

                            realm.beginTransaction();
                            realm.insertOrUpdate(transaction);
                            realm.commitTransaction();
                        } catch (Exception e) {
                            e.printStackTrace();
                            i--;
                        }

                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        myquery.addListenerForSingleValueEvent(eventListener2);
        myquery.orderByKey();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        myquery = mDatabase.child(user.getUid()).child("subcategories");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (realm.where(Subcategory.class).findAll().size() != dataSnapshot.getChildrenCount())
                    for (int i = 1, j = 1; i <= dataSnapshot.getChildrenCount(); i++, j++) {
                        try {
                            Subcategory subcategory = new Subcategory();
                            subcategory.setCategory(dataSnapshot.child("" + j).getValue(Subcategory.class).getCategory());
                            subcategory.setSubcategoryName(dataSnapshot.child("" + j).getValue(Subcategory.class).getSubcategoryName());
                            subcategory.setCategoryName(dataSnapshot.child("" + j).getValue(Subcategory.class).getCategoryName());
                            subcategory.setId(j);
                            realm.beginTransaction();
                            Category category = realm.where(Category.class).equalTo("name", (String) subcategory.getCategory()).findFirst();
                            realm.insertOrUpdate(subcategory);
                            category.getSubcategories().add(subcategory);
                            realm.insertOrUpdate(category);
                            realm.commitTransaction();
                        } catch (Exception e) {
                            e.printStackTrace();
                            i--;
                        }

                    }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        myquery.addListenerForSingleValueEvent(eventListener);
        myquery.orderByKey();

    }

    private FirebaseUser getFirebaseUser() {
        //get active user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user == null) {
            Intent it = new Intent(this, LoginActivity.class);
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(it);
        }
        return user;
    }
}
