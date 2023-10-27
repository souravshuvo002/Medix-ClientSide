package com.example.medix.Activity.MedicineShop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medix.Adapter.MedicineConfirmCartAdapter;
import com.example.medix.Api.ApiClient;
import com.example.medix.Api.ApiService;
import com.example.medix.Common.Common;
import com.example.medix.Database.DatabaseMedicine;
import com.example.medix.Helper.NetWorkConfig;
import com.example.medix.Model.Cart;
import com.example.medix.Model.Country;
import com.example.medix.Model.Coupon;
import com.example.medix.Model.Result;
import com.example.medix.Model.StateRegion;
import com.example.medix.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedicineCheckoutActivity extends AppCompatActivity {

    private static final String TAG = "ConfirmationCheckoutAct";

    TextView textViewUserName, textViewUserPhone, textViewUserEmail, textViewUserAddress,
            textViewCardHolderName, textViewCardNumber, textViewCardCVC, textViewCardExpiryDate;
    TextView textViewContactEdit, textViewPaymentEdit, textViewItemEdit, textViewCouponEdit, textViewSubAmount, textViewOtherAddress,
            textViewPromoEnter;
    ImageView imageViewCard, imageViewBack;
    public Button btn_confirm, btn_back;
    public TextView textViewDate, textViewBillTotal;
    List<Cart> cartCart = new ArrayList<>();
    List<Country> countryList = new ArrayList<>();
    List<StateRegion> stateRegionList = new ArrayList<>();
    List<Coupon> couponCategoryList = new ArrayList<>();
    MedicineConfirmCartAdapter adapter;
    RecyclerView recyclerViewCart;
    public NetWorkConfig netWorkConfig;

    private String country_id, state_id, countryName, stateName;
    private double SUB_TOTAL;
    private boolean isCodeApplied = false;
    private String discountPrice = "0.0000", CouponCode;
    private String GEN_CUSTOMER_ID;
    private Coupon coupon;
    private EditText editTextFirstName, editTextLastName, editTextCompany,
            editTextAddress1, editTextAddress2, editTextCity, editTextPostCode, editTextPhone;
    private EditText editTextPromoCode;
    private Button buttonApplyCode;

    private Spinner spinner_country, spinner_state_region;

    private LinearLayout layCouponInfo2;
    private LinearLayout linearLayContactExpand, layoutPromo;
    private LinearLayout linearLayCODPayment, linearLayBkashPayment, linearLayRocketPayment, linearLayTrxID;
    private EditText editTextTrxID;
    private TextView textViewBkashNumber, textViewRocketNumber;

    private RadioGroup radioPaymentType;
    private LinearLayout linearLayCoupon;
    private TextView text_coupon_rate, text_Total, textViewCouponTitle;
    private String payment = "Cash On Delivery";

    private int radioIndex = 0;
    double exactCatTotal = 0.00;
    double total = 0.00;
    android.app.AlertDialog waitingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_checkout);
        // Change status bar color
        changeStatusBarColor("#008577");

        netWorkConfig = new NetWorkConfig(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MedicineCheckoutActivity.this);
        GEN_CUSTOMER_ID = sharedPreferences.getString("CUSTOMER_ID", null);

        // getting views
        spinner_country = (Spinner) findViewById(R.id.spinner_country);
        spinner_state_region = (Spinner) findViewById(R.id.spinner_state_region);
        editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
        editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        editTextCompany = (EditText) findViewById(R.id.editTextCompany);
        editTextAddress1 = (EditText) findViewById(R.id.editTextAddress1);
        editTextAddress2 = (EditText) findViewById(R.id.editTextAddress2);
        editTextCity = (EditText) findViewById(R.id.editTextCity);
        editTextPostCode = (EditText) findViewById(R.id.editTextPostCode);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);

        /*textViewUserName = (TextView) findViewById(R.id.textViewUserName);
        textViewUserPhone = (TextView) findViewById(R.id.textViewUserPhone);
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        textViewUserAddress = (TextView) findViewById(R.id.textViewUserAddress);*/
        textViewPromoEnter = (TextView) findViewById(R.id.textViewPromoEnter);

        editTextPromoCode = (EditText) findViewById(R.id.editTextPromoCode);
        buttonApplyCode = (Button) findViewById(R.id.buttonApplyCode);

        textViewContactEdit = (TextView) findViewById(R.id.textViewContactEdit);
        textViewPaymentEdit = (TextView) findViewById(R.id.textViewPaymentEdit);
        textViewItemEdit = (TextView) findViewById(R.id.textViewItemEdit);
        textViewCouponEdit = (TextView) findViewById(R.id.textViewCouponEdit);
        textViewSubAmount = (TextView) findViewById(R.id.text_sub_amount);
        imageViewCard = (ImageView) findViewById(R.id.imageViewCard);
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

        layCouponInfo2 = (LinearLayout) findViewById(R.id.layCouponInfo2);

        linearLayContactExpand = (LinearLayout) findViewById(R.id.linearLayContactExpand);
        layoutPromo = (LinearLayout) findViewById(R.id.layoutPromo);

        linearLayCODPayment = (LinearLayout) findViewById(R.id.linearLayCODPayment);
        linearLayBkashPayment = (LinearLayout) findViewById(R.id.linearLayBkashPayment);
        linearLayRocketPayment = (LinearLayout) findViewById(R.id.linearLayRocketPayment);
        linearLayTrxID = (LinearLayout) findViewById(R.id.linearLayTrxID);
        editTextTrxID = (EditText) findViewById(R.id.editTextTrxID);
        textViewBkashNumber = (TextView) findViewById(R.id.textViewBkashNumber);
        textViewRocketNumber = (TextView) findViewById(R.id.textViewRocketNumber);

        radioPaymentType = (RadioGroup) findViewById(R.id.radioPaymentType);

        linearLayCoupon = (LinearLayout) findViewById(R.id.linearLayCoupon);
        text_coupon_rate = (TextView) findViewById(R.id.text_coupon_rate);
        text_Total = (TextView) findViewById(R.id.text_Total);
        textViewCouponTitle = (TextView) findViewById(R.id.textViewCouponTitle);

        recyclerViewCart = (RecyclerView) findViewById(R.id.recyclerViewCart);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewCart.setHasFixedSize(true);
        recyclerViewCart.setItemAnimator(new DefaultItemAnimator());
        recyclerViewCart.addItemDecoration(
                new DividerItemDecoration(MedicineCheckoutActivity.this, LinearLayoutManager.VERTICAL) {
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
        textViewDate = (TextView) findViewById(R.id.textViewDate);
        textViewBillTotal = (TextView) findViewById(R.id.textViewTotalBill);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_back = (Button) findViewById(R.id.btn_back);

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
                startActivity(new Intent(MedicineCheckoutActivity.this, MedicineCartActivity.class));
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
                startActivity(new Intent(MedicineCheckoutActivity.this, MedicineHomeActivity.class));
                finish();
            }
        });


        buttonApplyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 *  Network Connection Check
                 */
                if (!netWorkConfig.isNetworkAvailable()) {
                    netWorkConfig.createNetErrorDialog();
                    return;
                }

                waitingDialog = new SpotsDialog(MedicineCheckoutActivity.this);
                waitingDialog.show();
                waitingDialog.setMessage("Please wait ...");

                codeAppliedCount = 0;
                exactCatTotal = 0.00;
                total = 0.00;
                discountPrice = "0.0000";
                isCodeApplied = false;

                String code = editTextPromoCode.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    Toast.makeText(getApplicationContext(), "Empty field.", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (Cart cart : cartCart) {
                    total += (Double.parseDouble(cart.getPrice())) * (Double.parseDouble(cart.getQuantity()));
                }

                checkCoupon(code, GEN_CUSTOMER_ID);
            }
        });

        radioPaymentType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                RadioButton rb = (RadioButton)findViewById(checkedId);
                int index = radioPaymentType.indexOfChild(rb);
                radioIndex = index;
                switch (index) {
                    case 0: // first button - Cash On Delivery
                        linearLayCODPayment.setVisibility(View.VISIBLE);
                        linearLayBkashPayment.setVisibility(View.GONE);
                        linearLayRocketPayment.setVisibility(View.GONE);
                        linearLayTrxID.setVisibility(View.GONE);
                        payment = "Cash On Delivery";
                        break;
                    case 1: // second button - Bkash
                        linearLayBkashPayment.setVisibility(View.VISIBLE);
                        textViewBkashNumber.setText("01673057333");
                        linearLayTrxID.setVisibility(View.VISIBLE);
                        linearLayRocketPayment.setVisibility(View.GONE);
                        linearLayCODPayment.setVisibility(View.GONE);
                        payment = "Bkash";
                        break;
                    case 2: // third button = Rocket
                        linearLayRocketPayment.setVisibility(View.VISIBLE);
                        textViewRocketNumber.setText("016730573330");
                        linearLayTrxID.setVisibility(View.VISIBLE);
                        linearLayBkashPayment.setVisibility(View.GONE);
                        linearLayCODPayment.setVisibility(View.GONE);
                        payment = "Rocket";
                        break;
                }
            }
        });

        getDateAndTotalBill();
        loadData();
    }

    private void checkCoupon(final String code, String customer_id) {

        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
        Call<Result> call = service.checkCouponCode(code, customer_id);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                if (!response.body().getError()) {
                    coupon = new Coupon();
                    coupon = response.body().getCouponDetails();

                    if (Integer.parseInt(coupon.getUses_total()) >= Integer.parseInt(coupon.getCode_uses_total()) &&
                            Integer.parseInt(coupon.getUses_customer()) >= Integer.parseInt(coupon.getCode_customer_uses_total())) {
                        if (total >= Double.parseDouble(coupon.getTotal())) {

                            if (coupon.getIsGen().equalsIgnoreCase("true")) {
                                //Toast.makeText(getApplicationContext(), "Code Applied", Toast.LENGTH_LONG).show();
                                isCodeApplied = true;
                                CouponCode = code;
                                if (coupon.getType().equals("F")) {
                                    discountPrice = coupon.getDiscount();
                                } else {
                                    discountPrice = String.valueOf(total / 100 * Double.parseDouble(coupon.getDiscount()));
                                }

                                linearLayCoupon.setVisibility(View.VISIBLE);
                                textViewCouponTitle.setText("Coupon (" + code + ")");
                                text_coupon_rate.setText(getResources().getString(R.string.currency_sign) + "-" + String.format("%.2f", Double.parseDouble(discountPrice)));
                                textViewPromoEnter.setVisibility(View.VISIBLE);
                                layCouponInfo2.setVisibility(View.GONE);
                                layoutPromo.setVisibility(View.GONE);
                                textViewCouponEdit.setVisibility(View.GONE);

                                double subTotal = total;
                                total = total + Common.SHIIPING_CHARGE - Double.parseDouble(discountPrice);

                                textViewBillTotal.setText(getResources().getString(R.string.currency_sign) + String.format("%.2f", total));
                                textViewSubAmount.setText(getResources().getString(R.string.currency_sign) + String.format("%.2f", subTotal));
                                text_Total.setText(getResources().getString(R.string.currency_sign) + String.format("%.2f", total));
                                waitingDialog.dismiss();

                            } else if (coupon.getIsCat().equalsIgnoreCase("true")) {
                                //Defining retrofit api service
                                ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
                                Call<Result> callCat = service.getCategoryByCouponID(coupon.getCoupon_id());

                                callCat.enqueue(new Callback<Result>() {
                                    @Override
                                    public void onResponse(Call<Result> call, Response<Result> response) {
                                        getCategoryDiscount(response.body().getAllCategoryByCouponId(), code);
                                    }

                                    @Override
                                    public void onFailure(Call<Result> call, Throwable t) {
                                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else if(coupon.getIsProduct().equalsIgnoreCase("true"))
                            {
                                //Defining retrofit api service
                                ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
                                Call<Result> callCat = service.getProductByCouponID(coupon.getCoupon_id());

                                callCat.enqueue(new Callback<Result>() {
                                    @Override
                                    public void onResponse(Call<Result> call, Response<Result> response) {
                                        getProductDiscount(response.body().getAllProductByCouponId(), code);
                                    }

                                    @Override
                                    public void onFailure(Call<Result> call, Throwable t) {
                                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            waitingDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Minimum Order price is below: " + coupon.getTotal(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        waitingDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "oaosoas", Toast.LENGTH_LONG).show();
                    }
                } else {
                    waitingDialog.dismiss();
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getProductDiscount(List<Coupon> allProductByCouponId, final String code) {
        for (int i = 0; i < allProductByCouponId.size(); i++) {
            for (int j = 0; j < cartCart.size(); j++) {
                if(allProductByCouponId.get(i).getProduct_id().equals(cartCart.get(j).getProduct_id()))
                {
                    exactCatTotal = exactCatTotal + (Double.parseDouble(cartCart.get(j).getPrice()) * Double.parseDouble(cartCart.get(j).getQuantity()));
                    isCodeApplied = true;
                    CouponCode = code;
                }
            }
        }
        if (coupon.getType().equals("F")) {
            discountPrice = coupon.getDiscount();
        } else {
            discountPrice = String.valueOf(exactCatTotal / 100 * Double.parseDouble(coupon.getDiscount()));
        }
        linearLayCoupon.setVisibility(View.VISIBLE);
        textViewCouponTitle.setText("Coupon (" + code + ")");
        text_coupon_rate.setText(getResources().getString(R.string.currency_sign) + "-" + String.format("%.2f", Double.parseDouble(discountPrice)));
        textViewPromoEnter.setVisibility(View.VISIBLE);
        layCouponInfo2.setVisibility(View.GONE);
        layoutPromo.setVisibility(View.GONE);
        textViewCouponEdit.setVisibility(View.GONE);

        double subTotal = total;
        total = total + Common.SHIIPING_CHARGE - Double.parseDouble(discountPrice);

        textViewBillTotal.setText(getResources().getString(R.string.currency_sign) + String.format("%.2f", total));
        textViewSubAmount.setText(getResources().getString(R.string.currency_sign) + String.format("%.2f", subTotal));
        text_Total.setText(getResources().getString(R.string.currency_sign) + String.format("%.2f", total));

        waitingDialog.dismiss();

    }

    int codeAppliedCount = 0;
    private void getCategoryDiscount(final List<Coupon> couponCategoryList, final String code) {

        for (int i = 0; i < couponCategoryList.size(); i++) {

            for (int j = 0; j < cartCart.size(); j++) {

                //Defining retrofit api service
                ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
                Call<Result> call = service.getCategoryByProductID(cartCart.get(j).getProduct_id());

                final int finalI = i;
                final int finalJ = j;
                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {

                        if (response.body().getAllCategoryByProduct().size() > 1) {
                            // means product has multiple category
                            for (int k = 0; k < response.body().getAllCategoryByProduct().size(); k++) {

                                if (couponCategoryList.get(finalI).getCategory_id().equals(response.body().getAllCategoryByProduct().get(k).getCategory_id())) {
                                    codeAppliedCount++;
                                    if(codeAppliedCount <= 1)
                                    {
                                        Log.d("Count: ", String.valueOf(codeAppliedCount));
                                        addCategoryWiseDiscount(finalJ, code);
                                    }
                                } else {
                                    getParentCategoryByCartCat(couponCategoryList.get(finalI).getCategory_id(), response.body().getAllCategoryByProduct().get(k).getCategory_id(), finalJ, code);
                                }
                            }
                        } else {
                            if (couponCategoryList.get(finalI).getCategory_id().equals(cartCart.get(finalJ).getCat_id())) {
                                exactCatTotal = exactCatTotal + (Double.parseDouble(cartCart.get(finalJ).getPrice()) * Double.parseDouble(cartCart.get(finalJ).getQuantity()));
                                isCodeApplied = true;
                                CouponCode = code;
                                if (coupon.getType().equals("F")) {
                                    discountPrice = coupon.getDiscount();
                                } else {
                                    discountPrice = String.valueOf(exactCatTotal / 100 * Double.parseDouble(coupon.getDiscount()));
                                }
                                linearLayCoupon.setVisibility(View.VISIBLE);
                                textViewCouponTitle.setText("Coupon (" + code + ")");
                                text_coupon_rate.setText(getResources().getString(R.string.currency_sign) + "-" + String.format("%.2f", Double.parseDouble(discountPrice)));

                                textViewPromoEnter.setVisibility(View.VISIBLE);
                                layoutPromo.setVisibility(View.GONE);
                                layCouponInfo2.setVisibility(View.GONE);
                                textViewCouponEdit.setVisibility(View.GONE);

                                double subTotal = total;
                                total = total + Common.SHIIPING_CHARGE - Double.parseDouble(discountPrice);

                                textViewBillTotal.setText(getResources().getString(R.string.currency_sign) + String.format("%.2f", total));
                                textViewSubAmount.setText(getResources().getString(R.string.currency_sign) + String.format("%.2f", subTotal));
                                text_Total.setText(getResources().getString(R.string.currency_sign) + String.format("%.2f", total));

                                waitingDialog.dismiss();
                            } else {
                                getParentCategoryByCartCat(couponCategoryList.get(finalI).getCategory_id(), cartCart.get(finalJ).getCat_id(), finalJ, code);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        waitingDialog.dismiss();
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void addCategoryWiseDiscount(int finalJ, String code) {

        exactCatTotal = exactCatTotal + (Double.parseDouble(cartCart.get(finalJ).getPrice()) * Double.parseDouble(cartCart.get(finalJ).getQuantity()));
        isCodeApplied = true;
        CouponCode = code;
        if (coupon.getType().equals("F")) {
            discountPrice = coupon.getDiscount();
        } else {
            discountPrice = String.valueOf(exactCatTotal / 100 * Double.parseDouble(coupon.getDiscount()));
        }
        linearLayCoupon.setVisibility(View.VISIBLE);
        textViewCouponTitle.setText("Coupon (" + code + ")");
        text_coupon_rate.setText(getResources().getString(R.string.currency_sign) + "-" + String.format("%.2f", Double.parseDouble(discountPrice)));

        textViewPromoEnter.setVisibility(View.VISIBLE);
        layoutPromo.setVisibility(View.GONE);
        layCouponInfo2.setVisibility(View.GONE);
        textViewCouponEdit.setVisibility(View.GONE);

        double subTotal = total;
        total = total + Common.SHIIPING_CHARGE - Double.parseDouble(discountPrice);

        textViewBillTotal.setText(getResources().getString(R.string.currency_sign) + String.format("%.2f", total));
        textViewSubAmount.setText(getResources().getString(R.string.currency_sign) + String.format("%.2f", subTotal));
        text_Total.setText(getResources().getString(R.string.currency_sign) + String.format("%.2f", total));

        waitingDialog.dismiss();
    }

    private void getParentCategoryByCartCat(final String category_id, String cat_id, final int position, final String code) {
        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
        Call<Result> parentCat = service.getParentCatByCatID(cat_id);
        parentCat.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (!response.body().getError()) // parent is not exist means its parent
                {
                    if (response.body().getCouponDetails().getParent_id().equals(category_id)) {
                        codeAppliedCount++;
                        if(codeAppliedCount <= 1)
                        {
                            exactCatTotal = exactCatTotal + (Double.parseDouble(cartCart.get(position).getPrice()) * Double.parseDouble(cartCart.get(position).getQuantity()));
                            isCodeApplied = true;
                            CouponCode = code;
                            if (coupon.getType().equals("F")) {
                                discountPrice = coupon.getDiscount();
                            } else {
                                discountPrice = String.valueOf(exactCatTotal / 100 * Double.parseDouble(coupon.getDiscount()));
                            }
                            linearLayCoupon.setVisibility(View.VISIBLE);
                            textViewCouponTitle.setText("Coupon (" + code + ")");
                            text_coupon_rate.setText(getResources().getString(R.string.currency_sign) + "-" + String.format("%.2f", Double.parseDouble(discountPrice)));

                            textViewPromoEnter.setVisibility(View.VISIBLE);
                            layoutPromo.setVisibility(View.GONE);
                            layCouponInfo2.setVisibility(View.GONE);
                            textViewCouponEdit.setVisibility(View.GONE);

                            double subTotal = total;
                            total = total + Common.SHIIPING_CHARGE - Double.parseDouble(discountPrice);

                            textViewBillTotal.setText(getResources().getString(R.string.currency_sign) + String.format("%.2f", total));
                            textViewSubAmount.setText(getResources().getString(R.string.currency_sign) + String.format("%.2f", subTotal));
                            text_Total.setText(getResources().getString(R.string.currency_sign) + String.format("%.2f", total));
                        }
                        waitingDialog.dismiss();
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        //Toast.makeText(getApplicationContext(), "Code Applied", Toast.LENGTH_LONG).show();

    }

    private void placeOrder() {
        /**
         *  Network Connection Check
         */
        if (!netWorkConfig.isNetworkAvailable()) {
            netWorkConfig.createNetErrorDialog();
            return;
        }

        if(radioIndex!=0)
        {
            if(editTextTrxID.getText().toString().isEmpty())
            {
                Toast.makeText(getApplicationContext(), "Transaction Id field is empty. please enter the trxid or select Cash on delivery method.", Toast.LENGTH_LONG).show();
                return;
            }
        }


        String firstName = editTextFirstName.getText().toString();
        String lastName = editTextLastName.getText().toString();
        String companyName = editTextCompany.getText().toString();
        String address_1 = editTextAddress1.getText().toString();
        String address_2 = editTextAddress2.getText().toString();
        String cityName = editTextCity.getText().toString();
        String postCode = editTextPostCode.getText().toString();
        String phone = editTextPhone.getText().toString();


        if (TextUtils.isEmpty(editTextFirstName.getText().toString())) {
            editTextFirstName.setError("Filed is mandatory");
            return;
        }
        if (TextUtils.isEmpty(editTextFirstName.getText().toString())) {
            editTextLastName.setError("Filed is mandatory");
            return;
        }
        if (TextUtils.isEmpty(editTextAddress1.getText().toString())) {
            editTextAddress1.setError("Filed is mandatory");
            return;
        }
        if (TextUtils.isEmpty(editTextCity.getText().toString())) {
            editTextCity.setError("Filed is mandatory");
            return;
        }
        if (TextUtils.isEmpty(editTextPhone.getText().toString())) {
            editTextPhone.setError("Filed is mandatory");
            return;
        }
        if (TextUtils.isEmpty(editTextPostCode.getText().toString())) {
            postCode = "";
        }
        if (TextUtils.isEmpty(editTextCompany.getText().toString())) {
            companyName = "";
        }
        if (TextUtils.isEmpty(editTextAddress2.getText().toString())) {
            address_2 = "";
        }

        // Calculate Total Price
        double total = 0.0;
        for (Cart cart : cartCart) {
            total += (Double.parseDouble(cart.getPrice())) * (Double.parseDouble(cart.getQuantity()));
        }

        SUB_TOTAL = total;

        if (isCodeApplied) {
            total = total + Common.SHIIPING_CHARGE - Double.parseDouble(discountPrice);
        } else {
            total = total + Common.SHIIPING_CHARGE;
        }


        final android.app.AlertDialog waitingDialog = new SpotsDialog(MedicineCheckoutActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Placing ...");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MedicineCheckoutActivity.this);
        final String CUSTOMER_ID = sharedPreferences.getString("CUSTOMER_ID", null);
        String EMAIL = sharedPreferences.getString("EMAIL", null);

        // First add to oc_order
        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
        Call<Result> call = service.addOrder("0", "FROM APP", "0", "Your Store",
                "https://dhacai.com.bd/dhacai2/", CUSTOMER_ID, "1", firstName,
                lastName, EMAIL, phone, "a", "a", firstName,
                lastName, companyName, address_1, address_2, cityName, postCode, countryName, country_id, stateName, state_id,
                "a", "[]", "Cash On Delivery", "cod", firstName,
                lastName, companyName, address_1, address_2, cityName, postCode, countryName, country_id, stateName, state_id,
                "a", "[]", "Flat Shipping Rate", "flat.flat",
                "a", total, "1", "0", "0.000", "0", "a",
                "1", "2", "USD", "1.00000000", "103.26.247.86", "a",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36",
                "en-US,en;q=0.9");

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                waitingDialog.dismiss();
                if (!response.body().getError()) {
                    getOrderID(CUSTOMER_ID);
                    Toast.makeText(getApplicationContext(), response.body().getMessage().toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getOrderID(String customer_id) {
        // First add to oc_order
        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
        Call<Result> call = service.getLasteInsertedID(customer_id);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                int order_id = response.body().getLast_order_id().getOrder_id();
                getOrderToTotal(SUB_TOTAL, order_id);
                postOrderProducts(order_id);
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getOrderToTotal(double sub_total, int order_id) {
        String[] itemsTitles = {"Sub-Total", "Flat Shipping Rate", "Total", "Coupon"};
        String[] itemsTitlesCode = {"sub_total", "shipping", "total", "coupon"};
        double[] value = {sub_total, Common.SHIIPING_CHARGE, sub_total + Common.SHIIPING_CHARGE, Double.parseDouble(discountPrice) * -1};
        String[] sort_order = {"1", "3", "9", "4"};

        if (isCodeApplied) {
            for (int i = 0; i < 4; i++) {
                if (i == 3) {
                    itemsTitles[i] = itemsTitles[i] + " (" + CouponCode + ")";
                }
                if (i == 2) {
                    value[i] = value[i] - Double.parseDouble(discountPrice);
                }
                //Defining retrofit api service
                ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
                Call<Result> call = service.addOrderTotalHistory(String.valueOf(order_id), itemsTitlesCode[i], itemsTitles[i], String.valueOf(value[i]), sort_order[i]);

                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {

                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            // push to oc_coupon_history table
            //Defining retrofit api service
            ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
            Call<Result> call = service.addCouponHistory(coupon.getCoupon_id(), String.valueOf(order_id), GEN_CUSTOMER_ID, "-" + discountPrice);

            call.enqueue(new Callback<Result>() {
                @Override
                public void onResponse(Call<Result> call, Response<Result> response) {
                }

                @Override
                public void onFailure(Call<Result> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            for (int i = 0; i < 3; i++) {
                //Defining retrofit api service
                ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
                Call<Result> call = service.addOrderTotalHistory(String.valueOf(order_id), itemsTitlesCode[i], itemsTitles[i], String.valueOf(value[i]), sort_order[i]);

                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {

                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void postOrderProducts(final int order_id) {

        // Delaying action for 1 second
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                boolean post = false;
                int count = new DatabaseMedicine(getApplicationContext()).getCarts().size();
                for (int i = 0; i < count; i++) {

                    // Calculate Total Price
                    double total = 0.0;
                    total += (Double.parseDouble(cartCart.get(i).getPrice())) * (Double.parseDouble(cartCart.get(i).getQuantity()));

                    Log.d("SELLER_ID: ", cartCart.get(i).getSeller_id());

                    //Defining retrofit api service
                    ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
                    //defining the call
                    Call<Result> call = service.addOrderProductDetails(
                            String.valueOf(order_id),
                            cartCart.get(i).getProduct_id(),
                            cartCart.get(i).getProduct_name(),
                            cartCart.get(i).getModel(),
                            cartCart.get(i).getQuantity(),
                            cartCart.get(i).getPrice(),
                            String.valueOf(total),
                            "0.0000",
                            "0"
                    );
                    //calling the api
                    call.enqueue(new Callback<Result>() {
                        @Override
                        public void onResponse(Call<Result> call, Response<Result> response) {

                        }

                        @Override
                        public void onFailure(Call<Result> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                    // Vendor
                    if (!cartCart.get(i).getSeller_id().equals("null")) {

                        if (i + 1 == count) {
                            post = true;
                        } else {
                            if (cartCart.get(i).getSeller_id().equals(cartCart.get(i + 1).getSeller_id())) {
                                post = false;
                            } else {
                                post = true;
                            }
                        }

                        // Post to vendor product details table
                        //Defining retrofit api service
                        ApiService service2 = ApiClient.getClientMedixShop().create(ApiService.class);
                        //defining the call
                        Call<Result> call2 = service2.addOrderProductDetailsForVendor(
                                cartCart.get(i).getSeller_id(),
                                cartCart.get(i).getProduct_id(),
                                String.valueOf(order_id),
                                "0.00",
                                cartCart.get(i).getQuantity(),
                                cartCart.get(i).getPrice(),
                                String.valueOf(total),
                                "1",
                                "0"
                        );
                        //calling the api
                        call2.enqueue(new Callback<Result>() {
                            @Override
                            public void onResponse(Call<Result> call, Response<Result> response) {

                            }

                            @Override
                            public void onFailure(Call<Result> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });

                        if (post) {
                            // post to vendor product history table
                            //Defining retrofit api service
                            ApiService serviceHistory = ApiClient.getClientMedixShop().create(ApiService.class);
                            //defining the call
                            Call<Result> callHistory = serviceHistory.addOrderProductHistoryForVendor(
                                    cartCart.get(i).getSeller_id(),
                                    String.valueOf(order_id),
                                    "1",
                                    "null",
                                    "0"
                            );
                            //calling the api
                            callHistory.enqueue(new Callback<Result>() {
                                @Override
                                public void onResponse(Call<Result> call, Response<Result> response) {

                                }

                                @Override
                                public void onFailure(Call<Result> call, Throwable t) {
                                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }

                // post to general order history table;
                //Defining retrofit api service
                ApiService serviceHistory = ApiClient.getClientMedixShop().create(ApiService.class);
                //defining the call
                Call<Result> callHistory = serviceHistory.addOrderProductGeneralHistory(
                        String.valueOf(order_id),
                        "1",
                        "0",
                        "null"
                );
                //calling the api
                callHistory.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {

                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

                // Remove All cart items
                new DatabaseMedicine(getApplicationContext()).removeAllCartItems();
                //sentNotificationToServer(id_order, total_price, address);
                //showOrderDialog(id_order, total_price,address, myPreferences.getString("NAME", null));
                Toast.makeText(getApplicationContext(), "Order Placed", Toast.LENGTH_LONG).show();
                //listener.addView();
                //loadCartItems();
                startActivity(new Intent(MedicineCheckoutActivity.this, AllMedicineOrderHistoryActivity.class));
            }
        }, 1000);
    }

    private void loadData() {

        loadCountryData();

        Picasso.with(MedicineCheckoutActivity.this).load(R.drawable.cod_bg).into(imageViewCard);

        // load cart Items
        cartCart = new DatabaseMedicine(this).getCarts();
        adapter = new MedicineConfirmCartAdapter(cartCart, this);
        recyclerViewCart.setAdapter(adapter);

        // Calculate Total Price
        double sub_total = 0.0, total = 0.00;
        for (Cart cart : cartCart) {
            sub_total += (Double.parseDouble(cart.getPrice())) * (Double.parseDouble(cart.getQuantity()));
        }

        total = sub_total + Common.SHIIPING_CHARGE - Double.parseDouble(discountPrice);

        textViewSubAmount.setText(getResources().getString(R.string.currency_sign) + String.format("%.2f", sub_total));
        text_Total.setText(getResources().getString(R.string.currency_sign) + String.format("%.2f", total));

    }

    private void loadCountryData() {
        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
        Call<Result> call = service.getCountry();

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                countryList = response.body().getCountryList();
                final List<String> countyNameID = new ArrayList<>();
                final Map<String, String> countryMap = new HashMap<String, String>();

                for (int i = 0; i < response.body().getCountryList().size(); i++) {
                    countryMap.put(response.body().getCountryList().get(i).getName(), response.body().getCountryList().get(i).getCountry_id());
                    countyNameID.add(response.body().getCountryList().get(i).getName());
                }

                // Creating adapter for spinner
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MedicineCheckoutActivity.this, R.layout.spinner_item, countyNameID);
                // Drop down layout style - list view with radio button
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                spinner_country.setAdapter(dataAdapter);
                spinner_country.setSelection(17); // For Default BD

                spinner_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String string = spinner_country.getSelectedItem().toString();
                        countryName = string;
                        country_id = countryMap.get(string);
                        loadStateRegion(country_id);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadStateRegion(String countryID) {
        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
        Call<Result> call = service.getStateRegion(countryID);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                stateRegionList = response.body().getStateRegionList();
                final List<String> stateNameID = new ArrayList<>();
                final Map<String, String> stateMap = new HashMap<String, String>();

                for (int i = 0; i < response.body().getStateRegionList().size(); i++) {
                    stateMap.put(response.body().getStateRegionList().get(i).getName(), response.body().getStateRegionList().get(i).getZone_id());
                    stateNameID.add(response.body().getStateRegionList().get(i).getName());
                }
                // Creating adapter for spinner
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MedicineCheckoutActivity.this, R.layout.spinner_item, stateNameID);
                // Drop down layout style - list view with radio button
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                spinner_state_region.setAdapter(dataAdapter);

                spinner_state_region.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String string = spinner_state_region.getSelectedItem().toString();
                        stateName = string;
                        state_id = stateMap.get(string);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDateAndTotalBill() {

        Date cDate = new Date();
        String currentDate = new SimpleDateFormat("MMM dd, yyyy").format(cDate);
        //getting Total Price
        double total = 0.0;
        List<Cart> carts = new DatabaseMedicine(MedicineCheckoutActivity.this).getCarts();
        for (Cart cart : carts) {
            total += (Double.parseDouble(cart.getPrice())) * (Double.parseDouble(cart.getQuantity()));
        }
        textViewBillTotal.setText(getResources().getString(R.string.currency_sign) + String.format("%.2f", total + Common.SHIIPING_CHARGE));
        textViewDate.setText(currentDate);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
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
    protected void onResume() {
        super.onResume();
        getDateAndTotalBill();
        loadData();
    }

}
