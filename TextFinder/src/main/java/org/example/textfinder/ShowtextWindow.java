package org.example.textfinder;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.io.*;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import java.net.URL;
import java.util.ResourceBundle;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

//segundo "controller" para la segunda pantalla
public class ShowtextWindow implements Initializable {

    @FXML
    private Label docuTextLABEL;

    @FXML
    private Label showDocuLABEL;

    StringBuilder textInsideDocu = new StringBuilder();

    ShowDocuSingleton showDocu = ShowDocuSingleton.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        StringBuilder textInsideDocu = new StringBuilder();
        showDocuLABEL.setText("Presentando documento:" + " " + showDocu.getFile().getName());

        if (showDocu.getFile().getName().endsWith("txt")) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(showDocu.getFile()));
                String line;
                while ((line = reader.readLine()) != null) {
                    textInsideDocu.append(line);
                }
                reader.close();
                docuTextLABEL.setText(String.valueOf(textInsideDocu));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (showDocu.getFile().getName().endsWith("docx")) {
            try (FileInputStream fis = new FileInputStream(showDocu.getFile());
                 XWPFDocument document = new XWPFDocument(fis);
                 XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
                String extractedText = extractor.getText();
                textInsideDocu.append(extractedText);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            docuTextLABEL.setText(String.valueOf(textInsideDocu));
        }
        if(showDocu.getFile().getName().endsWith("pdf")){
            try (PDDocument document = PDDocument.load(showDocu.getFile())) {
                PDFTextStripper textStripper = new PDFTextStripper();
                String extractedText = textStripper.getText(document);
                textInsideDocu.append(extractedText);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            docuTextLABEL.setText(String.valueOf(textInsideDocu));
        }
    }
}
