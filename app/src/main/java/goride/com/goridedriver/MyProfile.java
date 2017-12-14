package goride.com.goridedriver;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MyProfile extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);


        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    public void bankAccountInfo(View view) {


        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_bank_account_info);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button saveBtn = (Button) dialog.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder confirmSave = new AlertDialog.Builder(MyProfile.this);

// Setting Dialog Title
                confirmSave.setTitle("Confirm Save");

// Setting Dialog Message
                confirmSave.setMessage("You cannot edit this information until after 7 days, are you sure you want to continue?");

// Setting Icon to Dialog
                confirmSave.setIcon(R.drawable.goride_logo);

// Setting Positive "Yes" Btn
                confirmSave.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog
                                Toast.makeText(getApplicationContext(),
                                        "Yes Button Clicked", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });

// Setting Negative "NO" Btn
                confirmSave.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.cancel();
                            }
                        });

// Showing Alert Dialog
                confirmSave.show();

            }
        });


        Button cancelBtn = (Button) dialog.findViewById(R.id.cancelBtn);


        cancelBtn.setOnClickListener(new View.OnClickListener() {


                                         @Override
                                         public void onClick(View view) {

                                             dialog.cancel();
                                         }


                                     }


        );
    }
}
