package goride.com.goridedriver.BookingRequest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import goride.com.goridedriver.BookingAccepted.BookingAccepted;
import goride.com.goridedriver.HomePage.HomePage;
import goride.com.goridedriver.R;
import goride.com.goridedriver.base.BaseActivity;
import goride.com.goridedriver.entities.Ride;


public class BookingRequest extends BaseActivity {

    @BindView(R.id.count_down_timer)
    TextView text1;
    @BindView(R.id.tv_persengers_location_booking_request)
    TextView pickupLocationTextView;
    @BindView(R.id.tv_persengers_destination_booking_request)
    TextView destinationTextView;

    private static final String FORMAT = "%02d";
    private String customerID = "";
    private ProgressDialog progressDialog;
    private Ride currentRide;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_request);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        customerID = intent.getStringExtra("customer_id");
        getRideDetails();
    }

    @OnClick(R.id.btn_accept_ride) public void acceptRideClick() {

        Intent intent = new Intent(this, BookingAccepted.class);
        intent.putExtra("customer_id", customerID);
        startActivity(intent);
    }
    @OnClick(R.id.btn_reject_ride) public void onRejectRide() {

    }
    private void getRideDetails() {

        DatabaseReference databaseReference =
                FirebaseDatabase.getInstance().getReference("ride_requests");
        databaseReference.child(customerID)
                .addListenerForSingleValueEvent(valueEventListener);
    }
    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            progressDialog.cancel();
            if(dataSnapshot.exists()) {
                currentRide = dataSnapshot.getValue(Ride.class);
                if(currentRide != null) {
                    pickupLocationTextView.setText(currentRide.getPickupAddress());
                    destinationTextView.setText(currentRide.getDestinationAddress());
                    startCountDownTimer();
                }
            }else {
                finish();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    void startCountDownTimer() {
        new CountDownTimer(15000, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {

                String text = String.format(FORMAT,
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                text1.setText(text);
            }

            public void onFinish() {

                Intent intent = new Intent(BookingRequest.this, HomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Toast toast = Toast.makeText(BookingRequest.this, "Oops! You missed this booking request", Toast.LENGTH_LONG);
                toast.show();
                toast.setGravity(Gravity.CENTER, 0, 0);

            }
        }.start();
    }
}