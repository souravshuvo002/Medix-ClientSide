package com.example.medix.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.example.medix.Activity.Diagnostic.AllUploadPrescriptionDetailsActivity;
import com.example.medix.Activity.Diagnostic.DiagnosticActivity;
import com.example.medix.Activity.Diagnostic.UploadPrescriptionActivity;
import com.example.medix.Activity.MedicineShop.MedicineHomeActivity;
import com.example.medix.Api.ApiClient;
import com.example.medix.Api.ApiService;
import com.example.medix.Api.ApiURL;
import com.example.medix.Model.Banner;
import com.example.medix.Model.Result;
import com.example.medix.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BaseSliderView.OnSliderClickListener{

    private DrawerLayout drawer;
    private LinearLayout linearLayDiagnostic, linearLayMedicineShop, linearLayDoctorCalling, linearLayNurseCalling,
            linearLayAmbulanceCalling, linearLayPsychotherapistCalling;
    private TextView usernameDrawer, userphoneDrawer;
    private Button buttonUploadPrescription;
    private SliderLayout sliderLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Change status bar color
        changeStatusBarColor("#00574B");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout_home);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        String UserName, Phone;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        UserName = sharedPreferences.getString("USERNAME", null);
        Phone = sharedPreferences.getString("PHONE", null);

        //Set Name for user
        View headerView = navigationView.getHeaderView(0);
        usernameDrawer = (TextView) headerView.findViewById(R.id.usernameDrawer);
        userphoneDrawer = (TextView) headerView.findViewById(R.id.userphoneDrawer);
        usernameDrawer.setText(UserName);
        userphoneDrawer.setText(Phone);

        // getting views
        sliderLayout = (SliderLayout) findViewById(R.id.sliderLay);
        linearLayMedicineShop = (LinearLayout) findViewById(R.id.linearLayMedicineShop);
        linearLayDoctorCalling = (LinearLayout) findViewById(R.id.linearLayDoctorCalling);
        linearLayNurseCalling = (LinearLayout) findViewById(R.id.linearLayNurseCalling);
        linearLayAmbulanceCalling = (LinearLayout) findViewById(R.id.linearLayAmbulanceCalling);
        linearLayPsychotherapistCalling = (LinearLayout) findViewById(R.id.linearLayPsychotherapistCalling);
        linearLayDiagnostic = (LinearLayout) findViewById(R.id.linearLayDiagnostic);
        buttonUploadPrescription = (Button) findViewById(R.id.button_UploadPrescription);


        ImageView mDrawerButton = findViewById(R.id.drawerButtonHome);
        mDrawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        linearLayMedicineShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MedicineHomeActivity.class));
            }
        });

        linearLayDiagnostic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, DiagnosticActivity.class));
            }
        });

        linearLayDoctorCalling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Coming Soon!", Toast.LENGTH_LONG).show();
            }
        });

        linearLayNurseCalling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Coming Soon!", Toast.LENGTH_LONG).show();
            }
        });

        linearLayAmbulanceCalling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Coming Soon!", Toast.LENGTH_LONG).show();
            }
        });

        linearLayPsychotherapistCalling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Coming Soon!", Toast.LENGTH_LONG).show();
            }
        });

        buttonUploadPrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UploadPrescriptionActivity.class));
            }
        });

        getBanner();
        updateTokenToServer();
    }


    private void getBanner() {
        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
        Call<Result> call = service.getBanners();

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                displayImages(response.body().getBanners());
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayImages(List<Banner> banners) {

        HashMap<String, String> bannerMap = new HashMap<>();
        for (Banner item : banners) {
            bannerMap.put(item.getName(), item.getImage());
        }
        for (String name : bannerMap.keySet()) {
            TextSliderView textSliderView = new TextSliderView(this);
            textSliderView.description(name)
                    .image(bannerMap.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(MainActivity.this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", name);

            sliderLayout.addSlider(textSliderView);
        }

        // Slider Animation
         /*sliderLayout.setPresetTransformer(SliderLayout.Transformer.Default);
         sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
         sliderLayout.setCustomAnimation(new DescriptionAnimation());
         sliderLayout.setDuration(8000);*/

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /*//noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if(id == R.id.history)
        {
            startActivity(new Intent(MainActivity.this, AllBookHistoryActivity.class));
        }
        else if (id == R.id.logout) {
            LogOut();
        } else if (id == R.id.upload_pres) {
            startActivity(new Intent(MainActivity.this, AllUploadPrescriptionDetailsActivity.class));
        }
        else if(id == R.id.cart)
        {
            startActivity(new Intent(MainActivity.this, CartActivity.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_home);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    private void LogOut() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
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

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                // Clearing Data from Shared Preferences
                editor.clear();
                editor.commit();

                //Delete Remember user & password
                FirebaseAuth.getInstance().signOut();

                // Clear All Activity
                Intent intent = new Intent(MainActivity.this, LoginActivity2.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "Successfully logged out!", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        AlertDialog dialog = alertDialog.create();
        dialog.show();
        Button b = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (b != null) {
            b.setTextColor(Color.parseColor("#FF8A65"));
        }
    }

    private void changeStatusBarColor(String color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
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
                        Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(this, slider.getBundle().get("extra") + "", Toast.LENGTH_SHORT).show();
    }
}
