package com.example.medix.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.devspark.appmsg.AppMsg;
import com.example.medix.Adapter.CartAdapter;
import com.example.medix.Database.Database;
import com.example.medix.Helper.RecyclerItemTouchHelper;
import com.example.medix.Model.Cart;
import com.example.medix.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

import static android.os.Build.VERSION_CODES.JELLY_BEAN;
import static com.devspark.appmsg.AppMsg.LENGTH_SHORT;
import static com.devspark.appmsg.AppMsg.STYLE_ALERT;

public class CartActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    public RecyclerView recyclerViewCart;
    public CartAdapter adapter;
    public Button buttonPlaceOrder;
    //public LinearLayout layRecyclerView;
    public RelativeLayout layEmpty, relLay_1;
    public Button buttonContinue;
    public TextView textSubTotal;
    public ImageView imageViewRemoveAll, imageViewBackButton;
    SharedPreferences myPreferences;
    private SwipeRefreshLayout swipeRefreshLayout;

    List<Cart> cartCart = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        // getting views
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().
                getColor(android.R.color.holo_blue_dark),getResources().
                getColor(android.R.color.holo_red_dark),getResources().
                getColor(android.R.color.holo_green_light),getResources().
                getColor(android.R.color.holo_orange_dark));
        buttonPlaceOrder = (Button) findViewById(R.id.btn_placeOrder);
        textSubTotal = (TextView) findViewById(R.id.textSubTotal);
        recyclerViewCart = (RecyclerView) findViewById(R.id.recycler_view_Cart);
        //layRecyclerView = (LinearLayout) findViewById(R.id.layRecyclerView);
        layEmpty = (RelativeLayout) findViewById(R.id.layEmpty);
        relLay_1 = (RelativeLayout) findViewById(R.id.relLay_1);
        //linearLayoutTotal = (LinearLayout) findViewById(R.id.LayTotal);
        imageViewBackButton = (ImageView) findViewById(R.id.imageViewBack);
        imageViewRemoveAll = (ImageView) findViewById(R.id.imageViewRemoveAll);
        buttonContinue = (Button) findViewById(R.id.btn_cart);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewCart.setHasFixedSize(true);
        recyclerViewCart.setItemAnimator(new DefaultItemAnimator());
        recyclerViewCart.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // adding item touch helper
        // only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right
        // add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerViewCart);

        buttonPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkProductExistence();
                //orderDialog();
                startActivity(new Intent(CartActivity.this, CheckOutActivity.class));
                finish();
            }
        });

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onBackPressed();
                startActivity(new Intent(CartActivity.this, MainActivity.class));
                finish();
            }
        });

        imageViewRemoveAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = new Database(getApplicationContext()).cartItemCount();
                if (count > 0) {
                    removeAllItems();
                } else {
                    AppMsg.makeText(CartActivity.this, "Cart is empty", STYLE_ALERT)
                            .setAnimation(android.R.anim.fade_in, android.R.anim.fade_out).show();
                }
            }
        });

        imageViewBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        // Load Cart Items from Database
        loadCartItems();
    }


    private void removeAllItems() {

        int items = new Database(this).cartItemCount();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Remove All from Cart");
        alertDialog.setMessage("Are you sure you want to remove all " + items + " item(s) from your cart?");
        alertDialog.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.setNegativeButton("REMOVE ALL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final android.app.AlertDialog waitingDialog = new SpotsDialog(CartActivity.this);
                waitingDialog.show();
                waitingDialog.setMessage("Removing ...");

                // Delaying action for 1 second
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        waitingDialog.dismiss();
                        // Remove All cart items
                        new Database(getApplicationContext()).removeAllCartItems();
                        loadCartItems();
                    }
                }, 1000);
            }
        });
        AlertDialog dialog = alertDialog.create();
        dialog.show();
        Button b = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (b != null) {
            b.setTextColor(Color.parseColor("#000000"));
        }
        Button b2 = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        if (b2 != null) {
            b2.setTextColor(Color.parseColor("#000000"));
        }
    }

    private void loadCartItems() {
        swipeRefreshLayout.setRefreshing(false);

        cartCart = new Database(this).getCarts();
        if (cartCart.size() <= 0) {
            layEmpty.setVisibility(View.VISIBLE);
            relLay_1.setVisibility(View.GONE);
            //linearLayoutTotal.setVisibility(View.GONE);
            //layRecyclerView.setVisibility(View.GONE);
            buttonPlaceOrder.setVisibility(View.GONE);
            imageViewRemoveAll.setVisibility(View.INVISIBLE);
        } else {
            layEmpty.setVisibility(View.GONE);
            relLay_1.setVisibility(View.VISIBLE);
            //linearLayoutTotal.setVisibility(View.VISIBLE);
            //layRecyclerView.setVisibility(View.VISIBLE);
            buttonPlaceOrder.setVisibility(View.VISIBLE);
            imageViewRemoveAll.setVisibility(View.VISIBLE);
        }

        adapter = new CartAdapter(cartCart, CartActivity.this);
        recyclerViewCart.setAdapter(adapter);

        // Calculate Total Price
        double total = 0.0;
        for (Cart cart : cartCart) {
            total += (Double.parseDouble(cart.getPrice())) * (Double.parseDouble(cart.getQuantity()));
        }
        //textSubTotal.setText("SubTotal: " + String.valueOf(cartCart.size() + " items"));
        textSubTotal.setText(String.valueOf(cartCart.size()) + " items / Total Cost " + getResources().getString(R.string.currency_sign) + String.format("%.2f", total));

    }

    /**
     * callback when recycler view is swiped
     * item will be removed on swiped
     * undo option will be provided in snackbar to restore the item
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CartAdapter.ViewHolder) {
            // get the removed item name to display it in snack bar
            String name = cartCart.get(viewHolder.getAdapterPosition()).getTest_name();

            // backup of removed item for undo purpose
            final Cart deletedItem = cartCart.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            AppMsg.makeText((Activity) CartActivity.this, name + " item removed", new AppMsg.Style(LENGTH_SHORT, R.color.colorPrimary))
                    .setAnimation(android.R.anim.fade_in, android.R.anim.fade_out).show();

            new Database(CartActivity.this).clearCartITemFromProduct(cartCart.get(deletedIndex).getTest_id(), cartCart.get(deletedIndex).getCenter_id());
            // remove the item from recycler view
            adapter.removeItem(viewHolder.getAdapterPosition());



            loadCartItems();

           /* // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(relLay_1, name + " removed from cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // undo is selected, restore the deleted item
                    adapter.restoreItem(deletedItem, deletedIndex);
                    new Database(CartActivity.this).addToCart(new Cart(
                            deletedItem.getTest_id(),
                            deletedItem.getTest_name(),
                            deletedItem.getTest_short_name(),
                            deletedItem.getCenter_id(),
                            deletedItem.getCenter_name(),
                            deletedItem.getCenter_address(),
                            deletedItem.getPrice(),
                            deletedItem.getQuantity()
                    ));
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();*/

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadCartItems();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartItems();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @TargetApi(JELLY_BEAN)
    private void enableChangingTransition() {
        ViewGroup animatedRoot = (ViewGroup) findViewById(R.id.animated_root);
        animatedRoot.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    }

    @Override
    public void onRefresh() {

        loadCartItems();
        //getBanner();
    }
}
