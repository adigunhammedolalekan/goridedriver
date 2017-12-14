package goride.com.goridedriver.Register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

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

public class Register extends BaseActivity implements AdapterView.OnItemSelectedListener{


    private String[] arraySpinner;
    private String selectedState, selectedAboutGoRide = "";

    @BindView(R.id.edt_full_name_register)
    EditText firstNameEditText;
    @BindView(R.id.edt_email_address_register)
    EditText emailEditText;
    @BindView(R.id.edt_phone_register)
    EditText phoneEditText;
    @BindView(R.id.edt_last_name_register)
    EditText lastNameEditText;
    @BindView(R.id.edt_lpn_register)
    EditText licensePlateNumberEditText;
    @BindView(R.id.edt_car_model_register)
    EditText carModelEditText;
    @BindView(R.id.edt_password_register)
    EditText passwordEditText;
    @BindView(R.id.edt_confirm_password_register)
    EditText confirmPasswordEditText;
    @BindView(R.id.edt_code_register)
    EditText codeEditText;
    @BindView(R.id.statesSpinner)
    Spinner statesSpinner;
    @BindView(R.id.aboutGoRideSpinner)
    Spinner aboutSpinner;

    @BindView(R.id.main_layout_register_user)
    CardView mainCardView;
    @BindView(R.id.otp_layout_register_driver)
    CardView otpCardView;

    private FirebaseAuth firebaseAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mChangedCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private ProgressDialog progressDialog;
    private FirebaseUser newDriver;
    private String verificationID = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        this.arraySpinner = new String[]{
                "State", "Abia", "Adamawa", "Akwa Ibom", "Anambra", "Bauchi", "Bayelsa", "Benue", "Borno", "Cross River", "Delta",
                "Ebonyi", "Enugu", "Edo", "Ekiti", "Ekiti", "Gombe", "Jigawa", "Kaduna", "Kano", "Katsina", "Kebbi", "Kogi",
                "Kwara", "Lagos", "Nasarawa", "Niger", "Ogun", "Ondo", "Osun", "Oyo", "Plateau", "Rivers",
                "Sokoto", "Taraba", "Yobe", "Zamfara",


        };
        statesSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arraySpinner));
        this.arraySpinner = new String[]{
                "How did you hear about GoRide?", "Friends & Family", "Outdoor", "Social Media",
                "GoRide Driver", "GoRide Staff", "Newsapaper/Print", "Internet Ad & Search", "Others"
        };
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        aboutSpinner.setAdapter(adapter2);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        aboutSpinner.setOnItemSelectedListener(this);
        statesSpinner.setOnItemSelectedListener(this);

        mChangedCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                codeEditText.setText(phoneAuthCredential.getSmsCode());
                signIn(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                progressDialog.cancel();
                showDialog("Error", "Failed to send verification code. Please retry");
                L.WTF(e);
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                resendingToken = forceResendingToken;
                verificationID = s;
                codeSent();
            }
        };
    }
    @OnClick(R.id.btn_sign_up_register) public void onSignUpClick() {

        if(Util.textOf(firstNameEditText).isEmpty()) {
            showDialog("Error", "Enter your first name");
            return;
        }
        if(Util.textOf(lastNameEditText).isEmpty()) {
            showDialog("Error", "Enter your last name");
            return;
        }
        String phone = Util.textOf(phoneEditText);
        if(phone.length() > 16 || phone.length() < 9) {
            showDialog("Error", "Invalid phone number.");
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(Util.textOf(emailEditText)).matches()) {
            showDialog("Error", "Invalid email address.");
            return;
        }
        if(Util.textOf(carModelEditText).isEmpty()) {
            showDialog("Error", "Enter car your car model name");
            return;
        }
        if(Util.textOf(licensePlateNumberEditText).isEmpty()) {
            showDialog("Error", "Enter license plate number");
            return;
        }
        if(selectedState == null || selectedState.isEmpty()) {
            showDialog("Error", "Select state of residence");
            return;
        }
        String password = Util.textOf(passwordEditText);
        String cPassword = Util.textOf(confirmPasswordEditText);
        if(password.isEmpty() || cPassword.isEmpty()) {
            showDialog("Error", "Enter password");
            return;
        }
        if(!password.equalsIgnoreCase(cPassword)) {
            showDialog("Error", "Password doesn't match.");
            return;
        }

        Util.hideKeyboard(this);
        progressDialog.setMessage("Wait a seconds...");
        progressDialog.show();
        FirebaseDatabase.getInstance()
                .getReference("drivers")
                .child(Util.textOf(phoneEditText))
                .addListenerForSingleValueEvent(valueEventListener);
    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            Driver driver = dataSnapshot.getValue(Driver.class);
            String message = "";
            if(driver != null) {
                progressDialog.cancel();
                message += "Phone number already registered by another user.";
                if(driver.getEmail().equalsIgnoreCase(Util.textOf(emailEditText))) {
                    message += "And email address already in use by another user.";
                }
                showDialog("Error", message);
            }else {
                PhoneAuthProvider.getInstance()
                        .verifyPhoneNumber(Util.textOf(phoneEditText), 60, TimeUnit.SECONDS,
                                Register.this, mChangedCallbacks);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            snack("Error response from remote server. Please retry");
        }
    };

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if(adapterView == statesSpinner) {
            selectedState = adapterView.getSelectedItem().toString();
        }else if(adapterView == aboutSpinner) {
            selectedAboutGoRide = adapterView.getSelectedItem().toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    private void codeSent() {

        progressDialog.cancel();

        mainCardView.setVisibility(View.GONE);
        otpCardView.setVisibility(View.VISIBLE);

    }
    private void signIn(AuthCredential credential) {

        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()) {
                            newDriver = task.getResult()
                                    .getUser();

                            createDriver();
                        }else {
                            showDialog("Error", "Invalid verification code.");
                        }
                    }
                });
    }
    @OnClick(R.id.btn_submit_otp_register) public void onSubmitCodeClick() {

        String code = Util.textOf(codeEditText);
        if(code.isEmpty()) {
            showDialog("Error", "Enter verification code.");
            return;
        }
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationID, code);
        signIn(phoneAuthCredential);
    }
    @OnClick(R.id.btn_resend_code) public void resendCodeClick() {

        PhoneAuthProvider.getInstance()
                .verifyPhoneNumber(Util.textOf(phoneEditText), 60,
                        TimeUnit.SECONDS, this, mChangedCallbacks, resendingToken);

    }
    private void createDriver() {

        final Driver driver = new Driver(Util.textOf(emailEditText), Util.textOf(firstNameEditText),
                Util.textOf(lastNameEditText), Util.textOf(carModelEditText),
                Util.textOf(licensePlateNumberEditText), Util.textOf(passwordEditText),
                Util.textOf(phoneEditText), selectedState, selectedAboutGoRide);

        FirebaseDatabase.getInstance()
                .getReference("drivers")
                .child(driver.getPhoneNumber())
                .setValue(driver)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        progressDialog.cancel();
                        if(task.isSuccessful()) {
                            snack("Welcome to GoRide!");

                            MemoryManager.manager()
                                    .phoneNumber(driver.getPhoneNumber());

                            Intent intent = new Intent(Register.this, HomePage.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }else {
                            snack("Error occurred. Please retry");
                        }
                    }
                });
    }
}
