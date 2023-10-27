package com.example.medix.Activity.MedicineShop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.medix.Database.Database;
import com.example.medix.Database.DatabaseMedicine;
import com.example.medix.Fragment.CategoryFragment;
import com.example.medix.Fragment.HomeFragment;
import com.example.medix.Fragment.ProfileFragment;
import com.example.medix.Fragment.SearchFragment;
import com.example.medix.Helper.CartInterface;
import com.example.medix.Model.Cart;
import com.example.medix.R;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MedicineHomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, CartInterface {

    private View notificationBadge;
    private BottomNavigationView navigation;
    List<Cart> cartCart = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_home);

        //loading the default fragment
        loadFragment(new HomeFragment());

        //getting bottom navigation view and attaching the listener
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        addBadgeView();
    }

    public void addBadgeView() {
        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigationView.getChildAt(0);
        BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(3);

        View notificationBadge = LayoutInflater.from(this).inflate(R.layout.notification_badge, menuView, false);
        TextView textVieCartCount = notificationBadge.findViewById(R.id.notificationsBadgeTextView);
        int count = new DatabaseMedicine(getApplicationContext()).cartItemCount();
        textVieCartCount.setText(String.valueOf(count));

        itemView.addView(notificationBadge);
    }

    private void refreshBadgeView() {
        boolean badgeIsVisible = notificationBadge.getVisibility() != VISIBLE;
        notificationBadge.setVisibility(badgeIsVisible ? VISIBLE : GONE);
        //button.setText(badgeIsVisible ? "Hide badge" : " Show badge");
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.navigation_home:
                fragment = new HomeFragment();
                break;

            case R.id.navigation_category:
                fragment = new CategoryFragment();
                break;

            case R.id.navigation_search:
                fragment = new SearchFragment();
                break;

            case R.id.navigation_cart:
                startActivity(new Intent(MedicineHomeActivity.this, MedicineCartActivity.class));
                addView();
                break;

            case R.id.navigation_profile:
                fragment = new ProfileFragment();
                break;
        }

        return loadFragment(fragment);
    }

    public boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (f instanceof HomeFragment) {
            finish();
        } else {
            navigation.setSelectedItemId(R.id.navigation_home);
            navigation.getMenu().findItem(R.id.navigation_home).setChecked(true);
        }
    }

    @Override
    public void addView() {
        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigationView.getChildAt(0);
        BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(3);

        View notificationBadge;
        notificationBadge = LayoutInflater.from(this).inflate(R.layout.notification_badge, menuView, false);
        TextView textVieCartCount = notificationBadge.findViewById(R.id.notificationsBadgeTextView);
        int count = new DatabaseMedicine(getApplicationContext()).cartItemCount();
        textVieCartCount.setText(String.valueOf(count));
        itemView.addView(notificationBadge);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //addView();
        addBadgeView();
    }

    public void BackToHomeFragemnt() {
        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setSelectedItemId(R.id.navigation_home);
        navigationView.getMenu().findItem(R.id.navigation_home).setChecked(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        addBadgeView();
    }
}
