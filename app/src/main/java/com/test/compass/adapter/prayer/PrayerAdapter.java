package com.test.compass.adapter.prayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.test.compass.R;
import com.test.compass.util.SharePreferencesController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class PrayerAdapter extends RecyclerView.Adapter<PrayerAdapter.LanguageViewHolder> {
    List<PrayerModel> listPrayer;
    Context context;
    OnItemNotification onItemNotification;
    OnClickSetting onClickSetting;
    SimpleDateFormat sdf;

    public PrayerAdapter(Context context, List<PrayerModel> listPrayer, OnItemNotification onItemNotification, OnClickSetting onClickSetting) {
        this.context = context;
        this.listPrayer = listPrayer;
        this.onItemNotification = onItemNotification;
        this.onClickSetting = onClickSetting;
    }

    @NonNull
    @Override
    public LanguageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LanguageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timer, parent, false));
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull LanguageViewHolder holder, @SuppressLint("RecyclerView") int position) {
        PrayerModel prayerModel = listPrayer.get(position);
        holder.tvName.setText(listPrayer.get(position).getName());

        boolean use24HourFormat = SharePreferencesController.getInstance(context).getBoolean("format_time",true);

        if (!use24HourFormat){
            String originalTime = listPrayer.get(position).getTimer();
            String[] timeParts = originalTime.split(":");

            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);
            String displayTime;
            if (hour == 0) {
                // Nếu giờ là 0, đặt giờ thành 12 AM
                displayTime = String.format("12:%02d AM", minute);
            } else if (hour < 12) {
                // Nếu giờ nhỏ hơn 12, đặt giờ và AM
                displayTime = String.format("%d:%02d AM", hour, minute);
            } else {
                // Nếu giờ lớn hơn hoặc bằng 12, chuyển sang định dạng 12 giờ và PM
                displayTime = String.format("%d:%02d PM", (hour == 12) ? 12 : hour - 12, minute);
            }
            holder.tvTime.setText(displayTime);

        }else {
            holder.tvTime.setText(listPrayer.get(position).getTimer());
        }




        if (listPrayer.get(position).getActive()) {
            holder.view.setVisibility(View.VISIBLE);
            holder.lnTimer.setBackgroundColor(ContextCompat.getColor(context, R.color.color_p_s));
        } else {
            holder.lnTimer.setBackgroundColor(ContextCompat.getColor(context, R.color.color_p_sn));
            holder.view.setVisibility(View.GONE);
        }

        holder.ivSetting.setOnClickListener(view -> onClickSetting.onClickSetting(position));

//        saveTimeAlarm(prayerModel.getId(), prayerModel.getTime_alarm());


        String savedTimeAlarm = readTimeAlarm(prayerModel.getId());

//        holder.tv_time_alarm.setText(savedTimeAlarm);


        if (!use24HourFormat){
            String originalTime = savedTimeAlarm;
            String[] timeParts = originalTime.split(":");

            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);
            String displayTime;
            if (hour == 0) {
                // Nếu giờ là 0, đặt giờ thành 12 AM
                displayTime = String.format("12:%02d AM", minute);
            } else if (hour < 12) {
                // Nếu giờ nhỏ hơn 12, đặt giờ và AM
                displayTime = String.format("%d:%02d AM", hour, minute);
            } else {
                // Nếu giờ lớn hơn hoặc bằng 12, chuyển sang định dạng 12 giờ và PM
                displayTime = String.format("%d:%02d PM", (hour == 12) ? 12 : hour - 12, minute);
            }
            holder.tv_time_alarm.setText(displayTime);

        }else {
            holder.tv_time_alarm.setText(savedTimeAlarm);
        }


        boolean isSavedAlarmState = readAlarmState(prayerModel.getId());
        if (isSavedAlarmState) {
            holder.tv_time_alarm.setVisibility(View.VISIBLE);
            holder.ivNotification.setImageResource(R.drawable.ic_notification_s_p);
        } else {
            holder.ivNotification.setImageResource(R.drawable.ic_notification_sn_p);
            holder.tv_time_alarm.setVisibility(View.INVISIBLE);
        }


        int backgroundColor = isSavedAlarmState  ? R.drawable.ic_notification_s_p : R.drawable.ic_notification_sn_p;
        holder.ivNotification.setImageResource(backgroundColor);
        holder.ivNotification.setOnClickListener(view -> {
            boolean newAlarmState = !prayerModel.isAlarm();
            prayerModel.setAlarm(newAlarmState);
            saveAlarmState(prayerModel.getId(), newAlarmState);
            saveTimeAlarm(prayerModel.getId(), savedTimeAlarm);
            notifyItemChanged(position);
        });

    }

    @Override
    public int getItemCount() {
        return listPrayer.size();
    }

    public static class LanguageViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvTime;
        private final TextView tv_time_alarm;
        private final ConstraintLayout lnTimer;
        private final ImageView ivSetting;
        private final ImageView ivNotification;
        View view;


        public LanguageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvTime = itemView.findViewById(R.id.tv_time);
            lnTimer = itemView.findViewById(R.id.ln_timer);
            ivSetting = itemView.findViewById(R.id.iv_setting);
            ivNotification = itemView.findViewById(R.id.iv_notification);
            tv_time_alarm = itemView.findViewById(R.id.tv_time_alarm);
            view = itemView.findViewById(R.id.view);
        }
    }

    public void setCheck(String code) {
        for (PrayerModel item : listPrayer) {
            item.setActive(item.getName().equals(code));
        }
        notifyDataSetChanged();
    }

    public interface OnItemNotification {
        void onItemNotification(String time);
    }

    public interface OnClickSetting {
        void onClickSetting(int pos);
    }

    private void saveAlarmState(int prayerId, boolean isAlarm) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("prayer_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("alarm_" + prayerId, isAlarm);
        editor.apply();
    }
    private boolean readAlarmState(int prayerId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("prayer_prefs", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("alarm_" + prayerId, false);
    }

    private void saveTimeAlarm(int prayerId, String timeAlarm) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("prayer_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("time_alarm_" + prayerId, timeAlarm);
        editor.apply();
    }
    private String readTimeAlarm(int prayerId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("prayer_prefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("time_alarm_" + prayerId, null);
    }
}
