package com.example.medix.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medix.Activity.MedicineShop.MedicineCartActivity;
import com.example.medix.Activity.MedicineShop.ProductActivity;
import com.example.medix.Adapter.RelatedProductAdapter;
import com.example.medix.Api.ApiClient;
import com.example.medix.Api.ApiService;
import com.example.medix.Common.Common;
import com.example.medix.Database.DatabaseMedicine;
import com.example.medix.Helper.GridSpacingItemDecoration;
import com.example.medix.Helper.NetWorkConfig;
import com.example.medix.Model.Cart;
import com.example.medix.Model.Result;
import com.example.medix.Model.SingleProduct;
import com.example.medix.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.smarteist.autoimageslider.IndicatorView.utils.DensityUtils.dpToPx;

public class OverViewMedicineFragment extends Fragment {
    private static final String TAG = "OverViewMedicineFragment";
    private TextView textViewProductName, textViewProductPrice;
    private RatingBar ratingBar;
    public TextView textViewReviews, textViewPrice, textViewStatus, textViewItemModel, textViewProductDiscountPrice,
            textViewSellerName;
    private String product_quantity;
    private FloatingActionButton fab;
    private Button btn_addCart;
    private Spinner spinnerQuantity;
    private NetWorkConfig netWorkConfig;
    public LinearLayout linearLay, linearLaySeller, linearLayRelatedProducts ;
    public RecyclerView recycler_view_related_products;

