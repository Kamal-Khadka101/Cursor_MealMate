package com.example.mealmate.ui.shopping;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mealmate.R;
import com.example.mealmate.models.ShoppingItem;
import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {
    private final List<ShoppingItem> items;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ShoppingItem item);
    }

    public ShoppingListAdapter(List<ShoppingItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shopping_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShoppingItem item = items.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemNameTextView;
        private final TextView quantityTextView;
        private final CheckBox completedCheckBox;

        ViewHolder(View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.item_name);
            quantityTextView = itemView.findViewById(R.id.item_quantity);
            completedCheckBox = itemView.findViewById(R.id.checkbox_completed);
        }

        void bind(final ShoppingItem item, final OnItemClickListener listener) {
            itemNameTextView.setText(item.getName());
            quantityTextView.setText(String.format("%d %s", item.getQuantity(), item.getUnit()));
            completedCheckBox.setChecked(item.isCompleted());

            itemView.setOnClickListener(v -> listener.onItemClick(item));
            completedCheckBox.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
} 