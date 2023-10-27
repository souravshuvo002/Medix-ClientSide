package com.example.medix.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.example.medix.Activity.MedicineShop.HotDealsActivity;
import com.example.medix.Activity.MedicineShop.LatestProductActivity;
import com.example.medix.Adapter.CategoryAdapter;
import com.example.medix.Adapter.HotDealAdapter;
import com.example.medix.Api.ApiClient;
import com.example.medix.Api.ApiService;
import com.example.medix.Helper.GridSpacingItemDecoration;
import com.example.medix.Helper.NetWorkConfig;
import com.example.medix.Model.Banner;
import com.example.medix.Model.MainCategory;
import com.example.medix.Model.Result;
import com.example.medix.Model.SingleProduct;
import com.example.medix.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.disposables.CompositeDisposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    SliderLayout sliderLayout;
    RecyclerView recycler_view_cat, recycler_view_latest, recycler_view_hot_deal;
    private LinearLayout linearLayMain, linearLayLatestProduct, linearLayHotDeals;
    private TextView tvSeeAllCat, tvSeeAllLatest, tvSeeAllHotDeal;
    private NetWorkConfig netWorkConfig;
    SharedPreferences myPreferences;
    SharedPreferences.Editor myEditor;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BottomNavigationView mBottomNavigationView;
    // RxJava
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Medix Medicine");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        netWorkConfig = new NetWorkConfig(getActivity());

        /**
         *  Network Connection Check
         */
        if (!netWorkConfig.isNetworkAvailable()) {
            netWorkConfig.createNetErrorDialog();
        }

        mBottomNavigationView = getActivity().findViewById(R.id.navigation);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().
                getColor(android.R.color.holo_blue_dark), getResources().
                getColor(android.R.color.holo_red_dark), getResources().
                getColor(android.R.color.holo_green_light), getResources().
                getColor(android.R.color.holo_orange_dark));
        linearLayMain = (LinearLayout) view.findViewById(R.id.linearLayMain);
        linearLayLatestProduct = (LinearLayout) view.findViewById(R.id.linearLayLatestProduct);
        linearLayHotDeals = (LinearLayout) view.findViewById(R.id.linearLayHotDeals);
        sliderLayout = (SliderLayout) view.findViewById(R.id.sliderLay);
        tvSeeAllCat = (TextView) view.findViewById(R.id.tvSeeAllCat);
        tvSeeAllLatest = (TextView) view.findViewById(R.id.tvSeeAllLatest);
        tvSeeAllHotDeal = (TextView) view.findViewById(R.id.tvSeeAllHotDeal);
        recycler_view_cat = (RecyclerView) view.findViewById(R.id.recycler_view_cat);
        recycler_view_cat.setHasFixedSize(false);
        /*LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recycler_view_cat.setLayoutManager(layoutManager);
        recycler_view_cat.setItemAnimator(new DefaultItemAnimator());*/
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
        recycler_view_cat.setLayoutManager(mLayoutManager);
        recycler_view_cat.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(0), true));
        recycler_view_cat.setItemAnimator(new DefaultItemAnimator());


        recycler_view_latest = (RecyclerView) view.findViewById(R.id.recycler_view_latest);
        recycler_view_latest.setHasFixedSize(true);
        LinearLayoutManager mLayoutManagerLatest = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recycler_view_latest.setLayoutManager(mLayoutManagerLatest);
        recycler_view_latest.setItemAnimator(new DefaultItemAnimator());
        recycler_view_latest.setItemViewCacheSize(20);
        recycler_view_latest.setDrawingCacheEnabled(true);
        recycler_view_latest.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        /*RecyclerView.LayoutManager mLayoutManagerLatest = new GridLayoutManager(getActivity(), 4);
        recycler_view_latest.setLayoutManager(mLayoutManagerLatest);
        recycler_view_latest.addItemDecoration(new GridSpacingItemDecoration(4, dpToPx(0), true));*/

        recycler_view_hot_deal = (RecyclerView) view.findViewById(R.id.recycler_view_hot_deal);
        recycler_view_hot_deal.setHasFixedSize(true);
        LinearLayoutManager mLayoutManagerFeature = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recycler_view_hot_deal.setLayoutManager(mLayoutManagerFeature);
        recycler_view_hot_deal.setItemAnimator(new DefaultItemAnimator());
        recycler_view_hot_deal.setItemViewCacheSize(20);
        recycler_view_hot_deal.setDrawingCacheEnabled(true);
        recycler_view_hot_deal.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        /*RecyclerView.LayoutManager mLayoutManagerFeature = new GridLayoutManager(getActivity(), 4);
        recycler_view_feature.setLayoutManager(mLayoutManagerFeature);
        recycler_view_feature.addItemDecoration(new GridSpacingItemDecoration(4, dpToPx(0), true));*/
        //recyclerViewMenu.setItemAnimator(new DefaultItemAnimator());

        tvSeeAllCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomNavigationView.setSelectedItemId(R.id.navigation_category);
                mBottomNavigationView.getMenu().findItem(R.id.navigation_category).setChecked(true);
            }
        });

        tvSeeAllLatest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LatestProductActivity.class));
            }
        });

        tvSeeAllHotDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), HotDealsActivity.class));
            }
        });

        // get Banner
        getBanner();
        // get MainCategory
        getMainCategory();
        // get Latest
        getLatestProduct();
        // get Hot Deal products
        getHotDealProduct();

        return view;
    }

    private void getBanner() {
        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
        Call<Result> call = service.getBanners();

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                displayBannerImages(response.body().getBanners());
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayBannerImages(List<Banner> banners) {

        HashMap<String, String> bannerMap = new HashMap<>();
        for (Banner item : banners) {
            bannerMap.put(item.getName(), item.getImage());
        }
        for (String name : bannerMap.keySet()) {
            TextSliderView textSliderView = new TextSliderView(getActivity());
            textSliderView.description(name)
                    .image(bannerMap.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(BaseSliderView slider) {
                            Toast.makeText(getActivity(), slider.getBundle().get("extra") + "", Toast.LENGTH_SHORT).show();
                        }
                    });

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", name);

            sliderLayout.addSlider(textSliderView);
        }

        // Slider Animation

        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Foreground2Background);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(4000);

    }

    private void getMainCategory() {
        swipeRefreshLayout.setRefreshing(false);
        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
        Call<Result> call = service.getMainCategoryByLimit();

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Log.d("Size: ", String.valueOf(response.body().getMenus().size()));
                displayCategory(response.body().getMenus());
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.d("Error: ", t.getMessage());
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayCategory(List<MainCategory> menus) {
        linearLayMain.setVisibility(View.VISIBLE);
        CategoryAdapter categoryAdapter = new CategoryAdapter(menus, getActivity());
        recycler_view_cat.setAdapter(categoryAdapter);
        //recycler_view_cat.addItemDecoration(new SpacesItemDecoration(10));
        ViewCompat.setNestedScrollingEnabled(recycler_view_cat, false);
    }

    private void getLatestProduct() {
        swipeRefreshLayout.setRefreshing(false);
        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
        Call<Result> call = service.getAllLatestProductLimit("10");

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                displayLatestProduct(response.body().getAllLatestProductLimit());
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.d("Error: ", t.getMessage());
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayLatestProduct(List<SingleProduct> singleProducts) {
        HotDealAdapter adapter = new HotDealAdapter(singleProducts, HomeFragment.this, getActivity());
        recycler_view_latest.setAdapter(adapter);
        //recycler_view_latest.addItemDecoration(new SpacesItemDecoration(10));
        ViewCompat.setNestedScrollingEnabled(recycler_view_latest, false);
    }

    private void getHotDealProduct() {
        swipeRefreshLayout.setRefreshing(false);
        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
        Call<Result> call = service.getAllHotDealProductLimit();

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if(response.body().getAllHotDealProduct().size() <=0)
                {
                    linearLayHotDeals.setVisibility(View.GONE);
                }
                else
                {
                    linearLayHotDeals.setVisibility(View.VISIBLE);
                    displayHotDealProduct(response.body().getAllHotDealProduct());
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.d("Error: ", t.getMessage());
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayHotDealProduct(List<SingleProduct> hotDeals) {
        HotDealAdapter adapter = new HotDealAdapter(hotDeals, HomeFragment.this, getActivity());
        recycler_view_hot_deal.setAdapter(adapter);
        //recycler_view_feature.setAdapter(menuAdapter);
        ViewCompat.setNestedScrollingEnabled(recycler_view_hot_deal, false);
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public void onRefresh() {
        getMainCategory();
        getLatestProduct();
        getHotDealProduct();
        //getBanner();
    }
}