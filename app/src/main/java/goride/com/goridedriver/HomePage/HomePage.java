package goride.com.goridedriver.HomePage;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import goride.com.goridedriver.BookingAccepted.BookingAccepted;
import goride.com.goridedriver.BookingRequest.BookingRequest;
import goride.com.goridedriver.History.FragmentHistory;
import goride.com.goridedriver.MyProfile;
import goride.com.goridedriver.R;
import goride.com.goridedriver.base.BaseActivity;
import goride.com.goridedriver.entities.Ride;
import goride.com.goridedriver.entities.User;
import goride.com.goridedriver.listviews.FragmentHome;
import goride.com.goridedriver.util.L;
import goride.com.goridedriver.util.MemoryManager;

public class HomePage extends BaseActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, ResultCallback<LocationSettingsResult> {


    GoogleMap mMap;
    SupportMapFragment sMapFragment;
    FragmentManager fm = getFragmentManager();
    android.support.v4.app.FragmentManager sFm = getSupportFragmentManager();

    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private Marker mCurrentLocationMarker;
    private LatLng driverLatLng;
    private String customerID = "";
    private DatabaseReference requestDatabaseReference;
    private Location mCustomerLocation;
    private int mCounter = 0;
    private Handler handler = new Handler();
    private Ride currentRide;

    public static final int REQUEST_LOCATION_PERMISSION = 1;
    public static final int REQUEST_LOCATION_SETTINGS = 2;
    public static final int LOCATION_UPDATE_INTERVAL = 10000;
    public static final int LOCATION_FASTEST_UPDATE_INTERVAL = LOCATION_UPDATE_INTERVAL / 2;
    public static final String KEY_LOCATION = "key_location";

    @BindView(R.id.switch_driver_go_online)
    SwitchCompat goOnlineSwitchCompat;
    @BindView(R.id.layout_booking_request_notification)
    FrameLayout mBookingRequestLayout;
    @BindView(R.id.tv_persengers_destination_booking_request)
    TextView percengerDestinationTextView;
    @BindView(R.id.tv_persengers_location_booking_request)
    TextView percengerPickupLocationTextView;
    @BindView(R.id.count_down_timer)
    TextView timerTextView;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    sMapFragment = SupportMapFragment.newInstance();
                    setContentView(R.layout.activity_home_page);
                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                    if (!sMapFragment.isAdded())
                        sFm.beginTransaction().add(R.id.map, sMapFragment).commit();
                    else
                        sFm.beginTransaction().show(sMapFragment).commit();

                    return true;
                case R.id.navigation_history:

