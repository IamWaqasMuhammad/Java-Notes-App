package org.example;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.example.model.Note;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.IOException;


import javax.swing.*;
import java.io.FileOutputStream;

public class PDFExporter {

    public static void exportNoteAsPDF(Note note) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File(note.getTitle() + ".pdf"));

        int option = fileChooser.showSaveDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            Document document = new Document();
            try{
                PdfWriter.getInstance(document, new FileOutputStream(fileChooser.getSelectedFile()));
                document.open();
                document.add(new Paragraph("Title: " + note.getTitle()));
                document.add(new Paragraph("Content:"));
                document.add(new Paragraph(note.getContent()));
                document.close();
                JOptionPane.showMessageDialog(null, "PDF exported successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error exporting PDF: " + e.getMessage());
            }
        }
    }
}
