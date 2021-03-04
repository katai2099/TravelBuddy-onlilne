package com.example.travelbuddyv2.ui.main;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.travelbuddyv2.GroupTripDetailFragment;
import com.example.travelbuddyv2.InventoryFragment;
import com.example.travelbuddyv2.R;

public class GroupSectionsPagerAdapter extends FragmentPagerAdapter {


    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private final Context mContext;
    private final Bundle bundle;

    public GroupSectionsPagerAdapter(Context mContext,@NonNull FragmentManager fm,Bundle bundle ) {
        super(fm);
        this.mContext = mContext;
        this.bundle = bundle;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        switch (position){
            case 0:
                fragment = new GroupTripDetailFragment();
                fragment.setArguments(bundle);
                break;
            case 1:
                fragment = new InventoryFragment();
                fragment.setArguments(bundle);
                break;
        }

        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return 2;
    }
}
