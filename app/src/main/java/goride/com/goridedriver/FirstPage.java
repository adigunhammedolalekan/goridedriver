package goride.com.goridedriver;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import goride.com.goridedriver.BookingAccepted.BookingAccepted;
import goride.com.goridedriver.BookingRequest.BookingRequest;
import goride.com.goridedriver.HomePage.HomePage;
import goride.com.goridedriver.Register.Register;
import goride.com.goridedriver.Register.RegisterationComplete;
import goride.com.goridedriver.base.BaseActivity;
import goride.com.goridedriver.entities.Driver;
import goride.com.goridedriver.login.LoginActivity;
import goride.com.goridedriver.util.Util;


public class FirstPage extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);


    }

    public void toLogin(View view){

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    public void toRegister(View view){

        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.fade_in);

    }
}

