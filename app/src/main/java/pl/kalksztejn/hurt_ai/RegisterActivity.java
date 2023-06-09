package pl.kalksztejn.hurt_ai;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import pl.kalksztejn.hurt_ai.service.AuthenticationService;

public class RegisterActivity extends AppCompatActivity {

    TextView infoTextView;
    EditText inputEmail, inputPassword, inputCPassword;
    Button btnRegister;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private AuthenticationService authenticationService;

    ProgressDialog progressDialog;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authenticationService = new AuthenticationService(this);
        setContentView(R.layout.activity_register);
        context =this.getApplicationContext();

        infoTextView =findViewById(R.id.textViewInfo);
        inputEmail = findViewById(R.id.editTextEmail);
        inputPassword = findViewById(R.id.editTextPassword);
        inputCPassword =findViewById(R.id.editTextCPassword);
        btnRegister = findViewById(R.id.buttonRegister);
        progressDialog = new ProgressDialog(this);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerforAuth();
            }
        });

        infoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin();
            }
        });
    }

    private void PerforAuth() {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String cPassword = inputCPassword.getText().toString();

        if(!email.matches(emailPattern)){
            inputEmail.setError("Enter email");
        }else if (password.isEmpty() || password.length()<6){
            inputPassword.setError("Password have to length 6 char");
        } else if (!password.equals(cPassword)){
            inputCPassword.setError("Not equal");
        }else{
            progressDialog.setMessage("Pleas wait ...");
            progressDialog.setTitle("Register");
            authenticationService.register(email,password,progressDialog);
        }


    }

    private void goToLogin(){
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }




}
