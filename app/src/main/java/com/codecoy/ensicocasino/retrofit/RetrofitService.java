package com.codecoy.ensicocasino.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitService {
  //  private static final String BASE_URL = "https://test.api.com/";
    private static RetrofitService INSTANCE;
    private RetrofitAPI retrofitAPI;



    /**
     * Method that returns the instance
     * @return
     */
    public static RetrofitService getInstance() {
//        if (INSTANCE == null) {
//            INSTANCE = new RetrofitService();
//        }
       // return INSTANCE;
        return new RetrofitService();
    }

//    public RetrofitAPI RetrofitService(Context context) {
//        Retrofit mRetrofit = new Retrofit.Builder()
//                .addConverterFactory(GsonConverterFactory.create())
//                .baseUrl(context.getSharedPreferences("MyPref", Context.MODE_PRIVATE).getString("baseUrl","https://93.103.81.142:51120/mobile/"))
//                .build();
//        retrofitAPI = mRetrofit.create(RetrofitAPI.class);
//        return retrofitAPI;
//    }


    public static OkHttpClient.Builder getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    public RetrofitAPI RetrofitServices(String BaseUrl) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .client(getUnsafeOkHttpClient().build())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        retrofitAPI = mRetrofit.create(RetrofitAPI.class);
        return retrofitAPI;
    }


    public RetrofitAPI getRetrofitAPI() {
        return retrofitAPI;
    }

    /**
     * Method that returns the API
     * @return
     */
}
