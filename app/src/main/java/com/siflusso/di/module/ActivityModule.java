package com.siflusso.di.module;

import com.siflusso.ui.animes.AnimeDetailsActivity;
import com.siflusso.ui.base.BaseActivity;
import com.siflusso.ui.casts.CastDetailsActivity;
import com.siflusso.ui.devices.UserDevicesManagement;
import com.siflusso.ui.downloadmanager.ui.main.DownloadManagerFragment;
import com.siflusso.ui.login.LoginActivity;
import com.siflusso.ui.login.PasswordForget;
import com.siflusso.ui.moviedetails.MovieDetailsActivity;
import com.siflusso.ui.notifications.NotificationManager;
import com.siflusso.ui.payment.Payment;
import com.siflusso.ui.payment.PaymentDetails;
import com.siflusso.ui.payment.PaymentPaypal;
import com.siflusso.ui.payment.PaymentStripe;
import com.siflusso.ui.player.activities.EasyPlexMainPlayer;
import com.siflusso.ui.player.activities.EmbedActivity;
import com.siflusso.ui.profile.EditProfileActivity;
import com.siflusso.ui.register.RegisterActivity;
import com.siflusso.ui.register.RegistrationSucess;
import com.siflusso.ui.seriedetails.EpisodeDetailsActivity;
import com.siflusso.ui.seriedetails.SerieDetailsActivity;
import com.siflusso.ui.splash.SplashActivity;
import com.siflusso.ui.streaming.StreamingetailsActivity;
import com.siflusso.ui.trailer.TrailerPreviewActivity;
import com.siflusso.ui.upcoming.UpcomingTitlesActivity;
import com.siflusso.ui.users.PhoneAuthActivity;
import com.siflusso.ui.users.UserProfiles;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Binds all sub-components within the app. Add bindings for other sub-components here.
 * @ContributesAndroidInjector was introduced removing the need to:
 * a) Create separate components annotated with @Subcomponent (the need to define @Subcomponent classes.)
 * b) Write custom annotations like @PerActivity.
 *
 * @author Yobex.
 */
@Module
public abstract class ActivityModule {

    @ContributesAndroidInjector()
    abstract UserDevicesManagement contributeUserDevicesManagement();


    @ContributesAndroidInjector()
    abstract PhoneAuthActivity contributePhoneAuthActivity();

    @ContributesAndroidInjector()
    abstract UserProfiles contributeUserProfiles();

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract BaseActivity contributeMainActivity();

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract DownloadManagerFragment contributeMainActivityDown();

    @ContributesAndroidInjector()
    abstract Payment contributePayment();

    @ContributesAndroidInjector()
    abstract PaymentPaypal contributePaymentPaypal();

    @ContributesAndroidInjector()
    abstract PaymentStripe contributePaymentStripe();

    @ContributesAndroidInjector()
    abstract NotificationManager contributeNotificationManager();

    @ContributesAndroidInjector()
    abstract PaymentDetails contributePaymentDetails();

    @ContributesAndroidInjector()
    abstract RegistrationSucess contributeRegistrationSucess();

    @ContributesAndroidInjector()
    abstract EditProfileActivity contributeEditProfileActivity();

    @ContributesAndroidInjector()
    abstract MovieDetailsActivity contributeMovieDetailActivity();

    @ContributesAndroidInjector()
    abstract SerieDetailsActivity contributeSerieDetailActivity();

    @ContributesAndroidInjector()
    abstract LoginActivity contributeLoginActivity();

    @ContributesAndroidInjector()
    abstract RegisterActivity contributeRegisterActivity();

    @ContributesAndroidInjector()
    abstract TrailerPreviewActivity contributeTrailerPreviewActivity();

    @ContributesAndroidInjector()
    abstract UpcomingTitlesActivity contributeUpcomingTitlesActivity();

    @ContributesAndroidInjector()
    abstract AnimeDetailsActivity contributeAnimeDetailsActivity();

    @ContributesAndroidInjector()
    abstract SplashActivity contributeSplashActivity();

    @ContributesAndroidInjector()
    abstract EmbedActivity contributeEmbedActivity();

    @ContributesAndroidInjector()
    abstract EasyPlexMainPlayer contributeEasyPlexMainPlayer();

    @ContributesAndroidInjector()
    abstract PasswordForget contributePasswordForget();

    @ContributesAndroidInjector()
    abstract CastDetailsActivity contributeCastDetailsActivity();

    @ContributesAndroidInjector()
    abstract StreamingetailsActivity contributeStreamingetailsActivity();

    @ContributesAndroidInjector()
    abstract EpisodeDetailsActivity contributeEpisodeDetailsActivity();
}
