package org.example;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class SplashScreen {

    public void show(Stage stage) {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #5a9bd4;");  // solid blue color

        Label title = new Label("Notes App");
        title.setFont(Font.font("Segoe UI", 36));
        title.setTextFill(Color.WHITE);

        root.getChildren().add(title);
        StackPane.setAlignment(title, Pos.CENTER);

        Scene scene = new Scene(root, 400, 250);
        stage.setScene(scene);
        stage.setTitle("Splash Screen");
        stage.show();
    }
}
