package com.example.afinal.ui.total;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.afinal.databinding.FragmentTotalBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TotalFragment extends Fragment {

    private FragmentTotalBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTotalBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // viewpager
        TabLayout tab = binding.tab;
        // viewpager
        ViewPager2 viewPager = binding.viewPager;
        TotalViewPagerAdapter totalAdapter = new TotalViewPagerAdapter(this.getActivity());
        viewPager.setAdapter(totalAdapter);
        viewPager.setCurrentItem(0);

        new TabLayoutMediator(tab, viewPager, (tab1, position) -> {
            if(position == 0) tab1.setText("分類統計");
            if(position == 1) tab1.setText("收支統計");
        }).attach();

        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
