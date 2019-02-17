package com.hhx7.im.sefu.awesomeloginui;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hhx7.im.App;
import com.hhx7.im.MainActivity;
import com.hhx7.im.data.fixtures.DialogsFixtures;
import com.hhx7.im.data.model.User;
import com.stfalcon.chatkit.sample.R;

public class Login extends AppCompatActivity {
    EditText edtPhoneNumber, edtPassword;
    TextView signupLink;
    Button signin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtPhoneNumber=(EditText)findViewById(R.id.editPhoneNumber);
        edtPassword=(EditText)findViewById(R.id.editPassword);

        signupLink = (TextView) findViewById(R.id.sigmuplink);
        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signupLink = new Intent(getApplicationContext(), Register.class);
                startActivity(signupLink);
            }
        });

        signin = (Button) findViewById(R.id.signin);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent walkThrough = new Intent(getApplicationContext(), WalkThrough.class);
                startActivity(walkThrough);*/
                login();
            }
        });
    }

    private void login(){

        User user=new User(
                DialogsFixtures.getRandomId(),
                edtPhoneNumber.getText().toString().trim(),
                DialogsFixtures.getRandomAvatar(),
                true
        );
        Log.i("zz","addr:"+Settings.Secure.getString(getContentResolver(), "bluetooth_address"));
        String mac = BluetoothAdapter.getDefaultAdapter().getAddress();
        if (mac == null || mac.equals("02:00:00:00:00:00")){
            mac =null;
        }
        user.setBtAddr(mac);

        ((App)getApplication()).setCurrentUser(user);

        Intent startupIntent = new Intent(this, MainActivity.class);
        startActivity(startupIntent);
        finish();

    }

}
