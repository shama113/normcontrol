package com.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class HistoryScreen {

    public void show(Stage primaryStage, MainApp mainApp) {
        primaryStage.setTitle("История проверок");

        Label title = new Label("История проверок");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Button backBtn = new Button("← Назад");
        styleNavButton(backBtn, "#3498db");
        backBtn.setOnAction(e -> {
            System.out.println("Back button clicked");
            mainApp.showMainScreen();
        });

        HBox topBar = new HBox(backBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10));

        TableView<HistoryEntry> tableView = new TableView<>();
        tableView.setItems(mainApp.getHistoryEntries());

        TableColumn<HistoryEntry, String> timeCol = new TableColumn<>("Время");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        timeCol.setPrefWidth(160);

        TableColumn<HistoryEntry, String> filenameCol = new TableColumn<>("Файл");
        filenameCol.setCellValueFactory(new PropertyValueFactory<>("filename"));
        filenameCol.setPrefWidth(200);

        TableColumn<HistoryEntry, String> sizeCol = new TableColumn<>("Размер");
        sizeCol.setCellValueFactory(cellData -> {
            long size = cellData.getValue().getFileSize();
            return new javafx.beans.property.SimpleStringProperty(formatSize(size));
        });
        sizeCol.setPrefWidth(100);

        TableColumn<HistoryEntry, String> resultCol = new TableColumn<>("Результат");
        resultCol.setCellValueFactory(cellData -> {
            String result = cellData.getValue().getResult();
            String text = result.equals("success") ? "✔ Успешно" : "❌ Ошибки";
            return new javafx.beans.property.SimpleStringProperty(text);
        });
        resultCol.setPrefWidth(120);

        TableColumn<HistoryEntry, String> errorsCol = new TableColumn<>("Ошибки");
        errorsCol.setCellValueFactory(new PropertyValueFactory<>("errors"));
        errorsCol.setPrefWidth(300);

        tableView.getColumns().addAll(timeCol, filenameCol, sizeCol, resultCol, errorsCol);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        tableView.setRowFactory(tv -> new TableRow<HistoryEntry>() {
            @Override
            protected void updateItem(HistoryEntry item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    setStyle(item.isSuccess() ? "-fx-background-color: #d5f4e6;" : "-fx-background-color: #fadbd8;");
                }
            }
        });

        Button clearBtn = new Button("Очистить историю");
        styleNavButton(clearBtn, "#e74c3c");
        clearBtn.setOnAction(e -> {
            mainApp.clearHistory();
            tableView.setItems(mainApp.getHistoryEntries());
        });

        HBox bottomBar = new HBox(clearBtn);
        bottomBar.setAlignment(Pos.CENTER_RIGHT);
        bottomBar.setPadding(new Insets(10));

        BorderPane centerPane = new BorderPane();
        centerPane.setCenter(tableView);
        centerPane.setBottom(bottomBar);
        centerPane.setPadding(new Insets(0, 10, 10, 10));

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(centerPane);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #f5f7fa, #dfe6e9);");

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }

    private void styleNavButton(Button button, String color) {
        String baseStyle = "-fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8 15 8 15; -fx-cursor: hand;";
        button.setStyle(String.format(baseStyle, color));
    }
}
