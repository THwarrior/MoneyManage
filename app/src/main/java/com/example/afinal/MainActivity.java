package com.example.afinal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;

import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.afinal.databinding.ActivityMainBinding;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    boolean set = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!set){
            set = true;
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
        }

        // binding
        ActivityMainBinding binding;
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // action bar setting
        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_total)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Context context = this;
        // Handle item selection.
        if(item.getItemId() == R.id.language_settings){
            String[] choices = {getString(R.string.default_text), "中文", "English(US)"};
            AtomicInteger chooseIndex = new AtomicInteger();

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder
                    .setTitle(getString(R.string.action_settings))
                    .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                        if(chooseIndex.get() == 0){
                            setLocal(this, "zh");
                            finish();
                            startActivity(getIntent());
                        }
                        else if(chooseIndex.get() == 1){
                            setLocal(this, "zh");
                            finish();
                            startActivity(getIntent());
                        }
                        else if(chooseIndex.get() == 2){
                            setLocal(this, "en");
                            finish();
                            startActivity(getIntent());
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {

                    })
                    .setSingleChoiceItems(choices, 0, (dialog, which) -> {
                        chooseIndex.set(which);
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
        else if(item.getItemId() == R.id.app_info){
            Intent intent = new Intent(this, appInfoActivity.class);
            startActivity(intent);
            return true;
        }
        else{
            return super.onOptionsItemSelected(item);
        }
    }

    public void setLocal(Activity activity, String langCode){
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        set = true;
    }
}