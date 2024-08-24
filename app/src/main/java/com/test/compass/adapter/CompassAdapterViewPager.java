package com.test.compass.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.test.compass.R;
import com.test.compass.activity.ListCompassDetail;


public class CompassAdapterViewPager extends RecyclerView.Adapter<CompassAdapterViewPager.ViewHolder> {

    Context context;
    ViewPager2 viewPager2;
    int selectposition = 0;
    private final int[] text = {R.string.Standard,R.string.Digital,R.string.Camera,R.string.Qibla,R.string.Satellite,R.string.Map,R.string.Aviation,R.string.Vintage};
    private final int[] bg = {R.drawable.stand, R.drawable.digital, R.drawable.camera, R.drawable.qibla, R.drawable.satellite, R.drawable.map, R.drawable.aviation, R.drawable.vintage};

    public CompassAdapterViewPager(Context context, ViewPager2 viewPager2) {
        this.context = context;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new ViewHolder(inflater.inflate(R.layout.item_compass_viewpager, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        selectposition = ListCompassDetail.select;
        if (selectposition == position){
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        }else {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#B1B8BE"));
        }
        holder.img_view.setImageResource(bg[position]);
        holder.tv_view.setText(text[position]);
    }

    @Override
    public int getItemCount() {
        return bg.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_view;
        TextView tv_view;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_view = itemView.findViewById(R.id.img_view);
            tv_view = itemView.findViewById(R.id.tv_view);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }
}
