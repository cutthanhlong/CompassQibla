
package com.test.compass.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.test.compass.R;
import com.test.compass.activity.AboutScreenActivity;
import com.test.compass.activity.LanguageScreenActivity;
import com.test.compass.databinding.FragmentSettingBinding;
import com.test.compass.util.SharePrefUtils;
import com.test.compass.util.SomeThingApp;
import com.test.compass.util.SystemUtil;


public class FragmentSetting extends Fragment {
    FragmentSettingBinding binding;
    private boolean isShareClicked = false;
    private boolean isClick = false;
    public static RelativeLayout rl_rate;
    public static View view_rate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentSettingBinding.inflate(getLayoutInflater());
        binding.toolbar.icStart.setVisibility(View.GONE);
        rl_rate = binding.rlRate;
        view_rate = binding.viewRate;
        binding.toolbar.tvToolbar.setText(getString(R.string.Setting));
        viewClick();
        String codeLang = SystemUtil.getPreLanguage(getContext());
        switch (codeLang) {
            case "en":
                binding.tvTextLanguage.setText("English");
                break;
            case "pt":
                binding.tvTextLanguage.setText("Portuguese");
                break;
            case "es":
                binding.tvTextLanguage.setText("Spanish");
                break;
            case "de":
                binding.tvTextLanguage.setText("German");
                break;
            case "fr":
                binding.tvTextLanguage.setText("French");
                break;
            case "zh":
                binding.tvTextLanguage.setText("China");
                break;
            case "hi":
                binding.tvTextLanguage.setText("Hindi");
                break;
            case "in":
                binding.tvTextLanguage.setText("Indonesia");
                break;
            case "ms":
                binding.tvTextLanguage.setText("Malay");
                break;
            case "tr":
                binding.tvTextLanguage.setText("Turkey");
                break;
            case "ar":
                binding.tvTextLanguage.setText("Saudi Arabia");
                break;
        }
        return binding.getRoot();
    }

    private void viewClick() {
        binding.rlLanguage.setOnClickListener(view -> {
            if (!isShareClicked) {
                startActivity(new Intent(requireActivity(), LanguageScreenActivity.class));

                isShareClicked = true;
            }
        });
        binding.rlAbout.setOnClickListener(view -> {
            if (!isShareClicked) {
                startActivity(new Intent(requireActivity(), AboutScreenActivity.class));
                isShareClicked = true;
            }
        });
        binding.rlShare.setOnClickListener(view -> {
            if (!isShareClicked) {
                SomeThingApp.shareApp(requireActivity());
                isShareClicked = true;
            }
        });
        binding.rlRate.setOnClickListener(view -> {
            if (!isClick) {

                if (!SharePrefUtils.isRated(requireActivity())) {
                    SomeThingApp.rateApp(requireActivity(), 0);
                }
                isClick = true;
            }
            new Handler().postDelayed(() -> isClick = false, 1000);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        isShareClicked = false;
        isClick = false;
        if (SharePrefUtils.isRated(requireActivity())) {
            rl_rate.setVisibility(View.GONE);
            view_rate.setVisibility(View.GONE);
        }

    }
}
