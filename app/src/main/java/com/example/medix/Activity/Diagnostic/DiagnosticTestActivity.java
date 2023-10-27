package com.example.medix.Activity.Diagnostic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.medix.Activity.CartActivity;
import com.example.medix.Adapter.DiagnosticTestAdapter;
import com.example.medix.Api.ApiClient;
import com.example.medix.Api.ApiService;
import com.example.medix.Common.Common;
import com.example.medix.Database.Database;
import com.example.medix.Model.Cart;
import com.example.medix.Model.Result;
import com.example.medix.Model.Test;
import com.example.medix.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class DiagnosticTestActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout empty_view;
    private RecyclerView diagnosticRecyclerView;
    List<Test> testList;
    private FloatingActionButton fab;
    private DiagnosticTestAdapter adapter;
    private String NAME, CENTER_ID;
    private EditText searchInput;
    CharSequence search = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnostic_test);
        // Change status bar color
        changeStatusBarColor("#00574B");

        // getting Intent From MenuAdapter
        NAME = getIntent().getStringExtra("NAME");
        CENTER_ID = getIntent().getStringExtra("CENTER_ID");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(NAME);

        //getting views
        searchInput = findViewById(R.id.search_input);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        empty_view = (LinearLayout) findViewById(R.id.empty_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().
                getColor(android.R.color.holo_blue_dark), getResources().
                getColor(android.R.color.holo_red_dark), getResources().
                getColor(android.R.color.holo_green_light), getResources().
                getColor(android.R.color.holo_orange_dark));

        diagnosticRecyclerView = (RecyclerView) findViewById(R.id.diagnosticRecyclerView);
        diagnosticRecyclerView.setHasFixedSize(false);
        diagnosticRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        diagnosticRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // load diagnostic data
        getAllDiagnostic();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DiagnosticTestActivity.this, CartActivity.class));
            }
        });

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
                filter(editable.toString());
            }
        });
    }

    private void getAllDiagnostic() {

        List<Cart> cartList = new Database(this).getCarts();
        if(cartList.size() <= 0)
        {
            fab.setVisibility(View.GONE);
        }
        else
        {
            fab.setVisibility(View.VISIBLE);
        }

        swipeRefreshLayout.setRefreshing(false);

        final android.app.AlertDialog waitingDialog = new SpotsDialog(DiagnosticTestActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");
        // Removed to Wish List
        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedix().create(ApiService.class);
        Call<Result> resultCall = service.getTestByCenterID(Common.CENTER_ID);

        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                testList = response.body().getAllTestByDiagnostic();
                if (testList.size() < 0) {
                    Toast.makeText(getApplicationContext(), "Empty Data", Toast.LENGTH_SHORT).show();
                    waitingDialog.dismiss();
                } else {
                    adapter = new DiagnosticTestAdapter(testList, DiagnosticTestActivity.this);
                    diagnosticRecyclerView.setAdapter(adapter);
                    waitingDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                waitingDialog.dismiss();
            }
        });

        //adapter = new DiagnosticTestAdapter(initDiagnosticTest(), DiagnosticTestActivity.this);
        //diagnosticRecyclerView.setAdapter(adapter);
        //recycler_view_cat.addItemDecoration(new SpacesItemDecoration(10));
        //ViewCompat.setNestedScrollingEnabled(diagnosticRecyclerView, false);
    }

    @Override
    public void onRefresh() {
        searchInput.setText("");
        search = "";
        getAllDiagnostic();
    }

    private List<Test> initDiagnosticTest() {
        testList = new ArrayList<>();

        testList.add(new Test("Cardiology", "Echocardiogram", "Echocardiogram short name", "৳500"));
        testList.add(new Test("Cardiology", "Echo Color Doppler", "Echo Color Doppler short name", "৳400"));
        testList.add(new Test("Cardiology", "Stress Test (TMT)", "Stress Test (TMT) short name", "৳700"));
        testList.add(new Test("Cardiology", "Holter monitor", "Holter monitor short name", "৳200"));
        testList.add(new Test("Cardiology", "Electrophysiology Study (EPS) & Ablation", "Electrophysiology Study (EPS) & Ablation short name", "৳800"));
        testList.add(new Test("Cardiology", "Electrocardiogram (ECG)", "Electrocardiogram (ECG) short name", "৳900"));
        testList.add(new Test("Diagnostic & Interventional Radiology", "X-ray", "X-ray short name", "৳500"));
        testList.add(new Test("Diagnostic & Interventional Radiology", "MRI", "MRI short name", "৳500"));
        testList.add(new Test("Diagnostic & Interventional Radiology", "CT scan", "CT scan short name", "৳500"));
        testList.add(new Test("Diagnostic & Interventional Radiology", "Mammography", "Mammography short name", "৳500"));
        testList.add(new Test("Diagnostic & Interventional Radiology", "Ultrasound", "Ultrasound short name", "৳500"));
        testList.add(new Test("ENT & Interventional Radiology", "Hearing screening test", "Hearing screening test short name", "৳500"));
        testList.add(new Test("Blood tests", "Blood Sugar", "Blood Sugar short name", "৳500"));
        testList.add(new Test("Blood tests", "Lipid profile", "Lipid profile short name", "৳500"));
        testList.add(new Test("Blood tests", "Liver Function Test", "Liver Function Test short name", "৳500"));
        testList.add(new Test("Blood tests", "Bilirubin", "Bilirubin short name", "৳500"));
        testList.add(new Test("Blood tests", "T3/T4", "T3/T4 short name", "৳500"));
        testList.add(new Test("Blood tests", "TSH", "TSH short name", "৳500"));
        testList.add(new Test("Blood tests", "Urea", "Urea short name", "৳500"));
        testList.add(new Test("Blood tests", "Creatinine", "Creatinine short name", "৳500"));
        testList.add(new Test("Blood tests", "Electrolytes (NA,K,CL,HCO3)", "Electrolytes (NA,K,CL,HCO3) short name", "৳500"));
        testList.add(new Test("Blood tests", "Blood Gases (ABG)", "Blood Gases (ABG) short name", "৳500"));
        testList.add(new Test("Blood tests", "PSA (Prostate Specific Antigen)", "PSA (Prostate Specific Antigen) short name", "৳500"));
        testList.add(new Test("Blood tests", "Troponin I", "Troponin I short name", "৳500"));

        return testList;
    }

    void filter(String text) {
        List<Test> temp = new ArrayList();
        for (Test d : testList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.getDiagnostic_center_name().toLowerCase().contains(text.toLowerCase())
                    || d.getName().toLowerCase().contains(text.toLowerCase())
                    || d.getShort_name().toLowerCase().contains(text.toLowerCase())
                    || d.getPrice().toLowerCase().contains(text.toLowerCase())) {
                temp.add(d);
            }
        }
        //update recyclerview
        adapter.updateList(temp);
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private void changeStatusBarColor(String color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // load all test data
        getAllDiagnostic();
    }

    public void getCartCount()
    {
        List<Cart> cartList = new Database(this).getCarts();
        if(cartList.size() <= 0)
        {
            fab.setVisibility(View.GONE);
        }
        else
        {
            fab.setVisibility(View.VISIBLE);
        }
    }
}
