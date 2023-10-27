package com.example.medix.Activity;

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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medix.Adapter.SingleBookStatusAdapter;
import com.example.medix.Api.ApiClient;
import com.example.medix.Api.ApiService;
import com.example.medix.Model.Result;
import com.example.medix.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.JELLY_BEAN;

public class SingleBookStatusActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    RelativeLayout relativeLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerViewOrderItems;
    TextView textViewOrderID, textViewOrderDate, textViewTotal_items, textViewItems_Price, textViewAddress;
    ImageView imageViewBackButton;
    public SingleBookStatusAdapter adapter;

    public int order_id;

    public double test_total_price = 0.00, total_price = 0.00;

    android.app.AlertDialog waitingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_book_status);

        order_id = getIntent().getIntExtra("BOOK_ID", 0);

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
        swipeRefreshLayout.setOnRefreshListener(SingleBookStatusActivity.this);
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

        waitingDialog = new SpotsDialog(SingleBookStatusActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Pleas wait ...");
    }

    private void loadOrderDetails(final int order_id) {

        swipeRefreshLayout.setRefreshing(true);

        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedix().create(ApiService.class);
        Call<Result> call = service.getBookDetails(java.lang.String.valueOf(order_id));

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                relativeLayout.setVisibility(View.VISIBLE);

                textViewOrderID.setText("Book No: " + order_id);
                //Date
                String strCurrentDate = response.body().getBookDetails().getDate_added();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date newDate = null;
                try {
                    newDate = format.parse(strCurrentDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                format = new SimpleDateFormat("MMM dd, yyyy hh:mm");
                String date = format.format(newDate);
                textViewOrderDate.setText("Order date: " + date);
                total_price = Double.parseDouble(response.body().getBookDetails().getTotal());
                Log.d("Total: ", String.valueOf(total_price));
                textViewItems_Price.setText(getResources().getString(R.string.currency_sign)+ response.body().getBookDetails().getTotal());
                textViewAddress.setText("Billing Address\n" + response.body().getBookDetails().getCustomer_name() + "\n" + response.body().getBookDetails().getCustomer_address() + "\n" + response.body().getBookDetails().getCustomer_phone());
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

        String CUSTOMER_ID;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SingleBookStatusActivity.this);
        CUSTOMER_ID = sharedPreferences.getString("CUSTOMER_ID", null);

        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedix().create(ApiService.class);
        Call<Result> call = service.getTestBookDetails(String.valueOf(order_id), CUSTOMER_ID);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                test_total_price = 0.00;

                for (int i=0;i<response.body().getAllbookTestDetails().size();i++)
                {
                    test_total_price += Double.parseDouble(response.body().getAllbookTestDetails().get(i).getTotal_price());
                }

                Log.d("Total: ", String.valueOf(total_price));
                Log.d("Test_total", String.valueOf(test_total_price));

                if (response.body().getAllbookTestDetails().size() > 1) {
                    if(total_price > test_total_price)
                    {
                        textViewTotal_items.setText(response.body().getAllbookTestDetails().size() + " test items with home delivery");
                    }
                    else
                    {
                        textViewTotal_items.setText(response.body().getAllbookTestDetails().size() + " test items without home delivery");
                    }
                } else {
                    if(total_price > test_total_price)
                    {
                        textViewTotal_items.setText(response.body().getAllbookTestDetails().size() + " test item with home delivery");
                    }
                    else
                    {
                        textViewTotal_items.setText(response.body().getAllbookTestDetails().size() + " test item without home delivery");
                    }
                }

                adapter = new SingleBookStatusAdapter(response.body().getAllbookTestDetails(), SingleBookStatusActivity.this);
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
