package com.example.ordermenu.Models;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.example.ordermenu.UI.DynamicSection;

import java.util.ArrayList;

public class TabAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;
    ArrayList<Section> mSections;

    public TabAdapter(FragmentManager fm, ArrayList<Section> sections) {
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.mNumOfTabs = sections.size();
        this.mSections=sections;
    }

    @Override
    public Fragment getItem(int position) {
        return DynamicSection.addfrag(mSections.get(position));
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
