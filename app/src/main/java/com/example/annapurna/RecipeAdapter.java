package com.example.annapurna;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClick(RecipeEntity recipe);
    }

    private List<RecipeEntity> recipeList;
    private final OnRecipeClickListener listener;

    public RecipeAdapter(List<RecipeEntity> recipeList, OnRecipeClickListener listener) {
        this.recipeList = recipeList;
        this.listener = listener;
    }

    public void updateList(List<RecipeEntity> newRecipes) {
        this.recipeList = newRecipes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        RecipeEntity recipe = recipeList.get(position);
        holder.bind(recipe, listener);
    }

    @Override
    public int getItemCount() {
        return recipeList != null ? recipeList.size() : 0;
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtName;
        private final TextView txtChef;
        private final TextView txtCategory;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtItemName);
            txtChef = itemView.findViewById(R.id.txtItemChef);
            txtCategory = itemView.findViewById(R.id.txtItemCategory);
        }

        public void bind(RecipeEntity recipe, OnRecipeClickListener listener) {
            if (txtName != null) txtName.setText(recipe.getName());
            if (txtChef != null) txtChef.setText(recipe.getChef());
            if (txtCategory != null) txtCategory.setText(recipe.getCategory());

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onRecipeClick(recipe);
            });
        }
    }
}