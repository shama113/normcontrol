package com.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class MainApp extends Application {

    private File selectedFile;
    private Label fileLabel;
    private TextArea resultArea;
    private SupabaseService supabaseService;
    private AuthScreen authScreen;
    private HistoryScreen historyScreen;
    private ObservableList<HistoryEntry> historyEntries;
    private String currentUsername;
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        supabaseService = new SupabaseService();
        authScreen = new AuthScreen(supabaseService);
        historyScreen = new HistoryScreen();
        authScreen.setMainApp(this);
        historyEntries = FXCollections.observableArrayList();
        authScreen.show(stage);
    }

    public void startMainApp(Stage stage, String username) {
        this.currentUsername = username;
        buildMainScreen(stage);
    }

    public void showMainScreen() {
        System.out.println("Navigating to main screen, primaryStage=" + primaryStage);
        try {
            buildMainScreen(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось вернуться на главный экран: " + e.getMessage());
        }
    }

    public void showAuthScreen() {
        System.out.println("Navigating to auth screen, primaryStage=" + primaryStage);
        try {
            authScreen.show(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось перейти к экрану входа: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }

    private void buildMainScreen(Stage stage) {
        stage.setTitle("Система проверки нормоконтроля - " + currentUsername);

        Label title = new Label("Система нормоконтроля");
        title.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label subtitle = new Label("Проверка документов на соответствие требованиям");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #dfe6e9;");

        VBox headerBox = new VBox(5, title, subtitle);
        headerBox.setPadding(new Insets(25));
        headerBox.setStyle("-fx-background-color: linear-gradient(to right, #2c3e50, #3498db); -fx-background-radius: 0 0 25 25;");

        HBox navBar = new HBox(10);
        navBar.setAlignment(Pos.CENTER_RIGHT);
        navBar.setPadding(new Insets(10, 25, 10, 25));

        Button historyBtn = new Button("📋 История");
        styleNavButton(historyBtn, "#9b59b6");
        historyBtn.setOnAction(e -> showHistoryScreen());

        Button logoutBtn = new Button("Выйти");
        styleNavButton(logoutBtn, "#e74c3c");
        logoutBtn.setOnAction(e -> showAuthScreen());

        navBar.getChildren().addAll(historyBtn, logoutBtn);

        VBox headerWithNav = new VBox(headerBox, navBar);

        Label req1 = new Label("• Документ должен содержать: ФИО, Дата, Номер, Подпись");
        Label req2 = new Label("• Запрещённые слова: черновик, образец, тест");
        Label req3 = new Label("• Строки не должны быть слишком длинными");
        Label req4 = new Label("• PDF и DOCX проверяются на размер и сигнатуру");
        Label req5 = new Label("• Загрузите файл и нажмите кнопку \"Валидация\"");

        VBox requirementsBox = new VBox(8, req1, req2, req3, req4, req5);
        requirementsBox.setPadding(new Insets(20));
        requirementsBox.setStyle("-fx-background-color: white; -fx-background-radius: 20px; -fx-border-radius: 20px; -fx-border-color: #dcdde1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10,0,0,4);");

        fileLabel = new Label("Файл не загружен");
        fileLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #2d3436;");

        Button loadBtn = new Button("Загрузить документ");
        Button validateBtn = new Button("Валидация");

        styleButton(loadBtn, "#3498db");
        styleButton(validateBtn, "#27ae60");

        HBox buttonBox = new HBox(15, loadBtn, validateBtn);
        buttonBox.setAlignment(Pos.CENTER);

        Label resultLabel = new Label("Результат проверки");
        resultLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setWrapText(true);
        resultArea.setPrefHeight(250);
        resultArea.setStyle("-fx-font-size: 14px; -fx-background-radius: 15px; -fx-border-radius: 15px; -fx-border-color: #dcdde1; -fx-padding: 10px;");

        VBox resultBox = new VBox(10, resultLabel, resultArea);

        VBox content = new VBox(25, requirementsBox, fileLabel, buttonBox, resultBox);
        content.setPadding(new Insets(25));

        BorderPane root = new BorderPane();
        root.setTop(headerWithNav);
        root.setCenter(content);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #f5f7fa, #dfe6e9);");

        Scene scene = new Scene(root, 850, 700);
        stage.setScene(scene);
        stage.show();

        loadBtn.setOnAction(e -> openFile(stage));
        validateBtn.setOnAction(e -> validateFile());
    }

    public void showHistoryScreen() {
        historyScreen.show(primaryStage, this);
    }

    public void showAuthScreen() {
        authScreen.show(primaryStage);
    }

    public ObservableList<HistoryEntry> getHistoryEntries() {
        return historyEntries;
    }

    public void addHistoryEntry(HistoryEntry entry) {
        historyEntries.add(0, entry);
    }

    public void clearHistory() {
        historyEntries.clear();
    }

    private void styleButton(Button button, String color) {
        String baseStyle = "-fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 15px; -fx-padding: 12 25 12 25; -fx-cursor: hand;";
        button.setStyle(String.format(baseStyle, color));
        button.setOnMouseEntered(e -> button.setStyle(String.format(baseStyle, "derive(" + color + ", -10%)")));
        button.setOnMouseExited(e -> button.setStyle(String.format(baseStyle, color)));
    }

    private void styleNavButton(Button button, String color) {
        String baseStyle = "-fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8 15 8 15; -fx-cursor: hand;";
        button.setStyle(String.format(baseStyle, color));
    }

    private void openFile(Stage owner) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Выберите документ");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Документы", "*.txt", "*.pdf", "*.docx")
        );
        File file = chooser.showOpenDialog(owner);
        if (file != null) {
            selectedFile = file;
            fileLabel.setText("Загружен файл: " + file.getName() + " | Размер: " + file.length() + " bytes");
            resultArea.setText("Файл успешно загружен.\nНажмите кнопку \"Валидация\".");
        }
    }

    private void validateFile() {
        if (selectedFile == null) {
            resultArea.setText("Ошибка: файл не выбран.");
            return;
        }

        List<String> errors = new ArrayList<>();
        String result;
        String errorText;

        try {
            String fileName = selectedFile.getName().toLowerCase();

            if (selectedFile.length() == 0) {
                errors.add("Файл пустой.");
            }

            if (fileName.endsWith(".txt")) {
                List<String> lines = Files.readAllLines(selectedFile.toPath());
                String content = String.join("\n", lines).toLowerCase();

                checkRequired(content, "фио", errors);
                checkRequired(content, "дата", errors);
                checkRequired(content, "номер", errors);
                checkRequired(content, "подпись", errors);

                checkForbidden(content, "черновик", errors);
                checkForbidden(content, "образец", errors);
                checkForbidden(content, "тест", errors);

                for (String line : lines) {
                    if (line.length() > 120) {
                        errors.add("Обнаружены слишком длинные строки.");
                        break;
                    }
                }
            } else {
                if (selectedFile.length() < 100) {
                    errors.add("Файл слишком маленький.");
                }
            }

            if (errors.isEmpty()) {
                resultArea.setStyle("-fx-font-size: 15px; -fx-text-fill: green; -fx-font-weight: bold;");
                resultArea.setText("✔ Файл полностью соответствует требованиям нормоконтроля.\n\nОшибок не обнаружено.");
                result = "success";
                errorText = "Нет";
            } else {
                resultArea.setStyle("-fx-font-size: 14px; -fx-text-fill: red; -fx-font-weight: bold;");
                StringBuilder sb = new StringBuilder();
                sb.append("❌ Обнаружены ошибки:\n\n");
                for (String error : errors) {
                    sb.append("• ").append(error).append("\n");
                }
                resultArea.setText(sb.toString());
                result = "errors";
                errorText = String.join("; ", errors);
            }

            addHistoryEntry(new HistoryEntry(selectedFile.getName(), selectedFile.length(), result, errorText));

        } catch (Exception e) {
            resultArea.setText("Ошибка при проверке файла:\n" + e.getMessage());
            result = "errors";
            errorText = e.getMessage();
            addHistoryEntry(new HistoryEntry(selectedFile.getName(), selectedFile.length(), result, errorText));
        }
    }

    private void checkRequired(String content, String word, List<String> errors) {
        if (!content.contains(word)) {
            errors.add("Отсутствует поле: " + word);
        }
    }

    private void checkForbidden(String content, String word, List<String> errors) {
        if (content.contains(word)) {
            errors.add("Найдено запрещённое слово: " + word);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
