package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GirisController {
    @FXML
    private TextField kullaniciAdiField;
    
    @FXML
    private PasswordField sifreField;
    
    @FXML
    private PasswordField tekrarSifreField;
    
    @FXML
    private Label tekrarSifreLabel;
    
    @FXML
    private GridPane gridPane;
    
    @FXML
    private Button girisButton;
    
    @FXML
    private Button kayitOlButton;
    
    private boolean kayitModu = false;
    private List<User> users = new ArrayList<>();
    public static String kullaniciAdi;
    public static float bakiye; // Static bakiye değişkeni
    public static int currentUserId;

    @FXML
    public void initialize() {
        loadUsersFromDatabase();
    }

    private void loadUsersFromDatabase() {
        try (Connection connection = Veritabani.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM users")) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                float bakiye = resultSet.getFloat("bakiye");

                List<Hisse> hisseler = loadHisselerForUser(id);

                User user = new User(id, username, password, bakiye, hisseler);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<Hisse> loadHisselerForUser(int userId) {
        List<Hisse> hisseler = new ArrayList<>();
        try (Connection connection = Veritabani.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM hisseleruser WHERE user_id = " + userId)) {

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int miktar = resultSet.getInt("miktar");
                Hisse hisse = new Hisse(name, miktar);
                hisseler.add(hisse);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hisseler;
    }

    @FXML
    private void handleGirisYap() {
        kullaniciAdi = kullaniciAdiField.getText();
        String sifre = sifreField.getText();
        
        if (kayitModu) {
            String tekrarSifre = tekrarSifreField.getText();
            if (!sifre.equals(tekrarSifre)) {
                System.out.println("Şifreler eşleşmiyor!");
                return;
            }
            for (User user : users) {
                if (user.username.equals(kullaniciAdi)) {
                    System.out.println("Kullanıcı adı zaten mevcut!");
                    return;
                }
            }
            User newUser = new User(users.size() + 1, kullaniciAdi, sifre, 0, new ArrayList<>());
            users.add(newUser);
            saveUserToDatabase(newUser);
            System.out.println("Kayıt başarılı: " + kullaniciAdi);
        } else {
            for (User user : users) {
                if (user.username.equals(kullaniciAdi) && user.password.equals(sifre)) {
                    System.out.println("Giriş başarılı: " + kullaniciAdi);
                    currentUserId = user.id;
                    bakiye = user.bakiye; // Bakiye bilgisini güncelle
                    loadUserScene();
                    return;
                }
            }
            System.out.println("Geçersiz kullanıcı adı veya şifre!");
        }
    }
    
    @FXML
    private void handleKayitOl() {
        if (!kayitModu) {
            tekrarSifreLabel.setVisible(true);
            tekrarSifreField.setVisible(true);
            girisButton.setText("Kayıt Ol");
            kayitOlButton.setText("Giriş Yap");
            kayitModu = true;
        } else {
            tekrarSifreLabel.setVisible(false);
            tekrarSifreField.setVisible(false);
            girisButton.setText("Giriş Yap");
            kayitOlButton.setText("Kayıt Ol");
            kayitModu = false;
        }
    }
    
    private void saveUserToDatabase(User user) {
        try (Connection connection = Veritabani.getConnection();
             PreparedStatement userStatement = connection.prepareStatement(
                 "INSERT INTO users (username, password, bakiye) VALUES (?, ?, ?)", 
                 Statement.RETURN_GENERATED_KEYS)) {
            
            // Kullanıcıyı veritabanına ekle
            userStatement.setString(1, user.username);
            userStatement.setString(2, user.password);
            userStatement.setFloat(3, user.bakiye);
            userStatement.executeUpdate();
    
            // Kullanıcının ID'sini al
            ResultSet generatedKeys = userStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int userId = generatedKeys.getInt(1);
    
                // Kullanıcının hisselerini veritabanına ekle
                try (PreparedStatement hisseStatement = connection.prepareStatement(
                    "INSERT INTO hisseler (user_id, name, miktar) VALUES (?, ?, ?)")) {
                    for (Hisse hisse : user.hisseler) {
                        hisseStatement.setInt(1, userId);
                        hisseStatement.setString(2, hisse.name);
                        hisseStatement.setInt(3, hisse.miktar);
                        hisseStatement.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<User> parseUsers(String json) {
        List<User> userList = new ArrayList<>();
        json = json.trim();
        if (json.startsWith("[") && json.endsWith("]")) {
            json = json.substring(1, json.length() - 1);
            String[] userJsonArray = json.split("},\\{");
            for (String userJson : userJsonArray) {
                userJson = userJson.replace("{", "").replace("}", "");
                String[] fields = userJson.split(",");
                int id = 0;
                String username = "";
                String password = "";
                float bakiye = 0;
                List<Hisse> hisseler = new ArrayList<>();
                for (String field : fields) {
                    String[] keyValue = field.split(":");
                    if (keyValue.length < 2) continue; // Eğer keyValue çift değilse atla
                    String key = keyValue[0].trim().replace("\"", "");
                    String value = keyValue[1].trim().replace("\"", "");
                    switch (key) {
                        case "id":
                            id = Integer.parseInt(value);
                            break;
                        case "username":
                            username = value;
                            break;
                        case "password":
                            password = value;
                            break;
                        case "bakiye":
                            bakiye = Float.parseFloat(value);
                            break;
                        case "hisseler":
                            String hisselerJson = value.replace("[", "").replace("]", "");
                            String[] hisseArray = hisselerJson.split("},\\{");
                            for (String hisseJson : hisseArray) {
                                hisseJson = hisseJson.replace("{", "").replace("}", "");
                                String[] hisseFields = hisseJson.split(",");
                                String hisseName = "";
                                int miktar = 0;
                                for (String hisseField : hisseFields) {
                                    String[] hisseKeyValue = hisseField.split(":");
                                    if (hisseKeyValue.length < 2) continue; // Eğer hisseKeyValue çift değilse atla
                                    String hisseKey = hisseKeyValue[0].trim().replace("\"", "");
                                    String hisseValue = hisseKeyValue[1].trim().replace("\"", "");
                                    switch (hisseKey) {
                                        case "name":
                                            hisseName = hisseValue;
                                            break;
                                        case "miktar":
                                            miktar = Integer.parseInt(hisseValue);
                                            break;
                                    }
                                }
                                hisseler.add(new Hisse(hisseName, miktar));
                            }
                            break;
                    }
                }
                userList.add(new User(id, username, password, bakiye, hisseler));
            }
        }
        return userList;
    }

    
    private void loadUserScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("user.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) gridPane.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setWidth(900);  // Genişlik
            stage.setHeight(600); // Yükseklik
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static class User {
        int id;
        String username;
        String password;
        float bakiye;
        List<Hisse> hisseler;

        User(int id, String username, String password, float bakiye, List<Hisse> hisseler) {
            this.id = id;
            this.username = username;
            this.password = password;
            this.bakiye = bakiye;
            this.hisseler = hisseler;
        }
    }
    
    public static class Hisse {
        String name;
        int miktar;

        Hisse(String name, int miktar) {
            this.name = name;
            this.miktar = miktar;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getMiktar() {
            return miktar;
        }

        public void setMiktar(int miktar) {
            this.miktar = miktar;
        }
    }
}

