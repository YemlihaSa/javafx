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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
            try (Connection connection = Veritabani.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT bakiye FROM users WHERE username = ?")) {
                statement.setString(1, kullaniciAdi);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    bakiye = resultSet.getFloat("bakiye");
                }
            } catch (SQLException e) {
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
