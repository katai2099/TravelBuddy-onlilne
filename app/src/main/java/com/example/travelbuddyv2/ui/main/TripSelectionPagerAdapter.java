package com.example.travelbuddyv2.ui.main;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.travelbuddyv2.GroupTripSelectionFragment;
import com.example.travelbuddyv2.PersonalTripSelectionFragment;
import com.example.travelbuddyv2.R;

public class TripSelectionPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_5, R.string.tab_text_3};
    private final Context mContext;
    private final Bundle bundle;

    public TripSelectionPagerAdapter(Context context,@NonNull FragmentManager fm,Bundle bundle) {
        super(fm);
        mContext = context;
        this.bundle = bundle;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = new PersonalTripSelectionFragment();
                fragment.setArguments(bundle);
                break;
            case 1:
                fragment = new GroupTripSelectionFragment();
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
