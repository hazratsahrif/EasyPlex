package com.siflusso.ui.seriedetails.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class CustomSereisAdapter extends PagerAdapter {

    private final List<RecyclerView> recyclerViews;
    public CustomSereisAdapter(List<RecyclerView> recyclerViews) {
        this.recyclerViews = recyclerViews;
    }

    @Override
    public int getCount() {
        return recyclerViews.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }


    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        RecyclerView recyclerView = recyclerViews.get(position);
        container.addView(recyclerView);
        return recyclerView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Episodes";
            case 1:
                return "Overview";
            case 2:
                return "Casts";
            case 3:
                return "Related";
            default:
                return null;
        }
    }
}
