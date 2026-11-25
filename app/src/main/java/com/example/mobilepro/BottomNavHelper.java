package com.example.mobilepro;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class BottomNavHelper {

    public static final int TAB_HOME = 0;
    public static final int TAB_EXPLORE = 1;
    public static final int TAB_CART = 2;
    public static final int TAB_ACCOUNT = 3;

    public static void setup(Activity activity, int activeTab) {
        LinearLayout[] tabs = {
                activity.findViewById(R.id.navTabHome),
                activity.findViewById(R.id.navTabExplore),
                activity.findViewById(R.id.navTabCart),
                activity.findViewById(R.id.navTabAccount)
        };

        ImageView[] icons = {
                activity.findViewById(R.id.ivNavHome),
                activity.findViewById(R.id.ivNavExplore),
                activity.findViewById(R.id.ivNavCart),
                activity.findViewById(R.id.ivNavAccount)
        };

        TextView[] labels = {
                activity.findViewById(R.id.tvNavHome),
                activity.findViewById(R.id.tvNavExplore),
                activity.findViewById(R.id.tvNavCart),
                activity.findViewById(R.id.tvNavAccount)
        };

        Class<?>[] destinations = {
                HomeActivity.class,
                ProductListActivity.class,
                CartActivity.class,
                ProfileActivity.class
        };

        int inactiveColor = ContextCompat.getColor(activity, R.color.brown_light);

        for (int i = 0; i < tabs.length; i++) {
            boolean isSelected = (i == activeTab);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabs[i].getLayoutParams();

            if (isSelected) {
                params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                params.weight = 0f;
                tabs[i].setBackgroundResource(R.drawable.bg_nav_pill);
                icons[i].setColorFilter(Color.WHITE);
                labels[i].setVisibility(View.VISIBLE);
            } else {
                params.width = 0;
                params.weight = 1f;
                tabs[i].setBackground(null);
                icons[i].setColorFilter(inactiveColor);
                labels[i].setVisibility(View.GONE);
            }
            tabs[i].setLayoutParams(params);

            final int index = i;
            tabs[i].setOnClickListener(v -> {
                if (index != activeTab) {
                    Intent intent = new Intent(activity, destinations[index]);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(0, 0);
                    activity.finish();
                }
            });
        }
    }
}