package com.example.usuari.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    LocationManager lm;
    LocationListener escuchador = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            //Location loc = lm.getLastKnownLocation(LocationManager.PASSIVE_ PROVIDER);
            String datos = "Altitud:" + location.getAltitude() + "|Longitud:" + location.getLongitude() + "|Latitud:" + location.getLatitude();
            Toast.makeText(MainActivity.this, datos, Toast.LENGTH_LONG).show();
            ComunicacionTask ctask = new ComunicacionTask();
            ctask.execute("10.0.2.2", "9000", datos);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        lm.removeUpdates(escuchador);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, escuchador);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    }
    private class ComunicacionTask extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... strings) {
            Socket sc=null;
            String resultado="";
            try {
                sc = new Socket(strings[0],Integer.parseInt(strings[1]));
                PrintStream ps = new PrintStream(sc.getOutputStream());
                ps.println(strings[2]);
                ps.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (sc!=null){
                    try {
                        sc.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }
}
