package com.example.medix.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.medix.Activity.Diagnostic.DiagnosticTestActivity;
import com.example.medix.Common.Common;
import com.example.medix.Model.Diagnostic;
import com.example.medix.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class DiagnosticListAdapter extends RecyclerView.Adapter<DiagnosticListAdapter.ViewHolder> {

    private List<Diagnostic> diagnosticLists;
    private Context context;

    public DiagnosticListAdapter(List<Diagnostic> diagnostics, Context context) {
        this.diagnosticLists = diagnostics;
        this.context = context;
        setHasStableIds(true);
    }

    @Override
    public DiagnosticListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_diagnostic_items, parent, false);
        return new DiagnosticListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final DiagnosticListAdapter.ViewHolder holder, final int position) {
        final Diagnostic diagnostic = diagnosticLists.get(position);

        /**
         *  Animation Part
         */
        //setFadeAnimation(holder.itemView);
        holder.linearLayout.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation));

        holder.textViewName.setText(diagnostic.getName());
        holder.textViewAddress.setText(diagnostic.getAddress());

        if (diagnostic.getDiscount().equals("0.0000")) {
            holder.textViewDiscount.setVisibility(View.GONE);
        } else {
            double discount = Double.parseDouble(diagnostic.getDiscount());
            int dis = (int) discount;
            holder.textViewDiscount.setVisibility(View.VISIBLE);
            holder.textViewDiscount.setText(String.valueOf(dis) + "% off");
        }

        Picasso.with(context)
                .load(R.drawable.diag)
                .error(R.drawable.diag)
                .into(holder.imageView);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DiagnosticTestActivity.class);
                intent.putExtra("NAME", diagnostic.getName());
                intent.putExtra("CENTER_ID", diagnostic.getDiagnostic_center_id());
                Common.CENTER_ID = diagnostic.getDiagnostic_center_id();

                context.startActivity(intent);
            }
        });
    }

    public void updateList(List<Diagnostic> list) {
        this.diagnosticLists = list;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return diagnosticLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewName, textViewAddress, textViewDiscount;
        public ImageView imageView;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewName = (TextView) itemView.findViewById(R.id.item_name);
            textViewAddress = (TextView) itemView.findViewById(R.id.item_address);
            textViewDiscount = (TextView) itemView.findViewById(R.id.item_discount);
            imageView = (ImageView) itemView.findViewById(R.id.item_image);
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