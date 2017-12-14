package goride.com.goridedriver.BookingAccepted;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import goride.com.goridedriver.R;
import goride.com.goridedriver.base.BaseActivity;
import goride.com.goridedriver.entities.Ride;
import goride.com.goridedriver.entities.User;

public class BookingAccepted extends BaseActivity implements OnMapReadyCallback {


    GoogleMap mMap;
    SupportMapFragment sMapFragment;
    FragmentManager fm = getFragmentManager();
    android.support.v4.app.FragmentManager sFm = getSupportFragmentManager();
    private String customerID = "";
    private User customer;
    private Ride currentRide;

    @BindView(R.id.loading_layout_booking_accept)
    LinearLayout loadingLayout;
    @BindView(R.id.layout_customer_details_booking_accepted)
    LinearLayout customerDetailsLayout;
    @BindView(R.id.tv_customer_username_booking_accepted)
    TextView customerUsernameTextView;

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

        Intent intent = getIntent();
        if(intent == null) {
            finish();
            return;
        }
        customerID = intent.getStringExtra("customer_id");
        getRideDetails();
        getCustomerDetails();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void testEndTrip(View view){

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_confirm_end_trip);
        dialog.show();
    }
    void getCustomerDetails() {

        DatabaseReference databaseReference =
                FirebaseDatabase.getInstance().getReference("users")
                .child(customerID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
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

            if(dataSnapshot.exists()) {
                currentRide = dataSnapshot.getValue(Ride.class);
                if(customer != null && currentRide != null) {
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
        customerDetailsLayout.setVisibility(View.VISIBLE);
        customerUsernameTextView.setText(customer.getFullName());
    }
}
