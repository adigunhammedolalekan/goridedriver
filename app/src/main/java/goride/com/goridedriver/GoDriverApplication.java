package goride.com.goridedriver;

import android.app.Application;

/**
 * Created by root on 11/21/17.
 */

public class GoDriverApplication extends Application {

    private static GoDriverApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    public static GoDriverApplication getApplication() {
        return application;
    }
}
