package com.example.medix.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.medix.Model.Rating;
import com.example.medix.R;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ProductCommentsAdapter extends RecyclerView.Adapter<ProductCommentsAdapter.ViewHolder> {

    private List<Rating> ratingList;
    private Context mCtx;

    public ProductCommentsAdapter(List<Rating> ratings, Context mCtx) {
        this.ratingList = ratings;
        this.mCtx = mCtx;

        setHasStableIds(true);
    }

    @Override
    public ProductCommentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_product_reviews, parent, false);
        return new ProductCommentsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ProductCommentsAdapter.ViewHolder holder, final int position) {
        final Rating rating = ratingList.get(position);


        holder.textViewMainRating.setText(getRatingText(rating.getRating()));

        String str = rating.getText();
        String replacedStr = str.replaceAll("&amp;", "&");
        holder.textViewComment.setText(replacedStr);
        holder.textViewDate.setText(rating.getDate_added());
        holder.textViewUsername.setText(rating.getAuthor());
        holder.ratingBar.setRating(rating.getRating());
    }

    @Override
    public int getItemCount() {
        return ratingList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewMainRating, textViewDate, textViewUsername, textViewComment;
        public RatingBar ratingBar;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewMainRating = (TextView) itemView.findViewById(R.id.textViewMainRating);
            textViewDate = (TextView) itemView.findViewById(R.id.textViewDate);
            textViewUsername = (TextView) itemView.findViewById(R.id.textViewUsername);
            textViewComment = (TextView) itemView.findViewById(R.id.textViewComment);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
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

    private String getRatingText(int rating)
    {
        String ratingText;

        switch (rating) {
            case 5:
                ratingText = "Excellent";
                break;
            case 4:
                ratingText = "Very Good";
                break;
            case 3:
                ratingText = "Quite Ok";
                break;
            case 2:
                ratingText = "Not Good";
                break;
            case 1:
                ratingText = "Very Bad";
                break;
            default:
                ratingText = "Very Good";
        }
        return ratingText;
    }
}