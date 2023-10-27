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

import com.example.medix.Activity.Diagnostic.DiagnosticTestActivity;
import com.example.medix.Common.Common;
import com.example.medix.Model.Book;
import com.example.medix.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class SingleBookStatusAdapter extends RecyclerView.Adapter<SingleBookStatusAdapter.ViewHolder> {

    private List<Book> bookList;
    private Context context;
    private int lastPosition = -1;

    public SingleBookStatusAdapter(List<Book> bookList, Context context) {
        this.bookList = bookList;
        this.context = context;

    }

    @Override
    public SingleBookStatusAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_book_status_item, parent, false);
        return new SingleBookStatusAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final SingleBookStatusAdapter.ViewHolder holder, final int position) {
        final Book book = bookList.get(position);

        /**
         *  Animation Part
         */
        setFadeAnimation(holder.itemView);

        Picasso.with(context)
                .load(R.drawable.diag)
                .error(R.drawable.diag)
                .into(holder.imageViewItem);


        holder.textViewItemShortName.setText("Abbreviation: " + book.getTest_short_name());
        holder.textViewItemName.setText(book.getTest_name());
        holder.textViewItemPrice.setText("Price: " + context.getResources().getString(R.string.currency_sign) + book.getPrice());
        holder.textViewItemQuantity.setText("Quantity: " + book.getQuantity());
        holder.textViewCenterName.setText(book.getDiagnostic_center_name());
        holder.textViewItemCenterAddress.setText(book.getAddress());
        holder.textViewItemOrderStatus.setText("Status: " + Common.convertCodeToStatus(book.getBook_status_id()));

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DiagnosticTestActivity.class);
                intent.putExtra("NAME", book.getDiagnostic_center_name());
                intent.putExtra("CENTER_ID", book.getCenter_id());
                Common.CENTER_ID = book.getCenter_id();

                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageViewItem;
        public TextView textViewItemShortName, textViewItemName, textViewItemPrice, textViewItemQuantity, textViewItemOrderStatus,
                textViewCenterName, textViewItemCenterAddress;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            imageViewItem = (ImageView) itemView.findViewById(R.id.image_item);
            textViewItemShortName = (TextView) itemView.findViewById(R.id.textViewItemShortName);
            textViewItemName = (TextView) itemView.findViewById(R.id.textViewItemName);
            textViewItemPrice = (TextView) itemView.findViewById(R.id.textViewItemPrice);
            textViewItemQuantity = (TextView) itemView.findViewById(R.id.textViewItemQuantity);
            textViewItemOrderStatus = (TextView) itemView.findViewById(R.id.textViewItemStatus);
            textViewCenterName = (TextView) itemView.findViewById(R.id.textViewCenterName);
            textViewItemCenterAddress = (TextView) itemView.findViewById(R.id.textViewItemCenterAddress);

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