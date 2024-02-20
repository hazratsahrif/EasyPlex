package com.siflusso.di.module;

import com.siflusso.ui.animes.EpisodesFragment;
import com.siflusso.ui.downloadmanager.ui.main.DownloadsFragment;
import com.siflusso.ui.downloadmanager.ui.main.FinishedDownloadsFragment;
import com.siflusso.ui.downloadmanager.ui.main.QueuedDownloadsFragment;
import com.siflusso.ui.home.HomeFragment;
import com.siflusso.ui.library.AnimesFragment;
import com.siflusso.ui.library.LibraryFragment;
import com.siflusso.ui.library.LibraryStyleFragment;
import com.siflusso.ui.library.MoviesFragment;
import com.siflusso.ui.library.NetworksFragment;
import com.siflusso.ui.library.NetworksFragment2;
import com.siflusso.ui.library.SeriesFragment;
import com.siflusso.ui.mylist.AnimesListFragment;
import com.siflusso.ui.mylist.ListFragment;
import com.siflusso.ui.mylist.MoviesListFragment;
import com.siflusso.ui.mylist.SeriesListFragment;
import com.siflusso.ui.mylist.StreamingListFragment;
import com.siflusso.ui.search.DiscoverFragment;
import com.siflusso.ui.settings.SettingsActivity;
import com.siflusso.ui.streaming.StreamingFragment;
import com.siflusso.ui.upcoming.UpComingFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/*
 * @author Yobex.
 * */
@Module
public abstract class FragmentBuildersModule {


    @ContributesAndroidInjector
    abstract EpisodesFragment contributeEpisodesFragment();

    @ContributesAndroidInjector
    abstract FinishedDownloadsFragment contributeFinishedDownloadsFragment();


    @ContributesAndroidInjector
    abstract QueuedDownloadsFragment contributeQueuedDownloadsFragment();

    @ContributesAndroidInjector
    abstract DownloadsFragment contributeDownloadsFragment();

    @ContributesAndroidInjector
    abstract HomeFragment contributeHomeFragment();

    @ContributesAndroidInjector
    abstract UpComingFragment contributeUpcomingFragment();

    @ContributesAndroidInjector
    abstract DiscoverFragment contributeDiscoverFragment();

    @ContributesAndroidInjector
    abstract MoviesFragment contributeMoviesFragment();


    @ContributesAndroidInjector
    abstract LibraryStyleFragment contributeLibraryStyleFragment();

    @ContributesAndroidInjector
    abstract SeriesFragment contributeSeriesFragment();

    @ContributesAndroidInjector
    abstract LibraryFragment contributeLibraryFragment();

    @ContributesAndroidInjector
    abstract MoviesListFragment contributeMyListMoviesFragment();

    @ContributesAndroidInjector
    abstract AnimesFragment contributeAnimesFragment();

    @ContributesAndroidInjector
    abstract StreamingFragment contributeLiveFragment();

    @ContributesAndroidInjector
    abstract SettingsActivity contributeSettingsFragment();

    @ContributesAndroidInjector
    abstract ListFragment contributeListFragment();

    @ContributesAndroidInjector
    abstract SeriesListFragment contributeSeriesListFragment();

    @ContributesAndroidInjector
    abstract AnimesListFragment contributeAnimesListFragment();


    @ContributesAndroidInjector
    abstract NetworksFragment contributeNetworksFragment();

    @ContributesAndroidInjector
    abstract NetworksFragment2 contributeNetworksFragment2();

    @ContributesAndroidInjector
    abstract StreamingListFragment contributeStreamingListFragment();

}
