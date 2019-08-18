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
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    @FXML
    private Label code;
    @FXML
    private ImageView img;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        
            btn.setOnAction(e->{
                try {
                    code();
                } catch (IOException | DocumentException ex) {
                    Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        

    }

    private void code() throws FileNotFoundException, IOException, BadElementException, DocumentException {
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

//write to png file
        FileOutputStream fos = new FileOutputStream("barcode.png");
        fos.write(baos.toByteArray());
        fos.flush();
        fos.close();

//write to pdf
        Image png = Image.getInstance(baos.toByteArray());
        png.setAbsolutePosition(0, 705);
        png.scalePercent(25);

        Document document;
        document = new Document();
        PdfPTable table = new PdfPTable(3);
        table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
        for (int aw = 0; aw < 27; aw++) {
            Paragraph p = new Paragraph("        Product Name");
            p.add("\n        Price:500");
//            p.add(createImageCell(png));
            PdfPTable intable = new PdfPTable(1);
            intable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
            intable.addCell(p);
            intable.addCell(png);
            intable.getDefaultCell().setBorder(0);
            
            table.addCell(intable);
        }
//        table.setBorder(Border.NO_BORDER);
        Paragraph p = new Paragraph("Product Name");
        p.add("\nPrice:500");
        p.add(png);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("barcodes.pdf"));
        document.open();
//        document.add();
        document.add(table);
        document.close();

        writer.close();
        

    }

}
