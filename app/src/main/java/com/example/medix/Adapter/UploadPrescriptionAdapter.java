package com.example.medix.Adapter;

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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medix.Activity.Diagnostic.ViewPrescriptionActivity;
import com.example.medix.Api.ApiURL;
import com.example.medix.Common.Common;
import com.example.medix.Model.Prescription;
import com.example.medix.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UploadPrescriptionAdapter extends RecyclerView.Adapter<UploadPrescriptionAdapter.ViewHolder> {

    private List<Prescription> prescriptionList;
    private Context context;
    private RecyclerView recyclerView = null;
    int previousExpandedPosition = -1;
    int mExpandedPosition = -1;

    public UploadPrescriptionAdapter(List<Prescription> prescriptionList, Context context) {
        this.prescriptionList = prescriptionList;
        this.context = context;
        setHasStableIds(true);
    }

    @Override
    public UploadPrescriptionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_upload_prescription_items, parent, false);
        return new UploadPrescriptionAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final UploadPrescriptionAdapter.ViewHolder holder, final int position) {
        final Prescription prescription = prescriptionList.get(position);

        /**
         *  Animation Part
         */
        setFadeAnimation(holder.itemView);
        //holder.linearLayout.setAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_scale_animation));

        final boolean isExpanded = position == mExpandedPosition;
        holder.linearLayView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.itemView.setActivated(isExpanded);
        if (isExpanded) {
            previousExpandedPosition = position;
        }

        holder.textViewCustomerName.setText(prescription.getCustomer_name());
        holder.textViewCustomerPhone.setText("Phone: " + prescription.getCustomer_phone());
        holder.textViewCustomerNote.setText("Note: " + prescription.getNote());

        String strCurrentDate = prescription.getDate_added();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date newDate = null;
        try {
            newDate = format.parse(strCurrentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //format = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
        format = new SimpleDateFormat("MMM dd, yyyy");
        String date = format.format(newDate);
        holder.textViewDate.setText(date);

        Picasso.with(context)
                .load(R.drawable.diag)
                .error(R.drawable.diag)
                .into(holder.imageView);


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

        holder.btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.PRESCRIPTION_IMAGE_URL = ApiURL.MEDIX_URL + prescription.getImage_url();
                Intent intent = new Intent(context, ViewPrescriptionActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return prescriptionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewCustomerPhone, textViewCustomerName, textViewCustomerNote, textViewDate;
        public ImageView imageView;
        public Button btnView;
        public LinearLayout linearLayout, linearLayView;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewCustomerPhone = (TextView) itemView.findViewById(R.id.textViewCustomerPhone);
            textViewCustomerName = (TextView) itemView.findViewById(R.id.textViewCustomerName);
            textViewCustomerNote = (TextView) itemView.findViewById(R.id.textViewCustomerNote);
            textViewDate = (TextView) itemView.findViewById(R.id.textViewDate);
            imageView = (ImageView) itemView.findViewById(R.id.item_image);
            btnView = (Button) itemView.findViewById(R.id.btnView);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLay);
            linearLayView = (LinearLayout) itemView.findViewById(R.id.linearLayView);
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

    public void updateList(List<Prescription> list) {
        this.prescriptionList = list;
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