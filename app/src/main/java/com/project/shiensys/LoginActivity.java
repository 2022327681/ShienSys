package com.project.shiensys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private ProgressBar progress;
    private TextView errorText, appTitle, emailLabel, passwordLabel;
    private Button loginBtn;
    private LinearLayout card;
    private ConstraintLayout root;

    private ApiService api;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_login);

        session = new SessionManager(this);
        api = RetrofitClient.get(this).create(ApiService.class);

        // UI references
        root = findViewById(R.id.rootLayout);
        card = findViewById(R.id.card);
        appTitle = findViewById(R.id.appTitle);
        errorText = findViewById(R.id.errorText);
        emailLabel = findViewById(R.id.emailLabel);
        passwordLabel = findViewById(R.id.passwordLabel);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        progress = findViewById(R.id.loginProgress);
        loginBtn = findViewById(R.id.loginBtn);

        // Already logged in
        if (session.isLoggedIn()) {
            errorText.setText(
                    "You're signed in as " + session.email() +
                            ". Tap Login to continue or long-press to switch account."
            );
            errorText.setVisibility(View.VISIBLE);

            loginBtn.setOnClickListener(v -> goHome());
            loginBtn.setOnLongClickListener(v -> {
                session.clear();
                Toast.makeText(this, "Switched account", Toast.LENGTH_SHORT).show();
                recreate();
                return true;
            });
            return;
        }

        loginBtn.setOnClickListener(v -> doLogin());
    }

    // =========================
    // LOGIN
    // =========================
    private void doLogin() {
        errorText.setVisibility(View.GONE);

        String email = emailInput.getText().toString().trim();
        String pass  = passwordInput.getText().toString();

        if (email.isEmpty()) {
            showError("Email is required");
            return;
        }

        progress.setVisibility(View.VISIBLE);

        api.login(new LoginRequest(email, pass))
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call,
                                           Response<LoginResponse> res) {
                        progress.setVisibility(View.GONE);

                        if (res.isSuccessful() && res.body() != null && res.body().ok) {
                            LoginResponse lr = res.body();
                            session.clear();
                            session.save(
                                    lr.token,
                                    lr.user_id,
                                    lr.name,
                                    lr.email,
                                    lr.role
                            );
                            goHome();
                        } else {
                            String msg = "Login failed";
                            if (res.body() != null && res.body().message != null)
                                msg = res.body().message;
                            showError(msg);
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        progress.setVisibility(View.GONE);
                        showError("Network error: " + t.getMessage());
                    }
                });
    }

    private void showError(String msg) {
        errorText.setText(msg);
        errorText.setVisibility(View.VISIBLE);
    }

    private void goHome() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
