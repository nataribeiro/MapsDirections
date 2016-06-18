package br.com.natanaelribeiro.www.mapsdirections;

import br.com.natanaelribeiro.www.mapsdirections.Estrutura.Directions;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by 631610277 on 18/06/16.
 */
public interface MyInterfaceRetrofit {

    @GET("maps/api/directions/json")
    Call<Directions> getCaminho(@Query("origin") String origin, @Query("destination") String destination, @Query("key") String key);

}
