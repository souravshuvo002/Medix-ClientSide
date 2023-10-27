package com.example.medix.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.medix.Activity.MedicineShop.SingleProductActivity;
import com.example.medix.Api.ApiURL;
import com.example.medix.Common.Common;
import com.example.medix.Model.Cart;
import com.example.medix.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class MedicineConfirmCartAdapter extends RecyclerView.Adapter<MedicineConfirmCartAdapter.ViewHolder> {

    private List<Cart> cartList;
    private Context context;
    private int lastPosition = -1;


    public MedicineConfirmCartAdapter(List<Cart> carts, Context context) {
        this.cartList = carts;
        this.context = context;
    }

    @Override
    public MedicineConfirmCartAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.medicine_confirm_cart_item, parent, false);
        return new MedicineConfirmCartAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MedicineConfirmCartAdapter.ViewHolder holder, final int position) {
        final Cart cart = cartList.get(position);

        /**
         *  Animation Part
         */
        setFadeAnimation(holder.itemView);

        holder.textViewItemName.setText(cart.getProduct_name());
        holder.textViewItemModel.setText("Model: " + cart.getModel());
        /*if(cart.getOrder_notes().isEmpty())
        {
            holder.textViewItemNotes.setVisibility(View.GONE);
        }
        else
        {
            holder.textViewItemNotes.setVisibility(View.VISIBLE);
            holder.textViewItemNotes.setText("Notes: " + cart.getOrder_notes());
        }*/
        holder.textViewItemSizeQuantity.setText("$" + cart.getPrice() + " x (" + cart.getQuantity() + ")");
        Picasso.with(context)
                .load(ApiURL.IMAGE_URL + cart.getImage_link())
                .error(R.drawable.diag)
                .into(holder.imageViewItem);


        holder.textViewPrice.setText(new StringBuilder(context.getResources().getString(R.string.currency_sign)).append((Double.parseDouble(cart.getPrice())) * (Double.parseDouble(cart.getQuantity()))));

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SingleProductActivity.class);
                intent.putExtra("Menu_ID", cart.getCat_id());
                intent.putExtra("Menu_NAME", cart.getProduct_name());
                Common.menu_id = cart.getCat_id();
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewItemModel, textViewItemName, textViewPrice, textViewItemSizeQuantity;
        public ImageView imageViewItem;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewItemModel = (TextView) itemView.findViewById(R.id.textViewItemModel);
            textViewItemName = (TextView) itemView.findViewById(R.id.textViewItemName);
            textViewItemSizeQuantity = (TextView) itemView.findViewById(R.id.textViewItemSizeQuantity);
            imageViewItem = (ImageView) itemView.findViewById(R.id.image_item);
            textViewPrice = (TextView) itemView.findViewById(R.id.textViewItemPrice);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLay);


        }
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