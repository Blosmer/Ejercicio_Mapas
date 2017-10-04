package com.example.mac.mismapas1;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private GoogleMap mapa;
    private Button btnDetalles, btnNext, btnBack;
    private boolean detalles = false;
    private LatLng coordenadas;
    private String nombreLugar, direccionLugar;
    private ArrayList<Marker> marcadores;
    private TextView distanciaTXT;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location locMarker;
    private Marker marcador;
    private int position = 0;
    private LugaresSQLiteHelper usdbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        marcadores = new ArrayList<Marker>();
        locMarker = new Location("localizacionMarcador");

        distanciaTXT = (TextView) findViewById(R.id.distancia);
        btnDetalles = (Button) findViewById(R.id.btnDetalles);
        btnNext = (Button) findViewById(R.id.btnSiguiente);
        btnBack = (Button) findViewById(R.id.btnAnterior);

        btnDetalles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayMetrics displaymetrics = new DisplayMetrics();
                mapFragment.getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                int height = displaymetrics.heightPixels;
                ViewGroup.LayoutParams params = mapFragment.getView().getLayoutParams();

                if (detalles == false) {
                    params.height = (height / 10) * 7;
                    mapFragment.getView().setLayoutParams(params);
                    detalles = true;
                } else {
                    params.height = FrameLayout.LayoutParams.MATCH_PARENT;
                    mapFragment.getView().setLayoutParams(params);
                    detalles = false;
                }
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap map) {
        try {
            mapa = map;
            mapa.getUiSettings().setMapToolbarEnabled(true);

            //Aplicamos el estilo personalizado de mapa
            mapa.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.formato_mapa));

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                    (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mapa.setMyLocationEnabled(true);

            CameraUpdate camUpd1 =
                    CameraUpdateFactory
                            .newLatLngZoom(new LatLng(38.992650, -1.853078), 17);
            mapa.moveCamera(camUpd1);

            //Abrimos la base de datos 'DBLugares' en modo lectura
            usdbh = new LugaresSQLiteHelper(this, "DBLugares", null, 1);
            final SQLiteDatabase db = usdbh.getReadableDatabase();

            //metodo rawQuery()
            Cursor c = db.rawQuery("SELECT * FROM Lugares", null);

            while (c.moveToNext()) {
                coordenadas = new LatLng(c.getDouble(c.getColumnIndexOrThrow("latitud")),
                        c.getDouble(c.getColumnIndexOrThrow("longitud")));
                nombreLugar = c.getString(c.getColumnIndexOrThrow("nombre"));
                direccionLugar = c.getString(c.getColumnIndexOrThrow("direccion"));

                marcador = mapa.addMarker(new MarkerOptions()
                        .position(coordenadas).title(nombreLugar).snippet(direccionLugar).visible(true).anchor(0f, 0.5f)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                marcador.showInfoWindow();
                marcadores.add(marcador);
            }

            mapa.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                public boolean onMarkerClick(Marker marker) {
                    locMarker.setLatitude(marker.getPosition().latitude);
                    locMarker.setLongitude(marker.getPosition().longitude);
                    nombreLugar = marker.getTitle();
                    marker.showInfoWindow();
                    mapa.moveCamera(CameraUpdateFactory
                            .newLatLngZoom((marker.getPosition()), 17));
                    actualizarPosicion();
                    return true;
                }
            });
            mapa.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    marker.getTitle();
                }
            });

            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position < marcadores.size() - 1) {
                        position++;
                        mapa.moveCamera(CameraUpdateFactory
                                .newLatLngZoom((marcadores.get(position).getPosition()), 17));
                        marcadores.get(position).showInfoWindow();
                        nombreLugar = marcadores.get(position).getTitle();
                        locMarker.setLatitude(marcadores.get(position).getPosition().latitude);
                        locMarker.setLongitude(marcadores.get(position).getPosition().longitude);
                        actualizarPosicion();
                    } else
                        Log.d("TAG", "Ese era el ultimo, colega");
                }
            });
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position > 0) {
                        position--;
                        mapa.moveCamera(CameraUpdateFactory
                                .newLatLngZoom((marcadores.get(position).getPosition()), 17));
                        marcadores.get(position).showInfoWindow();
                        nombreLugar = marcadores.get(position).getTitle();
                        locMarker.setLatitude(marcadores.get(position).getPosition().latitude);
                        locMarker.setLongitude(marcadores.get(position).getPosition().longitude);
                        actualizarPosicion();
                    } else
                        Log.d("TAG", "Ese era el ultimo, colega");
                }
            });

            actualizarPosicion();

            mapa.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    usdbh.insertarLugar(db, "unknown", latLng.latitude, latLng.longitude);
                    mapa.addMarker(new MarkerOptions()
                            .position(latLng).title("unknown").visible(true).anchor(0f, 0.5f)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                }
            });

        } catch (Exception e) {
            Toast toast = Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }

    }


    private void actualizarPosicion() {
        //Obtenemos una referencia al LocationManager
        locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Obtenemos la ultima posicion conocida
        Location lastLocation =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        //Mostramos la ultima posicion conocida
        muestraPosicion(lastLocation);

        //Nos registramos para recibir actualizaciones de la posicion
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                muestraPosicion(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.i("LocAndroid", "Provider Status: " + status);
            }

            public void onProviderEnabled(String s) {

            }

            public void onProviderDisabled(String s) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 15000, 0, locationListener);
    }

    private void muestraPosicion(Location loc) {
        double distancia = 0;
        if (locMarker == null || loc == null) {
            distanciaTXT.setText("Selecciona un marcador");
        } else {
            distancia = loc.distanceTo(locMarker) / 1000;
            distanciaTXT.setText(String.format("Distancia hasta %s %.3f KM", nombreLugar, distancia));
        }
    }
}

/**
public class FireMissilesDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog, null))
                // Add action buttons
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        LoginDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

}
**/