    private RelativeLayout relativeLayout;
    private TextView txtDay, txtHour, txtMinute, txtSecond;
    private TextView tvEventStart;
    private Handler handler;
    private Runnable runnable;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.overview_medicine_fragment, container, false);

        netWorkConfig = new NetWorkConfig(getActivity());

        /**
         *  Network Connection Check
         */
        if (!netWorkConfig.isNetworkAvailable()) {
            netWorkConfig.createNetErrorDialog();
        }
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                Intent intent = new Intent(getContext(), MedicineCartActivity.class);
                startActivity(intent);
            }
        });
        linearLay = (LinearLayout) view.findViewById(R.id.linearLay);
        linearLaySeller = (LinearLayout) view.findViewById(R.id.linearLaySeller);
        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        textViewStatus = (TextView) view.findViewById(R.id.textViewStatus);
        textViewItemModel = (TextView) view.findViewById(R.id.textViewItemModel);
        textViewSellerName = (TextView) view.findViewById(R.id.textViewSellerName);
        textViewReviews = (TextView) view.findViewById(R.id.textViewReviews);
        textViewProductName = (TextView) view.findViewById(R.id.textViewProductName);
        textViewProductPrice = (TextView) view.findViewById(R.id.textViewProductPrice);
        textViewProductDiscountPrice = (TextView) view.findViewById(R.id.textViewProductDiscountPrice);
        btn_addCart = (Button) view.findViewById(R.id.btn_addCart);
        spinnerQuantity = (Spinner) view.findViewById(R.id.spinner_quantity);

        linearLayRelatedProducts = (LinearLayout) view.findViewById(R.id.linearLayRelatedProducts);

        recycler_view_related_products = (RecyclerView) view.findViewById(R.id.recycler_view_related_products);
        recycler_view_related_products.setHasFixedSize(true);
        /*LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recycler_view_cat.setLayoutManager(layoutManager);
        recycler_view_cat.setItemAnimator(new DefaultItemAnimator());*/
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recycler_view_related_products.setLayoutManager(mLayoutManager);
        recycler_view_related_products.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(0), true));
        recycler_view_related_products.setItemAnimator(new DefaultItemAnimator());

        relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayout);
        txtDay = (TextView) view.findViewById(R.id.txtDay);
        txtHour = (TextView) view.findViewById(R.id.txtHour);
        txtMinute = (TextView) view.findViewById(R.id.txtMinute);
        txtSecond = (TextView) view.findViewById(R.id.txtSecond);

        if(Common.singleProduct.getDate_start().equals("0000-00-00") || Common.singleProduct.getDate_end().equals("0000-00-00"))
        {
            relativeLayout.setVisibility(View.GONE);
        }
        else
        {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date strDate = null;
            try {
                strDate = sdf.parse(Common.singleProduct.getDate_end());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (System.currentTimeMillis() > strDate.getTime()) {
                relativeLayout.setVisibility(View.GONE);
                textViewProductDiscountPrice.setVisibility(View.GONE);
            }
            else
            {
                relativeLayout.setVisibility(View.VISIBLE);
                textViewProductDiscountPrice.setVisibility(View.VISIBLE);
                countDownStart();
            }
        }

        ProductActivity activity = (ProductActivity) getActivity();
        //food_id = activity.getFoodID();

        //Log.d(TAG, food_id);
        viewCart();

        loadSpinnerData();
        spinnerQuantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                product_quantity = spinnerQuantity.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btn_addCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCartMethod();
            }
        });
        btn_addCart.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                removeCartMethod();
                return false;
            }
        });

        checkCartExistence();
        getProductDesc();
        loadRelatedProducts();

        return view;
    }

    private void loadRelatedProducts() {
        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
        Call<Result> call = service.getRelatedProduct(Common.singleProduct.getProduct_id());

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                displayRelatedProduct(response.body().getAllRelatedProduct());
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.d("Error: ", t.getMessage());
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayRelatedProduct(List<SingleProduct> allRelatedProduct) {

        if(allRelatedProduct.size() <= 0)
        {
            linearLayRelatedProducts.setVisibility(View.GONE);
        }
        else
        {
            linearLayRelatedProducts.setVisibility(View.VISIBLE);
            RelatedProductAdapter adapter = new RelatedProductAdapter(allRelatedProduct, OverViewMedicineFragment.this, getActivity());
            recycler_view_related_products.setAdapter(adapter);
            //recycler_view_latest.addItemDecoration(new SpacesItemDecoration(10));
            ViewCompat.setNestedScrollingEnabled(recycler_view_related_products, false);
        }
    }

    public void countDownStart() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            "yyyy-MM-dd");
                    // Please here set your event date//YYYY-MM-DD
                    Date futureDate = dateFormat.parse(Common.singleProduct.getDate_end());
                    Date currentDate = new Date();
                    if (!currentDate.after(futureDate)) {
                        long diff = futureDate.getTime()
                                - currentDate.getTime();
                        long days = diff / (24 * 60 * 60 * 1000);
                        diff -= days * (24 * 60 * 60 * 1000);
                        long hours = diff / (60 * 60 * 1000);
                        diff -= hours * (60 * 60 * 1000);
                        long minutes = diff / (60 * 1000);
                        diff -= minutes * (60 * 1000);
                        long seconds = diff / 1000;
                        txtDay.setText("" + String.format("%02d", days));
                        txtHour.setText("" + String.format("%02d", hours));
                        txtMinute.setText(""
                                + String.format("%02d", minutes));
                        txtSecond.setText(""
                                + String.format("%02d", seconds));
                    } else {
                        tvEventStart.setVisibility(View.VISIBLE);
                        tvEventStart.setText("The event started!");
                        //textViewGone();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 1 * 1000);
    }

    public void viewCart() {
        final int count = new DatabaseMedicine(getActivity()).cartItemCount();

        if (count > 0) {
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }
    }

    private void removeCartMethod() {
        // Remove item from Cart
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Remove from Cart");
        alertDialog.setMessage("Are you sure you want to remove " + Common.singleProduct.getProduct_name() + " - from your cart?");
        alertDialog.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.setNegativeButton("REMOVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                new DatabaseMedicine(getActivity()).clearCartITemFromProcut(Common.singleProduct.getProduct_id(), Common.singleProduct.getCategory_id());
                btn_addCart.setText("Add to cart");
                btn_addCart.setBackgroundResource(R.drawable.btn_bg);
                viewCart();
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

    private void addCartMethod() {
        // Checking if the product is already added or not(Not Added)
        if (!new DatabaseMedicine(getActivity()).checkExistence(Common.singleProduct.getProduct_id())) {

            final android.app.AlertDialog waitingDialog = new SpotsDialog(getActivity());
            waitingDialog.show();
            waitingDialog.setMessage("Waiting ...");
            // Delaying action for 1.0 seconds
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new DatabaseMedicine(getActivity()).addToCart(new Cart(
                            Common.singleProduct.getProduct_id(),
                            Common.singleProduct.getCategory_id(),
                            Common.product_name,
                            Common.singleProduct.getModel(),
                            Common.singleProduct.getImage(),
                            Common.product_price,
                            product_quantity,
                            Common.singleProduct.getCat_name(),
                            Common.singleProduct.getCat_name(),
                            Common.singleProduct.getStore_name(),
                            Common.singleProduct.getSeller_id()
                    ));
                    btn_addCart.setBackgroundResource(R.drawable.btn_bg_color);
                    String quantity = new DatabaseMedicine(getActivity()).countQuantity(Common.singleProduct.getProduct_id(), Common.singleProduct.getCategory_id());
                    btn_addCart.setText("Added to cart " + "(x " + quantity + " times)");
                    viewCart();
                    waitingDialog.dismiss();
                }
            }, 500);
        } else {
            final android.app.AlertDialog waitingDialog = new SpotsDialog(getActivity());
            waitingDialog.show();
            waitingDialog.setMessage("Updating ...");
            // Delaying action for 1.0 seconds
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // if item is exist in cart then simply increase the quantity value
                    String quantity = new DatabaseMedicine(getActivity()).countQuantity(Common.singleProduct.getProduct_id(), Common.singleProduct.getCategory_id());
                    int q1 = Integer.parseInt(quantity) + Integer.parseInt(product_quantity);

                    if (q1 > 15) {
                        Toast.makeText(getActivity(), "Limit Crossed! (Max quantity amount is 15)", Toast.LENGTH_LONG).show();

                    } else {
                        // Update carts
                        new DatabaseMedicine(getActivity()).updateCartItemFromFav(String.valueOf(q1), Common.singleProduct.getProduct_id(), Common.singleProduct.getCategory_id());
                        btn_addCart.setText("Added to cart " + "(x " + q1 + " times)");
                        btn_addCart.setBackgroundResource(R.drawable.btn_bg_color);
                        viewCart();
                    }
                    waitingDialog.dismiss();
                }
            }, 500);
        }
    }

    private void checkCartExistence() {
        if (new DatabaseMedicine(getContext()).checkExistence(Common.singleProduct.getProduct_id())) {
            btn_addCart.setBackgroundResource(R.drawable.btn_bg_color);
            String quantity = new DatabaseMedicine(getActivity()).countQuantity(Common.singleProduct.getProduct_id(), Common.singleProduct.getCategory_id());
            btn_addCart.setText("Added to cart " + "(x " + quantity + " times)");
        }
    }

    private void loadSpinnerData() {
        String[] data = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, data);
        spinnerQuantity.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void getProductDesc() {

        final android.app.AlertDialog waitingDialog = new SpotsDialog(getActivity());
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");

        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
        Call<Result> call = service.getReviews(Common.singleProduct.getProduct_id());

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                waitingDialog.dismiss();
                linearLay.setVisibility(View.VISIBLE);

                int rating=0;
                if(response.body().getProductratingList().size() <=0)
                {
                    ratingBar.setRating(0);
                    textViewReviews.setText("(0 reviews)");
                }
                else
                {

                    for(int i=0;i<response.body().getProductratingList().size();i++)
                    {
                        rating+=response.body().getProductratingList().get(i).getRating();
                    }

                    if(response.body().getProductratingList().get(0).getStatus().equals("1"))
                    {
                        textViewStatus.setText("In stock");
                    }
                    else
                    {
                        textViewStatus.setText("Out of stock");

                    }
                    ratingBar.setRating(rating/response.body().getProductratingList().size());
                    textViewReviews.setText(String.valueOf("( " + response.body().getProductratingList().size() + " reviews)"));
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                waitingDialog.dismiss();
            }
        });

        linearLay.setVisibility(View.VISIBLE);
        textViewItemModel.setText(Common.singleProduct.getModel());
        textViewProductName.setText(Common.product_name);
        textViewProductPrice.setText(getResources().getString(R.string.currency_sign) + Common.product_price);

        if(!Common.singleProduct.getDiscount_price().equals("0.0000"))
        {
            double original_price = Double.parseDouble(Common.singleProduct.getPrice());
            double selling_price = Double.parseDouble(Common.singleProduct.getDiscount_price());
            double discount = original_price - selling_price;

            //formula to get percentage
            double percentage = Math.ceil((discount / original_price) * 100);
            int result = (int) percentage;
            //textViewProductDiscountPrice.setVisibility(View.VISIBLE);
            textViewProductDiscountPrice.setText(String.valueOf(result) + "%");
        }
        else
        {
            textViewProductDiscountPrice.setVisibility(View.GONE);
        }

        if(Common.singleProduct.getStore_name().equals("null"))
        {
            linearLaySeller.setVisibility(View.GONE);

        }
        else {
            linearLaySeller.setVisibility(View.VISIBLE);
            textViewSellerName.setText(Common.singleProduct.getStore_name());
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        checkCartExistence();
    }
}