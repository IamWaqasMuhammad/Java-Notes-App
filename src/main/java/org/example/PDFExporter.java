package org.example;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.example.model.Note;

import javax.swing.*;
import java.io.FileOutputStream;

public class PDFExporter {
    public static boolean exportNoteAsPDF(Note note) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File(note.getTitle() + ".pdf"));
        int option = fileChooser.showSaveDialog(null);

        if (option == JFileChooser.APPROVE_OPTION) {
            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(fileChooser.getSelectedFile()));
                document.open();
                document.add(new Paragraph("Title: " + note.getTitle()));
                document.add(new Paragraph("Content:\n" + note.getContent()));
                document.close();
                JOptionPane.showMessageDialog(null, "PDF exported successfully!");
                return true;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error exporting PDF: " + e.getMessage());
            }
        }
        return false;
    }
}
