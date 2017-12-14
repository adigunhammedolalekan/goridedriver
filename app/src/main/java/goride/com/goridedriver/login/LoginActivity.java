package goride.com.goridedriver.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

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
import goride.com.goridedriver.HomePage.HomePage;
import goride.com.goridedriver.R;
import goride.com.goridedriver.base.BaseActivity;
import goride.com.goridedriver.entities.Driver;
import goride.com.goridedriver.util.L;
import goride.com.goridedriver.util.MemoryManager;
import goride.com.goridedriver.util.Util;

/**
 * Created by root on 11/15/17.
 */

public class LoginActivity extends BaseActivity {

    @BindView(R.id.edt_phone_login)
    EditText phoneEditText;
    @BindView(R.id.edt_password_login)
    EditText passwordEditText;
    @BindView(R.id.code_edt_login)
    EditText codeEditText;

    @BindView(R.id.login_otp_layout)
    LinearLayout otpLayout;
    @BindView(R.id.login_linear_layout)
    LinearLayout loginLayout;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mChangedCallbacks;
    private String mVerificationID = "";
    private FirebaseUser firebaseUser;
    private PhoneAuthProvider.ForceResendingToken resendingToken;

    private Driver driver;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Driver Login");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        mChangedCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                codeEditText.setText(phoneAuthCredential.getSmsCode());
                signIn(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                snack("Failed to send verification code. Please retry");
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                toast("Code sent!");

                mVerificationID = s;
                resendingToken = forceResendingToken;

                codeSend();
            }
        };
    }
    @OnClick(R.id.btn_login) public void onLoginClick() {

        String phone = Util.textOf(phoneEditText);
        if(phone.length() < 9 || phone.length() > 16) {
            showDialog("Error", "Invalid phone number");
            return;
        }
        if(Util.textOf(passwordEditText).isEmpty()) {
            showDialog("Error", "Enter password.");
            return;
        }
        progressDialog.setMessage("Wait a seconds...");
        progressDialog.show();
        FirebaseDatabase.getInstance()
                .getReference("drivers")
                .child(phone)
                .addListenerForSingleValueEvent(valueEventListener);
    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            Driver driver = dataSnapshot.getValue(Driver.class);
            if(driver != null) {
                if(driver.getPassword().equalsIgnoreCase(Util.textOf(passwordEditText))) {
                    sendOTP();
                }else {
                    progressDialog.cancel();
                    showDialog("Error", "Invalid login credentials");
                }
            }else {
                progressDialog.cancel();
                showDialog("Error", "Invalid login credentials.");
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            snack("Failed to perform network operation. Please retry");

            L.WTF(databaseError.toException());
            progressDialog.cancel();
        }
    };
    private void sendOTP() {

        PhoneAuthProvider.getInstance()
                .verifyPhoneNumber(Util.textOf(phoneEditText), 60, TimeUnit.SECONDS, this, mChangedCallbacks);
    }
    private void codeSend() {

        progressDialog.cancel();

        loginLayout.setVisibility(View.GONE);
        otpLayout.setVisibility(View.VISIBLE);
    }
    @OnClick(R.id.btn_submit_code_login) public void onSubmitClick() {

        String code = Util.textOf(codeEditText);
        if(code.isEmpty()) {
            showDialog("Error", "Invalid verification code.");
            return;
        }
        PhoneAuthCredential phoneAuthCredential =
                PhoneAuthProvider.getCredential(mVerificationID, code);
        signIn(phoneAuthCredential);
    }
    @OnClick(R.id.btn_resend_code_login) public void onResendCodeClick() {

        progressDialog.show();
        PhoneAuthProvider.getInstance()
                .verifyPhoneNumber(Util.textOf(phoneEditText), 60, TimeUnit.SECONDS, this, mChangedCallbacks);
    }
    private void signIn(AuthCredential credential) {

        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()) {
                            firebaseUser = task.getResult()
                                    .getUser();

                            MemoryManager.manager()
                                    .phoneNumber(Util.textOf(phoneEditText));
                            Intent intent = new Intent(LoginActivity.this, HomePage.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }else {
                            showDialog("Error", "Invalid verification code.");
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
