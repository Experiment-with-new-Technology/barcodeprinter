/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

/**
 * FXML Controller class
 *
 * @author ajay
 */
public class MainViewController implements Initializable {

    @FXML
    private Button btn;

    @FXML
    private TextField id, noOfBarcode;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
            btn.setOnAction(e->{
                ToastController.showToast(ToastController.TOAST_WARN,btn,"Barcode Creating");
                try {
                    code();
                } catch (IOException | DocumentException ex) {
                    Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
    }

    private void code() throws FileNotFoundException, IOException, BadElementException, DocumentException {
        Integer numberOfBarcode = 0;
        if(!noOfBarcode.getText().isEmpty()) {
            try {
                numberOfBarcode = Integer.parseInt(noOfBarcode.getText());
            } catch (Exception e) {
                ToastController.showToast(ToastController.TOAST_ERROR,btn,"Input Valid Number in 'Number of Barcode'");
            }

        }
        if(!id.getText().isEmpty() && numberOfBarcode != 0) {
            Code128Bean code128 = new Code128Bean();
            code128.setHeight(15f);
            code128.setModuleWidth(0.3);
            code128.setQuietZone(10);
            code128.doQuietZone(true);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(baos, "image/x-png", 400, BufferedImage.TYPE_BYTE_BINARY, false, 0);
            code128.generateBarcode(canvas, id.getText());
            System.out.println(noOfBarcode.getText());
            canvas.finish();
//write to pdf
            Image png = Image.getInstance(baos.toByteArray());
            png.setAbsolutePosition(0, 705);
            png.scalePercent(25);

            Document document;
            document = new Document();
            PdfPTable table = new PdfPTable(1);
            table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
            for (int aw = 0; aw < numberOfBarcode; aw++) {
                Paragraph p = new Paragraph();
                PdfPTable intable = new PdfPTable(1);
                intable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
                intable.addCell(p);
                intable.addCell(png);
                intable.getDefaultCell().setBorder(0);
                table.addCell(intable);
            }
            Paragraph p = new Paragraph();
            p.add(png);
            try {
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("barcodes.pdf"));
                document.open();
                document.add(table);
                document.close();
                writer.close();
                Desktop.getDesktop().open(new File("barcodes.pdf"));
                ToastController.showToast(ToastController.TOAST_SUCCESS,btn,"Barcode Creation Done.");
            } catch (Exception e) {
                if(e.getMessage().contains("section open")) {
                    ToastController.showToast(ToastController.TOAST_ERROR,btn,"Close Previous one & try again");
                }
            }

        } else {
            ToastController.showToast(ToastController.TOAST_ERROR,btn,"'Accession Number' or 'No of Barcode' is invalid");
        }
    }
}
