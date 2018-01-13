package goride.com.goridedriver.BookingAccepted;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import butterknife.OnClick;
import goride.com.goridedriver.HomePage.HomePage;
import goride.com.goridedriver.R;
import goride.com.goridedriver.base.BaseActivity;
import goride.com.goridedriver.entities.History;
import goride.com.goridedriver.entities.Ride;
import goride.com.goridedriver.entities.User;
import goride.com.goridedriver.util.L;
import goride.com.goridedriver.util.MemoryManager;

public class BookingAccepted extends BaseActivity
        implements OnMapReadyCallback, DirectionCallback {


    GoogleMap mMap;
    SupportMapFragment sMapFragment;
    FragmentManager fm = getFragmentManager();
    android.support.v4.app.FragmentManager sFm = getSupportFragmentManager();
    private String customerID = "";
    private User customer;
    private Ride currentRide;
    private Marker currentLocationMarker;
    private Marker destinationMarker;
    private List<Polyline> polylines = new ArrayList<>();
    private LatLng start, end, destination;
    private TripStatus tripStatus = TripStatus.NOT_STARTED;
    private AtomicBoolean historyStored = new AtomicBoolean(false);

    @BindView(R.id.loading_layout_booking_accept)
    LinearLayout loadingLayout;
    @BindView(R.id.layout_customer_details_booking_accepted)
    LinearLayout customerDetailsLayout;
    @BindView(R.id.tv_customer_username_booking_accepted)
    TextView customerUsernameTextView;
    @BindView(R.id.btn_start_or_end_trip)
    Button startOrEndTrip;
    @BindView(R.id.time_to_passenger_location)
    TextView timeToPassengerLocationTextView;
    @BindView(R.id.tv_destination_location_booking_accepted)
    TextView destinationLocationTextView;
    @BindView(R.id.tv_pick_up_location_booking_accepted)
    TextView pickUpLocationTextView;

    View endTripDialogView;
    TextView tripFareTextView;
    TextView promotionFeeTextView;
    TextView othersFeeTextView;
    TextView totalFeeTextView;
    Button confirmEndTrip;
    Button cancelEndTrip;

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {

        if(direction.isOK()) {
            if (mMap != null) {
                mMap.clear();
                Route route = direction.getRouteList().get(0);
                mMap.addMarker(new MarkerOptions().position(start));
                mMap.addMarker(new MarkerOptions().position(end));

                ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                mMap.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 5, Color.BLACK));
                setCameraWithCoordinationBounds(route);
            }
        }

    }

    @Override
    public void onDirectionFailure(Throwable t) {
        L.WTF(t);
    }

    public void setCameraWithCoordinationBounds(Route route) {

        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    enum TripStatus {
        STARTED, NOT_STARTED
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sMapFragment = SupportMapFragment.newInstance();
        setContentView(R.layout.activity_booking_accepted);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if (!sMapFragment.isAdded())
            sFm.beginTransaction().add(R.id.map, sMapFragment).commit();
        else
            sFm.beginTransaction().show(sMapFragment).commit();

        sMapFragment.getMapAsync(this);

        Intent intent = getIntent();
        if(intent == null) {
            finish();
            return;
        }
        setupEndTripDialogView();
        customerID = intent.getStringExtra("customer_id");
        destinationLocationTextView.setText("...");
        pickUpLocationTextView.setText("...");
        getRideDetails();
        getCustomerDetails();
    }

    private void setupEndTripDialogView() {

        endTripDialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_confirm_end_trip, null, false);

        tripFareTextView = endTripDialogView.findViewById(R.id.tv_trip_fare_end_trip_dialog);
        promotionFeeTextView = endTripDialogView.findViewById(R.id.tv_promotion_end_trip_dialog);
        othersFeeTextView = endTripDialogView.findViewById(R.id.tv_other_fee_end_trip_dialog);
        totalFeeTextView = endTripDialogView.findViewById(R.id.tv_total_fee_end_trip_dialog);
        confirmEndTrip = endTripDialogView.findViewById(R.id.btn_confirm_end_trip);
        cancelEndTrip = endTripDialogView.findViewById(R.id.btn_cancel_end_trip);

        confirmEndTrip.setOnClickListener(clickListener);
        cancelEndTrip.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.btn_confirm_end_trip:
                    toast("End Trip work in progress");
                    break;
                case R.id.btn_cancel_end_trip:
                    toast("Cancel end Trip work in progress");
                    break;
            }
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    private void storeHistory() {

        /*
        * render() was called twice. needed a state variable to detect if we've stored history before!
        * */
        if (historyStored.get()) return;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (currentRide != null && user != null) {
            History history = new History();
            history.setRide(currentRide);

            FirebaseDatabase
                    .getInstance()
                    .getReference("ride_history")
                    .child(user.getUid())
                    .push()
                    .setValue(history);

            historyStored.set(true);
        }

    }

    void getCustomerDetails() {

        DatabaseReference databaseReference =
                FirebaseDatabase.getInstance().getReference("users")
                .child(customerID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if( dataSnapshot != null && dataSnapshot.exists()) {
                    customer = dataSnapshot.getValue(User.class);
                    if(customer != null && currentRide != null) {
                        loadingLayout.setVisibility(View.GONE);
                        render();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    void getRideDetails() {

        FirebaseDatabase.getInstance().getReference("ride_requests")
        .child(customerID)
        .addListenerForSingleValueEvent(rideRequestValueEventListener);

    }
    private ValueEventListener rideRequestValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if( dataSnapshot != null && dataSnapshot.exists()) {
                currentRide = dataSnapshot.child("rideRequest").getValue(Ride.class);
                L.fine("Ride ==> " + dataSnapshot.toString());
                if (currentRide == null)
                    currentRide = dataSnapshot.getValue(Ride.class);
                if(customer != null && currentRide != null) {
                    L.fine("Status ==> " + currentRide.getStatus());
                    loadingLayout.setVisibility(View.GONE);
                    render();
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    private void render() {

        Location currentLocation = MemoryManager.manager().getLocation();
        start = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        end = new LatLng(currentRide.getPickupLat(), currentRide.getPickupLon());
        destinationLocationTextView.setText(currentRide.getDestinationAddress());
        pickUpLocationTextView.setText(currentRide.getPickupAddress());

        loadingLayout.setVisibility(View.GONE);
        customerDetailsLayout.setVisibility(View.VISIBLE);
        customerUsernameTextView.setText(customer.getFullName());

        if (mMap != null) {
            currentLocationMarker =
                    mMap.addMarker(new MarkerOptions().position(start));
            destinationMarker =
                    mMap.addMarker(new MarkerOptions().position(end));
        }else {
            L.fine("Map == null");
        }

        if (!currentRide.getStatus().trim().equalsIgnoreCase("cancelled")) {
            storeHistory();

            executeRouteGetter(start, end);
        }else {
            new AlertDialog.Builder(this)
                    .setTitle("Ride Canceled")
                    .setMessage("Ride request has been canceled by passenger.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(BookingAccepted.this, HomePage.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    })
                    .create()
                    .show();
        }
    }

    private void executeRouteGetter(LatLng start, LatLng end) {

        GoogleDirection.withServerKey(getString(R.string.google_api_key))
                .from(start)
                .to(end)
                .transportMode(TransportMode.DRIVING)
                .execute(this);
    }

    @OnClick(R.id.btn_start_or_end_trip) public void onStartOrEndTripClick() {

        if (tripStatus == TripStatus.NOT_STARTED) {
            tripStatus = TripStatus.STARTED;
            startOrEndTrip.setText("END TRIP");
            startOrEndTrip.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
            if (start != null && currentRide != null) {
                if (destinationMarker != null)
                    destinationMarker.remove();

                destination = new LatLng(currentRide.getDestinationLat(), currentRide.getDestinationLon());
                if (mMap != null) {
                    destinationMarker =
                            mMap.addMarker(new MarkerOptions().position(destination));
                }

                //change destination to persenger destination
                end = destination;

                executeRouteGetter(start, end);
            }
        }else {
            tripStatus = TripStatus.NOT_STARTED;
            endTrip();
        }
    }

    private void endTrip() {

        if (endTripDialogView != null) {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(endTripDialogView);
            dialog.show();
        }
    }

    @OnClick(R.id.btn_sms_passenger) public void onSendMessageClick() {

        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + customer.getPhoneNumber()));
        intent.putExtra("sms_body", "Hi, " + customer.getFullName());
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
    @OnClick(R.id.btn_call_passenger) public void onCallClick() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + customer.getPhoneNumber()));

        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    private void onTripEnded() {

    }
}
