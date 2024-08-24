package com.test.compass.dialog;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.test.compass.R;
import com.test.compass.util.SystemUtil;


public class DialogRating extends Dialog {
    private OnPress onPress;
    private final RatingBar rtb;
    private final ImageView imgIcon;
    private final Context context;
    private final TextView btnRate;
    private final TextView btnNotNow;

    public DialogRating(Context context2) {
        super(context2);
        this.context = context2;
        SystemUtil.setLocale(getContext());
        setContentView(R.layout.dialog_rating_app);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(attributes);
        getWindow().setSoftInputMode(16);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        rtb = findViewById(R.id.rtb);
        imgIcon = findViewById(R.id.imgIcon);
        btnRate = findViewById(R.id.btnRateUs);
        btnNotNow = findViewById(R.id.btnNotNow);
        onclick();
        changeRating();

    }

    public interface OnPress {
        void send();

        void rating();

        void later();
    }

    public void init(Context context, OnPress onPress) {
        this.onPress = onPress;
    }

    public void changeRating() {
        rtb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                String getRating = String.valueOf(rtb.getRating());
                switch (getRating) {
                    case "1.0":
                        btnRate.setText(context.getResources().getString(R.string.Thankyou));
                        btnNotNow.setVisibility(View.GONE);
                        imgIcon.setImageResource(R.drawable.rating_1);
                        break;
                    case "2.0":
                        btnNotNow.setVisibility(View.GONE);
                        btnRate.setText(context.getResources().getString(R.string.Thankyou));
                        imgIcon.setImageResource(R.drawable.rating_2);
                        break;
                    case "3.0":
                        btnNotNow.setVisibility(View.GONE);
                        btnRate.setText(context.getResources().getString(R.string.Thankyou));
                        imgIcon.setImageResource(R.drawable.rating_3);
                        break;
                    case "4.0":
                        btnNotNow.setVisibility(View.GONE);
                        btnRate.setText(context.getResources().getString(R.string.Thankyou));
                        imgIcon.setImageResource(R.drawable.rating_4);
                        break;
                    case "5.0":
                        btnNotNow.setVisibility(View.GONE);
                        btnRate.setText(context.getResources().getString(R.string.Thankyou));
                        imgIcon.setImageResource(R.drawable.rating_5);
                        break;
                    default:
                        btnRate.setText(context.getResources().getString(R.string.rate_us));
                        btnNotNow.setVisibility(View.VISIBLE);
                        imgIcon.setImageResource(R.drawable.rating_0);
                        break;
                }
            }
        });


    }

    public String getRating() {
        return String.valueOf(this.rtb.getRating());
    }

    public void onclick() {
        btnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rtb.getRating() == 0) {
                    return;
                }
                if (rtb.getRating() <= 4.0) {
                    imgIcon.setVisibility(View.GONE);
                    onPress.send();
                } else {
                    imgIcon.setVisibility(View.VISIBLE);
                    onPress.rating();
                }
            }
        });

        btnNotNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPress.later();
            }
        });

    }

}
