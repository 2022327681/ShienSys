package com.project.shiensys;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketDetailActivity extends AppCompatActivity {

    private TextView
            titleText,
            statusText,
            descText,
            solutionText,
            engineerText,
            createdText,
            updatedText,
            closedText;

    private Button backButton;
    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);

        backButton   = findViewById(R.id.backButton);

        titleText    = findViewById(R.id.ticketTitle);
        statusText   = findViewById(R.id.ticketStatus);
        descText     = findViewById(R.id.ticketDescription);
        solutionText = findViewById(R.id.ticketSolution);

        engineerText = findViewById(R.id.ticketEngineer);
        createdText  = findViewById(R.id.ticketCreatedAt);
        updatedText  = findViewById(R.id.ticketUpdatedAt);
        closedText   = findViewById(R.id.ticketClosedAt);

        backButton.setOnClickListener(v -> finish());

        api = RetrofitClient.get(this).create(ApiService.class);

        String ticketNo = getIntent().getStringExtra("ticket_no");
        if (ticketNo == null || ticketNo.trim().isEmpty()) {
            finish();
            return;
        }

        loadDetails(ticketNo);
    }

    private void loadDetails(String ticketNo) {
        api.getTicketDetail(ticketNo)
                .enqueue(new Callback<TicketDetailResponse>() {
                    @Override
                    public void onResponse(Call<TicketDetailResponse> call,
                                           Response<TicketDetailResponse> res) {

                        if (!res.isSuccessful()
                                || res.body() == null
                                || !res.body().ok
                                || res.body().data == null) {

                            Toast.makeText(
                                    TicketDetailActivity.this,
                                    "Failed to load ticket details",
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }

                        TicketDetail t = res.body().data;

                        titleText.setText(
                                t.title != null ? t.title : "-"
                        );

                        statusText.setText(
                                formatStatus(t.status)
                        );

                        descText.setText(
                                t.description != null
                                        ? t.description
                                        : "No description"
                        );

                        solutionText.setText(
                                t.solution != null && !t.solution.isEmpty()
                                        ? t.solution
                                        : "No solution yet"
                        );

                        engineerText.setText(
                                "Assigned to: " +
                                        (t.assigned_engineer != null
                                                ? t.assigned_engineer
                                                : "Unassigned")
                        );

                        createdText.setText(
                                "Created: " +
                                        (t.created_at != null
                                                ? t.created_at
                                                : "-")
                        );

                        updatedText.setText(
                                "Last updated: " +
                                        (t.updated_at != null
                                                ? t.updated_at
                                                : "-")
                        );

                        closedText.setText(
                                t.closed_at != null
                                        ? "Closed at: " + t.closed_at
                                        : "Not closed"
                        );
                    }

                    @Override
                    public void onFailure(Call<TicketDetailResponse> call,
                                          Throwable t) {
                        Toast.makeText(
                                TicketDetailActivity.this,
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private String formatStatus(String status) {
        if (status == null) return "-";

        switch (status.toLowerCase()) {
            case "inprogress":
                return "IN PROGRESS";
            case "onhold":
                return "ON HOLD";
            default:
                return status.toUpperCase();
        }
    }
}
