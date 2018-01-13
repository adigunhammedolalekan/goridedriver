package goride.com.goridedriver;

import android.app.Application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by root on 11/21/17.
 */

public class GoDriverApplication extends Application {

    private static GoDriverApplication application;
    private ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    public static GoDriverApplication getApplication() {
        return application;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }
}
