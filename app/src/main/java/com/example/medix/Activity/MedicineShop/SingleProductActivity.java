package com.example.medix.Activity.MedicineShop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medix.Adapter.SingleProductAdapter;
import com.example.medix.Api.ApiClient;
import com.example.medix.Api.ApiService;
import com.example.medix.Common.Common;
import com.example.medix.Helper.NetWorkConfig;
import com.example.medix.Model.Result;
import com.example.medix.Model.SingleProduct;
import com.example.medix.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SingleProductActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    String menu_id, menu_name;
    RecyclerView recyclerViewDrink;
    SingleProductAdapter adapter;
    public LinearLayout layoutViewCart, layoutCart;
    public TextView textViewPrice, textViewMenuName;
    public ImageView imageViewBackButton, imageViewSort, imageViewSwitch;
    private NetWorkConfig netWorkConfig;
    private SwipeRefreshLayout swipeRefreshLayout;
    boolean isViewWithCatalog = true;
    List<SingleProduct> singleProductList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_product);

        netWorkConfig = new NetWorkConfig(this);

        /**
         *  Network Connection Check
         */
        if (!netWorkConfig.isNetworkAvailable()) {
            netWorkConfig.createNetErrorDialog();
            return;
        }

        // getting Intent From MenuAdapter
        menu_id = getIntent().getStringExtra("Menu_ID");
        menu_name = getIntent().getStringExtra("Menu_NAME");
        Log.d("MENU_ID: ", menu_id);

        String str = menu_name;
        String replacedStr = str.replaceAll("&amp;", "&");
        textViewMenuName = (TextView) findViewById(R.id.textViewMenuName);
        textViewMenuName.setText(replacedStr);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().
                getColor(android.R.color.holo_blue_dark), getResources().
                getColor(android.R.color.holo_red_dark), getResources().
                getColor(android.R.color.holo_green_light), getResources().
                getColor(android.R.color.holo_orange_dark));
        layoutViewCart = (LinearLayout) findViewById(R.id.layViewCart);
        //textViewItems = (TextView) findViewById(R.id.tvItems);
        textViewPrice = (TextView) findViewById(R.id.tvPrice);
        imageViewBackButton = (ImageView) findViewById(R.id.imageViewBack);
        imageViewSort = (ImageView) findViewById(R.id.imageViewSort);
        imageViewSwitch = (ImageView) findViewById(R.id.imageViewSwitch);
        layoutCart = (LinearLayout) findViewById(R.id.layCart);
        recyclerViewDrink = (RecyclerView) findViewById(R.id.recycler_view_drink);
        // Removes blinks
        ((SimpleItemAnimator) recyclerViewDrink.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerViewDrink.setHasFixedSize(true);
        //recyclerViewDrink.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewDrink.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        imageViewBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        imageViewSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });

        imageViewSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isViewWithCatalog = !isViewWithCatalog;
                supportInvalidateOptionsMenu();
                //loading = false;
                recyclerViewDrink.setLayoutManager(isViewWithCatalog ? new LinearLayoutManager(getApplicationContext()) : new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                recyclerViewDrink.setAdapter(adapter);
            }
        });

        // get Single Product
        getSingleProduct(menu_id);
    }

    private void getSingleProduct(String menu_id) {

        final android.app.AlertDialog waitingDialog = new SpotsDialog(SingleProductActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");

        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
        Call<Result> call = service.getProductBySubCatId(menu_id);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                waitingDialog.dismiss();
                singleProductList = response.body().getSingleProductList();
                displaySingleProduct(response.body().getSingleProductList());

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displaySingleProduct(List<SingleProduct> singleProductList) {
        swipeRefreshLayout.setRefreshing(false);
        if (singleProductList.size() <= 0) {
            Toast.makeText(getApplicationContext(), "No product found!", Toast.LENGTH_SHORT).show();
            finish();
        }
        adapter = new SingleProductAdapter(singleProductList, this);
        recyclerViewDrink.setAdapter(adapter);
        ViewCompat.setNestedScrollingEnabled(recyclerViewDrink, false);

    }

    private void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_sort_dialog, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);

        dialog.show();

        LinearLayout linearLayoutName, linearLayoutLowToHigh, linearLayoutHighToLow, linearLayDiscount;
        linearLayoutName = (LinearLayout) view.findViewById(R.id.linearLayName);
        linearLayDiscount = (LinearLayout) view.findViewById(R.id.linearLayDiscount);
        linearLayoutLowToHigh = (LinearLayout) view.findViewById(R.id.linearLayLowToHigh);
        linearLayoutHighToLow = (LinearLayout) view.findViewById(R.id.linearLayHighToLow);

        linearLayoutName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadDataName(singleProductList);
                dialog.dismiss();
            }
        });

        linearLayoutLowToHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadDataLowToHigh(singleProductList);
                dialog.dismiss();
            }
        });

        linearLayDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadDataDiscount(singleProductList);
                dialog.dismiss();
            }
        });

        linearLayoutHighToLow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadDataHighToLow(singleProductList);
                dialog.dismiss();
            }
        });
    }

    private void loadDataName(List<SingleProduct> singleProductList) {
        List<SingleProduct> list = sortNameList(singleProductList);
        adapter = new SingleProductAdapter(list, this);
        recyclerViewDrink.setAdapter(adapter);
        ViewCompat.setNestedScrollingEnabled(recyclerViewDrink, false);

    }

    private List<SingleProduct> sortNameList(List<SingleProduct> list) {
        if (list != null && list.size() > 1) {
            Collections.sort(list, new Comparator<SingleProduct>() {
                public int compare(SingleProduct o1, SingleProduct o2) {
                    return (int) (o1.getProduct_name().compareTo(o2.getProduct_name()));
                }
            });
        }
        return list;
    }

    private void loadDataDiscount(List<SingleProduct> singleProductList) {
        List<SingleProduct> list = sortDiscountData(singleProductList);
        adapter = new SingleProductAdapter(list, SingleProductActivity.this);
        recyclerViewDrink.setAdapter(adapter);
        ViewCompat.setNestedScrollingEnabled(recyclerViewDrink, false);
    }

    private List<SingleProduct> sortDiscountData(List<SingleProduct> list) {
        if (list != null && list.size() > 1) {
            Collections.sort(list, new Comparator<SingleProduct>() {
                public int compare(SingleProduct o1, SingleProduct o2) {
                    if (Double.parseDouble(o1.getDiscount_price()) == Double.parseDouble(o2.getDiscount_price())) return 0;
                    return Double.parseDouble(o1.getDiscount_price()) < Double.parseDouble(o2.getDiscount_price()) ? 1 : -1;
                }
            });
        }
        return list;
    }

    private void loadDataHighToLow(List<SingleProduct> singleProductList) {
        List<SingleProduct> list = sortHighToLowList(singleProductList);
        adapter = new SingleProductAdapter(list, this);
        recyclerViewDrink.setAdapter(adapter);
        ViewCompat.setNestedScrollingEnabled(recyclerViewDrink, false);

    }

    private List<SingleProduct> sortHighToLowList(List<SingleProduct> list) {
        if (list != null && list.size() > 1) {
            Collections.sort(list, new Comparator<SingleProduct>() {
                public int compare(SingleProduct o1, SingleProduct o2) {
                    if (Double.parseDouble(o1.getPrice()) == Double.parseDouble(o2.getPrice())) return 0;
                    return Double.parseDouble(o1.getPrice()) < Double.parseDouble(o2.getPrice()) ? 1 : -1;
                }
            });
        }
        return list;
    }

    private void loadDataLowToHigh(List<SingleProduct> singleProductList) {
        List<SingleProduct> list = sortLowToHighList(singleProductList);
        adapter = new SingleProductAdapter(list, this);
        recyclerViewDrink.setAdapter(adapter);
        ViewCompat.setNestedScrollingEnabled(recyclerViewDrink, false);

    }

    private List<SingleProduct> sortLowToHighList(List<SingleProduct> list) {
        if (list != null && list.size() > 1) {
            Collections.sort(list, new Comparator<SingleProduct>() {
                public int compare(SingleProduct o1, SingleProduct o2) {
                    if (Double.parseDouble(o1.getPrice()) == Double.parseDouble(o2.getPrice())) return 0;
                    return Double.parseDouble(o1.getPrice()) > Double.parseDouble(o2.getPrice()) ? 1 : -1;
                }
            });
        }
        return list;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onRefresh() {
        getSingleProduct(Common.menu_id);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSingleProduct(Common.menu_id);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
