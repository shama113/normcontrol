package com.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HistoryEntry {
    private String filename;
    private long fileSize;
    private String result; // "errors" or "success"
    private String errors;
    private LocalDateTime timestamp;

    public HistoryEntry(String filename, long fileSize, String result, String errors) {
        this.filename = filename;
        this.fileSize = fileSize;
        this.result = result;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }

    public String getFilename() {
        return filename;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getResult() {
        return result;
    }

    public String getErrors() {
        return errors;
    }

    public String getTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return timestamp.format(formatter);
    }

    public boolean isSuccess() {
        return result.equals("success");
    }
}
