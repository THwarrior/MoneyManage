package com.example.afinal.ui.total;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TotalViewPagerAdapter extends FragmentStateAdapter {

    public TotalViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 0) return new CatTotalFragment();
        else return new InOutTotalFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
