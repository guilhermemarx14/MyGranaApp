package com.guilhermemarx14.mygrana;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.guilhermemarx14.mygrana.Adapters.TransactionsAdapter;
import com.guilhermemarx14.mygrana.Dialogs.SelectDateFilterStatements;
import com.guilhermemarx14.mygrana.RealmObjects.Transaction;
import com.guilhermemarx14.mygrana.RealmObjects.UserProfilePhoto;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

import io.realm.Realm;
import io.realm.RealmResults;

public class StatementsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseUser user;
    UserProfilePhoto upp;
    Realm realm;
    public RecyclerView rv;
    float balance = 0, positive = 0, negative = 0;
    public TransactionsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statements);
        Toolbar toolbar = getToolbar();
        Realm.init(this);
        setTitle(R.string.text_statements);
        user = getFirebaseUser();
        realm = Realm.getDefaultInstance();

        setNavigationDrawer(toolbar);

        rv = findViewById(R.id.rvTransactions);
        RealmResults<Transaction> result = realm.where(Transaction.class).findAll();
        if (result.size() == 0)
            findViewById(R.id.tvlistEmpty).setVisibility(View.VISIBLE);
        else {
            ArrayList<Transaction> myList = new ArrayList<>();
            myList.addAll(result);
            Collections.sort(myList);
            adapter = new TransactionsAdapter(this, myList);
            rv.setAdapter(adapter);
            rv.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private Toolbar getToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        return toolbar;
    }

    private FirebaseUser getFirebaseUser() {
        //get active user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Intent it = new Intent(this, LoginActivity.class);
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(it);
        }

        return mAuth.getCurrentUser();
    }

    private void setNavigationDrawer(Toolbar toolbar) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        for (int i = 0; i < navigationView.getMenu().size(); i++)
            navigationView.getMenu().getItem(i).setCheckable(false);
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
        String nome = user.getDisplayName().split(" ")[0];
        nome = nome.substring(0,1).toUpperCase() + nome.substring(1).toLowerCase();
        ((TextView) v.findViewById(R.id.navHeaderTitle)).setText("Olá, " + nome);


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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent it = new Intent(this, MenuActivity.class);
            startActivity(it);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.statements, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_statements) {
            RealmResults<Transaction> result = realm.where(Transaction.class).findAll();
            ArrayList<Transaction> myList = new ArrayList<>();
            myList.addAll(result);
            Collections.sort(myList);
            adapter = new TransactionsAdapter(this, myList);
            rv.setAdapter(adapter);
            rv.setLayoutManager(new LinearLayoutManager(this));
        } else if (id == R.id.nav_date_filter) {
            SelectDateFilterStatements sdfs = new SelectDateFilterStatements(this);
            sdfs.show();
        } else if (id == R.id.nav_payd_filter) {
            RealmResults<Transaction> result = realm.where(Transaction.class).findAll();
            ArrayList<Transaction> myList = new ArrayList<>();
            for (int i = 0; i < result.size(); i++)
                if (result.get(i).isPayd())
                    myList.add(result.get(i));
            Collections.sort(myList);
            adapter = new TransactionsAdapter(this, myList);
            rv.setAdapter(adapter);
            rv.setLayoutManager(new LinearLayoutManager(this));
        } else if (id == R.id.nav_unpayd_filter) {
            RealmResults<Transaction> result = realm.where(Transaction.class).findAll();
            ArrayList<Transaction> myList = new ArrayList<>();
            for (int i = 0; i < result.size(); i++)
                if (!result.get(i).isPayd())
                    myList.add(result.get(i));
            Collections.sort(myList);
            adapter = new TransactionsAdapter(this, myList);
            rv.setAdapter(adapter);
            rv.setLayoutManager(new LinearLayoutManager(this));
        } else if (id == R.id.nav_home) {
            Intent it = new Intent(this, MenuActivity.class);
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(it);
        }if (id == R.id.nav_share){
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.app_name));
            share.putExtra(Intent.EXTRA_TEXT, getString(R.string.menu_send));
            startActivity(Intent.createChooser(share,getString(R.string.app_name)));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
