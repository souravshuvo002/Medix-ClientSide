package com.example.medix.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.medix.Api.ApiClient;
import com.example.medix.Api.ApiService;
import com.example.medix.Api.ApiURL;
import com.example.medix.Common.Common;
import com.example.medix.Model.Result;
import com.example.medix.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class LoginActivity2 extends AppCompatActivity {

    public AppCompatButton btn_reg, btn_login;
    private TextInputEditText editTextEmail, editTextPassword;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        makeFullScreen();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity2.this);
        editor = sharedPreferences.edit();

        // getting views
        editTextEmail = (TextInputEditText) findViewById(R.id.input_email);
        editTextPassword = (TextInputEditText) findViewById(R.id.input_password);

        btn_reg = (AppCompatButton) findViewById(R.id.btn_create_account);
        btn_login = (AppCompatButton) findViewById(R.id.btn_login);
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity2.this, RegistrationActivity2.class));
                finish();
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginCustomer();
            }
        });
    }

    private void loginCustomer() {
        final android.app.AlertDialog waitingDialog = new SpotsDialog(LoginActivity2.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");


        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
        Call<Result> call = service.loginCustomer2(editTextEmail.getText().toString(), editTextPassword.getText().toString());

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                waitingDialog.dismiss();

                if(!response.body().getError())
                {
                    // Clearing shared preferences
                    editor.clear();
                    editor.commit();

                    editor.putString("USERNAME", response.body().getUser().getFirstname() + " " + response.body().getUser().getLastname());
                    editor.putString("EMAIL", editTextEmail.getText().toString());
                    editor.putString("PASSWORD", editTextPassword.getText().toString());
                    editor.putString("CUSTOMER_ID", response.body().getUser().getCustomer_id());
                    editor.apply();

                    Common.Customer_email = editTextEmail.getText().toString();
                    Common.CUSTOMER_ID = response.body().getUser().getCustomer_id();

                    updateTokenToServer();

                    Toast.makeText(getApplicationContext(), "Welcome back " + response.body().getUser().getFirstname() + " " + response.body().getUser().getLastname(), Toast.LENGTH_LONG).show();

                    startActivity(new Intent(LoginActivity2.this, MainActivity.class));
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

    public void makeFullScreen() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
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
                                .baseUrl(ApiURL.MEDIX_SHOP_URL)
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
                        Toast.makeText(LoginActivity2.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        String Email;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity2.this);
        Email = sharedPreferences.getString("EMAIL", null);
        if (Email != null) {
            Intent intent = new Intent(LoginActivity2.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

}
