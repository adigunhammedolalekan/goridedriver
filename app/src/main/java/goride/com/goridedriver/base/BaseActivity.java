package goride.com.goridedriver.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import butterknife.ButterKnife;

/**
 * Created by root on 11/12/17.
 */

public class BaseActivity extends AppCompatActivity {

    /*
    *
    * state variable, use to check is Activity is still on ForeGround
    * */
    private volatile boolean isOn = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onStart() {
        super.onStart();
        isOn = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isOn = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isOn = true;
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        ButterKnife.bind(this);
    }

    /*
        * Show toast
        * */
    public void toast(String message) {

        /*
        * It doesn't make sense to show toast while app is not visible.
        * */
        if(!isOn)
            return;

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void showDialog(String title, String message) {

        if(!isOn)
            return;

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .create().show();
    }
    /*
    * display snackbar
    * */
    public void snack(String message) {

        if (!isOn) return;

        try {
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                    .show();
        } catch (Exception e) {//who knows?}
        }
    }
}
