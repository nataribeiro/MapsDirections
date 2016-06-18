package br.com.natanaelribeiro.www.mapsdirections;

import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.android.PolyUtil;

import java.util.List;

import br.com.natanaelribeiro.www.mapsdirections.Estrutura.Directions;
import br.com.natanaelribeiro.www.mapsdirections.Estrutura.LatLongObj;
import br.com.natanaelribeiro.www.mapsdirections.Estrutura.Legs;
import br.com.natanaelribeiro.www.mapsdirections.Estrutura.Routes;
import br.com.natanaelribeiro.www.mapsdirections.Estrutura.Step;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public MyInterfaceRetrofit service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        EditText editText_destino = (EditText)findViewById(R.id.editText_destino);
        editText_destino.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){

                    mMap.clear();
                    EditText editText_origem = (EditText)findViewById(R.id.editText_origem);
                    EditText editText_destino = (EditText)findViewById(R.id.editText_destino);

                    Gson gson = new GsonBuilder().create();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("https://maps.googleapis.com/")
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();

                    service = retrofit.create(MyInterfaceRetrofit.class);
                    Call<Directions> call = service.getCaminho(editText_origem.getText().toString(), editText_destino.getText().toString(), getString(R.string.google_maps_key_server));
                    call.enqueue(new Callback<Directions>() {
                        @Override
                        public void onResponse(Call<Directions> call, Response<Directions> response) {
                            if(response.body().status.equalsIgnoreCase("OK")) {


                                for (Routes route : response.body().routes) {

                                    List<LatLng> latLngList = PolyUtil.decode(route.overview_polyline.points);
                                    PolylineOptions polOpt = new PolylineOptions();
                                    polOpt.addAll(latLngList);
                                    polOpt.color(Color.BLUE);
                                    polOpt.width(5);
                                    mMap.addPolyline(polOpt);

                                    //for(Legs leg : route.legs){
                                    //    for(Step step : leg.steps){
                                    //        PolylineOptions polOpt = new PolylineOptions();
                                    //        polOpt.add(new LatLng(step.start_location.lat, step.start_location.lng));
                                    //        polOpt.add(new LatLng(step.end_location.lat, step.end_location.lng));
                                    //        polOpt.color(Color.BLUE);
                                    //        polOpt.width(5);
                                    //        mMap.addPolyline(polOpt);
                                    //    }
                                    //}
                                    LatLngBounds llb = new LatLngBounds.Builder()
                                            .include(new LatLng(route.bounds.northeast.lat, route.bounds.northeast.lng))
                                            .include(new LatLng(route.bounds.southwest.lat, route.bounds.southwest.lng))
                                            .build();
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(llb, 50));
                                }

                            } else {
                                Toast.makeText(getBaseContext(), "Não encontrado!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Directions> call, Throwable t) {

                        }
                    });

                }
                return false;
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
