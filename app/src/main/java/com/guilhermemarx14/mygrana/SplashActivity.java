package com.guilhermemarx14.mygrana;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.guilhermemarx14.mygrana.RealmObjects.Category;
import com.guilhermemarx14.mygrana.RealmObjects.Subcategory;
import com.guilhermemarx14.mygrana.RealmObjects.Transaction;
import com.guilhermemarx14.mygrana.RealmObjects.University;
import com.guilhermemarx14.mygrana.RealmObjects.UserProfilePhoto;

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
    boolean firstTime = false;

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
        realm.commitTransaction();
        (new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(4000);

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
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myquery = mDatabase.child(user.getUid());
        ValueEventListener eventListener4 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child("university").hasChildren()) {
                    firstTime = true;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        myquery.addListenerForSingleValueEvent(eventListener4);
        upp = realm.where(UserProfilePhoto.class).findFirst();
        setUpFirstTimeUser();
    }

    private void setUpFirstTimeUser() {

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myquery = mDatabase.child("Universidades");
        ValueEventListener eventListener3 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (int i = 1; i < dataSnapshot.getChildrenCount(); i++) {
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

        mDatabase = FirebaseDatabase.getInstance().getReference();
        myquery = mDatabase.child(user.getUid()).child("transactions");
        ValueEventListener eventListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (int i = 1; i <= dataSnapshot.getChildrenCount(); i++) {
                    try {
                        Transaction transaction = new Transaction();
                        transaction.setCategory(dataSnapshot.child("" + i).getValue(Transaction.class).getCategory());
                        transaction.setId(i);
                        transaction.setSubcategory(dataSnapshot.child("" + i).getValue(Transaction.class).getSubcategory());
                        transaction.setDescription(dataSnapshot.child("" + i).getValue(Transaction.class).getDescription());
                        transaction.setDate(dataSnapshot.child("" + i).getValue(Transaction.class).getDate());
                        transaction.setPayd(dataSnapshot.child("" + i).getValue(Transaction.class).isPayd());
                        transaction.setCategoryName(dataSnapshot.child("" + i).getValue(Transaction.class).getCategoryName());
                        transaction.setValue(dataSnapshot.child("" + i).getValue(Transaction.class).getValue());

                        realm.beginTransaction();
                        realm.insertOrUpdate(transaction);
                        realm.commitTransaction();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        myquery.addListenerForSingleValueEvent(eventListener2);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        myquery = mDatabase.child(user.getUid()).child("subcategories");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (int i = 1; i <= dataSnapshot.getChildrenCount(); i++) {
                    try {
                        Subcategory subcategory = new Subcategory();
                        subcategory.setCategory(dataSnapshot.child("" + i).getValue(Subcategory.class).getCategory());
                        subcategory.setSubcategoryName(dataSnapshot.child("" + i).getValue(Subcategory.class).getSubcategoryName());
                        subcategory.setCategoryName(dataSnapshot.child("" + i).getValue(Subcategory.class).getCategoryName());
                        subcategory.setId(i);
                        realm.beginTransaction();
                        Category category = realm.where(Category.class).equalTo("name", (String) subcategory.getCategory()).findFirst();
                        realm.insertOrUpdate(subcategory);
                        category.getSubcategories().add(subcategory);
                        realm.insertOrUpdate(category);
                        realm.commitTransaction();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        myquery.addListenerForSingleValueEvent(eventListener);

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
