package com.example.medix.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.medix.Api.ApiClient;
import com.example.medix.Api.ApiService;
import com.example.medix.Api.ApiURL;
import com.example.medix.Common.Common;
import com.example.medix.Model.Result;
import com.example.medix.R;
import com.example.medix.Service.MyFirebaseMessaging;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class RegistrationActivity extends AppCompatActivity {


    private TextInputEditText editTextFirstName, editTextLastName, editTextEmail, editTextPhone, editTextPassword, editTextConPassword, editTextAddress;
    private CheckBox checkboxTermAgreement;
    private LinearLayout laySignIn;
    private Button btn_reg;

    private String strFName, strLName, strEmail, strPhone, strAddress, strPass, strConPass;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RegistrationActivity.this);
        editor = sharedPreferences.edit();

        // getting views
        editTextFirstName = (TextInputEditText) findViewById(R.id.editTextFirstName);
        editTextLastName = (TextInputEditText) findViewById(R.id.editTextLastName);
        editTextEmail = (TextInputEditText) findViewById(R.id.editTextEmail);
        editTextPhone = (TextInputEditText) findViewById(R.id.editTextPhone);
        editTextAddress = (TextInputEditText) findViewById(R.id.editTextAddress);
        editTextPassword = (TextInputEditText) findViewById(R.id.editTextPassword);
        editTextConPassword = (TextInputEditText) findViewById(R.id.editTextConPassword);
        checkboxTermAgreement = (CheckBox) findViewById(R.id.checkboxTermAgreement);
        laySignIn = (LinearLayout) findViewById(R.id.laySignIn);
        btn_reg = (Button) findViewById(R.id.btn_reg);

        // setting phone number
        editTextPhone.setText(Common.Customer_phone);

        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });


        laySignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void registerUser() {
        if(checkboxTermAgreement.isChecked()){

            strFName = editTextFirstName.getText().toString().trim();
            strLName = editTextLastName.getText().toString().trim();
            strEmail = editTextEmail.getText().toString().trim();
            strPhone = editTextPhone.getText().toString().trim();
            strAddress = editTextAddress.getText().toString().trim();
            strPass = editTextPassword.getText().toString().trim();
            strConPass = editTextConPassword.getText().toString().trim();

            if(!strPass.equals(strConPass))
            {
                Toast.makeText(getApplicationContext(), "Password doesn't match", Toast.LENGTH_LONG).show();
                return;
            }
            else
            {

                final android.app.AlertDialog waitingDialog = new SpotsDialog(RegistrationActivity.this);
                waitingDialog.show();
                waitingDialog.setMessage("Please wait ...");

                //Defining retrofit api service
                ApiService service = ApiClient.getClientMedix().create(ApiService.class);
                Call<Result> call = service.registerCustomer(strFName, strLName, strEmail, strPhone, strPass, strAddress);

                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        waitingDialog.dismiss();

                        if(!response.body().getError())
                        {
                            editor.putString("USERNAME", response.body().getUser().getFirst_name() + " " + response.body().getUser().getLast_name());
                            editor.putString("PHONE", editTextPhone.getText().toString());
                            editor.putString("PASSWORD", editTextPassword.getText().toString());
                            editor.putString("CUSTOMER_ID", response.body().getUser().getCustomer_id());
                            editor.apply();

                            Common.Customer_phone = strPhone;
                            Common.CUSTOMER_ID = response.body().getUser().getCustomer_id();

                            updateTokenToServer();

                            Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                            startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                            finish();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }
        else
        {
            Toast.makeText(getApplicationContext(), "You must accept privacy policy and term & condition in order to sign up for Dhacai.", Toast.LENGTH_LONG).show();
        }
    }

    private void updateTokenToServer() {


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String CUSTOMER_ID = sharedPreferences.getString("CUSTOMER_ID", null);

        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {

                        //building retrofit object
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(ApiURL.MEDIX_URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        //Defining retrofit api service
                        ApiService service = retrofit.create(ApiService.class);

                        //defining the call
                        Call<Result> call = service.updateCustomerToken(CUSTOMER_ID, instanceIdResult.getToken());
                        //calling the api
                        call.enqueue(new Callback<Result>() {
                            @Override
                            public void onResponse(Call<Result> call, Response<Result> response) {
                                Log.e("MA_Debug: ", response.body().getMessage());
                            }

                            @Override
                            public void onFailure(Call<Result> call, Throwable t) {
                                Log.e("MA_Debug: ", t.getMessage());
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegistrationActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}