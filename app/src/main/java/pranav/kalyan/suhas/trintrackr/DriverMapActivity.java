package pranav.kalyan.suhas.trintrackr;

import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class DriverMapActivity extends FragmentActivity implements OnMapReadyCallback {

    // Broad Street other end location = 41.745589, -72.687198
    // Good step for longitude (2nd value) will be -0.000043 per second
    // Good step for latitude (1st value) will be -0.0002 per second

    private GoogleMap mMap;
    private LatLng trin = new LatLng(41.747270, -72.690354);
    private LatLng home = new LatLng(41.752264, -72.687111);
    private Marker mVehicle;
    private Button mPass1Button;
    private Button mPass2Button;
    private Button mPass3Button;

    private Button mShuttleStart;
    private Button mShuttleStop;
    private boolean mShuttleStarted;
    private boolean pass1check, pass2check, pass3check;

    private Marker mPassenger1;
    private Marker mPassenger2;
    private Marker mPassenger3;

    private int mInterval = 1000; // 1 seconds by default, can be changed later
    private int mTest = 1000;
    private Handler mHandler;

    public String mDriver;

    /* Students on the road */
    private int mNumStudent = 0;
    private String[] mStudents = new String[255];

    public String toString(){
        String string = "|";
        for (int i=1; i<=mNumStudent; i++){
            string+=this.mStudents[3*i-3]+" | "+this.mStudents[3*i-2]+" | "+this.mStudents[3*i-1]+" | ";
        }
        return string;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.driverMap);
        mapFragment.getMapAsync(this);

        final GetStLocActivity getStudent = new GetStLocActivity(DriverMapActivity.this);
        getStudent.execute();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                mNumStudent = getStudent.getStNum();
                mStudents = getStudent.getStudents();
                Toast.makeText(DriverMapActivity.this, getStudent.toString(), Toast.LENGTH_SHORT).show();
                Toast.makeText(DriverMapActivity.this, String.valueOf(mNumStudent), Toast.LENGTH_SHORT).show();
            }
        }, 2000);

        mPass1Button = (Button) findViewById(R.id.passenger1);
        mPass2Button = (Button) findViewById(R.id.passenger2);
        mPass3Button = (Button) findViewById(R.id.passenger3);
        mShuttleStart = (Button) findViewById(R.id.driver_start_shuttle);
        mShuttleStop = (Button) findViewById(R.id.driver_stop_shuttle);

        mShuttleStarted = false;
        mPass1Button.setEnabled(false);
        mPass2Button.setEnabled(false);
        mPass3Button.setEnabled(false);
        mShuttleStop.setEnabled(false);
        mShuttleStart.setEnabled(true);
        pass2check = false;
        pass1check = false;
        pass3check = false;


        mShuttleStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShuttleStarted = true;
                mPass1Button.setEnabled(true);
                mPass2Button.setEnabled(true);
                mPass3Button.setEnabled(true);
                mShuttleStop.setEnabled(true);
                mShuttleStart.setEnabled(false);

                Toast.makeText(DriverMapActivity.this, "Starting Shuttle", Toast.LENGTH_SHORT).show();
                new DriverRequestActivity(DriverMapActivity.this).execute("suhas", "1", "10", "20");

            }
        });

        mShuttleStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pass1check) {
                    mPassenger1.remove();
                }
                if (pass2check) {
                    mPassenger2.remove();
                }
                if (pass3check){
                    mPassenger3.remove();
                }
                mPass1Button.setEnabled(false);
                mPass2Button.setEnabled(false);
                mPass3Button.setEnabled(false);
                mShuttleStop.setEnabled(false);
                mShuttleStart.setEnabled(true);

                mShuttleStarted = false;


                Toast.makeText(DriverMapActivity.this, "Stopping Shuttle", Toast.LENGTH_SHORT).show();
                new DriverRequestActivity(DriverMapActivity.this).execute("suhas", "0", "0", "0");

            }
        });


        mHandler = new Handler();
//        startRepeatingTask();
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(trin, 16));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        mMap.animateCamera(zoom);
        MarkerOptions mOptions = new MarkerOptions().position(home).title("Where do you want the shuttle?");
        mVehicle = mMap.addMarker(mOptions);
        mVehicle.setDraggable(true);

        mPass1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarkerOptions mar = new MarkerOptions()
                        .position(new LatLng(41.747977, -72.693216))
                        .title("This is my title")
                        .snippet("and snippet")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                mPassenger1 = mMap.addMarker(mar);
                pass1check = true;
            }
        });

        mPass2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarkerOptions mar = new MarkerOptions()
                        .position(new LatLng(41.751824, -72.687094))
                        .title("This is my title")
                        .snippet("and snippet")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                mPassenger2 = mMap.addMarker(mar);
                pass2check = true;
            }
        });

        mPass3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarkerOptions mar = new MarkerOptions()
                        .position(new LatLng(41.747056, -72.687155))
                        .title("This is my title")
                        .snippet("and snippet")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                mPassenger3 = mMap.addMarker(mar);
                pass3check = true;
            }
        });
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            LatLng pos = mVehicle.getPosition();
            double x = pos.longitude;
            double y = pos.latitude;
            x = x + 0.001;


            mVehicle.remove();
            MarkerOptions mar = new MarkerOptions()
                    .position(new LatLng(y, x))
                    .title("This is my title")
                    .snippet("and snippet")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            mVehicle = mMap.addMarker(mar);


            mHandler.postDelayed(mStatusChecker, mInterval);
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }
}