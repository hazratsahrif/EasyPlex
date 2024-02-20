package com.siflusso.ui.home.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.siflusso.R;
import com.siflusso.data.local.entity.Media;
import com.siflusso.data.model.genres.Genre;
import com.siflusso.databinding.ItemMovieBinding;
import com.siflusso.ui.moviedetails.MovieDetailsActivity;
import com.siflusso.util.Constants;
import com.siflusso.util.Tools;
import java.util.List;
import java.util.Locale;


/**
 * Adapter for Popular Movies.
 *
 * @author Yobex.
 */
public class PopularMoviesAdapter extends RecyclerView.Adapter<PopularMoviesAdapter.MainViewHolder> {

    private List<Media> castList;
    private Context context;


    public void addPopular(List<Media> castList,Context context) {
        this.castList = castList;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PopularMoviesAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemMovieBinding binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new PopularMoviesAdapter.MainViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularMoviesAdapter.MainViewHolder holder, int position) {
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

        private final ItemMovieBinding binding;

        MainViewHolder(@NonNull ItemMovieBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }



        void onBind(final int position) {

            if (Tools.isRTL(Locale.getDefault())){
                binding.mgenres.setBackgroundResource(R.drawable.bg_label_rtl);
            }

            final Media media = castList.get(position);

            if (media.getSubtitle() !=null) {

                binding.substitle.setText(media.getSubtitle());
            }else {

                binding.substitle.setVisibility(View.GONE);
            }

            if (media.getPremuim() == 1) {

                binding.moviePremuim.setVisibility(View.VISIBLE);


            }else {

                binding.moviePremuim.setVisibility(View.GONE);
            }

            binding.movietitle.setText(media.getTitle());





         if (media.getPremuim() == 1) {

           binding.moviePremuim.setVisibility(View.VISIBLE);

            }else {

          binding.moviePremuim.setVisibility(View.GONE);
        }


            binding.rootLayout.setOnClickListener(view -> {


                Intent intent = new Intent(context, MovieDetailsActivity.class);
                intent.putExtra(Constants.ARG_MOVIE, media);
                context.startActivity(intent);


            });

            binding.ratingBar.setRating(media.getVoteAverage() / 2);
            binding.viewMovieRating.setText(String.valueOf(media.getVoteAverage()));

            Tools.onLoadMediaCoverAdapters(context,binding.itemMovieImage, media.getPosterPath());

            binding.mgenres.setText(media.getGenreName());


        }

    }
}

