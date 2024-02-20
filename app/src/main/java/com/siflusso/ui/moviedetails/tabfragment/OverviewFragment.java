package com.siflusso.ui.moviedetails.tabfragment;

import static android.view.View.GONE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG;
import static com.siflusso.util.Constants.DEFAULT_WEBVIEW_ADS_RUNNING;
import static com.siflusso.util.Constants.SERVER_BASE_URL;
import static com.siflusso.util.Tools.ToastHelper;
import static com.siflusso.util.Tools.getViewFormat;
import static com.siflusso.util.Tools.onLoadNetworksInter;
import static com.siflusso.util.Tools.onShareMedia;
import static com.siflusso.util.Tools.onloadBanners;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.datatransport.runtime.dagger.Provides;
import com.google.android.material.snackbar.Snackbar;
import com.siflusso.R;
import com.siflusso.data.local.entity.Download;
import com.siflusso.data.local.entity.Media;
import com.siflusso.data.model.MovieResponse;
import com.siflusso.data.model.credits.Cast;
import com.siflusso.data.model.genres.Genre;
import com.siflusso.data.model.media.StatusFav;
import com.siflusso.data.repository.AuthRepository;
import com.siflusso.data.repository.MediaRepository;
import com.siflusso.databinding.FragmentOverviewBinding;
import com.siflusso.di.component.DaggerAppComponent;
import com.siflusso.ui.login.LoginActivity;
import com.siflusso.ui.manager.SettingsManager;
import com.siflusso.ui.moviedetails.MovieDetailsActivity;
import com.siflusso.ui.moviedetails.adapters.CastAdapter;
import com.siflusso.ui.moviedetails.adapters.CastTabAdapter;
import com.siflusso.ui.viewmodels.MovieDetailViewModel;
import com.siflusso.util.DialogHelper;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

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
    private CastTabAdapter mCastAdapter;
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