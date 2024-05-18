package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import java.io.IOException;

public class UserController {

    @FXML
    private StackPane contentPane;

    @FXML
    private VBox menuVBox;

    @FXML
    private Label userInfoLabel;

    private String kullaniciAdi = GirisController.kullaniciAdi;
    private float bakiye = 0;


    @FXML
    private void initialize() {
        //users.json dosyasından bakiye bilgisini al
        try {
            String content = new String(Files.readAllBytes(Paths.get("src/application/users.json")));
            List<GirisController.User> users = new GirisController().parseUsers(content);
            for (GirisController.User user : users) {
                if (user.username.equals(kullaniciAdi)) {
                    bakiye = user.bakiye;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Kullanıcı bilgilerini ayarla
        if (kullaniciAdi != null) {
            userInfoLabel.setText("Kullanıcı Adı: " + kullaniciAdi +"\nBakiye: " + bakiye + " TL");
        } else {
            userInfoLabel.setText("Kullanıcı Adı: Bilinmiyor");
        }
        handleMenu1();
    }


    
    @FXML
    private void handleMenu1() {
        loadContent("menu1.fxml");
    }

    @FXML
    private void handleMenu2() {
        loadContent("menu2.fxml");
    }

    @FXML
    private void handleMenu3() {
        loadContent("menu3.fxml");
    }

    @FXML
    private void handleLogout() {
        // Çıkış yapma işlemleri
        System.out.println("Çıkış yapılıyor...");
        // Giriş ekranına dön
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("giris.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) menuVBox.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadContent(String fxmlFile) {
        try {
            Node content = FXMLLoader.load(getClass().getResource(fxmlFile));
            contentPane.getChildren().setAll(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
