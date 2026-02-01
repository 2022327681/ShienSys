package com.project.shiensys;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RequestsAdapter
        extends RecyclerView.Adapter<RequestsAdapter.RequestViewHolder> {

    private final Context context;
    private List<RequestItem> items = new ArrayList<>();

    public RequestsAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<RequestItem> newItems) {
        items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_request, parent, false);
        return new RequestViewHolder(v);
    }

    @Override
    public void onBindViewHolder(
            @NonNull RequestViewHolder holder, int position) {

        RequestItem item = items.get(position);

        holder.issueNo.setText(item.ticket_no);
        holder.status.setText(formatStatus(item.status));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TicketDetailActivity.class);
            intent.putExtra("ticket_no", item.ticket_no);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String formatStatus(String status) {
        if (status == null) return "";

        switch (status.toLowerCase()) {
            case "inprogress":
                return "IN PROGRESS";
            case "onhold":
                return "ON HOLD";
            default:
                return status.toUpperCase();
        }
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {

        TextView issueNo, status;

        RequestViewHolder(View itemView) {
            super(itemView);
            issueNo = itemView.findViewById(R.id.issueNumber);
            status  = itemView.findViewById(R.id.issueStatus);
        }
    }
}
