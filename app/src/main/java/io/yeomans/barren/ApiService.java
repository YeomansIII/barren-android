package io.yeomans.barren;

import io.yeomans.barren.models.Device;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by jason on 6/20/16.
 */
public interface ApiService {

    @POST("devices")
    Call<Device> createDevice(@Body Device createDevice);

    @PUT("devices/{id}")
    Call<Device> updateDevice(@Path("id") String id, @Body Device createDevice);

}
