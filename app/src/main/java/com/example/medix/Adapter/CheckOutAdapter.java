package com.example.medix.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.medix.Activity.MedicineShop.SingleProductActivity;
import com.example.medix.Common.Common;
import com.example.medix.Model.Cart;
import com.example.medix.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CheckOutAdapter extends RecyclerView.Adapter<CheckOutAdapter.ViewHolder> {

    private List<Cart> cartList;
    private Context context;
    private int lastPosition = -1;


    public CheckOutAdapter(List<Cart> carts, Context context) {
        this.cartList = carts;
        this.context = context;
    }

    @Override
    public CheckOutAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_checkout_items, parent, false);
        return new CheckOutAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CheckOutAdapter.ViewHolder holder, final int position) {
        final Cart cart = cartList.get(position);

        /**
         *  Animation Part
         */
        setFadeAnimation(holder.itemView);

        holder.textViewItemName.setText(cart.getTest_name());
        holder.textViewDiagnosticName.setText(cart.getCenter_name());
        holder.textViewDiagnosticAddress.setText(cart.getCenter_address());
        Picasso.with(context)
                .load(R.drawable.diag)
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
                Toast.makeText(context, cart.getTest_name(), Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewDiagnosticAddress, textViewItemName, textViewPrice, textViewDiagnosticName;
        public ImageView imageViewItem;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewItemName = (TextView) itemView.findViewById(R.id.textViewItemName);
            textViewDiagnosticName = (TextView) itemView.findViewById(R.id.textViewDiagnosticName);
            textViewDiagnosticAddress = (TextView) itemView.findViewById(R.id.textViewDiagnosticAddress);
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