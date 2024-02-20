package com.siflusso.data.local;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.siflusso.data.local.converters.CastConverter;
import com.siflusso.data.local.converters.GenreConverter;
import com.siflusso.data.local.converters.MediaStreamConverter;
import com.siflusso.data.local.converters.MediaSubstitlesConverter;
import com.siflusso.data.local.converters.SaisonConverter;
import com.siflusso.data.local.converters.VideosConverter;
import com.siflusso.data.local.dao.AnimesDao;
import com.siflusso.data.local.dao.MoviesDao;
import com.siflusso.data.local.dao.DownloadDao;
import com.siflusso.data.local.dao.HistoryDao;
import com.siflusso.data.local.dao.ResumeDao;
import com.siflusso.data.local.dao.SeriesDao;
import com.siflusso.data.local.dao.StreamListDao;
import com.siflusso.data.local.entity.Animes;
import com.siflusso.data.local.entity.History;
import com.siflusso.data.local.entity.Media;
import com.siflusso.data.local.entity.Download;
import com.siflusso.data.local.entity.Series;
import com.siflusso.data.local.entity.Stream;
import com.siflusso.data.model.media.Resume;


/**
 * The Room database that contains the Favorite Movies & Series & Animes table
 * Define an abstract class that extends RoomDatabase.
 * This class is annotated with @Database, lists the entities contained in the database,
 * and the DAOs which access them.
 */
@Database(entities = {Media.class, Series.class, Animes.class, Download.class, History.class, Stream.class, Resume.class
}, version =52,exportSchema = true)
@TypeConverters({GenreConverter.class,
        CastConverter.class,
        VideosConverter.class,
        SaisonConverter.class,
        MediaSubstitlesConverter.class,
        MediaStreamConverter.class})
public abstract class EasyPlexDatabase extends RoomDatabase {

    public abstract MoviesDao favoriteDao();
    public abstract SeriesDao seriesDao();
    public abstract AnimesDao animesDao();
    public abstract DownloadDao progressDao();
    public abstract HistoryDao historyDao();
    public abstract StreamListDao streamListDao();
    public abstract ResumeDao resumeDao();


}
