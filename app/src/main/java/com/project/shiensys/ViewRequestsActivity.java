package com.project.shiensys;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewRequestsActivity extends AppCompatActivity {

    private Spinner statusSpinner;
    private RecyclerView recyclerView;
    private RequestsAdapter requestsAdapter;
    private ApiService api;
    private Button backButton;

    private final String[] statuses = {
            "All",
            "Open",
            "In Progress",
            "On Hold",
            "Closed"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requests);

        api = RetrofitClient.get(this).create(ApiService.class);

        statusSpinner = findViewById(R.id.statusFilterSpinner);
        recyclerView  = findViewById(R.id.requestsRecyclerView);
        backButton    = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> finish());

        setupRecycler();
        setupSpinner();

        loadRequests("All");
    }

    private void setupSpinner() {
        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(this, R.layout.spinner_item, statuses);

        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        statusSpinner.setAdapter(spinnerAdapter);

        statusSpinner.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            android.widget.AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        String apiStatus = mapStatus(statuses[position]);
                        loadRequests(apiStatus);
                    }

                    @Override
                    public void onNothingSelected(android.widget.AdapterView<?> parent) {

                    }
                }
        );
    }

    private void setupRecycler() {
        requestsAdapter = new RequestsAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(requestsAdapter);
    }

    private void loadRequests(String status) {
        api.getRequests(status)
                .enqueue(new Callback<RequestsResponse>() {
                    @Override
                    public void onResponse(
                            Call<RequestsResponse> call,
                            Response<RequestsResponse> res) {

                        if (res.isSuccessful()
                                && res.body() != null
                                && res.body().ok
                                && res.body().data != null) {

                            requestsAdapter.setItems(res.body().data);

                        } else {
                            requestsAdapter.setItems(null);
                            Toast.makeText(
                                    ViewRequestsActivity.this,
                                    "No requests found",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<RequestsResponse> call, Throwable t) {
                        Toast.makeText(
                                ViewRequestsActivity.this,
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private String mapStatus(String uiStatus) {
        switch (uiStatus) {
            case "Open":
                return "open";
            case "In Progress":
                return "inprogress";
            case "On Hold":
                return "onhold";
            case "Closed":
                return "closed";
            default:
                return "All";
        }
    }
}
