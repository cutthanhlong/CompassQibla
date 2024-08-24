package com.test.compass.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.test.compass.R;
import com.test.compass.language.Interface.IClickItemLanguage;
import com.test.compass.language.Model.LanguageModel;

import java.util.List;

public class LanguageStartAdapter extends RecyclerView.Adapter<LanguageStartAdapter.LanguageViewHolder> {
    private List<LanguageModel> languageModelList;
    private IClickItemLanguage iClickItemLanguage;
    private Context context;

    public LanguageStartAdapter(List<LanguageModel> languageModelList, IClickItemLanguage listener, Context context) {
        this.languageModelList = languageModelList;
        this.iClickItemLanguage = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public LanguageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_language, parent, false);
        return new LanguageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LanguageViewHolder holder, int position) {
        LanguageModel languageModel = languageModelList.get(position);
        if (languageModel == null) {
            return;
        }
        holder.tvLang.setText(languageModel.getName());
        if (languageModel.getActive()) {
            holder.tvLang.setTextColor(Color.parseColor("#09324F"));
            holder.layoutItem.setBackgroundResource(R.drawable.bg_ln_select);
        } else {
            holder.tvLang.setTextColor(Color.parseColor("#1F3848"));
            holder.layoutItem.setBackgroundResource(R.drawable.bg_unselect);
        }

        switch (languageModel.getCode()) {
            case "fr":
                Glide.with(context).load(R.drawable.ic_lang_fr).into(holder.icLang);
                break;
            case "es":
                Glide.with(context).load(R.drawable.ic_lang_es).into(holder.icLang);
                break;
            case "zh":
                Glide.with(context).load(R.drawable.ic_lang_zh).into(holder.icLang);
                break;
            case "in":
                Glide.with(context).load(R.drawable.ic_lang_in).into(holder.icLang);
                break;
            case "hi":
                Glide.with(context).load(R.drawable.ic_lang_hi).into(holder.icLang);
                break;
            case "de":
                Glide.with(context).load(R.drawable.ic_lang_ge).into(holder.icLang);
                break;
            case "pt":
                Glide.with(context).load(R.drawable.ic_lang_pt).into(holder.icLang);
                break;
            case "ms":
                Glide.with(context).load(R.drawable.ic_lang_malay).into(holder.icLang);
                break;
            case "tr":
                Glide.with(context).load(R.drawable.ic_lang_turkey).into(holder.icLang);
                break;
            case "en":
                Glide.with(context).load(R.drawable.ic_lang_en).into(holder.icLang);
                break;
            case "ar":
                Glide.with(context).load(R.drawable.ic_lang_a_rap).into(holder.icLang);
                break;
        }

        holder.layoutItem.setOnClickListener(v -> {
            setCheck(languageModel.getCode());
            iClickItemLanguage.onClickItemLanguage(languageModel.getCode());
            notifyDataSetChanged();
        });

    }

    @Override
    public int getItemCount() {
        if (languageModelList != null) {
            return languageModelList.size();
        } else {
            return 0;
        }
    }

    public class LanguageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvLang;
        private LinearLayout layoutItem;
        private ImageView icLang;


        public LanguageViewHolder(@NonNull View itemView) {
            super(itemView);
            icLang = itemView.findViewById(R.id.icLang);
            tvLang = itemView.findViewById(R.id.tvLang);
            layoutItem = itemView.findViewById(R.id.layoutItem);

        }
    }

    public void setCheck(String code) {
        for (LanguageModel item : languageModelList) {
            item.setActive(item.getCode().equals(code));

        }
        notifyDataSetChanged();
    }
}
