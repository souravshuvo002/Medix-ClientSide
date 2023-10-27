package com.example.medix.Activity.MedicineShop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.medix.Adapter.LatestProductAdapter;
import com.example.medix.Api.ApiClient;
import com.example.medix.Api.ApiService;
import com.example.medix.Helper.NetWorkConfig;
import com.example.medix.Model.Result;
import com.example.medix.Model.SingleProduct;
import com.example.medix.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LatestProductActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    public RecyclerView recycler_view_latest_product;
    public ImageView imageViewBackButton, imageViewSort, imageViewSwitch;
    private FloatingActionButton fab;
    private LatestProductAdapter adapter;
    private NetWorkConfig netWorkConfig;
    private SwipeRefreshLayout swipeRefreshLayout;
    boolean isViewWithCatalog = true;
    private List<SingleProduct> singleProductList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latest_product);

        netWorkConfig = new NetWorkConfig(this);
        /**
         *  Network Connection Check
         */
        if(!netWorkConfig.isNetworkAvailable())
        {
            netWorkConfig.createNetErrorDialog();
            return;
        }

        // getting views
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                Intent intent = new Intent(LatestProductActivity.this, MedicineCartActivity.class);
                startActivity(intent);
                finish();
            }
        });
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().
                getColor(android.R.color.holo_blue_dark),getResources().
                getColor(android.R.color.holo_red_dark),getResources().
                getColor(android.R.color.holo_green_light),getResources().
                getColor(android.R.color.holo_orange_dark));
        imageViewBackButton = (ImageView) findViewById(R.id.imageViewBack);
        imageViewSort = (ImageView) findViewById(R.id.imageViewSort);
        imageViewSwitch = (ImageView) findViewById(R.id.imageViewSwitch);
        recycler_view_latest_product = (RecyclerView) findViewById(R.id.recycler_view_latest_product);
        recycler_view_latest_product.setHasFixedSize(true);
        recycler_view_latest_product.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

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
                recycler_view_latest_product.setLayoutManager(isViewWithCatalog ? new LinearLayoutManager(getApplicationContext()) : new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                recycler_view_latest_product.setAdapter(adapter);
            }
        });

        // get all Latest product
        getLatestProduct();
    }

    private void getLatestProduct() {

        final android.app.AlertDialog waitingDialog = new SpotsDialog(LatestProductActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");

        swipeRefreshLayout.setRefreshing(false);
        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
        Call<Result> call = service.getAllLatestProductLimit("50");

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                singleProductList = response.body().getAllLatestProductLimit();
                displayHotDealProduct(response.body().getAllLatestProductLimit());
                waitingDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.d("Error: ", t.getMessage());
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayHotDealProduct(List<SingleProduct> latestProduct) {
        adapter = new LatestProductAdapter(latestProduct, this);
        recycler_view_latest_product.setAdapter(adapter);
        ViewCompat.setNestedScrollingEnabled(recycler_view_latest_product, false);
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
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

        linearLayDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadDataDiscount(singleProductList);
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
        adapter = new LatestProductAdapter(list, this);
        recycler_view_latest_product.setAdapter(adapter);
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
        Log.d("DIS_SIZE: ", String.valueOf(singleProductList.size()));
        List<SingleProduct> list = sortDiscountData(singleProductList);
        adapter = new LatestProductAdapter(list, this);
        recycler_view_latest_product.setAdapter(adapter);
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
        adapter = new LatestProductAdapter(list, this);
        recycler_view_latest_product.setAdapter(adapter);
    }

    private List<SingleProduct> sortHighToLowList(List<SingleProduct> list) {
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

    private void loadDataLowToHigh(List<SingleProduct> singleProductList) {
        List<SingleProduct> list = sortLowToHighList(singleProductList);
        adapter = new LatestProductAdapter(list, this);
        recycler_view_latest_product.setAdapter(adapter);
    }

    private List<SingleProduct> sortLowToHighList(List<SingleProduct> list) {
        if (list != null && list.size() > 1) {
            Collections.sort(list, new Comparator<SingleProduct>() {
                public int compare(SingleProduct o1, SingleProduct o2) {
                    if (Double.parseDouble(o1.getDiscount_price()) == Double.parseDouble(o2.getDiscount_price())) return 0;
                    return Double.parseDouble(o1.getDiscount_price()) > Double.parseDouble(o2.getDiscount_price()) ? 1 : -1;
                }
            });
        }
        return list;
    }

    @Override
    public void onRefresh() {
        getLatestProduct();
        //getBanner();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLatestProduct();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
