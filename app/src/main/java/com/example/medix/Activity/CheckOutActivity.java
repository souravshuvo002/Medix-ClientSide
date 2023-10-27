package com.example.medix.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.medix.Adapter.CheckOutAdapter;
import com.example.medix.Api.ApiClient;
import com.example.medix.Api.ApiService;
import com.example.medix.Api.ApiURL;
import com.example.medix.Api.IFCMService;
import com.example.medix.Common.Common;
import com.example.medix.Database.Database;
import com.example.medix.Model.Cart;
import com.example.medix.Model.DataMessage;
import com.example.medix.Model.MyResponse;
import com.example.medix.Model.Result;
import com.example.medix.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CheckOutActivity extends AppCompatActivity {

    private static final String TAG = "CheckOutActivity";
    TextView textViewContactEdit, textViewPaymentEdit, textViewItemEdit,
            textViewCouponEdit, textViewSubAmount, textViewOtherAddress, textViewPromoEnter;
    private TextView textViewReportCenterNames;
    ImageView imageViewBack;
    public Button btn_confirm, btn_back;
    public TextView textViewDate, textViewBillTotal;
    List<Cart> cartCart = new ArrayList<>();
    List<Cart> distinctElements = new ArrayList<>();
    CheckOutAdapter adapter;
    RecyclerView recyclerViewCart;
    private double SUB_TOTAL;
    private String GEN_CUSTOMER_ID;
    private EditText editTextCustomerName, editTextCustomerEmail,
            editTextCustomerAddress, editTextCustomerPhone;
    private EditText editTextPromoCode;
    private Button buttonApplyCode;

    private Spinner spinner_country, spinner_state_region;

    private LinearLayout layCouponInfo2;
    private LinearLayout linearLayContactExpand, layoutPromo;
    private LinearLayout linearLayCODPayment, linearLayBkashPayment, linearLayRocketPayment, linearLayTrxID;
    private EditText editTextTrxID;
    private TextView textViewBkashNumber, textViewRocketNumber;

    private RadioGroup radioPaymentType, radioReportType;
    private LinearLayout linearLayCoupon, linearLayHomeDelivery;
    private TextView text_coupon_rate, text_Total, textViewCouponTitle, text_delivery_rate;
    private String payment = "Cash On Delivery";
    private String reportType = "";
    private String centerNames = "";

    private int book_id;

    private int radioIndex = 0;
    double exactCatTotal = 0.00;
    double total = 0.00;
    android.app.AlertDialog waitingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        // Change status bar color
        changeStatusBarColor("#008577");

        initViews();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CheckOutActivity.this);
        GEN_CUSTOMER_ID = sharedPreferences.getString("CUSTOMER_ID", null);


        cartCart = new Database(this).getCarts();
                // Get distinct objects by key
        distinctElements = cartCart.stream()
                .filter(distinctByKey(p -> p.getCenter_id()))
                .collect(Collectors.toList());
        for (int i=0;i<distinctElements.size();i++)
        {
            centerNames = centerNames + distinctElements.get(i).getCenter_name();
            if(i!=distinctElements.size() - 1)
            {
                centerNames = centerNames + ", ";
            }
            else
            {
                centerNames = centerNames + ".";
            }
        }
        textViewReportCenterNames.setText(centerNames);

        textViewContactEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linearLayContactExpand.getVisibility() == View.VISIBLE) {
                    textViewContactEdit.setText("Show");
                    linearLayContactExpand.setVisibility(View.GONE);
                } else {
                    textViewContactEdit.setText("Hide");
                    linearLayContactExpand.setVisibility(View.VISIBLE);
                }
            }
        });

        textViewCouponEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutPromo.getVisibility() == View.VISIBLE) {
                    textViewCouponEdit.setText("Show");
                    layoutPromo.setVisibility(View.GONE);
                } else {
                    textViewCouponEdit.setText("Hide");
                    layoutPromo.setVisibility(View.VISIBLE);
                }
            }
        });

        textViewItemEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CheckOutActivity.this, CartActivity.class));
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeOrder();
            }
        });

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CheckOutActivity.this, MainActivity.class));
                finish();
            }
        });

        radioReportType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                RadioButton rb = (RadioButton)findViewById(checkedId);
                int index = radioReportType.indexOfChild(rb);
                radioIndex = index;
                switch (index) {
                    case 0: // first button - Yes
                        reportType = "Yes";
                        getDateAndTotalBill();
                        loadData();
                        break;
                    case 1: // second button - NO
                        reportType = "NO";
                        getDateAndTotalBill();
                        loadData();
                        break;
                }
            }
        });

        getDateAndTotalBill();
        loadData();
    }

    private void initViews() {
        // getting views
        textViewDate = (TextView) findViewById(R.id.textViewDate);
        textViewBillTotal = (TextView) findViewById(R.id.textViewTotalBill);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_back = (Button) findViewById(R.id.btn_back);

        editTextCustomerName = (EditText) findViewById(R.id.editTextCustomerName);
        editTextCustomerEmail = (EditText) findViewById(R.id.editTextCustomerEmail);
        editTextCustomerPhone = (EditText) findViewById(R.id.editTextCustomerPhone);
        editTextCustomerAddress = (EditText) findViewById(R.id.editTextCustomerAddress);

        textViewPromoEnter = (TextView) findViewById(R.id.textViewPromoEnter);

        editTextPromoCode = (EditText) findViewById(R.id.editTextPromoCode);
        buttonApplyCode = (Button) findViewById(R.id.buttonApplyCode);

        textViewContactEdit = (TextView) findViewById(R.id.textViewContactEdit);
        textViewPaymentEdit = (TextView) findViewById(R.id.textViewPaymentEdit);
        textViewItemEdit = (TextView) findViewById(R.id.textViewItemEdit);
        textViewCouponEdit = (TextView) findViewById(R.id.textViewCouponEdit);
        textViewSubAmount = (TextView) findViewById(R.id.text_sub_amount);
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

        textViewReportCenterNames = (TextView) findViewById(R.id.textViewReportCenterNames);

        layCouponInfo2 = (LinearLayout) findViewById(R.id.layCouponInfo2);

        linearLayContactExpand = (LinearLayout) findViewById(R.id.linearLayContactExpand);
        layoutPromo = (LinearLayout) findViewById(R.id.layoutPromo);

        /*linearLayCODPayment = (LinearLayout) findViewById(R.id.linearLayCODPayment);
        linearLayBkashPayment = (LinearLayout) findViewById(R.id.linearLayBkashPayment);
        linearLayRocketPayment = (LinearLayout) findViewById(R.id.linearLayRocketPayment);
        linearLayTrxID = (LinearLayout) findViewById(R.id.linearLayTrxID);
        editTextTrxID = (EditText) findViewById(R.id.editTextTrxID);
        textViewBkashNumber = (TextView) findViewById(R.id.textViewBkashNumber);
        textViewRocketNumber = (TextView) findViewById(R.id.textViewRocketNumber);*/

        radioPaymentType = (RadioGroup) findViewById(R.id.radioPaymentType);
        radioReportType = (RadioGroup) findViewById(R.id.radioReportType);

        linearLayCoupon = (LinearLayout) findViewById(R.id.linearLayCoupon);
        linearLayHomeDelivery = (LinearLayout) findViewById(R.id.linearLayHomeDelivery);
        text_coupon_rate = (TextView) findViewById(R.id.text_coupon_rate);
        text_delivery_rate = (TextView) findViewById(R.id.text_delivery_rate);
        text_Total = (TextView) findViewById(R.id.text_Total);
        textViewCouponTitle = (TextView) findViewById(R.id.textViewCouponTitle);

        recyclerViewCart = (RecyclerView) findViewById(R.id.recyclerViewCart);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewCart.setHasFixedSize(true);
        recyclerViewCart.setItemAnimator(new DefaultItemAnimator());
        recyclerViewCart.addItemDecoration(
                new DividerItemDecoration(CheckOutActivity.this, LinearLayoutManager.VERTICAL) {
                    @Override
                    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                        int position = parent.getChildAdapterPosition(view);
                        // hide the divider for the last child
                        if (position == parent.getAdapter().getItemCount() - 1) {
                            outRect.setEmpty();
                        } else {
                            super.getItemOffsets(outRect, view, parent, state);
                        }
                    }
                }
        );
    }

    private void placeOrder() {

        String customerName = editTextCustomerName.getText().toString();
        String customerEmail = editTextCustomerEmail.getText().toString();
        String customerPhone = editTextCustomerPhone.getText().toString();
        String customerAddress = editTextCustomerAddress.getText().toString();

        if (TextUtils.isEmpty(editTextCustomerName.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Information field can't be empty!", Toast.LENGTH_SHORT).show();
            editTextCustomerName.setError("Filed is mandatory");
            return;
        }
        if (TextUtils.isEmpty(editTextCustomerEmail.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Information field can't be empty!", Toast.LENGTH_SHORT).show();
            editTextCustomerEmail.setError("Filed is mandatory");
            return;
        }
        if (TextUtils.isEmpty(editTextCustomerPhone.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Information field can't be empty!", Toast.LENGTH_SHORT).show();
            editTextCustomerPhone.setError("Filed is mandatory");
            return;
        }
        if (TextUtils.isEmpty(editTextCustomerAddress.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Information field can't be empty!", Toast.LENGTH_SHORT).show();
            editTextCustomerAddress.setError("Filed is mandatory");
            return;
        }


        // Calculate Total Price
        double sub_total = 0.0, total = 0.00;
        for (Cart cart : cartCart) {
            sub_total += (Double.parseDouble(cart.getPrice())) * (Double.parseDouble(cart.getQuantity()));
        }

        if(reportType.equalsIgnoreCase("Yes"))
        {
            linearLayHomeDelivery.setVisibility(View.VISIBLE);
            text_delivery_rate.setText(String.valueOf(Common.report_charge * (double)distinctElements.size()));
            sub_total = sub_total + (Common.report_charge * distinctElements.size());
            total = sub_total;

        }
        else
        {
            linearLayHomeDelivery.setVisibility(View.GONE);
            total = sub_total;
        }

        /*if (isCodeApplied) {
            total = total + Common.SHIIPING_CHARGE - Double.parseDouble(discountPrice);
        } else {
            total = total + Common.SHIIPING_CHARGE;
        }*/


        final android.app.AlertDialog waitingDialog = new SpotsDialog(CheckOutActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Placing ...");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CheckOutActivity.this);
        final String CUSTOMER_ID = sharedPreferences.getString("CUSTOMER_ID", null);

        // First add to oc_order
        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedix().create(ApiService.class);
        Call<Result> call = service.addBookTest("Medix", CUSTOMER_ID, customerName, customerEmail,
                customerPhone, customerAddress, "Cash on delivery", String.valueOf(total), "1");

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                waitingDialog.dismiss();
                if (!response.body().getError()) {
                    getBookID(CUSTOMER_ID);
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getBookID(String customer_id) {
        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedix().create(ApiService.class);
        Call<Result> call = service.getLastInsertedID(customer_id);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                book_id = response.body().getLastBookID().getTest_book_id();
                //Toast.makeText(getApplicationContext(), "Book ID: " + String.valueOf(book_id), Toast.LENGTH_LONG).show();
                //getOrderToTotal(SUB_TOTAL, order_id);
                postBookingTestToCenter(book_id);

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postBookingTestToCenter(int book_id) {
        // Delaying action for 1 second
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int count = new Database(getApplicationContext()).getCarts().size();
                for (int i = 0; i < count; i++) {

                    Date d = new Date();
                    CharSequence s = android.text.format.DateFormat.format("yy-MM-dd hh-mm-ss", d.getTime());

                    // Calculate Total Price
                    double total = 0.0;
                    total += (Double.parseDouble(cartCart.get(i).getPrice())) * (Double.parseDouble(cartCart.get(i).getQuantity()));

                    //Defining retrofit api service
                    ApiService service = ApiClient.getClientMedix().create(ApiService.class);
                    //defining the call
                    Call<Result> call = service.addTestDetailsToCenter(
                            String.valueOf(book_id),
                            cartCart.get(i).getCenter_id(),
                            cartCart.get(i).getTest_id(),
                            GEN_CUSTOMER_ID,
                            cartCart.get(i).getQuantity(),
                            cartCart.get(i).getPrice(),
                            String.valueOf(total),
                            "1",
                            s.toString()
                    );
                    //calling the api
                    int finalI = i;
                    call.enqueue(new Callback<Result>() {
                        @Override
                        public void onResponse(Call<Result> call, Response<Result> response) {
                            Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                            sentNotificationToServer(String.valueOf(book_id), cartCart.get(finalI).getCenter_id());
                        }

                        @Override
                        public void onFailure(Call<Result> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }

                if(reportType.equalsIgnoreCase("Yes"))
                {
                    addHomeDeliveryDetails();
                }
                else
                {
                    // Remove All cart items
                    new Database(getApplicationContext()).removeAllCartItems();
                    //sentNotificationToServer(id_order, total_price, address);
                    //showOrderDialog(id_order, total_price,address, myPreferences.getString("NAME", null));
                    //Toast.makeText(getApplicationContext(), "Order Placed", Toast.LENGTH_LONG).show();
                    //listener.addView();
                    //loadCartItems();
                    startActivity(new Intent(CheckOutActivity.this, MainActivity.class));
                }


            }
        }, 1000);
    }

    private void addHomeDeliveryDetails() {

        for (int i=0;i<distinctElements.size();i++)
        {
            //Defining retrofit api service
            ApiService service = ApiClient.getClientMedix().create(ApiService.class);
            Call<Result> call = service.addCenterHomeDelivery(String.valueOf(book_id),distinctElements.get(i).getCenter_id(), String.valueOf(Common.report_charge));

            call.enqueue(new Callback<Result>() {
                @Override
                public void onResponse(Call<Result> call, Response<Result> response) {
                    // Remove All cart items
                    new Database(getApplicationContext()).removeAllCartItems();
                    //sentNotificationToServer(id_order, total_price, address);
                    //showOrderDialog(id_order, total_price,address, myPreferences.getString("NAME", null));
                    //Toast.makeText(getApplicationContext(), "Order Placed", Toast.LENGTH_LONG).show();
                    //listener.addView();
                    //loadCartItems();
                    startActivity(new Intent(CheckOutActivity.this, MainActivity.class));
                }
                @Override
                public void onFailure(Call<Result> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getDateAndTotalBill() {

        Date cDate = new Date();
        String currentDate = new SimpleDateFormat("MMM dd, yyyy").format(cDate);

        // Calculate Total Price
        double sub_total = 0.0;
        for (Cart cart : cartCart) {
            sub_total += (Double.parseDouble(cart.getPrice())) * (Double.parseDouble(cart.getQuantity()));
        }
        if(reportType.equalsIgnoreCase("Yes"))
        {
            sub_total = sub_total + (Common.report_charge * distinctElements.size());
        }

        textViewBillTotal.setText(getResources().getString(R.string.currency_sign) + String.format("%.2f", sub_total));
        textViewDate.setText(currentDate);
    }

    private void loadData() {

        // load cart Items
        cartCart = new Database(this).getCarts();
        adapter = new CheckOutAdapter(cartCart, this);
        recyclerViewCart.setAdapter(adapter);

        // Calculate Total Price
        double sub_total = 0.0, total = 0.00;
        for (Cart cart : cartCart) {
            sub_total += (Double.parseDouble(cart.getPrice())) * (Double.parseDouble(cart.getQuantity()));
        }

        if(reportType.equalsIgnoreCase("Yes"))
        {
            linearLayHomeDelivery.setVisibility(View.VISIBLE);
            text_delivery_rate.setText(getResources().getString(R.string.currency_sign) + "+" + String.format("%.2f", Common.report_charge * (double)distinctElements.size()));
            total = sub_total + (Common.report_charge * (double)distinctElements.size());
        }
        else
        {
            linearLayHomeDelivery.setVisibility(View.GONE);
            total = sub_total;
        }

        Log.d("TOTAL_PRICE: ", String.valueOf(total));


        textViewSubAmount.setText(getResources().getString(R.string.currency_sign) + String.format("%.2f", sub_total));
        text_Total.setText(getResources().getString(R.string.currency_sign) + String.format("%.2f", total));

    }

    private void changeStatusBarColor(String color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }

    private void sentNotificationToServer(final String book_id, final String center_id) {

        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiURL.MEDIX_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Defining retrofit api service
        ApiService service = retrofit.create(ApiService.class);

        //defining the call
        Call<Result> call = service.getDiagnosticToken(center_id);
        //calling the api
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                // When we have token, then we just sent notification to this token
                Map<String, String> contentSend = new HashMap<>();
                contentSend.put("title", "New Order");
                contentSend.put("message", "You have new Order: " + book_id);

                DataMessage dataMessage = new DataMessage();
                if(response.body().getDiagnosticToken().getToken() != null)
                {
                    dataMessage.setTo(response.body().getDiagnosticToken().getToken());
                }
                dataMessage.setData(contentSend);
                IFCMService ifcmService = Common.getFCMService();
                ifcmService.sendNotification(dataMessage)
                        .enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                if(response.code() == 200)
                                {
                                    if(response.body().success == 1)
                                    {
                                        Toast.makeText(CheckOutActivity.this, "Order Submitted", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(CheckOutActivity.this, "Send Notification failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {

                            }
                        });
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.d("Msg: ", "CheckOut");
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    //Utility function
    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor)
    {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

}
