package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.model.Note;
import org.example.service.MongoService;

import java.util.List;

public class MainUI {
    private final MongoService mongoService = new MongoService();
    private final VBox notesContainer = new VBox(10);

    public void show(Stage primaryStage) {
        // AppBar
        Label title = new Label("Notes App");
        title.setStyle("-fx-font-size: 24px; -fx-text-fill: white;");

        HBox appBar = new HBox(title);
        appBar.setAlignment(Pos.CENTER_LEFT);
        appBar.setPadding(new Insets(15));
        appBar.setStyle("-fx-background-color: #6200EE;");

        // Notes container
        notesContainer.setPadding(new Insets(10));

        // Floating Action Button (FAB)
        Button fab = new Button("+");
        fab.setStyle("-fx-background-radius: 50%; -fx-background-color: #6200EE; -fx-text-fill: white; -fx-font-size: 24px;");
        fab.setPrefSize(60, 60);
        fab.setOnAction(e -> showAddNoteDialog());

        StackPane.setAlignment(fab, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(fab, new Insets(0, 20, 20, 0));

        // Layout
        VBox layout = new VBox(appBar, notesContainer);
        StackPane root = new StackPane(layout, fab);

        refreshNotes();

        primaryStage.setScene(new Scene(root, 400, 600));
        primaryStage.show();
    }

    private void refreshNotes() {
        notesContainer.getChildren().clear();
        List<Note> notes = mongoService.getAllNotes();
        for (Note note : notes) {
            notesContainer.getChildren().add(createNoteCard(note));
        }
    }

    private HBox createNoteCard(Note note) {
        // Title label
        Label titleLabel = new Label(note.getTitle());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        titleLabel.setWrapText(true);

        // Content label
        Label contentLabel = new Label(note.getContent());
        contentLabel.setWrapText(true);

        // VBox for title and content vertically stacked
        VBox textContent = new VBox(5, titleLabel, contentLabel);
        textContent.setAlignment(Pos.TOP_LEFT);
        textContent.setMaxWidth(280);

        // Status label for exported notes
        Label statusLabel = new Label(note.isExported() ? "Exported" : "");
        statusLabel.setStyle("-fx-text-fill: green; -fx-font-style: italic;");
        statusLabel.setMinWidth(60);
        statusLabel.setAlignment(Pos.CENTER);

        // Context menu items
        MenuItem editItem = new MenuItem("Edit");
        MenuItem deleteItem = new MenuItem("Delete");
        MenuItem exportItem = new MenuItem("Export to PDF");
        ContextMenu menu = new ContextMenu(editItem, deleteItem, exportItem);

        Button menuButton = new Button("⋮");
        menuButton.setStyle("-fx-background-color: transparent; -fx-font-size: 16px;");
        menuButton.setOnAction(e -> menu.show(menuButton, Side.BOTTOM, 0, 0));

        editItem.setOnAction(e -> showUpdateNoteDialog(note));
        deleteItem.setOnAction(e -> {
            mongoService.deleteNoteById(note.getId());
            refreshNotes();
        });
        exportItem.setOnAction(e -> {
            menu.hide();
            boolean success = PDFExporter.exportNoteAsPDF(note);
            if (success) {
                mongoService.markNoteAsExported(note.getId());
                refreshNotes();
            }
        });

        // Card container
        HBox card = new HBox(10, textContent, statusLabel, menuButton);
        HBox.setHgrow(textContent, Priority.ALWAYS);

        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: white; -fx-border-radius: 10; -fx-background-radius: 10; "
                + "-fx-effect: dropshadow(gaussian, lightgray, 5, 0, 0, 2);");

        return card;
    }

    private void showAddNoteDialog() {
        Dialog<Note> dialog = new Dialog<>();
        dialog.setTitle("Add Note");

        TextField titleField = new TextField();
        TextArea contentArea = new TextArea();

        VBox content = new VBox(10, new Label("Title:"), titleField, new Label("Content:"), contentArea);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                return new Note(titleField.getText(), contentArea.getText());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(note -> {
            mongoService.addNote(note);
            refreshNotes();
        });
    }

    private void showUpdateNoteDialog(Note note) {
        Dialog<Note> dialog = new Dialog<>();
        dialog.setTitle("Update Note");

        TextField titleField = new TextField(note.getTitle());
        TextArea contentArea = new TextArea(note.getContent());

        VBox content = new VBox(10, new Label("Title:"), titleField, new Label("Content:"), contentArea);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                note.setTitle(titleField.getText());
                note.setContent(contentArea.getText());
                return note;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedNote -> {
            mongoService.updateNote(updatedNote);
            refreshNotes();
        });
    }
}
