package com.siflusso.ui.moviedetails.adapters;

import static com.siflusso.util.Constants.ARG_MOVIE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.siflusso.data.local.entity.Media;
import com.siflusso.databinding.ItemRelatedLayoutBinding;
import com.siflusso.databinding.ItemRelatedsBinding;
import com.siflusso.ui.moviedetails.MovieDetailsActivity;
import com.siflusso.util.Tools;

import java.util.List;

/**
 * Adapter for Movie Casts.
 *
 * @author Yobex.
 */
public class RelatedsTabAdapter extends RecyclerView.Adapter<RelatedsTabAdapter.MainViewHolder> {

    private List<Media> castList;

    @SuppressLint("NotifyDataSetChanged")
    public void addToContent(List<Media> castList) {
        this.castList = castList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RelatedsTabAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemRelatedLayoutBinding binding = ItemRelatedLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new RelatedsTabAdapter.MainViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RelatedsTabAdapter.MainViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        if (castList != null) {
            return castList.size();
        } else {
            return 0;
        }
    }

    class MainViewHolder extends RecyclerView.ViewHolder {



        private final ItemRelatedLayoutBinding binding;


        MainViewHolder(@NonNull ItemRelatedLayoutBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }


        void onBind(final int position) {


            final Media related = castList.get(position);
            Context context = binding.imageMovie.getContext();

            binding.movieName.setText(related.getTitle());

                binding.tvType.setText(related.getType());

            binding.tvDate.setText(related.getReleaseDate());

            binding.rootLayout.setOnClickListener(v -> {
                ((Activity)context).finish();
                Intent intent = new Intent(context, MovieDetailsActivity.class);
                intent.putExtra(ARG_MOVIE, related);
                context.startActivity(intent);

            });

            Tools.onLoadMediaCover(context,binding.imageMovie,related.getPosterPath());


        }
    }
}
