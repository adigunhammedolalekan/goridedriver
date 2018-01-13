package goride.com.goridedriver.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by root on 11/12/17.
 */

public class Util {

    public static String textOf(EditText editText) {
        return editText.getText().toString().trim();
    }

    public static boolean empty(EditText editText) {
        return textOf(editText).isEmpty();
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        try {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }catch (Exception e) {
            //if there is NPE
        }
    }
    public static File getCompressedFile(String path, Context context) throws IOException {
        if(context == null)
            return new File(path);
        if(path == null) {
            return null;
        }
        File fresh = new File(path);
        long size = fresh.length() / 1000; //To KB

        //doesn't need compression. Use as it is.
        if(Math.abs(size) <= 400) {
            return fresh;
        }


        File cacheDIR = context.getExternalCacheDir();
        if(cacheDIR == null)
            cacheDIR = context.getCacheDir();
        String tempCacheDIR = cacheDIR.getAbsolutePath() + "/DevAfrica/TempC";
        File tempCacheFile = new File(tempCacheDIR);
        if(!tempCacheFile.exists())
            tempCacheFile.mkdirs();

        Bitmap bitmap = BitmapUtil.decodeImageFromFiles(path, 300, 300);
        if(bitmap == null) {
            //are you kidding me?
            return null;
        }
        File mainFile = new File(tempCacheFile, String.valueOf(System.currentTimeMillis()) + ".jpg");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);

        FileOutputStream fileOutputStream = new FileOutputStream(mainFile);
        fileOutputStream.write(outputStream.toByteArray());
        fileOutputStream.flush();

        fileOutputStream.close();

        return mainFile;
    }
}
