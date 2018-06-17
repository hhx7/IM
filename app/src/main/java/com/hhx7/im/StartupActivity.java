package com.hhx7.im;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.hhx7.im.sefu.awesomeloginui.Login;

public class StartupActivity extends Activity {

    private static final int AUTHENTICATION_REQUEST_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isLoggedIn()) {
            Intent startupIntent = new Intent(this, MainActivity.class);
            startupIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(startupIntent);
            finish();
        } else {
            Intent startupIntent = new Intent(this, Login.class);
            startActivity(startupIntent);
            finish();
        }


    }

    private boolean isLoggedIn() {
        // Check SharedPreferences or wherever you store login information
        return ((App)getApplication()).getCurrentUser()!=null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTHENTICATION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Intent startupIntent = new Intent(this, MainActivity.class);
            startupIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(startupIntent);
        }

        finish();
    }
}