                    fm = getFragmentManager();
                    fm.beginTransaction().replace(R.id.content_frame, new FragmentHistory()).commit();
                    return true;
                case R.id.navigation_payments:
                    return true;
                case R.id.navigation_notifications:

                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
        sMapFragment = SupportMapFragment.newInstance();
        setContentView(R.layout.activity_home_page);
        if (!sMapFragment.isAdded())
            sFm.beginTransaction().add(R.id.map, sMapFragment).commit();
        else
            sFm.beginTransaction().show(sMapFragment).commit();

        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.content, new FragmentHome()).commit();

        sMapFragment.getMapAsync(this);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        initLocationManager();
        requestPermission();

        requestDatabaseReference = FirebaseDatabase.getInstance().getReference().child("drivers")
                .child(MemoryManager.manager().phoneNumber());
        requestDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot != null && dataSnapshot.getChildrenCount() > 0) {

                    L.fine("Datasnap ==> " + dataSnapshot.toString());
                     currentRide = dataSnapshot.child("rideRequest").getValue(Ride.class);
                    if(currentRide != null && currentRide.getStatus().trim().equalsIgnoreCase("newRide")) {
                        promptNewRide(currentRide);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                L.WTF(databaseError.toException());
            }
        });
    }

    private void promptNewRide(Ride toPrompt) {

        timerTextView.setText(String.valueOf(mCounter));
        mCounter = 10;
        percengerDestinationTextView.setText(toPrompt.getDestinationAddress());
        percengerPickupLocationTextView.setText(toPrompt.getPickupAddress());
        mBookingRequestLayout.setVisibility(View.VISIBLE);
        startCountDownTimer();

    }
    private void startCountDownTimer() {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                mCounter--;
                timerTextView.setText(String.valueOf(mCounter));
                if(mCounter <= 0) {
                    rejectRide();
                }
                startCountDownTimer();
            }
        }, 1000);
    }
    @OnClick(R.id.btn_accept_ride) public void onAcceptRide() {
        acceptRide();
    }

    @OnClick(R.id.btn_reject_ride) public void onRejectRide() {

    }
    private void rejectRide() {
        changeStatus("rejected");
        mBookingRequestLayout.setVisibility(View.GONE);
        handler.removeCallbacksAndMessages(null);
    }
    private void acceptRide() {

        handler.removeCallbacksAndMessages(null);
        changeStatus("accepted");
        Intent intent = new Intent(this, BookingAccepted.class);
        intent.putExtra("customer_id", customerID);
        try {
            intent.putExtra("ride", currentRide.toJson());
        }catch (Exception e) {}

        startActivity(intent);
    }

    private void changeStatus(String newStatus) {

        Map<String, Object> update = new HashMap<>();
        update.put("status", newStatus);
        FirebaseDatabase.getInstance().getReference("drivers")
                .child(MemoryManager.manager().phoneNumber())
                .child("rideRequest").updateChildren(update);
    }

    @OnCheckedChanged(R.id.switch_driver_go_online) public void onGoOnlineClick() {
        L.fine("Checked change");
        if(goOnlineSwitchCompat.isChecked()) {
            beOnline();
        }else {
            beOffline();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(mCurrentLocation != null) {

            if(mCurrentLocationMarker != null)
                mCurrentLocationMarker.remove();

            mCurrentLocationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())));

        }
    }



    public void goToProfile(View view){

        Intent intent = new Intent(this, MyProfile.class);
        startActivity(intent);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        try {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }catch (SecurityException e) {}

        initLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        L.fine("Connection suspended ==> " + i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        L.fine("Connection Failed ==> " + connectionResult.getErrorMessage());
    }

    @Override
    public void onLocationChanged(Location location) {

        mCurrentLocation = location;
        L.fine("Location Changed ==> " + mCurrentLocation.toString());
        initLocation();

    }
    void requestPermission() {

        if(PackageManager.PERMISSION_GRANTED ==
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            buildGoogleClient();
            setupLocationRequest();
            setupLocationSettingsRequest();
            if(!isLocationEnabled())
                requestTurnOnLocation();
            return;
        }
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this,
                    new String []{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }else {
            ActivityCompat.requestPermissions(this,
                    new String []{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED) {
            buildGoogleClient();
            setupLocationRequest();
            setupLocationSettingsRequest();
            if(!isLocationEnabled())
                requestTurnOnLocation();
        }
    }
    private void initLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        }
    }
    private boolean isLocationEnabled() {

        if(mLocationManager == null)
            return false;

        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    private synchronized void buildGoogleClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();
    }
    private synchronized void setupLocationRequest() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(LOCATION_FASTEST_UPDATE_INTERVAL);

    }
    private synchronized void setupLocationSettingsRequest() {

        mLocationSettingsRequest =
                new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest)
                .setAlwaysShow(true).build();
    }
    private void requestTurnOnLocation() {

        PendingResult<LocationSettingsResult> pendingResult =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, mLocationSettingsRequest);
        pendingResult.setResultCallback(this);
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsRequest) {

        Status status = locationSettingsRequest.getStatus();

        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(HomePage.this, REQUEST_LOCATION_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    //
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                break;
        }
    }
    protected void startLocationUpdates() {
        try {
            L.fine("Location update started.");
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient,
                    mLocationRequest,
                    this
            ).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    L.fine(status.toString());
                }
            });
        }catch (SecurityException e) {}

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_LOCATION_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        startLocationUpdates();
                        break;
                    case RESULT_CANCELED:
                        L.fine("User cancelled it!");
                        break;
                }
        }
    }
    private void initLocation() {

        if (mCurrentLocation == null)
            return;

        driverLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

        if(customerID == null || customerID.isEmpty()) {
            setDriverAvailability();
        } else {
            setDriverWorking();
        }
    }

    private void setDriverWorking() {

        if(!goOnlineSwitchCompat.isChecked())
            return;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference =
                FirebaseDatabase.getInstance().getReference("online_drivers");
        DatabaseReference workingDriversReference =
                FirebaseDatabase.getInstance().getReference("working_drivers");
        if(user != null) {
            GeoFire geoFire = new GeoFire(workingDriversReference);
            geoFire.removeLocation(user.getUid());
        }
        if(user != null) {
            GeoFire fire = new GeoFire(databaseReference);
            fire.setLocation(user.getUid(),
                    new GeoLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
        }
    }

    private void setDriverAvailability() {

        if(!goOnlineSwitchCompat.isChecked())
            return;

        L.fine("Updating location to geofire");
        DatabaseReference databaseReference =
                FirebaseDatabase.getInstance().getReference("online_drivers");
        DatabaseReference workingDriversReference =
                FirebaseDatabase.getInstance().getReference("working_drivers");
        GeoFire geoFire = new GeoFire(workingDriversReference);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null)
            geoFire.removeLocation(user.getUid());

        if(user != null) {
            GeoFire fire = new GeoFire(databaseReference);
            fire.setLocation(user.getUid(),
                    new GeoLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String s, DatabaseError databaseError) {

                        }
                    });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        removeLocationUpdates();
    }
    private void removeLocationUpdates() {

        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }catch (Exception e) {}
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("online_drivers");
        GeoFire geoFire = new GeoFire(databaseReference);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null) {
            geoFire.removeLocation(firebaseUser.getUid());
        }
    }
    private void beOnline() {
        startLocationUpdates();
    }
    private void beOffline() {
        removeLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();

        startLocationUpdates();
        if (mCurrentLocation != null && goOnlineSwitchCompat.isChecked()) {
            beOnline();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

        if(mCurrentLocation == null) {
            super.onSaveInstanceState(outState, outPersistentState);
            return;
        }

        outState.putParcelable(KEY_LOCATION, mCurrentLocation);
        super.onSaveInstanceState(outState, outPersistentState);
    }
}
