package com.example.medix.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.medix.Activity.MedicineShop.SingleMedicineOrderStatusActivity;
import com.example.medix.Model.Orders;
import com.example.medix.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class AllMedicineOrderHistoryAdapter extends RecyclerView.Adapter<AllMedicineOrderHistoryAdapter.ViewHolder> {

    private List<Orders> requestList;
    private Context context;
    private int lastPosition = -1;


    public AllMedicineOrderHistoryAdapter(List<Orders> requests, Context context) {
        this.requestList = requests;
        this.context = context;
    }

    @Override
    public AllMedicineOrderHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_medicine_order_history_item, parent, false);
        return new AllMedicineOrderHistoryAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AllMedicineOrderHistoryAdapter.ViewHolder holder, final int position) {
        final Orders request = requestList.get(position);

        /**
         *  Animation Part
         */
        /*Animation animation = AnimationUtils.loadAnimation(context,
                (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
        holder.itemView.startAnimation(animation);
        lastPosition = position;*/

        holder.textViewOrderID.setText("Order ID: " + request.getOrder_id());

        String strCurrentDate = request.getDate_added();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date newDate = null;
        try {
            newDate = format.parse(strCurrentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        format = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
        String date = format.format(newDate);



        holder.textViewOrderDate.setText(date);

        holder.textViewItemPrice.setText(context.getResources().getString(R.string.currency_sign)+ request.getTotal());
        holder.textViewOrderStatus.setText(request.getName());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SingleMedicineOrderStatusActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Order_ID", request.getOrder_id());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewOrderID, textViewOrderDate, textViewItemPrice, textViewOrderStatus;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewOrderID = (TextView) itemView.findViewById(R.id.textViewOrderID);
            textViewOrderDate = (TextView) itemView.findViewById(R.id.textViewOrderDate);
            textViewItemPrice = (TextView) itemView.findViewById(R.id.textViewItemPrice);
            textViewOrderStatus = (TextView) itemView.findViewById(R.id.textViewOrderStatus);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLay);
        }
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

}
