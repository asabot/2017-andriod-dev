package v.datagatherer;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.widget.EditText;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    String lat;
    String longi;
    int key = 0;
    String savedData = "";

    //SharedPreferences sharedPref = getSharedPreferences("dataFile", Context.MODE_PRIVATE);
   // SharedPreferences.Editor editor = sharedPref.edit();


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET }, 10 );
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            lat = (String.valueOf(mLastLocation.getLatitude()));
            longi = (String.valueOf(mLastLocation.getLongitude()));
        }
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this /* FragmentActivity */,
                            (OnConnectionFailedListener) this /* OnConnectionFailedListener */)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    public void onConnectionSuspended(int cause) {
        // We are not connected anymore!
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    public void displayCoord(View view) {

        TextView textViewOne = (TextView) findViewById(R.id.TextView_1);
        TextView textViewTwo = (TextView) findViewById(R.id.TextView_2);
        TextView textViewFour = (TextView) findViewById(R.id.TextView_4);
        textViewOne.setText(lat);
        textViewTwo.setText(longi);


        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String millisInString  = dateFormat.format(new Date());
       // textViewFour.setText(millisInString);

        String combinedForStorage = millisInString.concat(" ").concat(lat).concat(" ").concat(longi);
        textViewFour.setText(combinedForStorage );

        SharedPreferences sharedPref = getSharedPreferences("dataFile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(String.valueOf(key), combinedForStorage);
        editor.apply();
        key++;


    }

    public void displayHistory(View view){

        SharedPreferences sharedPref = getSharedPreferences("dataFile", Context.MODE_PRIVATE);
        //String savedData = sharedPref.getString("userInputMessage", "cannot find");

        savedData = "";
        for (int i = 0; i < key; i++){
            String savedLocation = sharedPref.getString(String.valueOf(i),"cannot find entry");
            savedData = savedData.concat("\n").concat(savedLocation);
        }

        TextView textViewThree = (TextView) findViewById(R.id.TextView_3);
        textViewThree.setMovementMethod(new ScrollingMovementMethod());
        textViewThree.setText(savedData);

        if (key == 0){
            textViewThree.setText("nothing to show");

        }

    }

    public void clearHistory(View view){

        SharedPreferences sharedPref = getSharedPreferences("dataFile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();

        key = 0;
        savedData = "";
        TextView textViewThree = (TextView) findViewById(R.id.TextView_3);
        textViewThree.setText("Cleared");

    }

    protected void sendEmail(View view) {
        Log.i("Send email", "");

        String[] TO = {"yu.xue@mail.utoronto.ca"};
        String[] CC = {"yu.xue@mail.utoronto.ca"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "This is an automated message generated by baby skynet");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "We know where you live!!!");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Finished sending email", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
