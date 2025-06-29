package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        SplashScreen splash = new SplashScreen();
        splash.show(primaryStage);  // ✅ Fix: use .show() instead of .start()

        // Delay for 2 seconds, then show MainUI
        Task<Void> sleeper = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(2000); // 2 seconds splash
                return null;
            }
        };

        System.out.println("SPlashSCreen shown");

        sleeper.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                MainUI mainUI = new MainUI();
                mainUI.show(primaryStage);  // ✅ This will now work after splash
            });
        });

        new Thread(sleeper).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
