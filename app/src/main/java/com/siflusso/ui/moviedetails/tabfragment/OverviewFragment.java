package com.siflusso.ui.moviedetails.tabfragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.siflusso.data.local.entity.Media;
import com.siflusso.data.repository.AuthRepository;
import com.siflusso.data.repository.MediaRepository;
import com.siflusso.databinding.FragmentOverviewBinding;
import com.siflusso.ui.manager.SettingsManager;
import com.siflusso.ui.moviedetails.adapters.OverviewAdapter;
import com.siflusso.ui.viewmodels.MovieDetailViewModel;

import javax.inject.Inject;

public class OverviewFragment extends Fragment {
    FragmentOverviewBinding binding;
    private SettingsManager settingsManager;
    private RecyclerView recyclerView;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    MediaRepository mediaRepository;
    @Inject
    AuthRepository authRepository;
    private MovieDetailViewModel movieDetailViewModel;
    private OverviewAdapter mCastAdapter;
    private Media media;
    public static OverviewFragment newInstance(Media media) {
        OverviewFragment fragment = new OverviewFragment();
        Bundle args = new Bundle();
        args.putParcelable("myObject",  media); // assuming MyObject implements Parcelable
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentOverviewBinding.inflate(inflater,container,false);


//        MyViewModelFactory viewModelFactory = new MyViewModelFactory(mediaRepository, settingsManager, authRepository);
//         movieDetailViewModel = new ViewModelProvider(this, viewModelFactory).get(MovieDetailViewModel.class);


        if (getArguments() != null) {
            media = getArguments().getParcelable("myObject");
            binding.textOverviewLabel.setText(media.getOverview());
            binding.tvGenre.setText(media.getGenre());
//            Toast.makeText(getContext(), "Media"+media.getCast().get(0).getName(), Toast.LENGTH_SHORT).show();
//            onLoadCast(media);
//            movieDetailViewModel.movieDetailMutableLiveData.observe(getViewLifecycleOwner(), movieDetail -> {
//
//                Toast.makeText(getContext(), "On load"+movieDetail.getTitle(), Toast.LENGTH_SHORT).show();
//
//            });
        }

        return  binding.getRoot();

    }

//    private void onLoadCast(Media movieDetail) {
//
//            if (movieDetail.getTmdbId() !=null) {
//
//                mCastAdapter = new CastTabAdapter(getContext(), movieDetail.getCast());
//                binding.RvCast.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
////                mCastAdapter.addCasts(movieDetail.getCast());
//                binding.RvCast.setAdapter(mCastAdapter);
//
////                movieDetailViewModel.getMovieCast(Integer.parseInt(movieDetail.getTmdbId()));
////                binding.RvCast.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
////                binding.RvCast.setAdapter(mCastAdapter);
////                movieDetailViewModel.movieCreditsMutableLiveData.observe(getViewLifecycleOwner(), credits -> {
////                    mCastAdapter = new CastTabAdapter(getContext(), false,);
////                    mCastAdapter.addCasts(credits.getCasts());
////                    // Starring RecycleView
////                    binding.RvCast.setAdapter(movieDetail);
////                });
//            }
//        else {
//
//                mCastAdapter = new CastTabAdapter(getContext(),movieDetail.getCast());
//                binding.RvCast.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
////            mCastAdapter.addCasts(media.getCast());
//            binding.RvCast.setAdapter(mCastAdapter);
//
//        }
//    }
}