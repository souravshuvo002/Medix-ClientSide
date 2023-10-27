package com.example.medix.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medix.Activity.ChangePasswordActivity;
import com.example.medix.Activity.EditCustomerInfoActivity;
import com.example.medix.Activity.LoginActivity2;
import com.example.medix.Activity.MedicineShop.AllMedicineOrderHistoryActivity;
import com.example.medix.Api.ApiClient;
import com.example.medix.Api.ApiService;
import com.example.medix.Helper.NetWorkConfig;
import com.example.medix.Model.Result;
import com.example.medix.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    public LinearLayout layOrderInfo, layWishListInfo, layLogOutInfo, layPersonalInfo, layChanePasswordInfo;
    public TextView textViewUserName, textViewUserPhone, textViewUserEmail, textViewUserAddress, textViewContactEdit;
    private NetWorkConfig netWorkConfig;
    private Toolbar toolbar;
    public BottomNavigationView mBottomNavigationView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        netWorkConfig = new NetWorkConfig(getActivity());

        mBottomNavigationView = getActivity().findViewById(R.id.navigation);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar); //toolbar id
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Dashboard");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomNavigationView.setSelectedItemId(R.id.navigation_home);
                mBottomNavigationView.getMenu().findItem(R.id.navigation_home).setChecked(true);
            }
        });
        textViewContactEdit = (TextView) view.findViewById(R.id.textViewContactEdit);
        textViewUserName = (TextView) view.findViewById(R.id.textViewUserName);
        textViewUserPhone = (TextView) view.findViewById(R.id.textViewUserPhone);
        textViewUserEmail = (TextView) view.findViewById(R.id.textViewUserEmail);
        textViewUserAddress = (TextView) view.findViewById(R.id.textViewUserAddress);
        layPersonalInfo = (LinearLayout) view.findViewById(R.id.layPersonalInfo);
        layOrderInfo = (LinearLayout) view.findViewById(R.id.layOrderInfo);
        layChanePasswordInfo = (LinearLayout) view.findViewById(R.id.layChanePasswordInfo);
        layWishListInfo = (LinearLayout) view.findViewById(R.id.layWishListInfo);
        layLogOutInfo = (LinearLayout) view.findViewById(R.id.layLogOutInfo);

        textViewContactEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditCustomerInfoActivity.class));
            }
        });

        layOrderInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AllMedicineOrderHistoryActivity.class));
            }
        });
        layWishListInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                final String Customer_Id = sharedPreferences.getString("CUSTOMER_ID", null);

                //Defining retrofit api service
                ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
                Call<Result> resultCall = service.getCustomerWishList(Customer_Id);

                resultCall.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        if(response.body().getWishListList().size() <= 0)
                        {
                            Toast.makeText(getActivity(), "No Wish list item", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Fragment favFragment = new WishListFragment();
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container, favFragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                            return;
                        }
                    }
                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        layChanePasswordInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
            }
        });
        layLogOutInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogOut();
            }
        });

        // get Customer Data
        getCustomerData();

        return view;
    }

    private void LogOut() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Log out Application");
        alertDialog.setMessage("Do you really want to log out from Dhacai?");
        alertDialog.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.setNegativeButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                // Clearing Data from Shared Preferences
                editor.clear();
                editor.commit();

                // Clear All Activity
                Intent intent = new Intent(getActivity(), LoginActivity2.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Toast.makeText(getActivity(), "Successfully logged out!", Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
        });
        AlertDialog dialog = alertDialog.create();
        dialog.show();
        Button b = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (b != null) {
            b.setTextColor(Color.parseColor("#FF8A65"));
        }
    }

    private void getCustomerData() {
        final android.app.AlertDialog waitingDialog = new SpotsDialog(getActivity());
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");

        String Email;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Email = sharedPreferences.getString("EMAIL", null);

        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
        Call<Result> call = service.getCustomerInfo(Email);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                waitingDialog.dismiss();

                if (!response.body().getError()) {

                    layPersonalInfo.setVisibility(View.VISIBLE);
                    textViewUserName.setText(response.body().getUser().getFirstname() + " " + response.body().getUser().getLastname());
                    textViewUserEmail.setText(response.body().getUser().getEmail());
                    textViewUserPhone.setText(response.body().getUser().getTelephone());
                    textViewUserAddress.setText("Bangladesh");
                } else {
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getCustomerData();
    }

}
