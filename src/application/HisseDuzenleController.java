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

public class HisseDuzenleController {

    @FXML
    private TableView<Hisse> tableView;

    @FXML
    private TableColumn<Hisse, String> hisseNameColumn;

    @FXML
    private TableColumn<Hisse, Double> alisFiyatiColumn;

    @FXML
    private TableColumn<Hisse, Double> satisFiyatiColumn;

    @FXML
    private TextField hisseNameField;

    @FXML
    private TextField miktarField;

    @FXML
    private TextField alisFiyatiField;

    @FXML
    private TextField satisFiyatiField;

    private ObservableList<Hisse> HisseList;

    @FXML
    public void initialize() {
        HisseList = FXCollections.observableArrayList();

        hisseNameColumn.setCellValueFactory(new PropertyValueFactory<>("hisseName"));
        alisFiyatiColumn.setCellValueFactory(new PropertyValueFactory<>("alisFiyati"));
        satisFiyatiColumn.setCellValueFactory(new PropertyValueFactory<>("satisFiyati"));

        loadUserHisseData();

        tableView.setItems(HisseList);

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                hisseNameField.setText(newValue.getHisseName());
                alisFiyatiField.setText(String.valueOf(newValue.getAlisFiyati()));
                satisFiyatiField.setText(String.valueOf(newValue.getSatisFiyati()));
            }
        });
    }

private void loadUserHisseData() {
	HisseList.clear();
    try (Connection connection = Veritabani.getConnection();
         PreparedStatement statement = connection.prepareStatement(
                 "SELECT name, alis, satis FROM hisseler")) {
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            String name = resultSet.getString("name");
            double alisFiyati = resultSet.getDouble("alis");
            double satisFiyati = resultSet.getDouble("satis");
            HisseList.add(new Hisse(name, alisFiyati, satisFiyati));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    @FXML
    private void handleAdd() {
        String hisseName = hisseNameField.getText();
        double alisFiyati = Double.parseDouble(alisFiyatiField.getText());
        double satisFiyati = Double.parseDouble(satisFiyatiField.getText());

        try (Connection connection = Veritabani.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO hisseler (name, alis, satis) VALUES (?, ?, ?)")) {
            statement.setString(1, hisseName);
            statement.setDouble(2, alisFiyati);
            statement.setDouble(3, satisFiyati);
            statement.executeUpdate();
            loadUserHisseData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
@FXML
private void handleUpdate() {
    Hisse selectedHisse = tableView.getSelectionModel().getSelectedItem();
    if (selectedHisse != null) {
        String hisseName = hisseNameField.getText();
        double alisFiyati = Double.parseDouble(alisFiyatiField.getText());
        double satisFiyati = Double.parseDouble(satisFiyatiField.getText());

        try (Connection connection = Veritabani.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE hisseler SET name = ?, alis = ?, satis = ? WHERE name = ?")) {
            statement.setString(1, hisseName);
            statement.setDouble(2, alisFiyati);
            statement.setDouble(3, satisFiyati);
            statement.setString(4, selectedHisse.getHisseName());
            statement.executeUpdate();
            loadUserHisseData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

    @FXML
    private void handleDelete() {
        Hisse selectedHisse = tableView.getSelectionModel().getSelectedItem();
        if (selectedHisse != null) {
            try (Connection connection = Veritabani.getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM hisseler WHERE name = ?")) {
                statement.setString(1, selectedHisse.getHisseName());
                statement.executeUpdate();
                loadUserHisseData();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

public static class Hisse {
   private String hisseName;
   private double alisFiyati;
   private double satisFiyati;

   public Hisse(String hisseName, double alisFiyati, double satisFiyati) {
       this.hisseName = hisseName;
       this.alisFiyati = alisFiyati;
       this.satisFiyati = satisFiyati;
   }

   public String getHisseName() {
       return hisseName;
   }

   public void setHisseName(String hisseName) {
       this.hisseName = hisseName;
   }

   public double getAlisFiyati() {
       return alisFiyati;
   }

   public void setAlisFiyati(double alisFiyati) {
       this.alisFiyati = alisFiyati;
   }

   public double getSatisFiyati() {
       return satisFiyati;
   }

   public void setSatisFiyati(double satisFiyati) {
       this.satisFiyati = satisFiyati;
   }
}
}
