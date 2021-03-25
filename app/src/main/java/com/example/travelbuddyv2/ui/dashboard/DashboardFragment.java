package com.example.travelbuddyv2.ui.dashboard;

import android.content.Context;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.travelbuddyv2.InventoryFragment;
import com.example.travelbuddyv2.R;
import com.example.travelbuddyv2.TripDetailFragment;
import com.google.android.material.tabs.TabLayout;

public class DashboardFragment extends Fragment {

   // private DashboardViewModel dashboardViewModel;

    private final String tag = "DashboardFragment";
    private PagerAdapter pagerAdapter;
    ViewPager viewPager;
    boolean changeToGroup =false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // dashboardViewModel =
        //       new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        //  final TextView textView = root.findViewById(R.id.text_dashboard);
        //  dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
        //    @Override
        //  public void onChanged(@Nullable String s) {
        //    textView.setText(s);
        //}
        //});

        Bundle bundle = getArguments();
        if(bundle!=null){
//            Log.d(tag,bundle.getString("changeToGroup"));
            changeToGroup = bundle.getBoolean("changeToGroup");
            Toast.makeText(getContext(),String.valueOf(changeToGroup),Toast.LENGTH_SHORT).show();
        }

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        pagerAdapter = new PagerAdapter(getContext(),getChildFragmentManager());
        viewPager = view.findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        if(changeToGroup){
            viewPager.setCurrentItem(1);
        }

    }
}





