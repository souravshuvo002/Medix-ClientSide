package com.example.medix.Activity.MedicineShop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medix.Adapter.ProductAdapter;
import com.example.medix.Adapter.SubCategoryAdapter;
import com.example.medix.Api.ApiClient;
import com.example.medix.Api.ApiService;
import com.example.medix.Common.Common;
import com.example.medix.Helper.NetWorkConfig;
import com.example.medix.Model.Result;
import com.example.medix.Model.SingleProduct;
import com.example.medix.Model.SubCategory;
import com.example.medix.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SubCategoryActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerViewSubCategories, recyclerViewProducts;
    private SubCategoryAdapter adapter;
    private ProductAdapter productAdapter;
    private LinearLayoutManager mLayoutManagerSubCategories;
    private TextView textViewSubCatName, textViewSubCatCount;
    public ImageView imageViewBackButton, imageViewHome, imageViewSort, imageViewSwitch;
    private NetWorkConfig netWorkConfig;
    public LinearLayout layoutViewCart, layCart, linearLayProduct;
    public RelativeLayout layEmpty;
    public TextView textViewPrice, textViewMenuName;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String category_id, category_name;
    private List<SingleProduct> singleProductList;
    boolean isViewWithCatalog = true;
    private NestedScrollView nsv;
    boolean isPressed = false;

    final int duration = 10;
    final int pixelsToMove = 30;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Runnable SCROLLING_RUNNABLE = new Runnable() {

        @Override
        public void run() {
            recyclerViewSubCategories.smoothScrollBy(pixelsToMove, 0);
            mHandler.postDelayed(this, duration);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);

        netWorkConfig = new NetWorkConfig(this);
        /**
         *  Network Connection Check
         */
        if (!netWorkConfig.isNetworkAvailable()) {
            netWorkConfig.createNetErrorDialog();
            return;
        }

        // getting Intent From MenuAdapter
        category_id = getIntent().getStringExtra("Menu_ID");
        category_name = getIntent().getStringExtra("Menu_NAME");
        Log.d("ID: ", category_id);

        // getting views
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().
                getColor(android.R.color.holo_blue_dark), getResources().
                getColor(android.R.color.holo_red_dark), getResources().
                getColor(android.R.color.holo_green_light), getResources().
                getColor(android.R.color.holo_orange_dark));
        textViewSubCatName = (TextView) findViewById(R.id.textViewSubCatName);
        textViewSubCatCount = (TextView) findViewById(R.id.textViewSubCatCount);
        imageViewBackButton = (ImageView) findViewById(R.id.imageViewBack);
        imageViewSort = (ImageView) findViewById(R.id.imageViewSort);
        imageViewSwitch = (ImageView) findViewById(R.id.imageViewSwitch);
        imageViewHome = (ImageView) findViewById(R.id.imageViewHome);

        linearLayProduct = (LinearLayout) findViewById(R.id.linearLayProduct);
        layEmpty = (RelativeLayout) findViewById(R.id.layEmpty);
        layCart = (LinearLayout) findViewById(R.id.layCart);
        layoutViewCart = (LinearLayout) findViewById(R.id.layViewCart);
        textViewPrice = (TextView) findViewById(R.id.tvPrice);

        recyclerViewSubCategories = (RecyclerView) findViewById(R.id.recyclerViewSubCategories);
        recyclerViewSubCategories.setHasFixedSize(true);
        mLayoutManagerSubCategories = new LinearLayoutManager(SubCategoryActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewSubCategories.setLayoutManager(mLayoutManagerSubCategories);
        recyclerViewSubCategories.setItemAnimator(new DefaultItemAnimator());
        //recyclerViewSubCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        recyclerViewProducts = (RecyclerView) findViewById(R.id.recyclerViewProducts);
        recyclerViewProducts.setHasFixedSize(true);
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        imageViewBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        imageViewHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SubCategoryActivity.this, MedicineHomeActivity.class));
                finish();
            }
        });

        imageViewSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isViewWithCatalog = !isViewWithCatalog;
                supportInvalidateOptionsMenu();
                //loading = false;
                recyclerViewProducts.setLayoutManager(isViewWithCatalog ? new LinearLayoutManager(getApplicationContext()) : new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                recyclerViewProducts.setAdapter(productAdapter);
            }
        });

        imageViewSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });

        // get all sub categories by Categories
        getSubCategories(category_id);
        // get all product by Main Categories
        getProduct(category_id);
        String str = category_name;
        String replacedStr = str.replaceAll("&amp;", "&");
        textViewSubCatName.setText(replacedStr);
    }

    private void getProduct(String category_id) {
        /*final android.app.AlertDialog waitingDialog = new SpotsDialog(SubCategoryActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");*/

        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
        Call<Result> call = service.getProductBySubCatId(category_id);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                //waitingDialog.dismiss();
                singleProductList = response.body().getSingleProductList();
                displaySingleProduct(response.body().getSingleProductList());

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displaySingleProduct(final List<SingleProduct> singleProductList) {
        swipeRefreshLayout.setRefreshing(false);
        if (singleProductList.size() <= 0) {
            layEmpty.setVisibility(View.VISIBLE);
            linearLayProduct.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "No product found!", Toast.LENGTH_SHORT).show();
            //finish();
        }
        //Toast.makeText(getApplicationContext(), singleProductList.size() + " products found!", Toast.LENGTH_LONG).show();
        productAdapter = new ProductAdapter(singleProductList, this);
        recyclerViewProducts.setAdapter(productAdapter);
        ViewCompat.setNestedScrollingEnabled(recyclerViewProducts, false);
    }

    private void getSubCategories(String category_id) {

        final android.app.AlertDialog waitingDialog = new SpotsDialog(SubCategoryActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");

        Log.d("Parent_Id: ", category_id);

        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
        Call<Result> call = service.getSubCategoryById(category_id);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                waitingDialog.dismiss();
                displaySubCategories(response.body().getSubCategories());
            }
            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displaySubCategories(List<SubCategory> subCategories) {
        swipeRefreshLayout.setRefreshing(false);
        if (subCategories.size() <= 0) {
            Toast.makeText(getApplicationContext(), "No sub category found!", Toast.LENGTH_SHORT).show();
            //finish();
        }
        textViewSubCatCount.setVisibility(View.VISIBLE);
        textViewSubCatCount.setText(subCategories.size() + " sub categories found.");
        adapter = new SubCategoryAdapter(subCategories, this);
        recyclerViewSubCategories.setAdapter(adapter);
        ViewCompat.setNestedScrollingEnabled(recyclerViewSubCategories, false);

        /*recyclerViewSubCategories.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastItem = mLayoutManagerSubCategories.findLastCompletelyVisibleItemPosition();
                if(lastItem == mLayoutManagerSubCategories.getItemCount()-1){
                    mHandler.removeCallbacks(SCROLLING_RUNNABLE);
                    Handler postHandler = new Handler();
                    postHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerViewSubCategories.setAdapter(null);
                            recyclerViewSubCategories.setAdapter(adapter);
                            mHandler.postDelayed(SCROLLING_RUNNABLE, 4000);
                        }
                    }, 4000);
                }
            }
        });
        mHandler.postDelayed(SCROLLING_RUNNABLE, 4000);*/

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
        productAdapter = new ProductAdapter(list, this);
        recyclerViewProducts.setAdapter(productAdapter);
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
        productAdapter = new ProductAdapter(list, this);
        recyclerViewProducts.setAdapter(productAdapter);
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
        productAdapter = new ProductAdapter(list, this);
        recyclerViewProducts.setAdapter(productAdapter);
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
        productAdapter = new ProductAdapter(list, this);
        recyclerViewProducts.setAdapter(productAdapter);
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
    public void onRefresh() {
        getSubCategories(Common.menu_id);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
