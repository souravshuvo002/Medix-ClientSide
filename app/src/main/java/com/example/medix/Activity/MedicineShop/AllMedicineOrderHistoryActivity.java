package com.example.medix.Activity.MedicineShop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.medix.Adapter.AllMedicineOrderHistoryAdapter;
import com.example.medix.Api.ApiClient;
import com.example.medix.Api.ApiService;
import com.example.medix.Helper.NetWorkConfig;
import com.example.medix.Model.Result;
import com.example.medix.R;

public class AllMedicineOrderHistoryActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public ImageView imageViewBackButton;
    public RecyclerView recyclerViewOrderStatus;
    public AllMedicineOrderHistoryAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private NetWorkConfig netWorkConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_medicine_order_history);

        netWorkConfig = new NetWorkConfig(this);

        /**
         *  Network Connection Check
         */
        if (!netWorkConfig.isNetworkAvailable()) {
            netWorkConfig.createNetErrorDialog();
            return;
        }

        // getting views
        imageViewBackButton = (ImageView) findViewById(R.id.imageViewBack);
        recyclerViewOrderStatus = (RecyclerView) findViewById(R.id.recycler_view_OrderStatus);
        recyclerViewOrderStatus.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewOrderStatus.setHasFixedSize(true);
        recyclerViewOrderStatus.setItemAnimator(new DefaultItemAnimator());
        recyclerViewOrderStatus.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(AllMedicineOrderHistoryActivity.this);
        // show loader and fetch messages
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        loadOrderStatusItems();
                    }
                }
        );

        imageViewBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // Load Cart Status Items from Database
        loadOrderStatusItems();
    }

    private void loadOrderStatusItems() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AllMedicineOrderHistoryActivity.this);
        String CUSTOMER_ID = sharedPreferences.getString("CUSTOMER_ID", null);

        swipeRefreshLayout.setRefreshing(true);
        final android.app.AlertDialog waitingDialog = new SpotsDialog(AllMedicineOrderHistoryActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");

        //Defining retrofit api service
        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
        Call<Result> call = service.getAllOrderForCustomer(CUSTOMER_ID);
        //Call<Result> call = service.getAllOrder(myPreferences.getString("PHONE", null));

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.body().getAllOrder().size() < 0) {
                    Toast.makeText(getApplicationContext(), "No Orders", Toast.LENGTH_SHORT).show();
                    finish();
                }
                adapter = new AllMedicineOrderHistoryAdapter(response.body().getAllOrder(), getApplicationContext());
                recyclerViewOrderStatus.setAdapter(adapter);
                waitingDialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onRefresh() {
        loadOrderStatusItems();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
