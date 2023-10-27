package com.example.medix.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.medix.Api.ApiClient;
import com.example.medix.Api.ApiService;
import com.example.medix.Api.ApiURL;
import com.example.medix.Common.Common;
import com.example.medix.Model.Result;
import com.example.medix.Model.User;
import com.example.medix.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    public AppCompatButton btn_reg, btn_login;
    private TextInputEditText editTextPhone, editTextPassword;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static final int REQUEST_CODE = 7171;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;
    private List<AuthUI.IdpConfig> providers;


    FirebaseDatabase database;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        providers = Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build());
        listener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null)
                checkUserFromFirebase(user);
        };

        setContentView(R.layout.activity_login);
        makeFullScreen();

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        //Init Firebase
        database = FirebaseDatabase.getInstance();
        users = database.getReference("User");

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        editor = sharedPreferences.edit();

        // getting views
        editTextPhone = (TextInputEditText) findViewById(R.id.input_phone);
        editTextPassword = (TextInputEditText) findViewById(R.id.input_password);

        btn_reg = (AppCompatButton) findViewById(R.id.btn_create_account);
        btn_login = (AppCompatButton) findViewById(R.id.btn_login);
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoginSystem();
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

        final android.app.AlertDialog waitingDialog = new SpotsDialog(LoginActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");

        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedix().create(ApiService.class);
        Call<Result> call = service.loginCustomer("+88" + editTextPhone.getText().toString(), editTextPassword.getText().toString());

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                waitingDialog.dismiss();

                if(!response.body().getError())
                {
                    // Clearing shared preferences
                    editor.clear();
                    editor.commit();

                    editor.putString("USERNAME", response.body().getUser().getFirst_name() + " " + response.body().getUser().getLast_name());
                    editor.putString("PHONE", editTextPhone.getText().toString());
                    editor.putString("PASSWORD", editTextPassword.getText().toString());
                    editor.putString("CUSTOMER_ID", response.body().getUser().getCustomer_id());
                    editor.apply();

                    Common.Customer_phone= editTextPhone.getText().toString();
                    Common.CUSTOMER_ID = response.body().getUser().getCustomer_id();

                    updateTokenToServer();

                    Toast.makeText(getApplicationContext(), "Welcome back " + response.body().getUser().getFirst_name() + " " + response.body().getUser().getLast_name(), Toast.LENGTH_LONG).show();

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
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

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(listener);

        String Phone;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        Phone = sharedPreferences.getString("PHONE", null);
        if (Phone != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    @Override
    protected void onStop() {
        if (listener != null)
            firebaseAuth.removeAuthStateListener(listener);
        super.onStop();
    }


    private void checkUserFromFirebase(FirebaseUser user) {
        //Show dialog
        final android.app.AlertDialog waitingDialog = new SpotsDialog(LoginActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait");
        waitingDialog.setCancelable(false);

        //Check if exists on Firebase Users
        users.orderByKey().equalTo(user.getPhoneNumber())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.child(user.getPhoneNumber()).exists()) // If not exists
                        {
                            //We will create new user and login
                            User newUser = new User();
                            newUser.setPhone(user.getPhoneNumber());
                            newUser.setFirst_name("");
                            newUser.setLast_name("");

                            //Add to Firebase
                            users.child(user.getPhoneNumber())
                                    .setValue(newUser)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful())
                                            Toast.makeText(LoginActivity.this, "User register successful!", Toast.LENGTH_SHORT).show();

                                        Common.Customer_phone = user.getPhoneNumber();
                                        startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));

                                    });

                        } else // If exists
                        {
                            //We will just login
                            //Login
                            users.child(user.getPhoneNumber())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            User localUser = dataSnapshot.getValue(User.class);

                                            updateTokenToServer();

                                            //Copy code from LoginActivity
                                            Intent homeIntent = new Intent(LoginActivity.this, MainActivity.class);
                                            Common.currentUser = localUser;
                                            startActivity(homeIntent);
                                            //Dismiss dialog
                                            waitingDialog.dismiss();
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            waitingDialog.dismiss();
                                            Toast.makeText(LoginActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        waitingDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


    }


    private void startLoginSystem() {
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(), REQUEST_CODE);
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
                        Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            } else
                Toast.makeText(this, "Failed to sign in", Toast.LENGTH_SHORT).show();
        }

    }

}