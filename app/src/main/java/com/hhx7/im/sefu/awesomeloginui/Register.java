package com.hhx7.im.sefu.awesomeloginui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hhx7.im.sefu.awesomeloginui.dialog.CustomDialogFragment;
import com.stfalcon.chatkit.sample.R;

public class Register extends AppCompatActivity {
    ImageView closeImage;
    Button register;
    TextView signgInLink;

    EditText edtPhoneNumber, edtUsername, edtPassword, edtConfirmPassword,edtVerificationCode;
    String fullname, email, password, dob,veriCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtPhoneNumber = (EditText) findViewById(R.id.editTexPhoneNumber);
        edtUsername = (EditText) findViewById(R.id.editTextEmail);
        edtPassword = (EditText) findViewById(R.id.editTextPassword);
        edtConfirmPassword = (EditText) findViewById(R.id.editTextDOB);

        edtVerificationCode = (EditText) findViewById(R.id.editVerificationCOde);
        signgInLink=(TextView)findViewById(R.id.sigmuplink) ;
        signgInLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent closeRegister = new Intent(getApplicationContext(), Login.class);
                startActivity(closeRegister);
            }
        });
        closeImage = (ImageView) findViewById(R.id.close);
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent closeRegister = new Intent(getApplicationContext(), Login.class);
                startActivity(closeRegister);
            }
        });


        register = (Button) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
                if (validate() == true) {
                    Toast.makeText(Register.this, "Enter Login Here", Toast.LENGTH_SHORT).show();
                    showCustomDialog();
                }
            }
        });
    }

    private void showCustomDialog() {
        CustomDialogFragment customDialog = new CustomDialogFragment();
        customDialog.show(getSupportFragmentManager(), "CustomDialogFragment");
    }

    public boolean validate() {
        boolean valid = true;

        fullname = edtPhoneNumber.getText().toString().trim();
        email = edtUsername.getText().toString().trim();
        password = edtPassword.getText().toString().trim();
        dob = edtConfirmPassword.getText().toString().trim();


        if (fullname.isEmpty() || fullname.length() < 5) {
            edtPhoneNumber.setError("at least 5 characters");
            valid = false;
        } else {
            edtPhoneNumber.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtUsername.setError("enter a valid email address");
            valid = false;
        } else {
            edtUsername.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            edtPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            edtPassword.setError(null);
        }


        return valid;
    }
}
