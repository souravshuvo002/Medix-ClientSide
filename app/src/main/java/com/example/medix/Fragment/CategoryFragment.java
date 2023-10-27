package com.example.medix.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.medix.Adapter.AllCategoriesAdapter;
import com.example.medix.Api.ApiClient;
import com.example.medix.Api.ApiService;
import com.example.medix.Helper.NetWorkConfig;
import com.example.medix.Model.MainCategory;
import com.example.medix.Model.Result;
import com.example.medix.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    RecyclerView recyclerViewCategories;
    AllCategoriesAdapter adapter;
    public ImageView imageViewBackButton;
    private NetWorkConfig netWorkConfig;
    private SwipeRefreshLayout swipeRefreshLayout;
    public BottomNavigationView mBottomNavigationView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        netWorkConfig = new NetWorkConfig(getActivity());
        /**
         *  Network Connection Check
         */
        if(!netWorkConfig.isNetworkAvailable())
        {
            netWorkConfig.createNetErrorDialog();
        }
        mBottomNavigationView = getActivity().findViewById(R.id.navigation);

        // getting views
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().
                getColor(android.R.color.holo_blue_dark),getResources().
                getColor(android.R.color.holo_red_dark),getResources().
                getColor(android.R.color.holo_green_light),getResources().
                getColor(android.R.color.holo_orange_dark));
        imageViewBackButton = (ImageView) view.findViewById(R.id.imageViewBack);
        recyclerViewCategories = (RecyclerView) view.findViewById(R.id.recyclerViewCategories);
        recyclerViewCategories.setHasFixedSize(true);
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        imageViewBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomNavigationView.setSelectedItemId(R.id.navigation_home);
                mBottomNavigationView.getMenu().findItem(R.id.navigation_home).setChecked(true);
            }
        });

        // get all categories
        getCategories();

        return view;
    }

    private void getCategories() {

        final android.app.AlertDialog waitingDialog = new SpotsDialog(getActivity());
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");

        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
        Call<Result> call = service.getAllCategory();

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                waitingDialog.dismiss();
                displayCategories(response.body().getMenus());

            }
            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayCategories(List<MainCategory> menus) {
        swipeRefreshLayout.setRefreshing(false);
        if(menus.size() <=0)
        {
            Toast.makeText(getActivity(),"No category found!", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
        adapter = new AllCategoriesAdapter(menus, getActivity());
        recyclerViewCategories.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {
        getCategories();
    }
}
