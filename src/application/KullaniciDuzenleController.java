package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class KullaniciDuzenleController {

    @FXML
    private TableView<User> userTableView;
    
    @FXML
    private TableColumn<User, String> idColumn;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> passwordColumn;

    @FXML
    private TableColumn<User, Double> balanceColumn;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField balanceField;

    private ObservableList<User> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {


        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));
        try (Connection connection = Veritabani.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT id,username, password, bakiye FROM users")) {
            ResultSet resultSet = statement.executeQuery();

            // Veritabanından kullanıcı bilgilerini çek ve listeye ekle
            while (resultSet.next()) {
            	int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                double balance = resultSet.getDouble("bakiye");
                userList.add(new User(id,username, password, balance));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        userTableView.setItems(userList);
    }

    @FXML
    private void handleAddUser() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        double balance = Double.parseDouble(balanceField.getText());

        try (Connection connection = Veritabani.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO users (username, password, bakiye) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setDouble(3, balance);
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1);
                User newUser = new User(id, username, password, balance);
                userList.add(newUser);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateUser() {
        User selectedUser = userTableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            String newUsername = usernameField.getText();
            String newPassword = passwordField.getText();
            double newBalance = Double.parseDouble(balanceField.getText());

            try (Connection connection = Veritabani.getConnection();
                 PreparedStatement statement = connection.prepareStatement("UPDATE users SET username = ?, password = ?, bakiye = ? WHERE id = ?")) {
                statement.setString(1, newUsername);
                statement.setString(2, newPassword);
                statement.setDouble(3, newBalance);
                statement.setInt(4, selectedUser.getId());
                statement.executeUpdate();

                selectedUser.setUsername(newUsername);
                selectedUser.setPassword(newPassword);
                selectedUser.setBalance(newBalance);
                userTableView.refresh();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleDeleteUser() {
        User selectedUser = userTableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            try (Connection connection = Veritabani.getConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE id = ?")) {
                statement.setInt(1, selectedUser.getId());
                statement.executeUpdate();
                userList.remove(selectedUser);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static class User {
    	private int id;
        private String username;
        private String password;
        private double balance;

        public User(int id,String username, String password, double balance) {
        	this.setId(id);
            this.username = username;
            this.password = password;
            this.balance = balance;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() { // Getter metodunun adı düzeltildi
            return password;
        }

        public void setPassword(String password) { // Setter metodunun adı düzeltildi
            this.password = password;
        }

        public double getBalance() {
            return balance;
        }

        public void setBalance(double balance) {
            this.balance = balance;
        }

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}
    }
}
