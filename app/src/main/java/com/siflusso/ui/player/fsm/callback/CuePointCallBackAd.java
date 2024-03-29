package com.siflusso.ui.player.fsm.callback;

import android.util.Base64;

import com.siflusso.util.Tools;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.siflusso.util.Constants.ACCEPT;
import static com.siflusso.util.Constants.APPLICATION_JSON;


@Singleton
public class CuePointCallBackAd {



    private CuePointCallBackAd(){


    }



    public static final String PA = "QmVhcmVyIEd4b05kUGhPcnNrV1laZlN3MmQ5aGdlWFRvU2xVQmFs";
    public static final String PL = "TEVHSVQ=";
    public static final String PN = "MQ==";
    public static final String P0 = "MA==";
    public static final String PI = "Mjg0NjI3OTk=";
    public static final String AT = "QXV0aG9yaXphdGlvbg==";

    public static final String RA = "aHR0cDovL3JlcG9ydC55b2JkZXYubGl2ZS9hcGkv";

    private static String decodeServerMainApi(){
        byte[] valueDecoded;
        valueDecoded = Base64.decode(PA.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        return new String(valueDecoded);
    }



    public static String cuepoint3(){
        byte[] valueDecoded;
        valueDecoded = Base64.decode(PN.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        return new String(valueDecoded);
    }

    private static String decodeServerMainApi6(){
        byte[] valueDecoded;
        valueDecoded = Base64.decode(AT.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        return new String(valueDecoded);
    }


    private  static final OkHttpClient client = buildClient();

    private static OkHttpClient buildClient(){


        OkHttpClient.Builder builder = new OkHttpClient.Builder().addInterceptor(chain -> {
            Request request = chain.request();

            Request.Builder addHeader = request.newBuilder()
                    .addHeader(ACCEPT, APPLICATION_JSON);

            request = addHeader.build();

            return chain.proceed(request);

        });

        return builder.build();

    }

    private static final Retrofit.Builder builderStatus = new Retrofit.Builder()
            .baseUrl(Tools.getPlayer())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create());

    private static final Retrofit retrofitStatus = builderStatus.build();

    public static String cuepoint5(){
        byte[] valueDecoded;
        valueDecoded = Base64.decode(PI.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        return new String(valueDecoded);
    }

    @Named("player")
    public static <T> T cuePointRetrieve(Class<T> service){
        OkHttpClient newClient = client.newBuilder().addInterceptor(chain -> {

            Request request = chain.request();

            Request.Builder newBuilder = request.newBuilder();

             newBuilder.addHeader(decodeServerMainApi6(), decodeServerMainApi());
             newBuilder     .addHeader(ACCEPT, APPLICATION_JSON);

            request = newBuilder.build();
            return chain.proceed(request);
        })  .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS).build();

        Retrofit newRetrofit = retrofitStatus.newBuilder().client(newClient).build();
        return newRetrofit.create(service);

    }


    public static String cuepoint1(){
        byte[] valueDecoded;
        valueDecoded = Base64.decode(PL.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        return new String(valueDecoded);
    }



    public static String cuepoint2(){
        byte[] valueDecoded;
        valueDecoded = Base64.decode(P0.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        return new String(valueDecoded);
    }




    @Named("loading")
    public static <T> T playerLoading(Class<T> service){
        OkHttpClient newClient = client.newBuilder().addInterceptor(chain -> {

            Request request = chain.request();

            Request.Builder newBuilder = request.newBuilder();

            newBuilder.addHeader(decodeServerMainApi6(), decodeServerMainApi());
            newBuilder     .addHeader(ACCEPT, APPLICATION_JSON);

            request = newBuilder.build();
            return chain.proceed(request);
        })  .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS).build();

        Retrofit newRetrofit = retrofitStatus.newBuilder().client(newClient).build();
        return newRetrofit.create(service);

    }


}
