package com.example.medix.Activity.Diagnostic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.medix.Adapter.UploadPrescriptionAdapter;
import com.example.medix.Api.ApiClient;
import com.example.medix.Api.ApiService;
import com.example.medix.Model.Prescription;
import com.example.medix.Model.Result;
import com.example.medix.R;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllUploadPrescriptionDetailsActivity extends AppCompatActivity  implements SwipeRefreshLayout.OnRefreshListener{

    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout empty_view;
    private RecyclerView recyclerViewAllPrescription;
    List<Prescription> prescriptionList;
    private UploadPrescriptionAdapter adapter;
    private EditText searchInput;
    CharSequence search = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_upload_prescription_details);

        // Change status bar color
        changeStatusBarColor("#00574B");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Prescription details");

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

        recyclerViewAllPrescription = (RecyclerView) findViewById(R.id.recyclerViewAllPrescription);
        recyclerViewAllPrescription.setHasFixedSize(false);
        recyclerViewAllPrescription.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewAllPrescription.setItemAnimator(new DefaultItemAnimator());

        // load diagnostic data
        getAllPrescription();

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


    private void getAllPrescription() {
        swipeRefreshLayout.setRefreshing(false);

        String CUSTOMER_ID;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AllUploadPrescriptionDetailsActivity.this);
        CUSTOMER_ID = sharedPreferences.getString("CUSTOMER_ID", null);

        final android.app.AlertDialog waitingDialog = new SpotsDialog(AllUploadPrescriptionDetailsActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");
        // Removed to Wish List
        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedix().create(ApiService.class);
        Call<Result> resultCall = service.getPrescriptionByCustomerID(CUSTOMER_ID);

        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                prescriptionList = response.body().getAllUploadedPrescription();
                if (prescriptionList.size() < 0) {
                    Toast.makeText(getApplicationContext(), "Empty Data", Toast.LENGTH_SHORT).show();
                    waitingDialog.dismiss();
                } else {
                    adapter = new UploadPrescriptionAdapter(prescriptionList, AllUploadPrescriptionDetailsActivity.this);
                    recyclerViewAllPrescription.setAdapter(adapter);
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

    void filter(String text) {
        List<Prescription> temp = new ArrayList();
        for (Prescription d : prescriptionList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.getCustomer_name().toLowerCase().contains(text.toLowerCase())
                    || d.getCustomer_phone().toLowerCase().contains(text.toLowerCase())
                    || d.getNote().toLowerCase().contains(text.toLowerCase())
                    || d.getDate_added().toLowerCase().contains(text.toLowerCase())) {
                temp.add(d);
            }
        }
        //update recyclerview
        adapter.updateList(temp);
    }

    @Override
    public void onRefresh() {
        searchInput.setText("");
        search = "";
        getAllPrescription();
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
