package pl.kalksztejn.hurt_ai.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import pl.kalksztejn.hurt_ai.SplashActivity;

public class AuthenticationService {

    private static final String PREFS_NAME = "AuthPrefs";
    private static final String PREF_KEY_IS_LOGGED_IN = "isLoggedIn";

    private final FirebaseAuth firebaseAuth;

    private FirebaseUser firebaseUser;
    private final MutableLiveData<Boolean> isLoggedIn;

    private final Context context;

    public AuthenticationService(Context context) {
        this.context = context;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        isLoggedIn = new MutableLiveData<>();
        isLoggedIn.setValue(false);
    }

    public static String getOwner() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        return firebaseUser.getEmail() != null ? firebaseUser.getEmail() : "Guest";
    }

    public LiveData<Boolean> isLoggedIn() {
        return isLoggedIn;
    }

    public void login(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Logowanie udane
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        isLoggedIn.setValue(true);
                        saveLoginStatus(true);
                    } else {
                        // Logowanie nieudane
                        isLoggedIn.setValue(false);
                        saveLoginStatus(false);
                    }
                });
    }

    public void logout() {
        firebaseAuth.signOut();
        isLoggedIn.setValue(false);
        saveLoginStatus(false);
    }

    public String getEmail() {
        return firebaseUser.getEmail() != null ? firebaseUser.getEmail() : "Guest";
    }

    public void checkLoginStatus() {
        boolean savedLoginStatus = getSavedLoginStatus();
        isLoggedIn.setValue(savedLoginStatus);
    }

    private void saveLoginStatus(boolean isLoggedIn) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREF_KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    private boolean getSavedLoginStatus() {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(PREF_KEY_IS_LOGGED_IN, false);
    }

    public void register(String email, String password, ProgressDialog progressDialog) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Rejestracja udana
                        firebaseUser = firebaseAuth.getCurrentUser();
                        isLoggedIn.setValue(true);
                        saveLoginStatus(true);
                        progressDialog.dismiss();
                        Toast.makeText(context.getApplicationContext(), "Register Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context.getApplicationContext(), SplashActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else {
                        // Rejestracja nieudana
                        isLoggedIn.setValue(false);
                        saveLoginStatus(false);
                        progressDialog.dismiss();
                        Toast.makeText(context.getApplicationContext(), "" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
