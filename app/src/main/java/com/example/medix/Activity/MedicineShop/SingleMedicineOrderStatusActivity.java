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

import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medix.Adapter.SingleMedicineOrderStatusAdapter;
import com.example.medix.Api.ApiClient;
import com.example.medix.Api.ApiService;
import com.example.medix.Helper.NetWorkConfig;
import com.example.medix.Model.Result;
import com.example.medix.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.JELLY_BEAN;

public class SingleMedicineOrderStatusActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    RelativeLayout relativeLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerViewOrderItems;
    TextView textViewOrderID, textViewOrderDate, textViewTotal_items, textViewItems_Price, textViewAddress;
    ImageView imageViewBackButton;
    private NetWorkConfig netWorkConfig;
    public SingleMedicineOrderStatusAdapter adapter;

    public int order_id;

    android.app.AlertDialog waitingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_medicine_order_status);

        netWorkConfig = new NetWorkConfig(this);

        /**
         *  Network Connection Check
         */
        if (!netWorkConfig.isNetworkAvailable()) {
            netWorkConfig.createNetErrorDialog();
            return;
        }

        order_id = getIntent().getIntExtra("Order_ID", 0);
        Log.d("ORDER_ID: ", String.valueOf(order_id));

        if (SDK_INT >= JELLY_BEAN) {
            enableChangingTransition();
        }

        // getting views
        relativeLayout = (RelativeLayout) findViewById(R.id.LayMain);
        imageViewBackButton = (ImageView) findViewById(R.id.imageViewBack);
        textViewOrderID = (TextView) findViewById(R.id.textViewOrderID);
        textViewOrderDate = (TextView) findViewById(R.id.textViewOrderDate);
        textViewTotal_items = (TextView) findViewById(R.id.textViewTotalItems);
        textViewItems_Price = (TextView) findViewById(R.id.textViewItems_Price);
        textViewAddress = (TextView) findViewById(R.id.textViewAddress);
        recyclerViewOrderItems = (RecyclerView) findViewById(R.id.recycler_view_OrderItems);
        recyclerViewOrderItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewOrderItems.setHasFixedSize(true);
        recyclerViewOrderItems.setItemAnimator(new DefaultItemAnimator());
        recyclerViewOrderItems.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(SingleMedicineOrderStatusActivity.this);
        // show loader and fetch messages
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        //load Order details
                        loadOrderDetails(order_id);
                        //load Order items
                        loadOrderItems(order_id);
                    }
                }
        );

        imageViewBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //load Order details
        loadOrderDetails(order_id);

        //load Order items
        loadOrderItems(order_id);

        waitingDialog = new SpotsDialog(SingleMedicineOrderStatusActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Pleas wait ...");
    }

    private void loadOrderDetails(final int order_id) {

        swipeRefreshLayout.setRefreshing(true);

        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
        Call<Result> call = service.getOrderDetails(java.lang.String.valueOf(order_id));

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                relativeLayout.setVisibility(View.VISIBLE);

                textViewOrderID.setText("Order No: " + order_id);
                //Date
                String strCurrentDate = response.body().getOrderDetailsList().getDate_added();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date newDate = null;
                try {
                    newDate = format.parse(strCurrentDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                format = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
                String date = format.format(newDate);
                textViewOrderDate.setText(date);

                textViewItems_Price.setText(getResources().getString(R.string.currency_sign)+ response.body().getOrderDetailsList().getTotal());
                textViewAddress.setText("Billing Address\n" + response.body().getOrderDetailsList().getShipping_firstname() + " " + response.body().getOrderDetailsList().getShipping_lastname() + "\n" + response.body().getOrderDetailsList().getShipping_address_1() + " " + response.body().getOrderDetailsList().getShipping_city() + " " + response.body().getOrderDetailsList().getShipping_country() + "\n" + response.body().getOrderDetailsList().getTelephone());
                swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void loadOrderItems(int order_id) {

        swipeRefreshLayout.setRefreshing(true);

        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
        Call<Result> call = service.getProductOrderDetails(String.valueOf(order_id));

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                if (response.body().getOrderProductDetails().size() > 1) {
                    textViewTotal_items.setText(response.body().getOrderProductDetails().size() + " items");
                } else {
                    textViewTotal_items.setText(response.body().getOrderProductDetails().size() + " item");
                }

                adapter = new SingleMedicineOrderStatusAdapter(response.body().getOrderProductDetails(), SingleMedicineOrderStatusActivity.this);
                recyclerViewOrderItems.setAdapter(adapter);

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

    @TargetApi(JELLY_BEAN)
    private void enableChangingTransition() {
        ViewGroup animatedRoot = (ViewGroup) findViewById(R.id.animated_root);
        animatedRoot.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    }

    @Override
    public void onRefresh() {
        //load Order details
        loadOrderDetails(order_id);
        //load Order items
        loadOrderItems(order_id);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
