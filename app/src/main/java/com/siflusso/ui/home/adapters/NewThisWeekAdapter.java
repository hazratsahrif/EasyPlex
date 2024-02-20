package com.siflusso.ui.home.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.siflusso.R;
import com.siflusso.data.local.entity.Media;
import com.siflusso.data.model.genres.Genre;
import com.siflusso.databinding.ItemMovieBinding;
import com.siflusso.ui.animes.AnimeDetailsActivity;
import com.siflusso.ui.moviedetails.MovieDetailsActivity;
import com.siflusso.ui.seriedetails.SerieDetailsActivity;
import com.siflusso.util.Tools;
import java.util.List;
import java.util.Locale;

import static com.siflusso.util.Constants.ARG_MOVIE;


/**
 * Adapter for This Week Movie.
 *
 * @author Yobex.
 */
public class NewThisWeekAdapter extends RecyclerView.Adapter<NewThisWeekAdapter.MainViewHolder> {

    private List<Media> castList;
    private Context context;

    public void addThisWeek(List<Media> castList,Context context) {
        this.castList = castList;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NewThisWeekAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemMovieBinding binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new NewThisWeekAdapter.MainViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NewThisWeekAdapter.MainViewHolder holder, int position) {
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


            if (Tools.isRTL(Locale.getDefault())){
                binding.mgenres.setBackgroundResource(R.drawable.bg_label_rtl);
            }


            if (media.getSubtitle() !=null) {

                binding.substitle.setText(media.getSubtitle());
            }else {

                binding.substitle.setVisibility(View.GONE);
            }

            String mediaType = media.getType();
            switch (mediaType) {
                case "movie":

                    binding.rootLayout.setOnLongClickListener(v -> {
                        Toast.makeText(context, "" + media.getName(), Toast.LENGTH_SHORT).show();
                        return false;
                    });

                    binding.rootLayout.setOnClickListener(view -> {


                        Intent intent = new Intent(context, MovieDetailsActivity.class);
                        intent.putExtra(ARG_MOVIE, media);
                        context.startActivity(intent);


                    });

                    binding.movietitle.setText(media.getName());

                    break;
                case "serie":

                    binding.movietitle.setText(media.getName());


                    binding.rootLayout.setOnLongClickListener(v -> {
                        Toast.makeText(context, "" + media.getName(), Toast.LENGTH_SHORT).show();
                        return false;
                    });

                    binding.rootLayout.setOnClickListener(view -> {


                        Intent intent = new Intent(context, SerieDetailsActivity.class);
                        intent.putExtra(ARG_MOVIE, media);
                        context.startActivity(intent);


                    });


                    if (media.getNewEpisodes() == 1) {

                        binding.hasNewEpisodes.setVisibility(View.VISIBLE);
                    }

                    break;
                case "anime":

                    binding.movietitle.setText(media.getName());

                    binding.rootLayout.setOnLongClickListener(v -> {
                        Toast.makeText(context, "" + media.getName(), Toast.LENGTH_SHORT).show();
                        return false;
                    });

                    binding.rootLayout.setOnClickListener(view -> {

                        Intent intent = new Intent(context, AnimeDetailsActivity.class);
                        intent.putExtra(ARG_MOVIE, media);
                        context.startActivity(intent);


                    });


                    if (media.getNewEpisodes() == 1) {

                        binding.hasNewEpisodes.setVisibility(View.VISIBLE);
                    }

                    break;
            }




            if (media.getPremuim() == 1) {

                binding.moviePremuim.setVisibility(View.VISIBLE);


            } else {

                binding.moviePremuim.setVisibility(View.GONE);
            }


            binding.ratingBar.setRating(media.getVoteAverage() / 2);
            binding.viewMovieRating.setText(String.valueOf(media.getVoteAverage()));

            Tools.onLoadMediaCoverAdapters(context, binding.itemMovieImage, media.getPosterPath());


            binding.mgenres.setText(media.getGenreName());

        }
    }





}
