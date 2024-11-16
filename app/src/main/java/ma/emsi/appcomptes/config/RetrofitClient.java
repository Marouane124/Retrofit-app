package ma.emsi.appcomptes.config;

import ma.emsi.appcomptes.api.ApiInterface;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;

    public static Retrofit getClient(String format) {
        String finalAcceptHeader = format.equals("application/xml") ? "application/xml" : "application/json";

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .addHeader("Accept", finalAcceptHeader)
                            .build();
                    return chain.proceed(request);
                })
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8082/") // Replace with your actual base URL
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }

    public static ApiInterface getApi(String format) {
        return getClient(format).create(ApiInterface.class);
    }
}
