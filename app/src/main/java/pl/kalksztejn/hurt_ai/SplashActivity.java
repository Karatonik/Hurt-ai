package pl.kalksztejn.hurt_ai;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import pl.kalksztejn.hurt_ai.service.AuthenticationService;

public class SplashActivity extends AppCompatActivity {

    private AuthenticationService authenticationService;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        context = this.getApplicationContext();

        authenticationService = new AuthenticationService(this);
        authenticationService.checkLoginStatus();

        authenticationService.isLoggedIn().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoggedIn) {
                Intent intent = null;
                if (isLoggedIn) {
                    intent = new Intent(context, MainActivity.class);
                } else {
                  intent = new Intent(context, RegisterActivity.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                finish();
            }
        });
    }
}
