package com.siflusso.ui.moviedetails.adapters;


import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.siflusso.R;
import com.siflusso.data.model.credits.Cast;
import com.siflusso.databinding.ListItemTabCastBinding;
import com.siflusso.databinding.OverviewTextLayoutBinding;
import com.siflusso.ui.moviedetails.model.OverviewModel;
import com.siflusso.util.GlideApp;

import java.util.List;


/**
 * Adapter for Movie Casts.
 *
 * @author Yobex.
 */
public class OverviewAdapter extends RecyclerView.Adapter<OverviewAdapter.CastViewHolder> {

    OverviewTextLayoutBinding binding;
    Context context;
    OverviewModel overviewModel;

    public OverviewAdapter(Context context, OverviewModel  overviewModel) {
        this.context = context;
        this.overviewModel = overviewModel;
    }

    @NonNull
    @Override
    public OverviewAdapter.CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = OverviewTextLayoutBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new CastViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OverviewAdapter.CastViewHolder holder, int position) {

            holder.binding.tvGenre.setText(overviewModel.getGenre());
        holder.binding.textOverviewLabel.setText(overviewModel.getOverView());


    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class CastViewHolder extends RecyclerView.ViewHolder {
        OverviewTextLayoutBinding binding;
        public CastViewHolder(@NonNull OverviewTextLayoutBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
