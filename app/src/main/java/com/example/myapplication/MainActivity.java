package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private Button btnAuthenticate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAuthenticate = findViewById(R.id.btnAuthenticate);

        if (checkBiometricSupport()) {
            setupBiometricPrompt();
            btnAuthenticate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    authenticateUser();
                }
            });
        } else {
            btnAuthenticate.setEnabled(false);
        }
    }

    // Check if biometric authentication is supported
    private boolean checkBiometricSupport() {
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Toast.makeText(this, "Biometric authentication is available", Toast.LENGTH_SHORT).show();
                return true;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, "No biometric hardware available", Toast.LENGTH_LONG).show();
                return false;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(this, "Biometric hardware unavailable", Toast.LENGTH_LONG).show();
                return false;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this, "No biometric data enrolled", Toast.LENGTH_LONG).show();
                promptUserToEnrollBiometrics();
                return false;
            default:
                return false;
        }
    }

    // Prompt user to enroll biometrics if none are enrolled
    private void promptUserToEnrollBiometrics() {
        Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
        startActivity(enrollIntent);
    }

    // Setup Biometric Prompt
    private void setupBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(MainActivity.this, "Authentication successful!", Toast.LENGTH_SHORT).show();
                // Proceed with app logic (e.g., unlock feature, grant access)
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(MainActivity.this, "Error: " + errString, Toast.LENGTH_SHORT).show();
            }
        });

        // Setup the biometric prompt dialog
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Use your fingerprint or face to unlock")
                .setNegativeButtonText("Cancel")
                .build();
    }

    // Start Biometric Authentication
    private void authenticateUser() {
        biometricPrompt.authenticate(promptInfo);
    }
}
