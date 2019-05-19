package com.guilhermemarx14.mygrana;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.guilhermemarx14.mygrana.Dialogs.AddSubcategoryDialog;
import com.guilhermemarx14.mygrana.Dialogs.AddTransactionDialog;
import com.guilhermemarx14.mygrana.RealmObjects.Transaction;
import com.guilhermemarx14.mygrana.RealmObjects.UserProfilePhoto;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import java.io.InputStream;

import io.fabric.sdk.android.services.concurrency.AsyncTask;
import io.realm.Realm;
import io.realm.RealmResults;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseUser user;
    UserProfilePhoto upp;
    Realm realm;
    Context context = this;
    float balance = 0, positive = 0, negative = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Toolbar toolbar = getToolbar();
        setTitle(R.string.app_name);
        user = getFirebaseUser();
        realm = Realm.getDefaultInstance();
   //     faker();
        setFloatingActionButton();

        setNavigationDrawer(toolbar);

    }

//    private void faker() {
//        Faker faker = new Faker();
//        RealmResults<Category> categories = realm.where(Category.class).findAll();
//        ArrayList<Category> myfake = new ArrayList<>();
//        myfake.addAll(categories);
//
//        myfake.add(0,new Category("Salário", RENDA));
//        myfake.add(0,new Category("Salário", RENDA));
//        myfake.add(0,new Category("Pensão", RENDA));
//        myfake.add(0,new Category("Pensão", RENDA));
//        for (int i = 0; i < 150; i++) {
//            int number = faker.number.positive(0, 10000);
//            Date date = faker.date.birthday(0, -1);
//            String description = faker.lorem.paragraph();
//
//            Transaction t = new Transaction();
//            t.setCategory(myfake.get(number % 12).getName());
//            if(number%12>5)
//                number=-number;
//            t.setValue((float) number / 100);
//            t.setDescription(description);
//            String mDate = "" + (date.getYear()+1900) + "-" + (date.getMonth() + 1) + "-" + date.getDay();
//            t.setDate(mDate);
//            t.setPayd(number % 2 == 0);
//            realm.beginTransaction();
//            realm.insertOrUpdate(t);
//            realm.commitTransaction();
//
//            FirebaseAuth mAuth = FirebaseAuth.getInstance();
//            FirebaseUser user = mAuth.getCurrentUser();
//            FirebaseDatabase database = FirebaseDatabase.getInstance();
//            DatabaseReference myRef = database.getReference(user.getUid());
//            myRef.child("transactions").push().setValue(t);
//
//        }
//    }

    private void setNavigationDrawer(Toolbar toolbar) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        setUpNavigationHeader(navigationView);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
    }

    private void setUpNavigationHeader(NavigationView navigationView) {
        View v = navigationView.getHeaderView(0);


        upp = realm.where(UserProfilePhoto.class).findFirst();
        if (upp == null) {
            new DownloadImageFromInternet(((ImageView) v.findViewById(R.id.navHeaderPhoto))).execute(user.getPhotoUrl().toString());
        } else
            ((ImageView) v.findViewById(R.id.navHeaderPhoto)).setImageBitmap(upp.getUserPhoto());

        ((TextView) v.findViewById(R.id.navHeaderTitle)).setText(user.getDisplayName());

        ((TextView) v.findViewById(R.id.navHeaderEmail)).setText(user.getEmail());

        setBalance(v);
    }

    private void setBalance(View v) {
        RealmResults<Transaction> result = realm.where(Transaction.class).findAll();
        for (Transaction a : result) {
            if (a.getValue() > 0)
                positive += a.getValue();
            else negative += a.getValue();
            balance += a.getValue();
        }
        ((TextView) v.findViewById(R.id.txPositive)).setText(String.format("R$ %.2f", positive));
        ((TextView) v.findViewById(R.id.txNegative)).setText(String.format("R$ %.2f", negative));
        if (balance >= 0)
            ((TextView) v.findViewById(R.id.txBalance)).setTextColor(getResources().getColor(R.color.colorAccent));
        else
            ((TextView) v.findViewById(R.id.txBalance)).setTextColor(getResources().getColor(R.color.colorRed));
        ((TextView) v.findViewById(R.id.txBalance)).setText(String.format("R$ %.2f", balance));
    }


    private Toolbar getToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        return toolbar;
    }

    private FirebaseUser getFirebaseUser() {
        //get active user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        return mAuth.getCurrentUser();
    }

    SpeedDialView fab;

    private void setFloatingActionButton() {
        fab = findViewById(R.id.speedDial);
        fab.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_new_subcategory, R.drawable.ic_add)
                .setLabel(R.string.menu_new_subcategory)
                .setLabelBackgroundColor(getResources().getColor(android.R.color.white))
                .setLabelColor(getResources().getColor(android.R.color.black))
                .create());

        fab.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_new_transaction, R.drawable.ic_playlist_add)
                .setLabel(R.string.title_activity_add_transaction)
                .setLabelBackgroundColor(getResources().getColor(android.R.color.white))
                .setLabelColor(getResources().getColor(android.R.color.black))
                .create());

        fab.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem actionItem) {
                switch (actionItem.getId()) {
                    case R.id.fab_new_subcategory:
                        dialogNewSubcategory();
                        return false;
                    case R.id.fab_new_transaction:
                        dialogNewTransaction();
                        return false;
                    default:
                        return false;
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            FirebaseAuth.getInstance().signOut();
            realm.beginTransaction();
            realm.deleteAll();
            realm.commitTransaction();
            Intent it = new Intent(this, LoginActivity.class);
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(it);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_statements) {
            Intent it = new Intent(this, StatementsActivity.class);
            startActivity(it);


        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void dialogNewSubcategory() {
        AddSubcategoryDialog asd = new AddSubcategoryDialog(this);
        asd.show();
    }

    private void dialogNewTransaction() {
        AddTransactionDialog add = new AddTransactionDialog(this);
        add.show();
    }

    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        private DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = null;
            Realm realm = Realm.getDefaultInstance();
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            upp = new UserProfilePhoto(bimage);
            realm.beginTransaction();
            realm.copyToRealm(upp);
            realm.commitTransaction();
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imageView.setImageBitmap(result);

            }
        }
    }

}
