package com.example.travelbuddyv2.ui.main;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.travelbuddyv2.InventoryFragment;
import com.example.travelbuddyv2.R;
import com.example.travelbuddyv2.TripDetailFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private final Context mContext;
    private final Bundle bundle;

    public SectionsPagerAdapter(Context context, FragmentManager fm,Bundle bundle) {
        super(fm);
        mContext = context;
        this.bundle = bundle;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        //return PlaceholderFragment.newInstance(position + 1);

        Fragment fragment = null;
        switch (position){
            case 0 :
                fragment = new TripDetailFragment();
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
        // Show 2 total pages.
        return 2;
    }
}