package com.example.travelbuddyv2.ui.dashboard;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.travelbuddyv2.GroupTripFragment;
import com.example.travelbuddyv2.InventoryFragment;
import com.example.travelbuddyv2.PersonalTripFragment;
import com.example.travelbuddyv2.R;
import com.example.travelbuddyv2.TripDetailFragment;
import com.example.travelbuddyv2.ui.home.HomeFragment;

public class PagerAdapter extends FragmentPagerAdapter {
    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_4, R.string.tab_text_3};
    private final Context mContext;

    public PagerAdapter(Context mContext, FragmentManager fm) {
        super(fm);
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0 :
                fragment = new PersonalTripFragment();
                break;
            case 1:
                fragment = new GroupTripFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }
}
