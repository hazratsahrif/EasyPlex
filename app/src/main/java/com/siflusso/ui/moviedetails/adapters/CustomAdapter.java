package com.siflusso.ui.moviedetails.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.easyplex.easyplexsupportedhosts.EasyPlexSupportedHosts;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.siflusso.data.local.entity.Download;
import com.siflusso.data.local.entity.History;
import com.siflusso.data.local.entity.Media;
import com.siflusso.data.model.episode.Episode;
import com.siflusso.data.model.serie.Season;
import com.siflusso.data.repository.MediaRepository;
import com.siflusso.ui.comments.CommentsAdapter;
import com.siflusso.ui.manager.AuthManager;
import com.siflusso.ui.manager.SettingsManager;
import com.siflusso.ui.manager.TokenManager;

import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class CustomAdapter extends PagerAdapter {

    private final List<RecyclerView> recyclerViews;
    public CustomAdapter(List<RecyclerView> recyclerViews) {
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
