package goride.com.goridedriver;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import goride.com.goridedriver.base.BaseActivity;
import goride.com.goridedriver.callbacks.IImageCompressionTaskListener;
import goride.com.goridedriver.entities.BankAccount;
import goride.com.goridedriver.entities.Driver;
import goride.com.goridedriver.ui.dialog.AccountNumberDialog;
import goride.com.goridedriver.util.BitmapUtil;
import goride.com.goridedriver.util.ImageCompressionTask;
import goride.com.goridedriver.util.L;
import goride.com.goridedriver.util.MemoryManager;
import goride.com.goridedriver.util.Util;

public class MyProfile extends BaseActivity {

    private Driver currentDriverProfile;
    private String photoPath = "";
    private ProgressDialog progressDialog;

    @BindView(R.id.edt_driver_name_profile)
    EditText driverNameEditText;
    @BindView(R.id.edt_drivers_phone_profile)
    EditText driverPhoneEditText;
    @BindView(R.id.edt_drivers_email_profile)
    EditText driverEmailEditText;
    @BindView(R.id.edt_drivers_car_name_profile)
    EditText driverCarNameEditText;
    @BindView(R.id.edt_drivers_state_profile)
    EditText driverStateEditText;
    @BindView(R.id.edt_drivers_plate_number_profile)
    EditText driverPlateNumberEditText;
    @BindView(R.id.iv_driver_profile)
    CircleImageView circleImageView;


    public static final int PERMISSION_CODE = 11;
    public static final int STORAGE_ACCESS_CODE = 12;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String phone = MemoryManager.manager().phoneNumber();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && !phone.isEmpty()) {
            FirebaseDatabase.getInstance().getReference("drivers")
                    .child(phone.trim())
                    .addListenerForSingleValueEvent(eventListener);
        }else {
            finish();
        }
    }

    private ValueEventListener eventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            progressDialog.cancel();
            if (dataSnapshot != null && dataSnapshot.exists()) {

                currentDriverProfile = dataSnapshot.getValue(Driver.class);
                render();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

            progressDialog.cancel();
            L.fine("Database Error " + databaseError);
        }
    };


    public void bankAccountInfo(View view) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            FirebaseDatabase.getInstance().getReference("drivers_account")
                    .child(user.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            progressDialog.cancel();
                            if (dataSnapshot != null) {
                                BankAccount account = dataSnapshot.getValue(BankAccount.class);
                                AccountNumberDialog dialog = AccountNumberDialog.newInstance(account);
                                dialog.show(getFragmentManager(), "Tag");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    private void render() {

        if (currentDriverProfile == null) { finish(); return; }

        driverNameEditText.setText(currentDriverProfile.getFirstName() + " " + currentDriverProfile.getLastName());
        driverPhoneEditText.setText(MemoryManager.manager().phoneNumber());
        driverEmailEditText.setText(currentDriverProfile.getEmail());
        driverCarNameEditText.setText(currentDriverProfile.getCarModel());
        driverPlateNumberEditText.setText(currentDriverProfile.getLicensePlateNumber());
        driverStateEditText.setText(currentDriverProfile.getDriverState());

        if (!currentDriverProfile.getPhotoURI().isEmpty()) {
            Glide.with(this)
                    .load(currentDriverProfile.getPhotoURI())
                    .into(circleImageView);
        }
    }

    @OnClick(R.id.tv_edit_drivers_profile) public void onEditDriverInfoClick() {
        changeEditableFieldState(true);
    }

    private void changeEditableFieldState(boolean newState) {
        driverNameEditText.setEnabled(newState);
        driverEmailEditText.setEnabled(newState);
        driverPlateNumberEditText.setEnabled(newState);
        driverCarNameEditText.setEnabled(newState);
        driverStateEditText.setEnabled(newState);
    }

    @OnClick(R.id.btn_save_drivers_info_profile) public void onSaveInfoClick() {

        if (Util.empty(driverNameEditText)
                || Util.empty(driverStateEditText)
                || Util.empty(driverCarNameEditText)
                || Util.empty(driverPlateNumberEditText)) {
            toast("Fill all field before committing edit");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(Util.textOf(driverEmailEditText)).matches()) {
            toast("Invalid/Malformed email address.");
            return;
        }

        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("driverState", Util.textOf(driverStateEditText));
        objectMap.put("carModel", Util.textOf(driverCarNameEditText));
        objectMap.put("licensePlateNumber", Util.textOf(driverPlateNumberEditText));
        objectMap.put("email", Util.textOf(driverEmailEditText));

        FirebaseDatabase.getInstance().getReference("drivers")
                .child(MemoryManager.manager().phoneNumber())
                .updateChildren(objectMap);
        toast("Changes saved!");
    }
    @OnClick(R.id.iv_driver_profile) public void onPhotoClick() {
        requestPermission();
    }
    private void requestPermission() {

        int status = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(status == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        }else {

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[]
                        {android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);
            }else {
                ActivityCompat.requestPermissions(this, new String[]
                        {android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        }else {
            showDialog("Permission Denied", "Failed to access external Storage, permission denied.");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == STORAGE_ACCESS_CODE && resultCode == RESULT_OK && data != null) {
            photoPath = BitmapUtil.getPath(this, data.getData());
            Glide.with(this)
                    .load(new File(photoPath))
                    .into(circleImageView);

            List<String> strings = new ArrayList<>();
            strings.add(photoPath);
            GoDriverApplication.getApplication()
                    .getExecutorService().execute(new ImageCompressionTask(this, iImageCompressionTaskListener, strings, PERMISSION_CODE));

        }
    }

    private ProgressDialog dialog;
    private IImageCompressionTaskListener iImageCompressionTaskListener = new IImageCompressionTaskListener() {
        @Override
        public void onCompressed(List<File> file, int id) {

            if (file == null || file.size() <= 0)
                return;

            File photo = file.get(0);
            dialog = new ProgressDialog(MyProfile.this);
            dialog.setMessage("Uploading photo...");
            dialog.setCancelable(false);
            dialog.show();

            Uri photoURI = Uri.fromFile(photo);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                UploadTask uploadTask =
                        FirebaseStorage.getInstance().getReference("photos")
                                .child("drivers")
                                .child(user.getUid())
                                .putFile(photoURI);

                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        dialog.dismiss();
                        if (task.isSuccessful()) {
                            if (currentDriverProfile != null) {

                                Uri uri = task.getResult().getDownloadUrl();
                                if (uri != null) {
                                    currentDriverProfile.setPhotoURI(uri.getPath());
                                    MemoryManager.manager().photo(uri.getPath());

                                    Map<String, Object> map = new HashMap<>();
                                    map.put("photoURI", uri.getPath());
                                    FirebaseDatabase
                                            .getInstance()
                                            .getReference("drivers")
                                            .child(MemoryManager.manager().phoneNumber())
                                            .updateChildren(map);
                                }
                            }
                        }else {
                            toast("Failed to upload photo. Please retry");
                        }
                    }
                });
            }
        }

        @Override
        public void onError(Throwable throwable) {
            L.WTF(throwable);
        }
    };

    private void openGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, STORAGE_ACCESS_CODE);

    }

}
