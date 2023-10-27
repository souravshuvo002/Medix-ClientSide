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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.appmsg.AppMsg;
import com.example.medix.Adapter.MedicineOrderAdapterActivity;
import com.example.medix.Database.DatabaseMedicine;
import com.example.medix.Helper.NetWorkConfig;
import com.example.medix.Model.Cart;
import com.example.medix.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.JELLY_BEAN;
import static android.widget.Toast.LENGTH_SHORT;
import static com.devspark.appmsg.AppMsg.STYLE_ALERT;

public class MedicineCartActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    public RecyclerView recyclerViewCart;
    public MedicineOrderAdapterActivity adapter;
    public Button buttonPlaceOrder;
    //public LinearLayout layRecyclerView;
    public RelativeLayout layEmpty, relLay_1;
    public Button buttonContinue;
    public TextView textSubTotal;
    public ImageView imageViewRemoveAll, imageViewBackButton;
    SharedPreferences myPreferences;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NetWorkConfig netWorkConfig;

    List<Cart> cartCart = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_cart);

        netWorkConfig = new NetWorkConfig(this);

        if (SDK_INT >= JELLY_BEAN) {
            enableChangingTransition();
        }

        myPreferences = getSharedPreferences("MyPref", MODE_PRIVATE);

        // getting views
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().
                getColor(android.R.color.holo_blue_dark), getResources().
                getColor(android.R.color.holo_red_dark), getResources().
                getColor(android.R.color.holo_green_light), getResources().
                getColor(android.R.color.holo_orange_dark));
        buttonPlaceOrder = (Button) findViewById(R.id.btn_placeOrder);
        textSubTotal = (TextView) findViewById(R.id.textSubTotal);
        recyclerViewCart = (RecyclerView) findViewById(R.id.recycler_view_Cart);
        //layRecyclerView = (LinearLayout) findViewById(R.id.layRecyclerView);
        layEmpty = (RelativeLayout) findViewById(R.id.layEmpty);
        relLay_1 = (RelativeLayout) findViewById(R.id.relLay_1);
        //linearLayoutTotal = (LinearLayout) findViewById(R.id.LayTotal);
        imageViewBackButton = (ImageView) findViewById(R.id.imageViewBack);
        imageViewRemoveAll = (ImageView) findViewById(R.id.imageViewRemoveAll);
        buttonContinue = (Button) findViewById(R.id.btn_cart);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewCart.setHasFixedSize(true);
        recyclerViewCart.setItemAnimator(new DefaultItemAnimator());
        recyclerViewCart.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


        buttonPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkProductExistence();
                //orderDialog();
                startActivity(new Intent(MedicineCartActivity.this, MedicineCheckoutActivity.class));
                finish();
            }
        });

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onBackPressed();
                startActivity(new Intent(MedicineCartActivity.this, MedicineHomeActivity.class));
                finish();
            }
        });

        imageViewRemoveAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = new DatabaseMedicine(getApplicationContext()).cartItemCount();
                if (count > 0) {
                    removeAllItems();
                } else {
                    AppMsg.makeText(MedicineCartActivity.this, "Cart is empty", STYLE_ALERT)
                            .setAnimation(android.R.anim.fade_in, android.R.anim.fade_out).show();
                }
            }
        });

        imageViewBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        // Load Cart Items from Database
        loadCartItems();

    }

    private void removeAllItems() {

        int items = new DatabaseMedicine(this).cartItemCount();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Remove All from Cart");
        alertDialog.setMessage("Are you sure you want to remove all " + items + " item(s) from your cart?");
        alertDialog.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.setNegativeButton("REMOVE ALL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final android.app.AlertDialog waitingDialog = new SpotsDialog(MedicineCartActivity.this);
                waitingDialog.show();
                waitingDialog.setMessage("Removing ...");

                // Delaying action for 1 second
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        waitingDialog.dismiss();
                        // Remove All cart items
                        new DatabaseMedicine(getApplicationContext()).removeAllCartItems();
                        loadCartItems();
                    }
                }, 1000);
            }
        });
        AlertDialog dialog = alertDialog.create();
        dialog.show();
        Button b = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (b != null) {
            b.setTextColor(Color.parseColor("#000000"));
        }
        Button b2 = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        if (b2 != null) {
            b2.setTextColor(Color.parseColor("#000000"));
        }
    }

    private void loadCartItems() {
        swipeRefreshLayout.setRefreshing(false);

        cartCart = new DatabaseMedicine(this).getCarts();
        if (cartCart.size() <= 0) {
            layEmpty.setVisibility(View.VISIBLE);
            relLay_1.setVisibility(View.GONE);
            //linearLayoutTotal.setVisibility(View.GONE);
            //layRecyclerView.setVisibility(View.GONE);
            buttonPlaceOrder.setVisibility(View.GONE);
            imageViewRemoveAll.setVisibility(View.INVISIBLE);
        } else {
            layEmpty.setVisibility(View.GONE);
            relLay_1.setVisibility(View.VISIBLE);
            //linearLayoutTotal.setVisibility(View.VISIBLE);
            //layRecyclerView.setVisibility(View.VISIBLE);
            buttonPlaceOrder.setVisibility(View.VISIBLE);
            imageViewRemoveAll.setVisibility(View.VISIBLE);
        }

        adapter = new MedicineOrderAdapterActivity(cartCart, MedicineCartActivity.this);
        recyclerViewCart.setAdapter(adapter);

        // Calculate Total Price
        double total = 0.0;
        for (Cart cart : cartCart) {
            total += (Double.parseDouble(cart.getPrice())) * (Double.parseDouble(cart.getQuantity()));
        }
        //textSubTotal.setText("SubTotal: " + String.valueOf(cartCart.size() + " items"));
        textSubTotal.setText(String.valueOf(cartCart.size()) + " items / Total Cost " + getResources().getString(R.string.currency_sign) + String.format("%.2f", total));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadCartItems();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartItems();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @TargetApi(JELLY_BEAN)
    private void enableChangingTransition() {
        ViewGroup animatedRoot = (ViewGroup) findViewById(R.id.animated_root);
        animatedRoot.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    }

    @Override
    public void onRefresh() {

        loadCartItems();
        //getBanner();
    }
}
