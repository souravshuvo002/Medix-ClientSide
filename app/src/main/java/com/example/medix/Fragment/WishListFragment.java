package com.example.medix.Fragment;

import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.medix.Adapter.WishListAdapter;
import com.example.medix.Api.ApiClient;
import com.example.medix.Api.ApiService;
import com.example.medix.Helper.NetWorkConfig;
import com.example.medix.Model.Result;
import com.example.medix.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.os.Build.VERSION_CODES.JELLY_BEAN;

public class WishListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public RecyclerView recyclerViewFavorites;
    public ImageView imageViewRemoveAll, imageViewBackButton;
    private NetWorkConfig netWorkConfig;
    public BottomNavigationView mBottomNavigationView;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wish_list, container, false);

        netWorkConfig = new NetWorkConfig(getActivity());
        mBottomNavigationView = getActivity().findViewById(R.id.navigation);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ((ViewGroup) view.findViewById(R.id.animated_root)).getLayoutTransition()
                    .enableTransitionType(LayoutTransition.CHANGING);
        }

        // getting views
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().
                getColor(android.R.color.holo_blue_dark), getResources().
                getColor(android.R.color.holo_red_dark), getResources().
                getColor(android.R.color.holo_green_light), getResources().
                getColor(android.R.color.holo_orange_dark));
        imageViewBackButton = (ImageView) view.findViewById(R.id.imageViewBack);
        imageViewRemoveAll = (ImageView) view.findViewById(R.id.imageViewRemoveAll);
        recyclerViewFavorites = (RecyclerView) view.findViewById(R.id.recycler_view_Fav);
        recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerViewFavorites.setHasFixedSize(true);
        recyclerViewFavorites.setItemAnimator(new DefaultItemAnimator());
        recyclerViewFavorites.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));


        imageViewRemoveAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAllItems();
            }
        });

        imageViewBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getActivity().onBackPressed();
                mBottomNavigationView.setSelectedItemId(R.id.navigation_profile);
                mBottomNavigationView.getMenu().findItem(R.id.navigation_profile).setChecked(true);
            }
        });

        // Load WishList Items from Database
        loadWishListItems();

        return view;
    }

    private void loadWishListItems() {

        swipeRefreshLayout.setRefreshing(false);

        final android.app.AlertDialog waitingDialog = new SpotsDialog(getActivity());
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String Customer_Id = sharedPreferences.getString("CUSTOMER_ID", null);

        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
        Call<Result> resultCall = service.getCustomerWishList(Customer_Id);

        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if(response.body().getWishListList().size() < 0)
                {
                    getActivity().onBackPressed();
                    imageViewRemoveAll.setVisibility(View.INVISIBLE);
                }
                else
                {
                    imageViewRemoveAll.setVisibility(View.VISIBLE);
                }
                waitingDialog.dismiss();
                WishListAdapter adapter = new WishListAdapter(response.body().getWishListList(), getActivity());
                recyclerViewFavorites.setAdapter(adapter);
            }
            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                waitingDialog.dismiss();
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @TargetApi(JELLY_BEAN)
    private void enableChangingTransition(View view) {
        ViewGroup animatedRoot = (ViewGroup) view.findViewById(R.id.animated_root);
        animatedRoot.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    }

    private void removeAllItems() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String Customer_Id = sharedPreferences.getString("CUSTOMER_ID", null);
        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
        Call<Result> resultCall = service.getCustomerWishList(Customer_Id);

        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, final Response<Result> response) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("Remove All from Wish List");
                alertDialog.setMessage("Are you sure you want to remove all " + String.valueOf(response.body().getWishListList().size()) + " item(s) from your Wish List?");
                alertDialog.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.setNegativeButton("REMOVE ALL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final android.app.AlertDialog waitingDialog = new SpotsDialog(getActivity());
                        waitingDialog.show();
                        waitingDialog.setMessage("Removing ...");

                        // Delaying action for 1.5 seconds
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                waitingDialog.dismiss();

                                for (int i = 0; i<response.body().getWishListList().size();i++)
                                {
                                    // Removed to Wish List
                                    //Defining retrofit api service
                                    ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
                                    Call<Result> resultCall = service.removeWishlist(Customer_Id, response.body().getWishListList().get(i).getProduct_id());

                                    resultCall.enqueue(new Callback<Result>() {
                                        @Override
                                        public void onResponse(Call<Result> call, Response<Result> response) {
                                        }
                                        @Override
                                        public void onFailure(Call<Result> call, Throwable t) {
                                        }
                                    });
                                }
                                // Remove All cart items
                                Toast.makeText(getActivity(), "No Wish list items", Toast.LENGTH_SHORT).show();
                                getActivity().onBackPressed();
                            }
                        }, 1500);
                    }
                });
                AlertDialog dialog = alertDialog.create();
                dialog.show();
                Button b = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                if(b != null) {
                    b.setTextColor(Color.parseColor("#000000"));
                }
                Button b2 = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                if(b2 != null) {
                    b2.setTextColor(Color.parseColor("#000000"));
                }
            }
            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRefresh() {
        loadWishListItems();
    }
}
