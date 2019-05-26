package com.guilhermemarx14.mygrana;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.model.GradientColor;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.FileUtils;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.guilhermemarx14.mygrana.Adapters.TransactionsAdapter;
import com.guilhermemarx14.mygrana.Dialogs.AddSubcategoryDialog;
import com.guilhermemarx14.mygrana.Dialogs.AddTransactionDialog;
import com.guilhermemarx14.mygrana.RealmObjects.Subcategory;
import com.guilhermemarx14.mygrana.RealmObjects.Transaction;
import com.guilhermemarx14.mygrana.RealmObjects.UserProfilePhoto;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.guilhermemarx14.mygrana.Utils.Constants.GASTO;
import static com.guilhermemarx14.mygrana.Utils.Constants.RENDA;
import static com.guilhermemarx14.mygrana.Utils.Constants.SALDO;
import static com.guilhermemarx14.mygrana.Utils.Constants.setSubcategoryId;
import static com.guilhermemarx14.mygrana.Utils.Constants.setTransactionId;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnChartValueSelectedListener, SeekBar.OnSeekBarChangeListener {


    protected final String[] parties = new String[]{
            "Salário", "Pensão", "Moradia", "Alimentação", "Lazer", "Vestimenta", "Transporte", "Investimentos", "Saúde"
    };
    FirebaseUser user;
    UserProfilePhoto upp;
    Realm realm;
    Context context = this;
    float balance = 0, positive = 0, negative = 0;
    private PieChart chart;
    private BarChart barChart1;
    private BarChart barChart2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = getToolbar();
        Realm.init(this);
        setTitle(R.string.app_name);
        user = getFirebaseUser();
        realm = Realm.getDefaultInstance();
        setFloatingActionButton();

        setNavigationDrawer(toolbar);

        if (realm.where(Transaction.class).max("id") != null)
            setTransactionId((long) realm.where(Transaction.class).max("id"));
        else setTransactionId(0);

        if (realm.where(Subcategory.class).max("id") != null)
            setSubcategoryId((long) realm.where(Transaction.class).max("id"));
        else setSubcategoryId(0);
        setUpBarChartEfetivado();
        setUpBarChartInadimplente();
    }

    private void setUpBarChartEfetivado() {


        barChart2 = findViewById(R.id.barChart2);
        barChart2.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }
        });

        barChart2.setDrawBarShadow(false);
        barChart2.setDrawValueAboveBar(true);
        barChart2.setPinchZoom(false);
        barChart2.setDoubleTapToZoomEnabled(false);
        barChart2.getDescription().setEnabled(false);


        // scaling can now only be done on x- and y-axis separately

        barChart2.setDrawGridBackground(false);
        // chart.setDrawYLabels(false);

        ValueFormatter xAxisFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {

                switch ((int) value % 3) {
                    case GASTO:
                        return "Gasto";
                    case RENDA:
                        return "Renda";
                    case SALDO:
                        return "Saldo";
                }
                return "";
            }
        };

        XAxis xAxis = barChart2.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);


        ValueFormatter custom = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("R$ %.2f", value);
            }
        };

        YAxis leftAxis = barChart2.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = barChart2.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        barChart2.getLegend().setEnabled(false);


        XYMarkerView mv = new XYMarkerView(this, xAxisFormatter);
        mv.setChartView(barChart2); // For bounds control
        barChart2.setMarker(mv); // Set the marker to the chart


        // chart.setDrawLegend(false);

        RealmResults<Transaction> transactions = realm.where(Transaction.class).findAll();
        ArrayList<Transaction> gastos = new ArrayList<>();
        ArrayList<Transaction> rendas = new ArrayList<>();
        for (Transaction t : transactions)
            if (t.isPayd()) {
                if (t.getValue() >= 0)
                    rendas.add(t);
                else gastos.add(t);
            }

        float totalRenda = 0;
        float totalGasto = 0;
        for (Transaction t : rendas)
            totalRenda += t.getValue();
        for (Transaction t : gastos)
            totalGasto -= t.getValue();

        float saldo = totalRenda - totalGasto;
        boolean saldoPositivo = saldo >= 0;

        ArrayList<BarEntry> values = new ArrayList<>();
        values.add(new BarEntry(GASTO, totalGasto, getResources().getDrawable(R.drawable.star)));
        values.add(new BarEntry(RENDA, totalRenda, getResources().getDrawable(R.drawable.star)));
        if (saldoPositivo)
            values.add(new BarEntry(SALDO, saldo, getResources().getDrawable(R.drawable.star)));
        else
            values.add(new BarEntry(SALDO, -saldo, getResources().getDrawable(R.drawable.star)));

        BarDataSet set1;

        if (barChart2.getData() != null &&
                barChart2.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) barChart2.getData().getDataSetByIndex(0);
            set1.setValues(values);
            barChart2.getData().notifyDataChanged();
            barChart2.notifyDataSetChanged();

        } else {
            set1 = new BarDataSet(values, "The year 2017");

            set1.setDrawIcons(false);

//            set1.setColors(ColorTemplate.MATERIAL_COLORS);

            /*int startColor = ContextCompat.getColor(this, android.R.color.holo_blue_dark);
            int endColor = ContextCompat.getColor(this, android.R.color.holo_blue_bright);
            set1.setGradientColor(startColor, endColor);*/

            int startColor1 = ContextCompat.getColor(this, R.color.colorRed);
            int startColor2 = ContextCompat.getColor(this, R.color.colorAccent);
            int startColor3;
            if(saldoPositivo)
                startColor3 = ContextCompat.getColor(this, R.color.colorAccent);
            else startColor3 = ContextCompat.getColor(this, R.color.colorRed);
            List<GradientColor> gradientColors = new ArrayList<>();
            gradientColors.add(new GradientColor(startColor1, startColor1));
            gradientColors.add(new GradientColor(startColor2, startColor2));
            gradientColors.add(new GradientColor(startColor3, startColor3));

            set1.setGradientColors(gradientColors);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(0.9f);

            barChart2.setData(data);
        }
    }

    private void setUpBarChartInadimplente() {


        barChart1 = findViewById(R.id.barChart1);
        barChart1.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }
        });

        barChart1.setDrawBarShadow(false);
        barChart1.setDrawValueAboveBar(true);
        barChart1.setPinchZoom(false);
        barChart1.setDoubleTapToZoomEnabled(false);
        barChart1.getDescription().setEnabled(false);


        // scaling can now only be done on x- and y-axis separately

        barChart1.setDrawGridBackground(false);
        // chart.setDrawYLabels(false);

        ValueFormatter xAxisFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {

                switch ((int) value % 3) {
                    case GASTO:
                        return "Gasto";
                    case RENDA:
                        return "Renda";
                    case SALDO:
                        return "Saldo";
                }
                return "";
            }
        };

        XAxis xAxis = barChart1.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);


        ValueFormatter custom = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("R$ %.2f", value);
            }
        };

        YAxis leftAxis = barChart1.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = barChart1.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        barChart1.getLegend().setEnabled(false);


        XYMarkerView mv = new XYMarkerView(this, xAxisFormatter);
        mv.setChartView(barChart1); // For bounds control
        barChart1.setMarker(mv); // Set the marker to the chart


        // chart.setDrawLegend(false);

        RealmResults<Transaction> transactions = realm.where(Transaction.class).findAll();
        ArrayList<Transaction> gastos = new ArrayList<>();
        ArrayList<Transaction> rendas = new ArrayList<>();
        for (Transaction t : transactions)
            if (!t.isPayd()) {
                if (t.getValue() >= 0)
                    rendas.add(t);
                else gastos.add(t);
            }

        float totalRenda = 0;
        float totalGasto = 0;
        for (Transaction t : rendas)
            totalRenda += t.getValue();
        for (Transaction t : gastos)
            totalGasto -= t.getValue();
        float saldo = totalRenda - totalGasto;
        boolean saldoPositivo = saldo >= 0;




        ArrayList<BarEntry> values = new ArrayList<>();
        values.add(new BarEntry(GASTO, totalGasto, getResources().getDrawable(R.drawable.star)));
        values.add(new BarEntry(RENDA, totalRenda, getResources().getDrawable(R.drawable.star)));
        if (saldoPositivo)
            values.add(new BarEntry(SALDO, saldo, getResources().getDrawable(R.drawable.star)));
        else
            values.add(new BarEntry(SALDO, -saldo, getResources().getDrawable(R.drawable.star)));

        BarDataSet set1;

        if (barChart1.getData() != null &&
                barChart1.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) barChart1.getData().getDataSetByIndex(0);
            set1.setValues(values);
            barChart1.getData().notifyDataChanged();
            barChart1.notifyDataSetChanged();

        } else {
            set1 = new BarDataSet(values, "The year 2017");

            set1.setDrawIcons(false);

//            set1.setColors(ColorTemplate.MATERIAL_COLORS);

            /*int startColor = ContextCompat.getColor(this, android.R.color.holo_blue_dark);
            int endColor = ContextCompat.getColor(this, android.R.color.holo_blue_bright);
            set1.setGradientColor(startColor, endColor);*/

            int startColor1 = ContextCompat.getColor(this, R.color.colorRed);
            int startColor2 = ContextCompat.getColor(this, R.color.colorAccent);
            int startColor3;
            if(saldoPositivo)
                startColor3 = ContextCompat.getColor(this, R.color.colorAccent);
            else startColor3 = ContextCompat.getColor(this, R.color.colorRed);
            List<GradientColor> gradientColors = new ArrayList<>();
            gradientColors.add(new GradientColor(startColor1, startColor1));
            gradientColors.add(new GradientColor(startColor2, startColor2));
            gradientColors.add(new GradientColor(startColor3, startColor3));

            set1.setGradientColors(gradientColors);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(0.9f);

            barChart1.setData(data);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void setPieChart(int gastoOuRenda) {
        findViewById(R.id.noValueChart).setVisibility(View.GONE);
        chart = findViewById(R.id.chart1);
        chart.setVisibility(View.VISIBLE);
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);

        chart.setDragDecelerationFrictionCoef(0.95f);

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);

        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);

        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);
        chart.setDrawCenterText(true);

        chart.setRotationAngle(0);
        // enable rotation of the chart by touch
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener
        chart.setOnChartValueSelectedListener(this);

        chart.animateY(1400, Easing.EaseInOutQuad);
        // chart.spin(2000, 0, 360);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        chart.setEntryLabelColor(Color.WHITE);

        chart.setEntryLabelTextSize(12f);
        setData(gastoOuRenda);
    }

    private void setData(int gastoOuRenda) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        RealmResults<Transaction> result = realm.where(Transaction.class).findAll();
        ArrayList<Transaction> list = new ArrayList<>();
        list.addAll(result);
        float valor;
        String nome;
        ArrayList<Integer> nonZeroPositions = new ArrayList<>();
        float soma[] = new float[9];
        for (int i = 0; i < 8; i++)
            soma[i] = 0;
        for (int i = 0; i < list.size(); i++) {
            valor = list.get(i).getValue();
            if (valor < 0) valor = -valor;
            nome = list.get(i).getCategoryName();
            soma[position(nome)] += valor;
            nonZeroPositions.add(position(nome));
        }
        boolean hasvalue = false;


        if (gastoOuRenda == GASTO) {
            for (int i = 2; i < 9; i++) {
                if (nonZeroPositions.contains(i)) {
                    entries.add(new PieEntry(soma[i], parties[i], null));
                    if (soma[i] != 0) {
                        hasvalue = true;
                    }
                }


            }
            if (!hasvalue) {
                TextView novalue = findViewById(R.id.noValueChart);
                novalue.setVisibility(View.VISIBLE);
                chart.setVisibility(View.GONE);
                novalue.setText(getString(R.string.empty_chart, "um gasto"));
            }
        } else {
            for (int i = 0; i < 2; i++) {
                entries.add(new PieEntry(soma[i], parties[i], null));
                if (soma[i] != 0) {
                    hasvalue = true;
                }
            }
            if (!hasvalue) {
                TextView novalue = findViewById(R.id.noValueChart);
                novalue.setVisibility(View.VISIBLE);
                chart.setVisibility(View.GONE);
                novalue.setText(getString(R.string.empty_chart, "uma renda"));
            }
        }
        PieDataSet dataSet = new PieDataSet(entries, getString(R.string.text_category));

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();


        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);


        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(chart));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        chart.setData(data);

        // undo all highlights
        chart.highlightValues(null);

        chart.invalidate();
    }

    public int position(String category) {
        for (int i = 0; i < parties.length; i++)
            if (category.equals(parties[i]))
                return i;

        return -1;
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
        nome = nome.substring(0, 1).toUpperCase() + nome.substring(1).toLowerCase();
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


    private Toolbar getToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        return toolbar;
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
            android.os.Process.killProcess(android.os.Process.myPid());
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
        } else if (id == R.id.nav_first_chart) {
            findViewById(R.id.linearLayoutHome).setVisibility(View.GONE);
            setPieChart(GASTO);
        } else if (id == R.id.nav_second_chart) {
            findViewById(R.id.linearLayoutHome).setVisibility(View.GONE);
            setPieChart(RENDA);
        } else if (id == R.id.nav_home) {
            findViewById(R.id.linearLayoutHome).setVisibility(View.VISIBLE);
            findViewById(R.id.chart1).setVisibility(View.GONE);
            findViewById(R.id.noValueChart).setVisibility(View.GONE);
        }
        if (id == R.id.nav_share) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            share.putExtra(Intent.EXTRA_TEXT, getString(R.string.menu_send));
            startActivity(Intent.createChooser(share, getString(R.string.app_name)));
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

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        chart.setUsePercentValues(!chart.isUsePercentValuesEnabled());
        chart.invalidate();
    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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

    public class MyValueFormatter extends ValueFormatter {

        private final DecimalFormat mFormat;
        private String suffix;

        public MyValueFormatter(String suffix) {
            mFormat = new DecimalFormat("###,###,###,##0.0");
            this.suffix = suffix;
        }

        @Override
        public String getFormattedValue(float value) {
            return suffix + " " + mFormat.format(value);
        }

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            if (axis instanceof XAxis) {
                return mFormat.format(value);
            } else if (value > 0) {
                return mFormat.format(value) + suffix;
            } else {
                return mFormat.format(value);
            }
        }
    }

    public class XYMarkerView extends MarkerView {

        private final TextView tvContent;
        private final ValueFormatter xAxisValueFormatter;

        private final DecimalFormat format;

        public XYMarkerView(Context context, ValueFormatter xAxisValueFormatter) {
            super(context, R.layout.custom_marker_view);

            this.xAxisValueFormatter = xAxisValueFormatter;
            tvContent = findViewById(R.id.tvContent);
            format = new DecimalFormat("###.0");
        }

        // runs every time the MarkerView is redrawn, can be used to update the
        // content (user-interface)
        @Override
        public void refreshContent(Entry e, Highlight highlight) {

            tvContent.setText(String.format("x: %s, y: %s", xAxisValueFormatter.getFormattedValue(e.getX()), format.format(e.getY())));

            super.refreshContent(e, highlight);
        }

        @Override
        public MPPointF getOffset() {
            return new MPPointF(-(getWidth() / 2), -getHeight());
        }
    }

}
