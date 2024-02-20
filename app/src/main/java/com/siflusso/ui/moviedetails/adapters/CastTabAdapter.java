package com.siflusso.ui.moviedetails.adapters;


import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;
import static com.siflusso.util.Constants.ARG_CAST;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.siflusso.R;
import com.siflusso.data.model.credits.Cast;
import com.siflusso.databinding.ListItemCastBinding;
import com.siflusso.databinding.ListItemTabCastBinding;
import com.siflusso.ui.casts.CastDetailsActivity;
import com.siflusso.ui.manager.SettingsManager;
import com.siflusso.util.GlideApp;
import com.siflusso.util.Tools;

import java.util.List;

import javax.inject.Inject;


/**
 * Adapter for Movie Casts.
 *
 * @author Yobex.
 */
public class CastTabAdapter extends RecyclerView.Adapter<CastTabAdapter.CastViewHolder> {

    ListItemTabCastBinding binding;
    Context context;
    List<Cast> castList;

    public CastTabAdapter(Context context, List<Cast> castList) {
        this.context = context;
        this.castList = castList;
    }

    @NonNull
    @Override
    public CastTabAdapter.CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ListItemTabCastBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new CastViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CastTabAdapter.CastViewHolder holder, int position) {
        Cast item = castList.get(position);
        holder.binding.castName.setText(item.getName());
//        holder.binding.placeOfBirth.setText(item.getPlaceOfBirth());
//        if(item.getPlaceOfBirth()!=null){
//        binding.placeOfBirth.setText(item.getPlaceOfBirth());
//        }

                GlideApp.with(context).asBitmap().load(item.getProfilePath())
                .fitCenter()
                .placeholder(R.color.fragment_content_detail_overlay_end)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(withCrossFade())
                .into(holder.binding.imageCast);

    }

    @Override
    public int getItemCount() {
        return castList.size();
    }

    public class CastViewHolder extends RecyclerView.ViewHolder {
        ListItemTabCastBinding binding;
        public CastViewHolder(@NonNull ListItemTabCastBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}

//
//    private List<Cast> castList;
//    private final Context context;
//    private final boolean internal;
//
//    @Inject
//    SettingsManager settingsManager;
//
//
//    public CastTabAdapter(Context context, boolean internal,List<Cast> castList) {
//        this.settingsManager = settingsManager;
//        this.context = context;
//        this.internal = internal;
//        this.castList = castList;
//    }
//
//    public void addCasts(List<Cast> castList) {
//        this.castList = castList;
//        notifyDataSetChanged();
//    }
//
//    @NonNull
//    @Override
//    public CastTabAdapter.CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//
//        ListItemCastBinding binding = ListItemCastBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
//
//        return new CastTabAdapter.CastViewHolder(binding);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull CastTabAdapter.CastViewHolder holder, int position) {
//        holder.onBind(position);
//    }
//
//    @Override
//    public int getItemCount() {
//        if (castList != null) {
//            return castList.size();
//        } else {
//            return 0;
//        }
//    }
//
//class CastViewHolder extends RecyclerView.ViewHolder {
//
//    private final ListItemCastBinding binding;
//
//
//    CastViewHolder(@NonNull ListItemCastBinding binding)
//    {
//        super(binding.getRoot());
//
//        this.binding = binding;
//    }
//
//    void onBind(final int position) {
//
//        final Cast cast = castList.get(position);
//
//
//        if (internal) {
//
//            Tools.loadUserAvatar(context,binding.imageCast,cast.getProfilePath());
//
//            binding.castName.setText(cast.getName());
//
//            binding.rootLayout.setOnClickListener(v -> {
//                Intent intent = new Intent(context, CastDetailsActivity.class);
//                intent.putExtra(ARG_CAST, cast);
//                context.startActivity(intent);
//
//            });
//
//            binding.rootLayout.setOnLongClickListener(v -> {
//                Toast.makeText(context, ""+cast.getName(), Toast.LENGTH_SHORT).show();
//                return false;
//            });
//
//        }else {
//
////                Tools.onLoadMediaCover(context,binding.imageCast,settingsManager.getSettings().getImdbCoverPath() + cast.getProfilePath());
//
//            binding.castName.setText(cast.getName());
//        }
//
//    }
//}