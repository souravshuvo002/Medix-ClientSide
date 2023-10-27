package com.example.medix.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import com.example.medix.Adapter.AllDiagnosticTestAdapter;
import com.example.medix.Adapter.DiagnosticTestAdapter;
import com.example.medix.Api.ApiClient;
import com.example.medix.Api.ApiService;
import com.example.medix.Database.Database;
import com.example.medix.Model.Cart;
import com.example.medix.Model.Result;
import com.example.medix.Model.Test;
import com.example.medix.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllDiagnosticTestActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout empty_view;
    private RecyclerView diagnosticRecyclerView;
    private FloatingActionButton fab;
    List<Test> testList;
    private AllDiagnosticTestAdapter adapter;
    private EditText searchInput;
    CharSequence search = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_diagnostic_test);

        // Change status bar color
        changeStatusBarColor("#00574B");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Diagnostic Test");

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

        // load all test data
        getAllDiagnosticTest();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AllDiagnosticTestActivity.this, CartActivity.class));
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

    private void getAllDiagnosticTest() {

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

        final android.app.AlertDialog waitingDialog = new SpotsDialog(AllDiagnosticTestActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");
        // Removed to Wish List
        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedix().create(ApiService.class);
        Call<Result> resultCall = service.getAllDiagnosticTest();

        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                testList = response.body().getAllDiagnosticTest();
                if (testList.size() < 0) {
                    Toast.makeText(getApplicationContext(), "Empty Data", Toast.LENGTH_SHORT).show();
                    waitingDialog.dismiss();
                } else {
                    adapter = new AllDiagnosticTestAdapter(testList, AllDiagnosticTestActivity.this);
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
        getAllDiagnosticTest();
    }

    void filter(String text) {
        List<Test> temp = new ArrayList();
        for (Test d : testList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.getDiagnostic_center_name().toLowerCase().contains(text.toLowerCase())
                    || d.getName().toLowerCase().contains(text.toLowerCase())
                    || d.getAddress().toLowerCase().contains(text.toLowerCase())
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
        getAllDiagnosticTest();
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
