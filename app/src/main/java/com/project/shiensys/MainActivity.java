package com.project.shiensys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ApiService api;
    private SessionManager session;

    private ProgressBar progress;
    private Spinner issueSpinner;
    private TextView issueDescription, userLabel, appTitle, descText, issueLabel;
    private Button helpButton, logoutButton, viewRequestsButton;
    private LinearLayout card;
    private ConstraintLayout rootLayout;

    private ArrayAdapter<String> adapter;
    private final Map<String, String> issueDescriptions = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(this);

        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        api = RetrofitClient.get(this).create(ApiService.class);

        rootLayout = findViewById(R.id.rootLayout);
        card = findViewById(R.id.card);
        helpButton = findViewById(R.id.helpButton);
        viewRequestsButton = findViewById(R.id.viewRequestsButton);
        logoutButton = findViewById(R.id.logoutButton);
        userLabel = findViewById(R.id.userLabel);
        progress = findViewById(R.id.progress);
        issueSpinner = findViewById(R.id.issueSpinner);
        issueDescription = findViewById(R.id.issueDescription);
        appTitle = findViewById(R.id.appTitle);
        descText = findViewById(R.id.descText);
        issueLabel = findViewById(R.id.issueLabel);

        userLabel.setText("Signed in as " + session.name());

        setupSpinner();

        helpButton.setOnClickListener(v -> sendHelp());

        viewRequestsButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ViewRequestsActivity.class));
        });

        logoutButton.setOnClickListener(v -> doLogout());
    }

    private void setupSpinner() {
        issueDescriptions.clear();
        issueDescriptions.put("Printer Issue", "Printer not responding, paper jam, or low toner.");
        issueDescriptions.put("Hardware Issue", "Problems with PC, monitor, keyboard, or devices.");
        issueDescriptions.put("Software Issue", "App not working, crashes, or installation errors.");
        issueDescriptions.put("Internet Issue", "Wi-Fi disconnected, slow, or no connection.");
        issueDescriptions.put("Email Issue", "Cannot send/receive emails or login errors.");
        issueDescriptions.put("Account Access", "Password reset or unable to login to company systems.");
        issueDescriptions.put("Other", "Any other IT-related request not listed above.");

        adapter = new ArrayAdapter<String>(
                this,
                R.layout.spinner_item,
                new ArrayList<>(issueDescriptions.keySet())
        ) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                view.setBackgroundResource(R.color.card);
                return view;
            }
        };

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        issueSpinner.setAdapter(adapter);

        issueSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String selected = parent.getItemAtPosition(pos).toString();
                issueDescription.setText(issueDescriptions.get(selected));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void sendHelp() {
        if (issueSpinner == null) return;

        String issueTitle = issueSpinner.getSelectedItem().toString();
        String issueDesc = issueDescriptions.get(issueTitle);
        String reqId = UUID.randomUUID().toString();

        TicketRequest body = new TicketRequest(issueTitle, issueDesc, "mobile", reqId);

        if (progress != null) progress.setVisibility(View.VISIBLE);

        api.createTicket(body).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> res) {
                if (progress != null) progress.setVisibility(View.GONE);

                if (res.isSuccessful() && res.body() != null && res.body().ok) {
                    Toast.makeText(
                            MainActivity.this,
                            "Ticket created: " + res.body().ticket_no,
                            Toast.LENGTH_LONG
                    ).show();
                } else {
                    Toast.makeText(
                            MainActivity.this,
                            "Failed to create ticket",
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (progress != null) progress.setVisibility(View.GONE);
                Toast.makeText(
                        MainActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void doLogout() {
        session.clear();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
