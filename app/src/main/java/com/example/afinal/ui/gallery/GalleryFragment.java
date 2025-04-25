package com.example.afinal.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.afinal.databinding.FragmentGalleryBinding;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ArrayList<String> sayingList = new ArrayList<>();

        sayingList.add("今天的你也有記帳嗎 ?\n真是太好了 !");
        sayingList.add("今天如果過得開心\n明天要過得更開心");
        sayingList.add("遇到煩心的事 ?\n休息一下 世界更美好");
        sayingList.add("人生是一場旅行\n別忘了欣賞沿途風景");
        sayingList.add("平凡的日子\n也可以充滿驚喜。");
        sayingList.add("即使今天陰天\n心中也能有陽光");
        sayingList.add("生活像Wi-Fi 時好時壞\n但總能連上");
        sayingList.add("人生最大的哲理：先填飽肚子");
        sayingList.add("每天都是限量版 不如快樂過");
        sayingList.add("人生苦短 笑點要長");

        TextView text = binding.textOutput;

        Button btn = binding.sayingButton;
        btn.setOnClickListener(v -> {
            int r = (int) (Math.random() * sayingList.size());
            text.setText(sayingList.get(r));
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}