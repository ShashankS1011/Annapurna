package com.example.annapurna;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

/**
 * Modern ListAdapter utilizing DiffUtil for asynchronous list diffing
 * and smooth item animations.
 */
public class RecipeAdapter extends ListAdapter<RecipeEntity, RecipeAdapter.RecipeViewHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClick(RecipeEntity recipe);
    }

    private final OnRecipeClickListener listener;

    private static final DiffUtil.ItemCallback<RecipeEntity> DIFF_CALLBACK = new DiffUtil.ItemCallback<RecipeEntity>() {
        @Override
        public boolean areItemsTheSame(@NonNull RecipeEntity oldItem, @NonNull RecipeEntity newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull RecipeEntity oldItem, @NonNull RecipeEntity newItem) {
            return oldItem.getId() == newItem.getId() &&
                    Objects.equals(oldItem.getName(), newItem.getName()) &&
                    Objects.equals(oldItem.getChef(), newItem.getChef()) &&
                    Objects.equals(oldItem.getCategory(), newItem.getCategory()) &&
                    oldItem.isFavorite() == newItem.isFavorite() &&
                    Objects.equals(oldItem.getVideoPath(), newItem.getVideoPath());
        }
    };

    public RecipeAdapter(OnRecipeClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        RecipeEntity recipe = getItem(position);
        holder.bind(recipe, listener);
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtName;
        private final TextView txtChef;
        private final TextView txtCategory;
        private final TextView txtBadge;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtItemName);
            txtChef = itemView.findViewById(R.id.txtItemChef);
            txtCategory = itemView.findViewById(R.id.txtItemCategory);
            txtBadge = itemView.findViewById(R.id.txtItemBadge);
        }

        public void bind(RecipeEntity recipe, OnRecipeClickListener listener) {
            if (txtName != null) txtName.setText(recipe.getName());
            if (txtChef != null) {
                String chef = recipe.getChef();
                txtChef.setText(chef != null && !chef.trim().isEmpty() ? "by " + chef : "");
            }
            if (txtCategory != null) txtCategory.setText(recipe.getCategory());

            if (txtBadge != null) {
                StringBuilder badgeStr = new StringBuilder();
                if (recipe.isFavorite()) {
                    badgeStr.append("⭐ ");
                }
                if (recipe.getVideoPath() != null && !recipe.getVideoPath().trim().isEmpty()) {
                    badgeStr.append("🎥");
                }
                txtBadge.setText(badgeStr.toString().trim());
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onRecipeClick(recipe);
            });
        }
    }
}
