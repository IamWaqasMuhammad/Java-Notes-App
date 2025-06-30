package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        SplashScreen splash = new SplashScreen();
        splash.show(primaryStage);

        Task<Void> sleeper = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(2000); // 2 seconds splash
                return null;
            }
        };


        sleeper.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                MainUI mainUI = new MainUI();
                mainUI.show(primaryStage);
            });
        });

        new Thread(sleeper).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
