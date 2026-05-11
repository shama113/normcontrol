package com.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class AuthScreen {

    private final SupabaseService supabaseService;
    private MainApp mainApp;

    public AuthScreen(SupabaseService supabaseService) {
        this.supabaseService = supabaseService;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void show(Stage primaryStage) {
        primaryStage.setTitle("Вход / Регистрация");

        // Toggle between Login and Register
        ToggleGroup toggleGroup = new ToggleGroup();

        RadioButton loginRadio = new RadioButton("Вход");
        loginRadio.setToggleGroup(toggleGroup);
        loginRadio.setSelected(true);

        RadioButton registerRadio = new RadioButton("Регистрация");
        registerRadio.setToggleGroup(toggleGroup);

        HBox toggleBox = new HBox(10, loginRadio, registerRadio);
        toggleBox.setAlignment(Pos.CENTER);

        // Form fields
        Label userLabel = new Label("Имя пользователя:");
        TextField userField = new TextField();
        userField.setPromptText("Введите имя пользователя");

        Label passLabel = new Label("Пароль:");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Введите пароль");

        VBox formBox = new VBox(10, userLabel, userField, passLabel, passField);
        formBox.setAlignment(Pos.CENTER_LEFT);

        // Buttons
        Button submitBtn = new Button("Войти");
        submitBtn.setStyle(
                "-fx-background-color: #3498db;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10px;" +
                "-fx-padding: 10 20 10 20;"
        );

        Button switchBtn = new Button("Перейти к регистрации");
        switchBtn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #3498db;" +
                "-fx-font-size: 13px;" +
                "-fx-underline: true;" +
                "-fx-cursor: hand;"
        );

        // Status label
        Label statusLabel = new Label("");
        statusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 13px;");

        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #f5f7fa, #dfe6e9);");

        VBox card = new VBox(15);
        card.setPadding(new Insets(30));
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(350);
        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 20px;" +
                "-fx-border-radius: 20px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15,0,0,5);"
        );

        Label title = new Label("Система нормоконтроля");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label subtitle = new Label("Проверка документов на соответствие требованиям");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #7f8c8d;");

        card.getChildren().addAll(title, subtitle, new Separator(), toggleBox, formBox, submitBtn, switchBtn, statusLabel);
        root.getChildren().add(card);

        Scene scene = new Scene(root, 450, 550);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Toggle logic
        loginRadio.setOnAction(e -> {
            submitBtn.setText("Войти");
            switchBtn.setText("Перейти к регистрации");
            statusLabel.setText("");
        });

        registerRadio.setOnAction(e -> {
            submitBtn.setText("Зарегистрироваться");
            switchBtn.setText("Перейти к входу");
            statusLabel.setText("");
        });

        // Switch button logic
        switchBtn.setOnAction(e -> {
            if (loginRadio.isSelected()) {
                registerRadio.setSelected(true);
                submitBtn.setText("Зарегистрироваться");
                switchBtn.setText("Перейти к входу");
            } else {
                loginRadio.setSelected(true);
                submitBtn.setText("Войти");
                switchBtn.setText("Перейти к регистрации");
            }
            statusLabel.setText("");
        });

        // Submit logic
        submitBtn.setOnAction(e -> {
            String username = userField.getText().trim();
            String password = passField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Заполните все поля");
                return;
            }

            if (password.length() < 6) {
                statusLabel.setText("Пароль минимум 6 символов");
                return;
            }

            boolean success;
            if (loginRadio.isSelected()) {
                success = supabaseService.login(username, password);
                if (success) {
                    openMainApp(primaryStage, username);
                } else {
                    statusLabel.setText("Неверное имя пользователя или пароль");
                }
            } else {
                success = supabaseService.register(username, password);
                if (success) {
                    statusLabel.setText("Регистрация успешна! Теперь войдите");
                    loginRadio.setSelected(true);
                    submitBtn.setText("Войти");
                    switchBtn.setText("Перейти к регистрации");
                } else {
                    statusLabel.setText("Ошибка регистрации. Попробуйте другое имя");
                }
            }
        });

        // Enter key to submit
        passField.setOnAction(e -> submitBtn.fire());
    }

    private void openMainApp(Stage stage, String username) {
        mainApp.startMainApp(stage, username);
    }
}
