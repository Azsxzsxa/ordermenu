package com.example.ordermenu.Models;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.ordermenu.UI.DynamicMenu;
import com.example.ordermenu.UI.DynamicSection;

import java.util.ArrayList;

public class TabMenuAdapter extends FragmentStatePagerAdapter {

    ArrayList<String> mManuCathegories;

    public TabMenuAdapter(FragmentManager fm, ArrayList<String> menuCathegories) {
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.mManuCathegories=menuCathegories;
    }

    @Override
    public Fragment getItem(int position) {
        return DynamicMenu.addfrag(mManuCathegories.get(position));
    }

    @Override
    public int getCount() {
        return mManuCathegories.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mManuCathegories.get(position);
    }
}
