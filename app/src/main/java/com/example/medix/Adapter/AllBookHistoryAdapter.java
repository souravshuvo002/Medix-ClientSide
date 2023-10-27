package com.example.medix.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.medix.Activity.SingleBookStatusActivity;
import com.example.medix.Common.Common;
import com.example.medix.Model.Book;
import com.example.medix.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class AllBookHistoryAdapter extends RecyclerView.Adapter<AllBookHistoryAdapter.ViewHolder> {

    private List<Book> bookList;
    private Context context;
    private int lastPosition = -1;


    public AllBookHistoryAdapter(List<Book> bookList, Context context) {
        this.bookList = bookList;
        this.context = context;
    }

    @Override
    public AllBookHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_all_book_history_items, parent, false);
        return new AllBookHistoryAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AllBookHistoryAdapter.ViewHolder holder, final int position) {
        final Book book = bookList.get(position);

        /**
         *  Animation Part
         */
        /*Animation animation = AnimationUtils.loadAnimation(context,
                (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
        holder.itemView.startAnimation(animation);
        lastPosition = position;*/

        holder.textViewOrderID.setText("Book ID: " + book.getTest_book_id());

        String strCurrentDate = book.getDate_added();
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

        holder.textViewItemPrice.setText(context.getResources().getString(R.string.currency_sign)+ book.getTotal());
        holder.textViewOrderStatus.setText(Common.convertCodeToStatus(book.getBook_status_id()));

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SingleBookStatusActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("BOOK_ID", book.getTest_book_id());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return bookList.size();
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