package co.id.idpay.ektp.services;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created      : Rahman on 8/25/2017.
 * Project      : EKTP.
 * ================================
 * Package      : com.esimtek.service.
 * Copyright    : idpay.com 2017.
 */
public interface ApiInterface {
    @FormUrlEncoded
    @POST("users")
    Call<ResponseBody> login(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("ektp")
    Call<ResponseBody> ektpSend(@FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST("logs")
    Call<ResponseBody> logSend(@FieldMap HashMap<String, String> params);
}
