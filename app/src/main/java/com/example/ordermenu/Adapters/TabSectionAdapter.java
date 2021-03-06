package com.example.ordermenu.Adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.ordermenu.Models.Section;
import com.example.ordermenu.UI.TablesSectionFragment;

import java.util.ArrayList;

public class TabSectionAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;
    ArrayList<Section> mSections;

    public TabSectionAdapter(FragmentManager fm, ArrayList<Section> sections) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.mNumOfTabs = sections.size();
        this.mSections = sections;
    }

    @Override
    public Fragment getItem(int position) {
        return TablesSectionFragment.addFragment(mSections.get(position));
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mSections.get(position).getName();
    }
}
