package com.example.medix.Adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.medix.Activity.MedicineShop.HotDealsActivity;
import com.example.medix.Activity.MedicineShop.ProductActivity;
import com.example.medix.Api.ApiClient;
import com.example.medix.Api.ApiService;
import com.example.medix.Api.ApiURL;
import com.example.medix.Common.Common;
import com.example.medix.Database.DatabaseMedicine;
import com.example.medix.Model.Cart;
import com.example.medix.Model.Result;
import com.example.medix.Model.SingleProduct;
import com.example.medix.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HotDealsAdapterActivity extends RecyclerView.Adapter<HotDealsAdapterActivity.ViewHolder> {

    List<SingleProduct> productList;
    HotDealsActivity context;
    private RecyclerView recyclerView = null;
    int previousExpandedPosition = -1;
    int mExpandedPosition = -1;

    public HotDealsAdapterActivity(List<SingleProduct> productList,HotDealsActivity context) {
        this.productList = productList;
        this.context = context;
        setHasStableIds(true);
        //viewCart();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_hot_deals_items_activity, parent, false);
        return new HotDealsAdapterActivity.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final HotDealsAdapterActivity.ViewHolder holder, final int position) {

        final SingleProduct product = productList.get(position);
        final boolean isExpanded = position==mExpandedPosition;
        holder.sub_item.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.itemView.setActivated(isExpanded);
        if (isExpanded)
        {
            previousExpandedPosition = position;

        }

        holder.textViewName.setText(product.getProduct_name());

        if(product.getDiscount_price().equals("0.0000"))
        {
            DecimalFormat df2 = new DecimalFormat("####0.00");
            double price = Double.parseDouble(product.getPrice());

            holder.textViewPercentage.setVisibility(View.GONE);
            holder.textViewPrice.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary1));
            holder.textViewPrice.setText(new StringBuilder(context.getResources().getString(R.string.currency_sign)).append(df2.format(price)));
            holder.linearLayoutDiscount.setVisibility(View.GONE);
        }
        else
        {

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date endDate = null;
            try {
                endDate = df.parse(product.getDate_end());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date currentDate = new Date();

            if(endDate.before(currentDate)) {


                DecimalFormat df2 = new DecimalFormat("####0.00");
                double price = Double.parseDouble(product.getPrice());

                holder.textViewPercentage.setVisibility(View.GONE);
                holder.textViewPrice.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary1));
                holder.textViewPrice.setText(new StringBuilder(context.getResources().getString(R.string.currency_sign)).append(df2.format(price)));
                holder.linearLayoutDiscount.setVisibility(View.GONE);
            }
            else
            {

                DecimalFormat df2 = new DecimalFormat("####0.00");
                double price = Double.parseDouble(product.getPrice());
                double discountPrice = Double.parseDouble(product.getDiscount_price());

                double original_price = Double.parseDouble(product.getPrice());
                double selling_price = Double.parseDouble(product.getDiscount_price());
                double discount = original_price - selling_price;

                //formula to get percentage
                double percentage = Math.ceil((discount / original_price) * 100);
                int result = (int) percentage;
                holder.textViewPercentage.setText(String.valueOf(result) + "%");

                holder.textViewPercentage.setVisibility(View.VISIBLE);
                holder.textViewPrice.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                holder.textViewPrice.setText(new StringBuilder(context.getResources().getString(R.string.currency_sign)).append(df2.format(price)));
                holder.textViewPrice.setPaintFlags(holder.textViewPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.linearLayoutDiscount.setVisibility(View.VISIBLE);
                holder.textViewDiscount.setText(new StringBuilder(context.getResources().getString(R.string.currency_sign)).append(df2.format(discountPrice)));
            }
        }

        Picasso.with(context)
                .load(ApiURL.IMAGE_URL + product.getImage())
                .into(holder.imageView);

        //viewCart();
        checkExistance(holder ,product.getProduct_id());

        if (new DatabaseMedicine(context).checkExistence(product.getProduct_id())) {
            holder.buttonAddToCart.setBackgroundResource(R.color.colorPrimary1);
            String quantity = new DatabaseMedicine(context).countQuantity(product.getProduct_id(), product.getCategory_id());
            holder.buttonAddToCart.setText("Added to cart " + "(x " + quantity + " times)");
            holder.buttonAddToCart.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f);

            // Removing Number button and making buttonAddToCart width match_parent
            holder.elegantNumberButton.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.buttonAddToCart.getLayoutParams();
            params.width = 100;
            holder.buttonAddToCart.setLayoutParams(params);

            holder.buttonAddToCart.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    // Remove item from Cart
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle("Remove from Cart");
                    alertDialog.setMessage("Are you sure you want to remove " + product.getName() + " - from your cart?");
                    alertDialog.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.setNegativeButton("REMOVE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            new DatabaseMedicine(context).clearCartITemFromProcut(product.getProduct_id(), product.getCategory_id());
                            holder.elegantNumberButton.setVisibility(View.VISIBLE);
                            holder.buttonAddToCart.setText("Buy");
                            holder.buttonAddToCart.setBackgroundColor(Color.parseColor("#000000"));

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

                    return false;
                }
            });
        }

        holder.buttonAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Pro_ID: " , product.getProduct_id());
                Log.d("Cat_ID: " , product.getCategory_id());
                Log.d("Cat_name", product.getCat_name());
                // Checking if the product is already added or not
                if (!new DatabaseMedicine(context).checkExistence(product.getProduct_id())) {

                    if(product.getDiscount_price().equals("0.0000"))
                    {
                        new DatabaseMedicine(context).addToCart(new Cart(
                                product.getProduct_id(),
                                product.getCategory_id(),
                                product.getProduct_name(),
                                product.getModel(),
                                product.getImage(),
                                product.getPrice(),
                                holder.elegantNumberButton.getNumber(),
                                product.getCat_name(),
                                product.getCat_name(),
                                product.getStore_name(),
                                product.getSeller_id()
                        ));
                    }
                    else
                    {
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        Date endDate = null;
                        try {
                            endDate = df.parse(product.getDate_end());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Date currentDate = new Date();

                        if(endDate.before(currentDate)) {
                            new DatabaseMedicine(context).addToCart(new Cart(
                                    product.getProduct_id(),
                                    product.getCategory_id(),
                                    product.getProduct_name(),
                                    product.getModel(),
                                    product.getImage(),
                                    product.getPrice(),
                                    holder.elegantNumberButton.getNumber(),
                                    product.getCat_name(),
                                    product.getCat_name(),
                                    product.getStore_name(),
                                    product.getSeller_id()
                            ));
                        }
                        else
                        {
                            new DatabaseMedicine(context).addToCart(new Cart(
                                    product.getProduct_id(),
                                    product.getCategory_id(),
                                    product.getProduct_name(),
                                    product.getModel(),
                                    product.getImage(),
                                    product.getDiscount_price(),
                                    holder.elegantNumberButton.getNumber(),
                                    product.getCat_name(),
                                    product.getCat_name(),
                                    product.getStore_name(),
                                    product.getSeller_id()
                            ));
                        }
                    }

                    //Toast.makeText(mCtx, product.getName() + " added to cart", Toast.LENGTH_SHORT).show();
                    /*AppMsg.makeText((Activity) mCtx, product.getName() + " added to cart", new AppMsg.Style(LENGTH_SHORT, R.color.toastColorInfo))
                            .setAnimation(android.R.anim.fade_in, android.R.anim.fade_out).show();*/
                    holder.buttonAddToCart.setBackgroundResource(R.color.colorPrimary1);
                    holder.buttonAddToCart.setText("Added to cart " + "(x " + holder.elegantNumberButton.getNumber() + " times)");
                    holder.buttonAddToCart.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f);
                    // Removing Number button and making buttonAddToCart width match_parent
                    holder.elegantNumberButton.setVisibility(View.GONE);
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.buttonAddToCart.getLayoutParams();
                    params.width = 100;
                    holder.buttonAddToCart.setLayoutParams(params);

                    //viewCart();

                } else {
                    Toast.makeText(context, "Long press to edit selection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.buttonAddToCart.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (new DatabaseMedicine(context).checkExistence(product.getProduct_id())) {
                    // Remove item from Cart
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle("Remove from Cart");
                    alertDialog.setMessage("Are you sure you want to remove " + product.getName() + " - from your cart?");
                    alertDialog.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.setNegativeButton("REMOVE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            new DatabaseMedicine(context).clearCartITemFromProcut(product.getProduct_id(), product.getCategory_id());
                            holder.elegantNumberButton.setVisibility(View.VISIBLE);
                            holder.elegantNumberButton.setNumber(String.valueOf(1));
                            holder.buttonAddToCart.setText("Buy");
                            holder.buttonAddToCart.setTypeface(Typeface.DEFAULT_BOLD);
                            holder.buttonAddToCart.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                            holder.buttonAddToCart.setBackgroundResource(R.color.colorPrimary);

                            //viewCart();

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
                return false;
            }
        });

        holder.imageViewFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                final String Customer_Id = sharedPreferences.getString("CUSTOMER_ID", null);
                // Check Wish List
                //Defining retrofit api service
                ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
                Call<Result> call = service.checkWishlist(Customer_Id, product.getProduct_id());

                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        if(!response.body().getError()) // exist
                        {
                            // Add to Wish List
                            //Defining retrofit api service
                            ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
                            Call<Result> resultCall = service.addWishlist(Customer_Id, product.getProduct_id());

                            resultCall.enqueue(new Callback<Result>() {
                                @Override
                                public void onResponse(Call<Result> call, Response<Result> response) {
                                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                    holder.imageViewFavorites.setImageResource(R.drawable.ic_favorite_black_24dp);
                                }
                                @Override
                                public void onFailure(Call<Result> call, Throwable t) {
                                    Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else
                        {
                            removeFromWishlist(holder ,Customer_Id, product.getProduct_id());
                            Toast.makeText(context, product.getProduct_name() + " was removed from wish list", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.product_name = product.getProduct_name();
                Log.d("Name: ", Common.product_name);
                String str = product.getCat_name();
                String replacedStr = str.replaceAll("&amp;", "&");
                Common.submenu_name = replacedStr;
                if(product.getDiscount_price().equals("0.0000"))
                {
                    Common.product_price = product.getPrice();
                }
                else
                {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    Date endDate = null;
                    try {
                        endDate = df.parse(product.getDate_end());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Date currentDate = new Date();

                    if(endDate.before(currentDate)) {
                        Common.product_price = product.getPrice();
                    }
                    else
                    {
                        Common.product_price = product.getDiscount_price();
                    }
                }
                Common.singleProduct = productList.get(position);
                Intent intent = new Intent(context, ProductActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1:position;
                notifyItemChanged(previousExpandedPosition);
                notifyItemChanged(position);

                final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if(layoutManager instanceof StaggeredGridLayoutManager) {
                    StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                    int distance;
                    View first = recyclerView.getChildAt(0);
                    int height = first.getHeight();
                    int current = recyclerView.getChildAdapterPosition(first);
                    int p = Math.abs(position - current);
                    if (p > 5) distance = (p - (p - 5)) * height;
                    else       distance = p * height;
                    manager.scrollToPositionWithOffset(position, distance);
                } else if(layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int distance;
                    View first = recyclerView.getChildAt(0);
                    int height = first.getHeight();
                    int current = recyclerView.getChildAdapterPosition(first);
                    int p = Math.abs(position - current);
                    if (p > 5) distance = (p - (p - 5)) * height;
                    else       distance = p * height;
                    manager.scrollToPositionWithOffset(position, distance);
                }
            }
        });

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
    public int getItemCount() {
        //return SingleProductAdapters.size();
        return (null != productList ? productList.size() : 0);
    }

    private void checkExistance(final HotDealsAdapterActivity.ViewHolder holder, String product_id) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String Customer_Id = sharedPreferences.getString("CUSTOMER_ID", null);
        // Check Wish List
        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
        Call<Result> call = service.checkWishlist(Customer_Id, product_id);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if(!response.body().getError()) // exist
                {
                    holder.imageViewFavorites.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                }
                else // not exist add new Wish List here
                {
                    holder.imageViewFavorites.setImageResource(R.drawable.ic_favorite_black_24dp);
                }
            }
            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeFromWishlist(final HotDealsAdapterActivity.ViewHolder holder, String customer_id, String product_id) {

        // Removed to Wish List
        //Defining retrofit api service
        ApiService service = ApiClient.getClientMedixShop().create(ApiService.class);
        Call<Result> resultCall = service.removeWishlist(customer_id, product_id);

        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                holder.imageViewFavorites.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            }
            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewName, textViewPrice, textViewDiscount, textViewPercentage;
        public LinearLayout linearLayoutPrice, linearLayoutDiscount;
        public ImageView imageView, imageViewFavorites;
        public Button buttonAddToCart;
        public ElegantNumberButton elegantNumberButton;
        public LinearLayout linearLayout, sub_item;

        public ViewHolder(View itemView) {
            super(itemView);

            linearLayoutDiscount = (LinearLayout) itemView.findViewById(R.id.linearLayDiscount);
            textViewName = (TextView) itemView.findViewById(R.id.item_name);
            textViewPrice = (TextView) itemView.findViewById(R.id.item_price);
            textViewDiscount = (TextView) itemView.findViewById(R.id.item_discount_price);
            textViewPercentage = (TextView) itemView.findViewById(R.id.textView_percentage);
            imageView = (ImageView) itemView.findViewById(R.id.item_image);
            imageViewFavorites = (ImageView) itemView.findViewById(R.id.imageView_fav);
            buttonAddToCart = (Button) itemView.findViewById(R.id.btn_addCart);
            elegantNumberButton = (ElegantNumberButton) itemView.findViewById(R.id.txt_count);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLay);
            sub_item = (LinearLayout) itemView.findViewById(R.id.sub_item);
        }
    }

    @Override
    public void onViewDetachedFromWindow(HotDealsAdapterActivity.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }
}