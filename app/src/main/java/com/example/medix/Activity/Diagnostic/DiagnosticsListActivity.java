package com.example.medix.Activity.Diagnostic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.medix.Adapter.DiagnosticListAdapter;
import com.example.medix.Api.ApiClient;
import com.example.medix.Api.ApiService;
import com.example.medix.Helper.GridSpacingItemDecoration;
import com.example.medix.Model.Diagnostic;
import com.example.medix.Model.Result;
import com.example.medix.R;

import java.util.ArrayList;
import java.util.List;

public class DiagnosticsListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    public LinearLayout empty_view;
    private RecyclerView diagnosticRecyclerView;
    private DiagnosticListAdapter adapter;
    private List<Diagnostic> list;
    private RecyclerView.LayoutManager layoutManager;
    private EditText searchInput;
    CharSequence search = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnostics_list);
        // Change status bar color
        changeStatusBarColor("#00574B");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Diagnostic Centers");

        //getting views
        searchInput = findViewById(R.id.search_input);
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
        //diagnosticRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        layoutManager = new GridLayoutManager(DiagnosticsListActivity.this, 2);
        diagnosticRecyclerView.setLayoutManager(layoutManager);
        diagnosticRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(0), true));
        diagnosticRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // load diagnostic data
        getAllDiagnostic();

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
        swipeRefreshLayout.setRefreshing(false);

        final android.app.AlertDialog waitingDialog = new SpotsDialog(DiagnosticsListActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");

        // Removed to Wish List
        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedix().create(ApiService.class);
        Call<Result> resultCall = service.getAllDiagnosticCenter();

        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                list = response.body().getAllDiagnosticCenter();
                if (list.size() < 0) {
                    Toast.makeText(getApplicationContext(), "Empty Data", Toast.LENGTH_SHORT).show();
                    waitingDialog.dismiss();
                } else {
                    adapter = new DiagnosticListAdapter(list, DiagnosticsListActivity.this);
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
        /*adapter = new DiagnosticListAdapter(initDiagnostic(), DiagnosticsListActivity.this);
        diagnosticRecyclerView.setAdapter(adapter);
        //recycler_view_cat.addItemDecoration(new SpacesItemDecoration(10));
        //ViewCompat.setNestedScrollingEnabled(diagnosticRecyclerView, false);*/
    }


    @Override
    public void onRefresh() {
        searchInput.setText("");
        search = "";
        getAllDiagnostic();
    }

    void filter(String text) {
        List<Diagnostic> temp = new ArrayList();
        for (Diagnostic d : list) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.getName().toLowerCase().contains(text.toLowerCase())
                    || d.getAddress().toLowerCase().contains(text.toLowerCase())
                    || d.getDiscount().toLowerCase().contains(text.toLowerCase())) {
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

}
