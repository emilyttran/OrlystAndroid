package com.example.stephanieangulo.orlyst;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.parceler.Parcels;

public class ItemDetailActivity extends AppCompatActivity {
    private static final String TAG = ItemDetailActivity.class.getSimpleName();
    private Context mContext;
    private TextView itemTitle;
    private TextView itemDescription;
    private TextView itemSeller;
    private ImageView itemImage;
    private Button watchlistBtn;
    private Button contactBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_item);
        mContext = this;

        itemTitle = findViewById(R.id.detail_item_title);
        itemDescription = findViewById(R.id.item_description_tv);
        itemSeller = findViewById(R.id.seller_name_tv);
        itemImage = findViewById(R.id.detail_item_image);
        watchlistBtn = findViewById(R.id.detail_watchlist_btn);
        contactBtn = findViewById(R.id.detail_contact_btn);

        Item item = Parcels.unwrap(getIntent().getParcelableExtra("Item"));
        User user = Parcels.unwrap(getIntent().getParcelableExtra("User"));

        Log.d(TAG, "User email is " + user.getEmail());
        itemTitle.setText(item.getItemName());
        itemDescription.setText(item.getDescription());
        if(item.getBytes() != null)
            setItemImage(item.getBytes());
        itemSeller.setText("by " + item.getSeller());
        itemSeller.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        watchlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Need to implement add to watchlist function");

            }
        });

        contactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Need to implement add to contact seller function");

            }
        });

        itemSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Seller clicked");
            }
        });

    }

    private void setItemImage(byte[] jpeg) {
        Glide.with(mContext)
                .load(jpeg)
                .asBitmap()
                .into(itemImage);

    }
}
