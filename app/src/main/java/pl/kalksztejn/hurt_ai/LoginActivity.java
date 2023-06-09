package pl.kalksztejn.hurt_ai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import pl.kalksztejn.hurt_ai.service.AuthenticationService;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
   private AuthenticationService authenticationService;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
   private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this.getApplicationContext();
        authenticationService = new AuthenticationService(this);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                login(email, password);
            }
        });
    }
    private void login(String email, String password) {
        if(!email.matches(emailPattern)){
            editTextEmail.setError("Enter email");
        }else if (password.isEmpty() || password.length()<6){
            editTextPassword.setError("Password have to length 6 char");
        }else {
            authenticationService.login(email, password);
            authenticationService.isLoggedIn().observe(this, isLoggedIn -> {
                Intent intent = null;
                if (isLoggedIn) {
                    intent = new Intent(context, SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show();
                    context.startActivity(intent);
                    finish();
                }

            });
        }
    }
}
