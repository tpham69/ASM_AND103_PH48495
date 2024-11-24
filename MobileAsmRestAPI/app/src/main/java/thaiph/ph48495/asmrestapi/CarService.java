package thaiph.ph48495.asmrestapi;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CarService {
    @GET("json-data")
    Call<List<CarModel>> getAllCar();

    @POST("create")
    Call<CarModel> createCar(@Body CarModel carModel);

    @PUT("{id}")
    Call<CarModel> updateCar(@Path("id") String carId, @Body CarModel carModel);

    @DELETE("{id}")
    Call<CarModel> deleteCar(@Path("id") String carId);
}
