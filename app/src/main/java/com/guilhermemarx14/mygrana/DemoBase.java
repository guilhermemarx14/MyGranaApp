
package com.guilhermemarx14.mygrana;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.mikephil.charting.charts.Chart;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.view.View;
import android.widget.Toast;


/**
 * Base class of all Activities of the Demo Application.
 *
 * @author Philipp Jahoda
 */
public abstract class DemoBase extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    protected final String[] months = new String[] {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    };
    protected final String[] parties = new String[] {
            "Salário", "Pensão", "Moradia", "Alimentação", "Lazer", "Vestimenta", "Transporte", "Investimentos"
    };


    private static final int PERMISSION_STORAGE = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected float getRandom(float range, float start) {
        return (float) (Math.random() * range) + start;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveToGallery();
            } else {
                Toast.makeText(getApplicationContext(), "Saving FAILED!", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    protected void requestStoragePermission(View view) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(view, "Write permission is required to save image to gallery", Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityCompat.requestPermissions(DemoBase.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_STORAGE);
                        }
                    }).show();
        } else {
            Toast.makeText(getApplicationContext(), "Permission Required!", Toast.LENGTH_SHORT)
                    .show();
            ActivityCompat.requestPermissions(DemoBase.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_STORAGE);
        }
    }

    protected void saveToGallery(Chart chart, String name) {
        if (chart.saveToGallery(name + "_" + System.currentTimeMillis(), 70))
            Toast.makeText(getApplicationContext(), "Saving SUCCESSFUL!",
                    Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getApplicationContext(), "Saving FAILED!", Toast.LENGTH_SHORT)
                    .show();
    }

    protected abstract void saveToGallery();
}
