package application;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HisseAlSatController {

    @FXML
    private TextField hisseAdiField;
    @FXML
    private TextField miktarField;
    @FXML
    private TableView<Hisse> tableView;
    @FXML
    private TableColumn<Hisse, String> nameColumn;
    @FXML
    private TableColumn<Hisse, Integer> miktarColumn;

    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        miktarColumn.setCellValueFactory(new PropertyValueFactory<>("miktar"));

        try {
            List<Hisse> hisseList = loadHisselerFromDatabase();
            setupTableView(hisseList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<Hisse> loadHisselerFromDatabase() throws SQLException {
        List<Hisse> hisseList = new ArrayList<>();
        try (Connection connection = Veritabani.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM hisseleruser WHERE user_id = ?")) {
            statement.setInt(1, GirisController.currentUserId); // Mevcut kullanıcı ID'sini kullanın
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int userId = resultSet.getInt("user_id");
                String name = resultSet.getString("name");
                int miktar = resultSet.getInt("miktar");

                Hisse hisse = new Hisse(id, userId, name, miktar);
                hisseList.add(hisse);
}
}
return hisseList;
}

private void setupTableView(List<Hisse> hisseList) {
tableView.getItems().setAll(hisseList);
}

@FXML
private void handleHisseAl() {
	if(hisseAdiField.getText() != "" && miktarField.getText() != "")
	{
		String hisseAdi = hisseAdiField.getText();
		int miktar = Integer.parseInt(miktarField.getText());

		try (Connection connection = Veritabani.getConnection();
		     PreparedStatement statement = connection.prepareStatement(
		             "INSERT INTO hisseleruser (user_id, name, miktar) VALUES (?, ?, ?)")) {
		    statement.setInt(1, GirisController.currentUserId); // Mevcut kullanıcı ID'sini kullanın
		    statement.setString(2, hisseAdi);
		    statement.setInt(3, miktar);
		    statement.executeUpdate();

		    // Tabloyu güncelle
		    List<Hisse> hisseList = loadHisselerFromDatabase();
		    setupTableView(hisseList);
		} catch (SQLException e) {
		    e.printStackTrace();
		}
	}
}

@FXML
private void handleHisseSat() {
	if(hisseAdiField.getText() != "" && miktarField.getText() != "")
	{
		String hisseAdi = hisseAdiField.getText();
		int miktar = Integer.parseInt(miktarField.getText());

		try (Connection connection = Veritabani.getConnection();
		     PreparedStatement statement = connection.prepareStatement(
		             "DELETE FROM hisseleruser WHERE user_id = ? AND name = ? AND miktar = ?")) {
		    statement.setInt(1, GirisController.currentUserId); // Mevcut kullanıcı ID'sini kullanın
		    statement.setString(2, hisseAdi);
		    statement.setInt(3, miktar);
		    statement.executeUpdate();

		    // Tabloyu güncelle
		    List<Hisse> hisseList = loadHisselerFromDatabase();
		    setupTableView(hisseList);
		} catch (SQLException e) {
		    e.printStackTrace();
		}
	}
}

public static class Hisse {
private int id;
private int userId;
private String name;
private int miktar;

public Hisse(int id, int userId, String name, int miktar) {
    this.id = id;
    this.userId = userId;
    this.name = name;
    this.miktar = miktar;
}

public int getId() {
    return id;
}

public void setId(int id) {
    this.id = id;
}

public int getUserId() {
    return userId;
}

public void setUserId(int userId) {
    this.userId = userId;
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