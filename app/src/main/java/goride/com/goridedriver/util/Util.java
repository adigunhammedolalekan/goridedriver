package goride.com.goridedriver.util;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by root on 11/12/17.
 */

public class Util {

    public static String textOf(EditText editText) {
        return editText.getText().toString().trim();
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
}
