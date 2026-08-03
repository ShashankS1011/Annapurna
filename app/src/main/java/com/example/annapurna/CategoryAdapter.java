package com.example.annapurna;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

/**
 * Adapter for horizontal category filter chips on the Home Dashboard.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category, int position);
    }

    private final List<Category> categoryList;
    private final OnCategoryClickListener listener;
    private int selectedPosition = 0;

    public CategoryAdapter(List<Category> categoryList, OnCategoryClickListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        Context context = holder.itemView.getContext();

        holder.txtCategoryName.setText(category.getName());

        if (position == selectedPosition) {
            holder.cardCategory.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary));
            holder.txtCategoryName.setTextColor(ContextCompat.getColor(context, R.color.textWhite));
        } else {
            holder.cardCategory.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_bg));
            holder.txtCategoryName.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
        }

        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);

            if (listener != null && selectedPosition != RecyclerView.NO_POSITION) {
                listener.onCategoryClick(categoryList.get(selectedPosition), selectedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList != null ? categoryList.size() : 0;
    }

    public void setSelectedPosition(int position) {
        int previous = selectedPosition;
        this.selectedPosition = position;
        notifyItemChanged(previous);
        notifyItemChanged(selectedPosition);
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardCategory;
        TextView txtCategoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cardCategory = itemView.findViewById(R.id.cardCategory);
            txtCategoryName = itemView.findViewById(R.id.txtCategoryName);
        }
    }
}