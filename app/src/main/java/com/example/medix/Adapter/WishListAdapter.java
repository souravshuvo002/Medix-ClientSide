package com.example.medix.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.appmsg.AppMsg;
import com.example.medix.Activity.MedicineShop.MedicineHomeActivity;
import com.example.medix.Activity.MedicineShop.SingleProductActivity;
import com.example.medix.Api.ApiClient;
import com.example.medix.Api.ApiService;
import com.example.medix.Api.ApiURL;
import com.example.medix.Common.Common;
import com.example.medix.Database.DatabaseMedicine;
import com.example.medix.Helper.NetWorkConfig;
import com.example.medix.Model.Cart;
import com.example.medix.Model.Result;
import com.example.medix.Model.WishList;
import com.example.medix.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.devspark.appmsg.AppMsg.LENGTH_SHORT;

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.ViewHolder> {

    private List<WishList> wishListList;
    private Context context;
    private int lastPosition = -1;

    private NetWorkConfig netWorkConfig;

    public WishListAdapter(List<WishList> wishListList, Context context) {
        this.wishListList = wishListList;
        this.context = context;
        this.netWorkConfig = new NetWorkConfig((Activity) context);

    }

    @Override
    public WishListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wish_list_item, parent, false);
        return new WishListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final WishListAdapter.ViewHolder holder, final int position) {
        final WishList wishList = wishListList.get(position);

        /**
         *  Animation Part
         */
        setFadeAnimation(holder.itemView);

        holder.textViewItemName.setText(wishList.getName());
        holder.textViewItemModel.setText(wishList.getModel());
        if (wishList.getStatus().equals("1")) {
            holder.textViewItemStatus.setText("In stock");
        } else {
            holder.textViewItemStatus.setText("Out of stock");
        }
        Picasso.with(context)
                .load(ApiURL.IMAGE_URL + wishList.getImage())
                .into(holder.imageViewItem);

        holder.textViewPrice.setText(new StringBuilder(context.getResources().getString(R.string.currency_sign)).append(wishList.getPrice()));

        holder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Remove from Favorites");
                alertDialog.setMessage("Are you sure you want to remove " + wishList.getName() + " - from your favorite?");
                alertDialog.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.setNegativeButton("REMOVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Deleting favorite item
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                        final String Customer_Id = sharedPreferences.getString("CUSTOMER_ID", null);
                        AppMsg.makeText((Activity) context, wishListList.get(holder.getAdapterPosition()).getName() + " item removed", new AppMsg.Style(LENGTH_SHORT, R.color.toastMessageColor))
                                .setAnimation(android.R.anim.fade_in, android.R.anim.fade_out).show();
                        //Toast.makeText(context, favoritesList.get(holder.getAdapterPosition()).getProduct_name() + " item removed", Toast.LENGTH_SHORT).show();
                        // Removed to Wish List
                        //Defining retrofit api service
                        ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
                        Call<Result> resultCall = service.removeWishlist(Customer_Id, wishList.getProduct_id());

                        resultCall.enqueue(new Callback<Result>() {
                            @Override
                            public void onResponse(Call<Result> call, Response<Result> response) {
                                wishListList.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
                                //Defining retrofit api service
                                ApiService service2 = ApiClient.getClientMedix().create(ApiService.class);
                                Call<Result> resultCall2 = service2.getCustomerWishList(Customer_Id);

                                resultCall2.enqueue(new Callback<Result>() {
                                    @Override
                                    public void onResponse(Call<Result> call, Response<Result> response) {
                                        if (response.body().getWishListList().size() <= 0) {
                                            Toast.makeText(context, "No Wish list items", Toast.LENGTH_SHORT).show();
                                            ((MedicineHomeActivity) context).BackToHomeFragemnt();
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<Result> call, Throwable t) {
                                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onFailure(Call<Result> call, Throwable t) {
                                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

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
        });

        holder.buttonAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 *  Network Connection Check
                 */
                if (!netWorkConfig.isNetworkAvailable()) {
                    netWorkConfig.createNetErrorDialog();
                    return;
                }

                // Checking if the product is already added or not
                if (!new DatabaseMedicine(context).checkExistence(wishList.getProduct_id())) {
                    new DatabaseMedicine(context).addToCart(new Cart(
                            wishList.getProduct_id(),
                            wishList.getCat_id(),
                            wishList.getName(),
                            wishList.getModel(),
                            wishList.getImage(),
                            wishList.getPrice(),
                            String.valueOf(1),
                            wishList.getCat_name(),
                            wishList.getCat_name(),
                            wishList.getStore_name(),
                            wishList.getSeller_id()
                    ));

                    AppMsg.makeText((Activity) context, wishList.getName() + " added to cart (1st) time", new AppMsg.Style(LENGTH_SHORT, R.color.toastMessageColor))
                            .setAnimation(android.R.anim.fade_in, android.R.anim.fade_out).show();

                } else {
                    // if item is exist in cart then simply increase the quantity value
                    String quantity = new DatabaseMedicine(context).countQuantity(wishList.getProduct_id(), wishList.getCat_id());
                    int q1 = Integer.parseInt(quantity) + 1;

                    if (q1 > 15) {
                        AppMsg.makeText((Activity) context, "Limit Crossed! (Max quantity amount is 15)", new AppMsg.Style(LENGTH_SHORT, R.color.toastMessageColor))
                                .setAnimation(android.R.anim.fade_in, android.R.anim.fade_out).show();

                    } else {
                        // Update carts
                        new DatabaseMedicine(context).updateCartItemFromFav(String.valueOf(q1), wishList.getProduct_id(), wishList.getCat_id());

                        AppMsg.makeText((Activity) context, wishList.getName() + " added to cart (" + q1 + ") times", new AppMsg.Style(LENGTH_SHORT, R.color.toastMessageColor))
                                .setAnimation(android.R.anim.fade_in, android.R.anim.fade_out).show();

                    }
                }
            }
        });


        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SingleProductActivity.class);
                intent.putExtra("Menu_ID", wishList.getCategory_id());
                intent.putExtra("Menu_NAME", wishList.getCat_name());
                Log.d("Cat_ID: ", wishList.getCategory_id());
                Common.menu_id = wishList.getCategory_id();

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return wishListList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewItemName, textViewPrice, textViewItemModel, textViewItemStatus;
        public ImageView imageViewItem, imageViewDelete;
        public Button buttonAddCart;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewItemStatus = (TextView) itemView.findViewById(R.id.textViewItemStatus);
            textViewItemModel = (TextView) itemView.findViewById(R.id.textViewItemModel);
            textViewItemName = (TextView) itemView.findViewById(R.id.textViewItemName);
            buttonAddCart = (Button) itemView.findViewById(R.id.btn_addCart);
            imageViewItem = (ImageView) itemView.findViewById(R.id.image_item);
            imageViewDelete = (ImageView) itemView.findViewById(R.id.imageViewDelete);
            textViewPrice = (TextView) itemView.findViewById(R.id.textViewItemPrice);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLay);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    public void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        view.startAnimation(anim);
    }

}
