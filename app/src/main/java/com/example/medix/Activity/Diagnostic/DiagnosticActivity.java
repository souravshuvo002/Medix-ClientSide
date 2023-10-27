package com.example.medix.Activity.Diagnostic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medix.Activity.AllDiagnosticTestActivity;
import com.example.medix.Adapter.AllDiagnosticTestAdapter;
import com.example.medix.Api.ApiClient;
import com.example.medix.Api.ApiService;
import com.example.medix.Model.Result;
import com.example.medix.Model.Test;
import com.example.medix.R;

import java.util.List;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiagnosticActivity extends AppCompatActivity {

    private LinearLayout linearLayDiagnosticCenter, linearLayDiagnosticTest, linearLayoutTest;
    private TextView tvSeeAll;
    private RecyclerView recycler_view_test;
    private List<Test> testList;
    private AllDiagnosticTestAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnostic);

        // Change status bar color
        changeStatusBarColor("#00574B");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Diagnostic");

        // getting views
        linearLayDiagnosticCenter = (LinearLayout) findViewById(R.id.linearLayDiagnosticCenter);
        linearLayDiagnosticTest = (LinearLayout) findViewById(R.id.linearLayDiagnosticTest);
        linearLayoutTest = (LinearLayout) findViewById(R.id.lay_labTest);
        tvSeeAll = (TextView) findViewById(R.id.tvSeeAll);
        recycler_view_test = (RecyclerView) findViewById(R.id.recycler_view_test);
        recycler_view_test.setHasFixedSize(false);
        recycler_view_test.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recycler_view_test.setItemAnimator(new DefaultItemAnimator());


        linearLayDiagnosticCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DiagnosticActivity.this, DiagnosticsListActivity.class));
            }
        });

        linearLayDiagnosticTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DiagnosticActivity.this, AllDiagnosticTestActivity.class));
            }
        });

        tvSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DiagnosticActivity.this, AllDiagnosticTestActivity.class));
            }
        });

        // get lab test items
        getTestItemLimit();
    }

    private void getTestItemLimit() {

        //swipeRefreshLayout.setRefreshing(false);

        final android.app.AlertDialog waitingDialog = new SpotsDialog(DiagnosticActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");
        // Removed to Wish List
        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedix().create(ApiService.class);
        Call<Result> resultCall = service.getAllDiagnosticTestLimit();

        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                testList = response.body().getAllDiagnosticTest();
                if (testList.size() < 0) {
                    Toast.makeText(getApplicationContext(), "Empty Data", Toast.LENGTH_SHORT).show();
                    waitingDialog.dismiss();
                } else {
                    adapter = new AllDiagnosticTestAdapter(testList, DiagnosticActivity.this);
                    recycler_view_test.setAdapter(adapter);
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
        getTestItemLimit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getTestItemLimit();
    }
}
