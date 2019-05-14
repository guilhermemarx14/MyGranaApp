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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.guilhermemarx14.mygrana.Dialogs.AddSubcategoryDialog;
import com.guilhermemarx14.mygrana.RealmObjects.Category;
import com.guilhermemarx14.mygrana.RealmObjects.UserProfilePhoto;

import java.io.InputStream;

import io.fabric.sdk.android.services.concurrency.AsyncTask;
import io.realm.Realm;

import static com.guilhermemarx14.mygrana.Utils.Constants.GASTO;
import static com.guilhermemarx14.mygrana.Utils.Constants.RENDA;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseUser user;
    UserProfilePhoto upp;
    Realm realm;
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Toolbar toolbar = getToolbar();

        user = getFirebaseUser();
        realm = Realm.getDefaultInstance();

        setFloatingActionButton();

        setNavigationDrawer(toolbar);


    }

    private void setNavigationDrawer(Toolbar toolbar) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        setUpNavigationHeader(navigationView);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setUpNavigationHeader(NavigationView navigationView) {
        View v = navigationView.getHeaderView(0);
        upp = realm.where(UserProfilePhoto.class).findFirst();
        if (upp == null) {
            setUpFirstTimeUser();
            new DownloadImageFromInternet(((ImageView) v.findViewById(R.id.navHeaderPhoto))).execute(user.getPhotoUrl().toString());
        }else
            ((ImageView) v.findViewById(R.id.navHeaderPhoto)).setImageBitmap(upp.getUserPhoto());

        ((TextView) v.findViewById(R.id.navHeaderTitle)).setText(user.getDisplayName());

        ((TextView) v.findViewById(R.id.navHeaderEmail)).setText(user.getEmail());
    }

    private void setUpFirstTimeUser() {
        realm.beginTransaction();
            realm.copyToRealm(new Category("Salário",RENDA));
            realm.copyToRealm(new Category("Pensão",RENDA));
            realm.copyToRealm(new Category("Moradia",GASTO));
            realm.copyToRealm(new Category("Alimentação",GASTO));
            realm.copyToRealm(new Category("Lazer",GASTO));
            realm.copyToRealm(new Category("Vestimenta",GASTO));
            realm.copyToRealm(new Category("Transporte",GASTO));
            realm.copyToRealm(new Category("Investimentos",GASTO));
        realm.commitTransaction();
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

    private void setFloatingActionButton() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(context,AddTransaction.class);
                startActivity(it);
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

        if (id == R.id.nav_new_subcategory) {
            dialogNewSubcategory();
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

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
