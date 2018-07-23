package adr_generator;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.security.Security;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class Adr_generator extends Application 
{
    // Encryption
    CAddress adr=new CAddress();
    
    private byte[] getQRCodeImage(String text, int width, int height) throws Exception 
    {
       QRCodeWriter qrCodeWriter = new QRCodeWriter();
       BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
    
       ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
       MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
       byte[] pngData = pngOutputStream.toByteArray(); 
       return pngData;
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        // Security provider
	Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        
        // Generate new address
        CAddress adr=new CAddress();
        adr.generate();
        
        // Load back image
        FileInputStream inputstream = new FileInputStream("./assets/begin_back.jpg"); 
        
        // Create image
        Image image = new Image(inputstream, 800, 648, false, false); 
        
        // Create image view
        ImageView imageView = new ImageView(image);
        
        // Set position
        imageView.setX(0); 
        imageView.setY(0); 
        
        // Panel
        Pane panel=new Pane();
       
        // Root
        Group root = new Group(panel);  
        
        // New scene
        Scene scene = new Scene(root, 800, 648);
        
        // Add pub key text box
       TextArea txt_pub_key = new TextArea(); 
        txt_pub_key.setLayoutX(342);
        txt_pub_key.setLayoutY(270);
        txt_pub_key.setPrefColumnCount(28);
        txt_pub_key.setPrefRowCount(4);
        txt_pub_key.setWrapText(true);
        txt_pub_key.setStyle("-fx-focus-color: transparent; -fx-text-box-border: transparent;");
        txt_pub_key.setText(adr.getPublic());
        
         // Add priv key text box
        TextArea txt_priv_key = new TextArea(); 
        txt_priv_key.setLayoutX(342);
        txt_priv_key.setLayoutY(428);
        txt_priv_key.setPrefColumnCount(28);
        txt_priv_key.setPrefRowCount(4);
        txt_priv_key.setWrapText(true);
        txt_priv_key.setStyle("-fx-focus-color: transparent; -fx-text-box-border: transparent;");
        txt_priv_key.setText(adr.getPrivate());
        
        // QR Image
        byte[] qr_img=this.getQRCodeImage(adr.getPublic()+","+adr.getPrivate(), 250, 250);
        Image img = new Image(new ByteArrayInputStream(qr_img));
        ImageView qr_view = new ImageView(img);
        qr_view.setX(0); 
        qr_view.setY(0);
        qr_view.setLayoutX(50);
        qr_view.setLayoutY(265);
        
        // Add back image
        panel.getChildren().add(imageView);
        panel.getChildren().add(txt_pub_key);
        panel.getChildren().add(txt_priv_key);
        panel.getChildren().add(qr_view);
        
        // Set title
        primaryStage.setTitle("Hello World!");
        
        // Set scene
        primaryStage.setScene(scene);
        
        // Show
        primaryStage.show();
    }

   
    public static void main(String[] args) 
    {
        launch(args);
    }
    
}
