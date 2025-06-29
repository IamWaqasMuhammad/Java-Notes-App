package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.geometry.Side;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.example.model.Note;
import org.example.service.MongoService;

import java.util.List;

public class MainUI {

    private final MongoService mongoService = new MongoService();
    private ListView<Note> listView;

    public void show(Stage primaryStage) {
        primaryStage.setTitle("Notes App");

        // App Bar
        Label titleLabel = new Label("Notes App");
        titleLabel.setFont(Font.font("Segoe UI", 20));
        titleLabel.setTextFill(Color.WHITE);

        Button fab = new Button("+");
        fab.setId("fab");
        fab.setOnAction(e -> showAddNoteDialog());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox appBar = new HBox(titleLabel, spacer, fab);
        appBar.setPadding(new Insets(10));
        appBar.setStyle("-fx-background-color: #2196f3;");
        appBar.setAlignment(Pos.CENTER_LEFT);

        // ListView setup
        listView = new ListView<>();
        refreshNotes();

        // Custom cell factory with 3-dot menu button
        listView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Note> call(ListView<Note> param) {
                return new NoteListCell();
            }
        });

        VBox root = new VBox(appBar, listView);
        root.setSpacing(5);
        root.setStyle("-fx-background-color: #e3f2fd;");

        Scene scene = new Scene(root, 600, 500);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(e -> mongoService.close());
    }

    private void refreshNotes() {
        List<Note> notes = mongoService.getAllNotes();
        listView.getItems().setAll(notes);
    }

    private void showAddNoteDialog() {
        Dialog<Note> dialog = new Dialog<>();
        dialog.setTitle("Add New Note");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        TextField titleField = new TextField();
        titleField.setPromptText("Title");

        TextArea contentArea = new TextArea();
        contentArea.setPromptText("Content");
        contentArea.setWrapText(true);

        VBox content = new VBox(10, titleField, contentArea);
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        Button addButton = (Button) dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);

        titleField.textProperty().addListener((obs, oldVal, newVal) -> {
            addButton.setDisable(newVal.trim().isEmpty());
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new Note(titleField.getText().trim(), contentArea.getText().trim());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(note -> {
            mongoService.addNote(note);
            refreshNotes();
        });
    }

    // Custom cell with 3-dot menu button
    private class NoteListCell extends ListCell<Note> {
        private final Label titleLabel = new Label();
        private final Button menuButton = new Button("⋮");
        private final HBox container = new HBox();

        public NoteListCell() {
            super();
            titleLabel.setFont(Font.font("Segoe UI", 16));
            HBox.setHgrow(titleLabel, Priority.ALWAYS);

            menuButton.setId("menu-button");
            menuButton.setFocusTraversable(false);

            menuButton.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && !isEmpty()) {
                    showContextMenu();
                }
            });

            container.setAlignment(Pos.CENTER_LEFT);
            container.setSpacing(10);
            container.getChildren().addAll(titleLabel, menuButton);
            container.setPadding(new Insets(5, 10, 5, 10));
        }

        private void showContextMenu() {
            ContextMenu menu = new ContextMenu();

            MenuItem updateItem = new MenuItem("Update");
            updateItem.setOnAction(e -> showUpdateDialog(getItem()));

            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(e -> {
                mongoService.deleteNoteById(getItem().getId());
                refreshNotes();
            });

            MenuItem exportItem = new MenuItem("Export to PDF");
            exportItem.setOnAction(e -> {
                PDFExporter.exportNoteAsPDF(getItem());
            });

            menu.getItems().addAll(updateItem, deleteItem, exportItem);
            menu.show(menuButton, Side.BOTTOM, 0, 0);
        }

        private void showUpdateDialog(Note note) {
            Dialog<Note> dialog = new Dialog<>();
            dialog.setTitle("Update Note");

            ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

            TextField titleField = new TextField(note.getTitle());
            TextArea contentArea = new TextArea(note.getContent());
            contentArea.setWrapText(true);

            VBox content = new VBox(10, titleField, contentArea);
            content.setPadding(new Insets(10));
            dialog.getDialogPane().setContent(content);

            Button updateButton = (Button) dialog.getDialogPane().lookupButton(updateButtonType);
            updateButton.setDisable(titleField.getText().trim().isEmpty());

            titleField.textProperty().addListener((obs, oldVal, newVal) -> {
                updateButton.setDisable(newVal.trim().isEmpty());
            });

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == updateButtonType) {
                    note.setTitle(titleField.getText().trim());
                    note.setContent(contentArea.getText().trim());
                    return note;
                }
                return null;
            });

            dialog.showAndWait().ifPresent(updatedNote -> {
                mongoService.updateNote(updatedNote);
                refreshNotes();
            });
        }

        @Override
        protected void updateItem(Note note, boolean empty) {
            super.updateItem(note, empty);
            if (empty || note == null) {
                setGraphic(null);
            } else {
                titleLabel.setText(note.getTitle());
                setGraphic(container);
            }
        }
    }
}
