package com.siflusso.ui.notifications;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.siflusso.R;
import com.siflusso.data.local.entity.Media;
import com.siflusso.data.model.episode.LatestEpisodes;
import com.siflusso.data.repository.AnimeRepository;
import com.siflusso.ui.manager.SettingsManager;
import com.siflusso.ui.splash.SplashActivity;
import com.siflusso.util.Constants;
import com.siflusso.util.GlideApp;
import com.siflusso.util.Tools;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import javax.inject.Inject;

import dagger.android.AndroidInjection;


/**
 * EasyPlex - Android Movie Portal App
 * @package EasyPlex - Android Movie Portal App
 * @author      @Y0bEX
 * @copyright Copyright (c) 2021 Y0bEX,
 * @license     <a href="http://codecanyon.net/wiki/support/legal-terms/licensing-terms/">...</a>
 * @profile <a href="https://codecanyon.net/user/yobex">...</a>
 * @link yobexd@gmail.com
 * @skype yobexd@gmail.com
 **/


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class NotificationManager extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "CHANNEL_ID";
    private Uri defaultSoundUri;




    @Inject
    SettingsManager settingsManager;

    @Inject
    AnimeRepository animeRepository;


    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        AndroidInjection.inject(this);

        if (remoteMessage.getData().size() > 0) {
            createNotification(remoteMessage);

        }

    }


    private void createNotification(RemoteMessage remoteMessage) {

        Map<String, String> remoteData = remoteMessage.getData();

        String imdb = remoteData.get("tmdb");
        String type = remoteData.get("type");
        String title = remoteData.get("title");
        String message = remoteData.get("message");
        String image = remoteData.get("image");
        String link = remoteData.get("link");

        defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (link !=null && !link.isEmpty()) {


            if (image !=null && !image.isEmpty()) {

                final Bitmap[] bitmap = {null};

                GlideApp.with(getApplicationContext())
                        .asBitmap()
                        .load(image)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                                bitmap[0] = resource;
                                notificationLink(bitmap[0], title, message,link);

                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                                //
                            }
                        });

            } else {

                onLoadNotificationFromLink(link,title,message);

            }


        }else if (type != null) {
            switch (type) {
                case "0": {
                    Media movieDetail = new Media();
                    assert imdb != null;
                    movieDetail.setId(imdb);
                    Tools.loadGlide(this, image, movieDetail, title, message, settingsManager, "0", null);
                    break;
                }
                case "1": {
                    Media movieDetail = new Media();
                    assert imdb != null;
                    movieDetail.setId(imdb);

                    Tools.loadGlide(this, image, movieDetail, title, message, settingsManager, "1", null);
                    break;
                }
                case "2": {
                    Media movieDetail = new Media();
                    assert imdb != null;
                    movieDetail.setId(imdb);

                    Tools.loadGlide(this, image, movieDetail, title, message, settingsManager, "2", null);
                    break;
                }
                case "3": {
                    Media movieDetail = new Media();
                    assert imdb != null;
                    movieDetail.setId(imdb);

                    Tools.loadGlide(this, image, movieDetail, title, message, settingsManager, "3", null);
                    break;
                }
                case "episode": {

                    LatestEpisodes latestEpisodes = new LatestEpisodes();
                    latestEpisodes.setType("serie");
                    if (imdb != null) {
                        latestEpisodes.setEpisodeId(Integer.parseInt(imdb));
                    }


                    Tools.loadGlide(this, image, null, title, message, settingsManager, "episode", latestEpisodes);

                    break;
                }
                case "episode_anime": {

                    LatestEpisodes latestEpisodes = new LatestEpisodes();
                    latestEpisodes.setType("anime");
                    if (imdb != null) {
                        latestEpisodes.setAnimeEpisodeId(Integer.parseInt(imdb));
                    }

                    Tools.loadGlide(this, image, null, title, message, settingsManager, "episode_anime", latestEpisodes);

                    break;
                }
                case "custom":
                    if (image != null && !image.isEmpty()) {

                        final Bitmap[] bitmap = {null};

                        GlideApp.with(getApplicationContext())
                                .asBitmap()
                                .load(image)
                                .into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                                        bitmap[0] = resource;
                                        notificationCustom(bitmap[0], title, message);

                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {

                                        //
                                    }

                                    @Override
                                    public void onLoadFailed(@Nullable Drawable errorDrawable) {

                                        notificationCustom(getBitmapFromURL(), title, message);
                                    }
                                });


                    } else {


                        Intent intent = new Intent(NotificationManager.this, SplashActivity.class);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
                        stackBuilder.addNextIntentWithParentStack(intent);


                        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                                0, Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_MUTABLE : PendingIntent.FLAG_UPDATE_CURRENT);

                        NotificationCompat.Builder notificationBuilder =
                                new NotificationCompat.Builder(NotificationManager.this, CHANNEL_ID)
                                        .setSmallIcon(R.drawable.notification_smal_size)
                                        .setContentTitle(title)
                                        .setContentText(message)
                                        .setAutoCancel(true)
                                        .setSound(defaultSoundUri)
                                        .setContentIntent(resultPendingIntent);


                        android.app.NotificationManager notificationManager = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                        // Since android Oreo notification channel is needed.
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                                    settingsManager.getSettings().getAppName(),
                                    android.app.NotificationManager.IMPORTANCE_DEFAULT);
                            notificationManager.createNotificationChannel(channel);
                        }

                        if (settingsManager.getSettings().getNotificationSeparated() == 1) {

                            notificationManager.notify(Tools.createRandomCode(2), notificationBuilder.build());

                        } else {

                            notificationManager.notify(0, notificationBuilder.build());
                        }

                    }
                    break;
            }
        }


    }


    private void onLoadNotificationFromLink(String link, String title, String message) {

        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(link));
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntentWithParentStack(intent);


        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0, Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_MUTABLE : PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(NotificationManager.this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.notification_smal_size)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(resultPendingIntent);


        android.app.NotificationManager notificationManager = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    settingsManager.getSettings().getAppName(),
                    android.app.NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        if (settingsManager.getSettings().getNotificationSeparated() == 1) {

            notificationManager.notify(Tools.createRandomCode(2), notificationBuilder.build());

        }else {

            notificationManager.notify(0, notificationBuilder.build());
        }
    }

    private void notificationLink(Bitmap bitmap, String title, String message, String link) {


        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.putExtra(Constants.MOVIE_LINK, link);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntentWithParentStack(intent);


        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0, Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_MUTABLE : PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(NotificationManager.this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.notification_smal_size)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                        .setContentIntent(resultPendingIntent);


        android.app.NotificationManager notificationManager = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    settingsManager.getSettings().getAppName(),
                    android.app.NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        if (settingsManager.getSettings().getNotificationSeparated() == 1) {

            notificationManager.notify(Tools.createRandomCode(2), notificationBuilder.build());

        }else {

            notificationManager.notify(0, notificationBuilder.build());
        }
    }

    private void notificationCustom(Bitmap bitmap, String title, String message) {


        Intent intent = new Intent(NotificationManager.this, SplashActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntentWithParentStack(intent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0, Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_MUTABLE : PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(NotificationManager.this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.notification_smal_size)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                        .setContentIntent(resultPendingIntent);


        android.app.NotificationManager notificationManager = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    settingsManager.getSettings().getAppName(),
                    android.app.NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        if (settingsManager.getSettings().getNotificationSeparated() == 1) {

            notificationManager.notify(Tools.createRandomCode(2), notificationBuilder.build());

        }else {

            notificationManager.notify(0, notificationBuilder.build());
        }



    }


    public Bitmap getBitmapFromURL() {

        return BitmapFactory.decodeResource(getResources(), R.drawable.placehoder_episodes);
    }

}
