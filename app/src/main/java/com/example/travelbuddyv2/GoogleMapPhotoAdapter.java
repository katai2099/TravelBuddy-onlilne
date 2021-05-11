package com.example.travelbuddyv2;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

// this is an adapter that holds place image fetches from place api
public class GoogleMapPhotoAdapter extends PagerAdapter {

    List<Bitmap> bitmapList ;
    Context context;
    public GoogleMapPhotoAdapter(Context context,List<Bitmap> bitmapList) {
        this.bitmapList = bitmapList;
        this.context = context;
    }
    @Override
    public int getCount() {
        return bitmapList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View itemView = LayoutInflater.from(container.getContext())
                .inflate(R.layout.google_map_photo,container,false);

        ImageView imageView ;
        imageView =  itemView.findViewById(R.id.googleImage);
        imageView.setImageBitmap(bitmapList.get(position));
        container.addView(itemView,0);
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
