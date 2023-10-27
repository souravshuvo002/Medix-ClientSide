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

import com.example.medix.Activity.MedicineShop.SubCategoryActivity;
import com.example.medix.Api.ApiURL;
import com.example.medix.Common.Common;
import com.example.medix.Model.MainCategory;
import com.example.medix.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class AllCategoriesAdapter extends RecyclerView.Adapter<AllCategoriesAdapter.ViewHolder> {

    private List<MainCategory> categoryAdapters;
    private Context mCtx;

    public AllCategoriesAdapter(List<MainCategory> menus, Context mCtx) {
        this.categoryAdapters = menus;
        this.mCtx = mCtx;

        setHasStableIds(true);
    }

    @Override
    public AllCategoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_all_cat_items, parent, false);
        return new AllCategoriesAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AllCategoriesAdapter.ViewHolder holder, final int position) {
        final MainCategory menu = categoryAdapters.get(position);

        /**
         *  Animation Part
         */
        setFadeAnimation(holder.itemView);

        String str = menu.getName();
        String replacedStr = str.replaceAll("&amp;", "&");
        holder.textViewName.setText(replacedStr);
        Picasso.with(mCtx)
                .load(ApiURL.IMAGE_URL + menu.getImage())
                .error(R.drawable.diag)
                .into(holder.imageView);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mCtx, SubCategoryActivity.class);
                intent.putExtra("Menu_ID", menu.getCategory_id());
                intent.putExtra("Menu_NAME", menu.getName());
                Common.tempImageLink = menu.getLink();
                Common.menu_id = menu.getCategory_id();
                mCtx.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return categoryAdapters.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewName;
        public ImageView imageView;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewName = (TextView) itemView.findViewById(R.id.item_name);
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