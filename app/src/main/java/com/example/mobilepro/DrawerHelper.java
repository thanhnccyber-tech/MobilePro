package com.example.mobilepro;

import android.app.Activity;
import android.content.Intent;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class DrawerHelper {
    
    public static void setup(Activity activity, DrawerLayout drawerLayout,
                             MaterialToolbar toolbar, NavigationView navigationView,
                             int activeMenuId) {

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                activity, drawerLayout, toolbar,
                android.R.string.ok, android.R.string.cancel
        );
        toggle.getDrawerArrowDrawable().setColor(activity.getColor(R.color.white));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setCheckedItem(activeMenuId);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            drawerLayout.closeDrawer(GravityCompat.START);

            if (id == activeMenuId) return true;

            if (id == R.id.nav_home) {
                navigateMainTab(activity, HomeActivity.class);
            } else if (id == R.id.nav_products) {
                navigateMainTab(activity, ProductListActivity.class);
            } else if (id == R.id.nav_cart) {
                navigateMainTab(activity, CartActivity.class);
            } else if (id == R.id.nav_profile) {
                navigateMainTab(activity, ProfileActivity.class);
            } else if (id == R.id.nav_orders) {
                activity.startActivity(new Intent(activity, OrderHistoryActivity.class));
            } else if (id == R.id.nav_favorites) {
                activity.startActivity(new Intent(activity, FavoriteActivity.class));
            } else if (id == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(activity, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);
            }
            return true;
        });
    }

    private static void navigateMainTab(Activity activity, Class<?> destination) {
        Intent intent = new Intent(activity, destination);
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
        activity.finish();
    }
}