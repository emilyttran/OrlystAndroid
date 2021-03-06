package com.example.stephanieangulo.orlyst;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.ViewHolder>
        implements Filterable  {
    private static final String TAG = "NewsFeedAdapter.java";
    private Context context;
    private List<Item> mItemsList;
    private List<Item> originalData;

    public NewsFeedAdapter(Context context, List<Item> itemsList) {
        this.context = context;
        this.mItemsList = itemsList;
        this.originalData = itemsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.newsfeed_item, parent, false);
        return new ViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Item item = mItemsList.get(position);
        holder.name.setText(item.getItemName());
        holder.price.setText("$" + item.getPrice());
        holder.category.setText(item.getCategory());
        holder.user.setText(item.getSeller());
        if(item.getBytes() != null) {
            Glide.with(context)
                    .load(item.getBytes())
                    .asBitmap()
                    .placeholder(R.drawable.loading_spinner)
                    .into(holder.thumbnail);
        }
        if(!item.isImageFound()) {
            holder.thumbnail.setImageResource(R.drawable.image_not_found);
        }
    }
    @Override
    public int getItemCount() {
        return mItemsList.size();
    }

     class ViewHolder extends RecyclerView.ViewHolder {
         TextView name, description, user, onWatchlist, price, category;
         ImageView thumbnail;


         ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.itemTitle);
            price = view.findViewById(R.id.itemPrice);
            category = view.findViewById(R.id.itemCategory);
            user = view.findViewById(R.id.userSellingItem);
            thumbnail = view.findViewById(R.id.itemImageView);
            onWatchlist = view.findViewById(R.id.onWatchlistText);
        }

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if(constraint.length() == 0 ||
                        constraint ==  null) {
                    Log.d(TAG, "HELLO THERE");
                    results.values = originalData;
                    results.count = originalData.size();
                } else {
                    List<Item> filteredData = new ArrayList<>();
                    for(Item item: originalData) {
                        String parameters = constraint.toString().toLowerCase();
                        String itemName = item.getItemName().toLowerCase();
                        String itemDescription = item.getDescription().toLowerCase();
                        String itemAuthor = item.getSeller().toLowerCase();
                        String itemCategory = item.getCategory().toLowerCase();
                        if(itemName.contains(parameters) || itemDescription.contains(parameters) ||
                                itemAuthor.contains(parameters)|| itemCategory.contains(parameters)) {
                            filteredData.add(item);
                        }
                    }
                    results.values = filteredData;
                    results.count = filteredData.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mItemsList = (ArrayList<Item>)results.values;
                notifyDataSetChanged();
            }
        };
    }
}
