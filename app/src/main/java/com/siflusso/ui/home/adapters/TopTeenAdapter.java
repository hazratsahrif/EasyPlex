package com.siflusso.ui.home.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.siflusso.data.local.entity.Media;
import com.siflusso.databinding.ItemMovieBinding;
import com.siflusso.databinding.ItemTopttenBinding;
import com.siflusso.ui.animes.AnimeDetailsActivity;
import com.siflusso.ui.moviedetails.MovieDetailsActivity;
import com.siflusso.ui.seriedetails.SerieDetailsActivity;
import com.siflusso.util.Tools;

import java.util.List;

import static com.siflusso.util.Constants.ARG_MOVIE;

/**
 * Adapter for Movie.
 *
 * @author Yobex.
 */
public class TopTeenAdapter extends RecyclerView.Adapter<TopTeenAdapter.MainViewHolder> {

    private List<Media> castList;
    private Context context;

    public void addMain(List<Media> mediaList,Context context) {
        this.castList = mediaList;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TopTeenAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemTopttenBinding binding = ItemTopttenBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new TopTeenAdapter.MainViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TopTeenAdapter.MainViewHolder holder, int position) {
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

        private final ItemTopttenBinding binding;

        MainViewHolder(@NonNull ItemTopttenBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        void onBind(final int position) {

            final Media media = castList.get(position);

            if (media.getSubtitle() !=null) {

                binding.substitle.setText(media.getSubtitle());
            }else {

                binding.substitle.setVisibility(View.GONE);
            }






            String mediaType = media.getType();
            switch (mediaType) {
                case "movie":

                    binding.movietitle.setText(media.getName());

                    binding.mgenres.setText(""+(position + 1));


                    binding.rootLayout.setOnLongClickListener(v -> {
                        Toast.makeText(context, ""+media.getTitle(), Toast.LENGTH_SHORT).show();
                        return false;
                    });

                    binding.rootLayout.setOnClickListener(view -> {


                        Intent intent = new Intent(context, MovieDetailsActivity.class);
                        intent.putExtra(ARG_MOVIE, media);
                        context.startActivity(intent);


                    });

                    break;
                case "serie":

                    binding.movietitle.setText(media.getName());
                    binding.mgenres.setText(""+(position + 1));


                    binding.rootLayout.setOnLongClickListener(v -> {
                        Toast.makeText(context, ""+media.getName(), Toast.LENGTH_SHORT).show();
                        return false;
                    });

                    binding.rootLayout.setOnClickListener(view -> {


                        Intent intent = new Intent(context, SerieDetailsActivity.class);
                        intent.putExtra(ARG_MOVIE, media);
                        context.startActivity(intent);


                    });

                    break;
                case "anime":

                    binding.movietitle.setText(media.getName());
                    binding.mgenres.setText(""+(position + 1));

                    binding.rootLayout.setOnLongClickListener(v -> {
                        Toast.makeText(context, ""+media.getName(), Toast.LENGTH_SHORT).show();
                        return false;
                    });

                    binding.rootLayout.setOnClickListener(view -> {


                        Intent intent = new Intent(context, AnimeDetailsActivity.class);
                        intent.putExtra(ARG_MOVIE, media);
                        context.startActivity(intent);

                    });
                    break;
            }



            if (media.getPremuim() == 1) {

            binding.moviePremuim.setVisibility(View.VISIBLE);

            }else {

                binding.moviePremuim.setVisibility(View.GONE);
            }


            binding.ratingBar.setRating(media.getVoteAverage() / 2);
            binding.viewMovieRating.setText(String.valueOf(media.getVoteAverage()));
            Tools.onLoadMediaCoverAdapters(context,binding.itemMovieImage, media.getPosterPath());

        }

    }

}
