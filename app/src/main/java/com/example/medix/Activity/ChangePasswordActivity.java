package com.example.medix.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.medix.Api.ApiClient;
import com.example.medix.Api.ApiService;
import com.example.medix.Common.Common;
import com.example.medix.Model.Result;
import com.example.medix.R;
import com.google.android.material.textfield.TextInputEditText;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputEditText editTextFirstName, editTextLastName, editTextEmail, editTextPhone, editTextPassword, editTextConPassword;
    private Button btn_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Password");

        // getting views
        editTextFirstName = (TextInputEditText) findViewById(R.id.editTextFirstName);
        editTextLastName = (TextInputEditText) findViewById(R.id.editTextLastName);
        editTextEmail = (TextInputEditText) findViewById(R.id.editTextEmail);
        editTextPhone = (TextInputEditText) findViewById(R.id.editTextPhone);
        editTextPassword = (TextInputEditText) findViewById(R.id.editTextPassword);
        editTextConPassword = (TextInputEditText) findViewById(R.id.editTextConPassword);

        btn_confirm = (Button) findViewById(R.id.btn_confirm);

        // get Customer Data
        getCustomerData();

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCustomerInfo();
            }
        });
    }


    private void updateCustomerInfo() {

        if(editTextPassword.getText().toString().equals(editTextConPassword.getText().toString()))
        {
            Log.d("ID: ", Common.CUSTOMER_ID);

            final android.app.AlertDialog waitingDialog = new SpotsDialog(ChangePasswordActivity.this);
            waitingDialog.show();
            waitingDialog.setMessage("Please wait ...");


            //Defining retrofit api service
            ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
            Call<Result> call = service.updateCustomerInfo(Common.CUSTOMER_ID,
                    editTextFirstName.getText().toString(),
                    editTextLastName.getText().toString(),
                    editTextEmail.getText().toString(),
                    editTextPhone.getText().toString(),
                    editTextPassword.getText().toString());

            call.enqueue(new Callback<Result>() {
                @Override
                public void onResponse(Call<Result> call, Response<Result> response) {
                    waitingDialog.dismiss();

                    if (!response.body().getError()) {
                        Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                        editTextFirstName.setText(response.body().getUser().getFirstname());
                        editTextLastName.setText(response.body().getUser().getLastname());
                        editTextPhone.setText(response.body().getUser().getTelephone());
                        editTextEmail.setText(response.body().getUser().getEmail());
                        editTextPassword.setText("");
                        editTextConPassword.setText("");
                        onBackPressed();
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Result> call, Throwable t) {
                    Toast.makeText(ChangePasswordActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Password doesn't match", Toast.LENGTH_LONG).show();
        }
    }

    private void getCustomerData() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ChangePasswordActivity.this);
        String Email = sharedPreferences.getString("EMAIL", null);

        final android.app.AlertDialog waitingDialog = new SpotsDialog(ChangePasswordActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");


        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
        Call<Result> call = service.getCustomerInfo(Email);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                waitingDialog.dismiss();

                if (!response.body().getError()) {
                    Common.CUSTOMER_ID = response.body().getUser().getCustomer_id();
                    editTextFirstName.setText(response.body().getUser().getFirstname());
                    editTextLastName.setText(response.body().getUser().getLastname());
                    editTextPhone.setText(response.body().getUser().getTelephone());
                    editTextEmail.setText(response.body().getUser().getEmail());
                    editTextPassword.setText("");
                    editTextConPassword.setText("");
                } else {
                    Toast.makeText(ChangePasswordActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(ChangePasswordActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }
}
