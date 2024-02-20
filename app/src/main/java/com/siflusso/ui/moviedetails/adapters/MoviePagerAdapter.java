package com.siflusso.ui.moviedetails.adapters;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.siflusso.data.local.entity.Media;
import com.siflusso.ui.moviedetails.tabfragment.CastFragment;
import com.siflusso.ui.moviedetails.tabfragment.OverviewFragment;
import com.siflusso.ui.moviedetails.tabfragment.RelatedFragment;

public class MoviePagerAdapter extends FragmentPagerAdapter {
    private Media movieModel;
    public MoviePagerAdapter(FragmentManager fm,Media movieModel) {
        super(fm);
        this.movieModel = movieModel;
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return  OverviewFragment.newInstance(movieModel);
            case 1:
                return  CastFragment.newInstance(movieModel);
            case 2:
                return new RelatedFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3; // Number of tabs
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Overview";
            case 1:
                return "Casts";
            case 2:
                return "Related";
            default:
                return null;
        }
    }
}

