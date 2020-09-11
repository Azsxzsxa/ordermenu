package com.example.ordermenu.Adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.ordermenu.UI.DynamicMenu;

import java.util.ArrayList;

public class TabMenuAdapter extends FragmentStatePagerAdapter {

    ArrayList<String> mMenuCategories;

    public TabMenuAdapter(FragmentManager fm, ArrayList<String> menuCathegories) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.mMenuCategories = menuCathegories;
    }

    @Override
    public Fragment getItem(int position) {
        return DynamicMenu.addFragment(mMenuCategories.get(position));
    }

    @Override
    public int getCount() {
        return mMenuCategories.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mMenuCategories.get(position);
    }
}
