package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class KullaniciHisseDuzenleController {

    @FXML
    private TableView<UserHisse> tableView;

    @FXML
    private TableColumn<UserHisse, Integer> userIdColumn;

    @FXML
    private TableColumn<UserHisse, String> usernameColumn;

    @FXML
    private TableColumn<UserHisse, String> hisseNameColumn;

    @FXML
    private TableColumn<UserHisse, Integer> miktarColumn;
    
    @FXML
    private TextField miktarField;

    private ObservableList<UserHisse> userHisseList;

    @FXML
    public void initialize() {
        userHisseList = FXCollections.observableArrayList();

        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        hisseNameColumn.setCellValueFactory(new PropertyValueFactory<>("hisseName"));
        miktarColumn.setCellValueFactory(new PropertyValueFactory<>("miktar"));

        loadUserHisseData();

        tableView.setItems(userHisseList);
    }

    private void loadUserHisseData() {
        try (Connection connection = Veritabani.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT hu.user_id, u.username, hu.name AS hisse_name, hu.miktar " +
                     "FROM hisseleruser hu " +
                     "JOIN users u ON hu.user_id = u.id")) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                String username = resultSet.getString("username");
                String hisseName = resultSet.getString("hisse_name");
                int miktar = resultSet.getInt("miktar");

                userHisseList.add(new UserHisse(userId, username, hisseName, miktar));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void updateHisse() {
    UserHisse selectedHisse = tableView.getSelectionModel().getSelectedItem();
    if (selectedHisse != null) {
        int newMiktar = Integer.parseInt(miktarField.getText());

        try (Connection connection = Veritabani.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE hisseleruser SET miktar = ? WHERE user_id = ? AND name = ?")) {
            statement.setInt(1, newMiktar);
            statement.setInt(2, selectedHisse.getUserId());
            statement.setString(3, selectedHisse.getHisseName());
            statement.executeUpdate();

            selectedHisse.setMiktar(newMiktar);
            tableView.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    }

    public static class UserHisse {
        private int userId;
        private String username;
        private String hisseName;
        private int miktar;

        public UserHisse(int userId, String username, String hisseName, int miktar) {
            this.userId = userId;
            this.username = username;
            this.hisseName = hisseName;
            this.miktar = miktar;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getHisseName() {
            return hisseName;
        }

        public void setHisseName(String hisseName) {
            this.hisseName = hisseName;
        }

        public int getMiktar() {
            return miktar;
        }

        public void setMiktar(int miktar) {
            this.miktar = miktar;
        }
    }
}
