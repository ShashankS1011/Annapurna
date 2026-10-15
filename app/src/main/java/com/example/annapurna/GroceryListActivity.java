package com.example.annapurna;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GroceryListActivity extends BaseActivity {

    private RecyclerView rvGroceryList;
    private LinearLayout layoutGroceryEmpty;
    private GroceryAdapter groceryAdapter;
    private final List<String> groceryItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_list);

        ImageView btnBack = findViewById(R.id.btnGroceryBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        rvGroceryList = findViewById(R.id.rvGroceryList);
        layoutGroceryEmpty = findViewById(R.id.layoutGroceryEmpty);

        rvGroceryList.setLayoutManager(new LinearLayoutManager(this));
        groceryAdapter = new GroceryAdapter(groceryItems);
        rvGroceryList.setAdapter(groceryAdapter);

        loadIngredientsFromDb();
    }

    private void loadIngredientsFromDb() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            List<RecipeEntity> recipes = db.recipeDao().getAllRecipesList();
            List<String> parsedItems = new ArrayList<>();

            for (RecipeEntity r : recipes) {
                String ing = r.getIngredients();
                if (ing != null && !ing.trim().isEmpty()) {
                    String[] lines = ing.split("\n");
                    for (String line : lines) {
                        String clean = line.trim();
                        if (!clean.isEmpty() && !parsedItems.contains(clean)) {
                            parsedItems.add(clean + " (from " + r.getName() + ")");
                        }
                    }
                }
            }

            runOnUiThread(() -> {
                groceryItems.clear();
                groceryItems.addAll(parsedItems);
                groceryAdapter.notifyDataSetChanged();

                if (groceryItems.isEmpty()) {
                    rvGroceryList.setVisibility(View.GONE);
                    layoutGroceryEmpty.setVisibility(View.VISIBLE);
                } else {
                    rvGroceryList.setVisibility(View.VISIBLE);
                    layoutGroceryEmpty.setVisibility(View.GONE);
                }
            });
        }).start();
    }

    private static class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.ViewHolder> {
        private final List<String> items;

        public GroceryAdapter(List<String> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grocery, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String item = items.get(position);
            holder.cbItem.setText(item);
            holder.cbItem.setOnCheckedChangeListener(null);
            holder.cbItem.setChecked(false);
            holder.cbItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    holder.cbItem.setAlpha(0.5f);
                } else {
                    holder.cbItem.setAlpha(1.0f);
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            CheckBox cbItem;
            ViewHolder(View itemView) {
                super(itemView);
                cbItem = itemView.findViewById(R.id.cbGroceryItem);
            }
        }
    }
}
