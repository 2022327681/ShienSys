package com.project.shiensys;

public class TicketRequest {
    String title;
    String description;
    String source;
    String request_id;


    public TicketRequest(String title, String description, String source, String request_id) {
        this.title = title;
        this.description = description;
        this.source = source;
        this.request_id = request_id;
    }
}
