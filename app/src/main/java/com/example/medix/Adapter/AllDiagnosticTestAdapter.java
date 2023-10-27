package com.example.medix.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medix.Activity.AllDiagnosticTestActivity;
import com.example.medix.Activity.SingleTestActivity;
import com.example.medix.Common.Common;
import com.example.medix.Database.Database;
import com.example.medix.Model.Cart;
import com.example.medix.Model.Test;
import com.example.medix.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AllDiagnosticTestAdapter extends RecyclerView.Adapter<AllDiagnosticTestAdapter.ViewHolder> {

    private List<Test> diagnosticTestLists;
    private Context context;
    private RecyclerView recyclerView = null;
    int previousExpandedPosition = -1;
    int mExpandedPosition = -1;

    public AllDiagnosticTestAdapter(List<Test> diagnosticTestLists, Context context) {
        this.diagnosticTestLists = diagnosticTestLists;
        this.context = context;
        setHasStableIds(true);
    }

    @Override
    public AllDiagnosticTestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_all_diagnostic_test_items, parent, false);
        return new AllDiagnosticTestAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AllDiagnosticTestAdapter.ViewHolder holder, final int position) {
        final Test test = diagnosticTestLists.get(position);

        /**
         *  Animation Part
         */
        setFadeAnimation(holder.itemView);
        //holder.linearLayout.setAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_scale_animation));

        final boolean isExpanded = position == mExpandedPosition;
        holder.linearLayBook.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.itemView.setActivated(isExpanded);
        if (isExpanded) {
            previousExpandedPosition = position;
        }

        holder.textViewDiagnosticName.setText(test.getDiagnostic_center_name());
        holder.textViewTestName.setText(test.getName());
        holder.textViewDiagnosticAddress.setText(test.getAddress());

        if (test.getDiscount_price().equals("0.0000")) {
            DecimalFormat df2 = new DecimalFormat("####0.00");
            double price = Double.parseDouble(test.getPrice());
            holder.textViewPrice.setText(new StringBuilder(context.getResources().getString(R.string.currency_sign)).append(df2.format(price)));
        } else {
            DecimalFormat df2 = new DecimalFormat("####0.00");
            double price = Double.parseDouble(test.getDiscount_price());
            holder.textViewPrice.setText(new StringBuilder(context.getResources().getString(R.string.currency_sign)).append(df2.format(price)));
        }
        Picasso.with(context)
                .load(R.drawable.diag)
                .error(R.drawable.diag)
                .into(holder.imageView);

        if (new Database(context).checkExistence(test.getTest_id())) {
            holder.btnBook.setBackgroundResource(R.color.colorPrimary1);
            holder.btnBook.setText("Booked");


            holder.btnBook.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    // Remove item from Cart
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle("Remove from Cart");
                    alertDialog.setMessage("Are you sure you want to remove " + test.getName() + " - from your cart?");
                    alertDialog.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.setNegativeButton("REMOVE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            new Database(context).clearCartITemFromProduct(test.getTest_id(), test.getCenter_id());
                            holder.btnBook.setText("Book");
                            holder.btnBook.setBackgroundResource(R.color.colorPrimary);

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
        
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1 : position;
                notifyItemChanged(previousExpandedPosition);
                notifyItemChanged(position);

                final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int distance;
                    View first = recyclerView.getChildAt(0);
                    int height = first.getHeight();
                    int current = recyclerView.getChildAdapterPosition(first);
                    int p = Math.abs(position - current);
                    if (p > 5) distance = (p - (p - 5)) * height;
                    else distance = p * height;
                    manager.scrollToPositionWithOffset(position, distance);
                }
            }
        });

        holder.btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Checking if the test is already added or not
                if (!new Database(context).checkExistence(test.getTest_id())) {

                    if (test.getDiscount_price().equals("0.0000")) {
                        new Database(context).addToCart(new Cart(
                                test.getTest_id(),
                                test.getName(),
                                test.getShort_name(),
                                test.getCenter_id(),
                                test.getDiagnostic_center_name(),
                                test.getAddress(),
                                test.getPrice(),
                                "1"
                        ));
                    } else {
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        Date endDate = null;
                        try {
                            endDate = df.parse(test.getDis_end_date());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Date currentDate = new Date();

                        if (endDate.before(currentDate)) {
                            new Database(context).addToCart(new Cart(
                                    test.getTest_id(),
                                    test.getName(),
                                    test.getShort_name(),
                                    test.getCenter_id(),
                                    test.getDiagnostic_center_name(),
                                    test.getAddress(),
                                    test.getPrice(),
                                    "1"
                            ));
                        } else {
                            new Database(context).addToCart(new Cart(
                                    test.getTest_id(),
                                    test.getName(),
                                    test.getShort_name(),
                                    test.getCenter_id(),
                                    test.getDiagnostic_center_name(),
                                    test.getAddress(),
                                    test.getDiscount_price(),
                                    "1"
                            ));
                        }
                    }
                    holder.btnBook.setBackgroundResource(R.color.colorPrimary1);
                    holder.btnBook.setText("Booked");
                    Toast.makeText(context, test.getName() + " added to cart", Toast.LENGTH_SHORT).show();
                    if (context.getClass().equals(AllDiagnosticTestActivity.class)) {
                        ((AllDiagnosticTestActivity)context).getCartCount();
                    }

                } else {
                    Toast.makeText(context, "Long press to remove", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.singleTest = diagnosticTestLists.get(position);
                context.startActivity(new Intent(context, SingleTestActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return diagnosticTestLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewDiagnosticName, textViewTestName, textViewDiagnosticAddress, textViewPrice;
        public ImageView imageView;
        public Button btnBook, btnDetails;
        public LinearLayout linearLayout, linearLayBook;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewDiagnosticName = (TextView) itemView.findViewById(R.id.textViewDiagnosticName);
            textViewTestName = (TextView) itemView.findViewById(R.id.textViewTestName);
            textViewDiagnosticAddress = (TextView) itemView.findViewById(R.id.textViewDiagnosticAddress);
            textViewPrice = (TextView) itemView.findViewById(R.id.textViewPrice);
            imageView = (ImageView) itemView.findViewById(R.id.item_image);
            btnBook = (Button) itemView.findViewById(R.id.btnBook);
            btnDetails = (Button) itemView.findViewById(R.id.btnDetails);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLay);
            linearLayBook = (LinearLayout) itemView.findViewById(R.id.linearLayBook);
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

    public void updateList(List<Test> list) {
        this.diagnosticTestLists = list;
        notifyDataSetChanged();
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

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }
}