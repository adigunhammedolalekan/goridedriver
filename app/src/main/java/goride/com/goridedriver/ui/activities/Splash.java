package goride.com.goridedriver.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import goride.com.goridedriver.FirstPage;
import goride.com.goridedriver.HomePage.HomePage;
import goride.com.goridedriver.R;
import goride.com.goridedriver.base.BaseActivity;
import goride.com.goridedriver.util.L;

/**
 * Created by Lekan Adigun on 12/13/2017.
 */

public class Splash extends BaseActivity {

    private Handler handler = new Handler();

    private void launch(final Class<?> c) {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(Splash.this, c);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }, 1000);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_splash);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if(user != null) {
            L.fine("F User ==> " + user.getUid());
            launch(HomePage.class);
        }else {
            launch(FirstPage.class);
        }
    }
}
