package com.example.medix.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devspark.appmsg.AppMsg;
import com.example.medix.Activity.MedicineShop.SingleProductActivity;
import com.example.medix.Api.ApiURL;
import com.example.medix.Common.Common;
import com.example.medix.Database.DatabaseMedicine;
import com.example.medix.Model.Cart;
import com.example.medix.Model.Orders;
import com.example.medix.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import dmax.dialog.SpotsDialog;

import static com.devspark.appmsg.AppMsg.LENGTH_SHORT;

public class SingleMedicineOrderStatusAdapter extends RecyclerView.Adapter<SingleMedicineOrderStatusAdapter.ViewHolder> {

    private List<Orders> requestList;
    private Context context;
    private int lastPosition = -1;

    public SingleMedicineOrderStatusAdapter(List<Orders> requests, Context context) {
        this.requestList = requests;
        this.context = context;

    }

    @Override
    public SingleMedicineOrderStatusAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_medicine_order_status_item, parent, false);
        return new SingleMedicineOrderStatusAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final SingleMedicineOrderStatusAdapter.ViewHolder holder, final int position) {
        final Orders request = requestList.get(position);

        /**
         *  Animation Part
         */
        setFadeAnimation(holder.itemView);

        Picasso.with(context)
                .load(ApiURL.IMAGE_URL + request.getImage())
                .error(R.drawable.cloths)
                .into(holder.imageViewItem);


        holder.textViewItemModel.setText("Model: " + request.getModel());
        holder.textViewItemName.setText(request.getName());
        holder.textViewItemPrice.setText("Price: "+ context.getResources().getString(R.string.currency_sign) + request.getProduct_total());
        holder.textViewItemQuantity.setText("Quantity: " + request.getQuantity());
        holder.textViewItemOrderStatus.setText("Status: " + Common.convertCodeToShopStatus(request.getVendor_order_status_id()));

        holder.buttonReOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final android.app.AlertDialog waitingDialog = new SpotsDialog(context);
                waitingDialog.show();
                waitingDialog.setMessage("Please wait ...");
                // Checking if the product is already added or not
                if (!new DatabaseMedicine(context).checkExistence(request.getProduct_id())) {

                    new DatabaseMedicine(context).addToCart(new Cart(
                            request.getProduct_id(),
                            request.getCategory_id(),
                            request.getName(),
                            request.getModel(),
                            request.getImage(),
                            request.getPrice(),
                            "1",
                            request.getCat_name(),
                            request.getCat_name(),
                            request.getStore_name(),
                            request.getSeller_id()
                    ));
                    waitingDialog.dismiss();
                    AppMsg.makeText((Activity) context, request.getName() + " added to cart (1st) time", new AppMsg.Style(LENGTH_SHORT, R.color.toastMessageColor))
                            .setAnimation(android.R.anim.fade_in, android.R.anim.fade_out).show();
                } else {
                    // if item is exist in cart then simply increase the quantity value
                    String quantity = new DatabaseMedicine(context).countQuantity(request.getProduct_id(), request.getCategory_id());
                    int q1 = Integer.parseInt(quantity) + 1;

                    if (q1 > 15) {
                        AppMsg.makeText((Activity) context, "Limit Crossed! (Max quantity amount is 15)", new AppMsg.Style(LENGTH_SHORT, R.color.toastMessageColor))
                                .setAnimation(android.R.anim.fade_in, android.R.anim.fade_out).show();

                    } else {
                        // Update carts
                        new DatabaseMedicine(context).updateCartItemFromFav(String.valueOf(q1), request.getProduct_id(), request.getCategory_id());

                        AppMsg.makeText((Activity) context, request.getName() + " added to cart (" + q1 + ") times", new AppMsg.Style(LENGTH_SHORT, R.color.toastMessageColor))
                                .setAnimation(android.R.anim.fade_in, android.R.anim.fade_out).show();
                    }
                    waitingDialog.dismiss();
                }

            }
        });

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SingleProductActivity.class);
                intent.putExtra("Menu_ID", request.getCategory_id());
                intent.putExtra("Menu_NAME", request.getCat_name());
                Common.menu_id = request.getCategory_id();
                Common.product_name = request.getName();
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageViewItem;
        public TextView textViewItemModel, textViewItemName, textViewItemPrice, textViewItemQuantity, textViewItemOrderStatus;
        public Button buttonReOrder;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            imageViewItem = (ImageView) itemView.findViewById(R.id.image_item);
            textViewItemModel = (TextView) itemView.findViewById(R.id.textViewItemModel);
            textViewItemName = (TextView) itemView.findViewById(R.id.textViewItemName);
            textViewItemPrice = (TextView) itemView.findViewById(R.id.textViewItemPrice);
            textViewItemQuantity = (TextView) itemView.findViewById(R.id.textViewItemQuantity);
            textViewItemOrderStatus = (TextView) itemView.findViewById(R.id.textViewItemStatus);
            buttonReOrder = (Button) itemView.findViewById(R.id.btn_reOrder);
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
