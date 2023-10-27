package com.example.medix.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medix.Adapter.SearchAdapter;
import com.example.medix.Api.ApiClient;
import com.example.medix.Api.ApiService;
import com.example.medix.Helper.NetWorkConfig;
import com.example.medix.Model.Result;
import com.example.medix.Model.SingleProduct;
import com.example.medix.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    String menu_id, menu_name;
    RecyclerView recyclerViewDrink;
    SearchAdapter adapter;
    private EditText edit_search;
    public LinearLayout layoutViewCart, layoutCart, layCart;
    public TextView textViewPrice, textViewTotal;
    public ImageView imageViewBackButton, imageViewSort, imageViewSwitch;
    private NetWorkConfig netWorkConfig;
    private SwipeRefreshLayout swipeRefreshLayout;
    boolean isViewWithCatalog = true;
    List<SingleProduct> singleProductList;
    private String max, min;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        netWorkConfig = new NetWorkConfig(getActivity());

        /**
         *  Network Connection Check
         */
        if (!netWorkConfig.isNetworkAvailable()) {
            netWorkConfig.createNetErrorDialog();
        }


        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().
                getColor(android.R.color.holo_blue_dark), getResources().
                getColor(android.R.color.holo_red_dark), getResources().
                getColor(android.R.color.holo_green_light), getResources().
                getColor(android.R.color.holo_orange_dark));
        layoutViewCart = (LinearLayout) view.findViewById(R.id.layViewCart);
        layCart = (LinearLayout) view.findViewById(R.id.layCart);

        textViewTotal = (TextView) view.findViewById(R.id.textViewTotal);
        edit_search = (EditText) view.findViewById(R.id.edit_search);
        textViewPrice = (TextView) view.findViewById(R.id.tvPrice);
        imageViewBackButton = (ImageView) view.findViewById(R.id.imageViewBack);
        imageViewSort = (ImageView) view.findViewById(R.id.imageViewSort);
        imageViewSwitch = (ImageView) view.findViewById(R.id.imageViewSwitch);
        layoutCart = (LinearLayout) view.findViewById(R.id.layCart);

        recyclerViewDrink = (RecyclerView) view.findViewById(R.id.recycler_view_drink);

        // Removes blinks
        ((SimpleItemAnimator) recyclerViewDrink.getItemAnimator()).setSupportsChangeAnimations(false);

        recyclerViewDrink.setHasFixedSize(true);
        //recyclerViewDrink.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewDrink.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        //recyclerViewDrink.setAdapter(adapter);

        imageViewBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
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
                getActivity().supportInvalidateOptionsMenu();
                //loading = false;
                recyclerViewDrink.setLayoutManager(isViewWithCatalog ? new LinearLayoutManager(getActivity()) : new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                recyclerViewDrink.setAdapter(adapter);
            }
        });

        //adding a TextChangedListener
        //to call a method whenever there is some change on the EditText
        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
                filter(editable.toString());
            }
        });

        // get all product
        getAllProduct();

        return view;
    }

    private void getAllProduct() {

        final android.app.AlertDialog waitingDialog = new SpotsDialog(getActivity());
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");

        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
        Call<Result> call = service.getAllProduct();

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                waitingDialog.dismiss();
                singleProductList = response.body().getAllProductList();
                displaySingleProduct(response.body().getAllProductList());

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displaySingleProduct(List<SingleProduct> singleProductList) {
        swipeRefreshLayout.setRefreshing(false);
        if (singleProductList.size() <= 0) {
            Toast.makeText(getActivity(), "No product found!", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
        textViewTotal.setText(singleProductList.size()+ " products found");
        adapter = new SearchAdapter(singleProductList, SearchFragment.this, getActivity());
        recyclerViewDrink.setAdapter(adapter);
        ViewCompat.setNestedScrollingEnabled(recyclerViewDrink, false);
        objectArrayList = singleProductList;
        adapter.notifyDataSetChanged();

    }

    private void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_sort_dialog, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
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

    private void loadPriceRange(List<SingleProduct> singleProductList, Number minValue, Number maxValue) {
        List<SingleProduct> list = sortPriceRangeData(singleProductList, minValue, maxValue);
        adapter = new SearchAdapter(list, SearchFragment.this, getActivity());
        recyclerViewDrink.setAdapter(adapter);
        ViewCompat.setNestedScrollingEnabled(recyclerViewDrink, false);
    }

    private List<SingleProduct> sortPriceRangeData(List<SingleProduct> list, Number minValue, Number maxValue) {
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

    private void loadDataDiscount(List<SingleProduct> singleProductList) {
        List<SingleProduct> list = sortDiscountData(singleProductList);
        adapter = new SearchAdapter(list, SearchFragment.this, getActivity());
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

    private void loadDataName(List<SingleProduct> singleProductList) {
        List<SingleProduct> list = sortNameList(singleProductList);
        adapter = new SearchAdapter(list, SearchFragment.this, getActivity());
        recyclerViewDrink.setAdapter(adapter);
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

    private void loadDataHighToLow(List<SingleProduct> singleProductList) {
        List<SingleProduct> list = sortHighToLowList(singleProductList);
        adapter = new SearchAdapter(list, SearchFragment.this, getActivity());
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
        adapter = new SearchAdapter(list, SearchFragment.this, getActivity());
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

    private List<SingleProduct> objectArrayList = new ArrayList<SingleProduct>();

    void filter(String text){
        List<SingleProduct> temp = new ArrayList();
        for(SingleProduct d: objectArrayList){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(d.getProduct_name().toLowerCase().contains(text.toLowerCase())
                    || d.getCat_name().toLowerCase().contains(text.toLowerCase())
                    || d.getModel().toLowerCase().contains(text.toLowerCase())
                    || d.getPrice().toLowerCase().contains(text.toLowerCase())
                    || d.getProduct_id().toLowerCase().contains(text.toLowerCase())
                    || d.getDiscount_price().toLowerCase().contains(text.toLowerCase())){
                temp.add(d);
            }
        }
        //update recyclerview
        adapter.updateList(temp);
    }

    @Override
    public void onRefresh() {
        edit_search.setText("");
        getAllProduct();
    }
}
