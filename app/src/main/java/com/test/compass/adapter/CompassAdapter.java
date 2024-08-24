package com.test.compass.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.test.compass.R;


public class CompassAdapter extends RecyclerView.Adapter<CompassAdapter.ViewHolder> {

    Context context;
    CompassAdapter.iClickListener mClick;
    public interface iClickListener {
        void onClickItem(int position);

    }

    private final int[] text  = {R.string.stand_compass,R.string.digital_compass,R.string.camera_compass,R.string.qibla_compass,R.string.satellite_compass,R.string.map_compass,R.string.aviation_compass,R.string.vintage_compass};
    private final int[] bg = {R.drawable.stand, R.drawable.digital, R.drawable.camera, R.drawable.qibla, R.drawable.satellite, R.drawable.map, R.drawable.aviation, R.drawable.vintage};

    public CompassAdapter(Context context,iClickListener mClick) {
        this.context = context;
        this.mClick = mClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new ViewHolder(inflater.inflate(R.layout.item_compass, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.img_view.setImageResource(bg[position]);
        holder.tv_view.setText(text[position]);
        holder.itemView.setOnClickListener(view -> {

            mClick.onClickItem(position);
        });
    }

    @Override
    public int getItemCount() {
        return bg.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_view;
        TextView tv_view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_view = itemView.findViewById(R.id.img_view);
            tv_view = itemView.findViewById(R.id.tv_view);

        }
    }
}
