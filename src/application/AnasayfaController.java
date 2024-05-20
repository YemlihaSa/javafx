package application;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AnasayfaController {

    @FXML
    private TableView<Hisse> tableView;
    @FXML
    private TableColumn<Hisse, String> nameColumn;
    @FXML
    private TableColumn<Hisse, Double> alisColumn;
    @FXML
    private TableColumn<Hisse, Double> satisColumn;

    @FXML
    private void initialize() {
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
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM Hisseler")) {
    
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                double alis = resultSet.getDouble("alis");
                double satis = resultSet.getDouble("satis");
    
                Hisse hisse = new Hisse(name, alis, satis);
                hisseList.add(hisse);
            }
        }
        return hisseList;
    }


    private void setupTableView(List<Hisse> hisseList) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        alisColumn.setCellValueFactory(new PropertyValueFactory<>("alis"));
        satisColumn.setCellValueFactory(new PropertyValueFactory<>("satis"));
        tableView.getItems().addAll(hisseList);
    }

    public static class Hisse {
        private String name;
        private double alis;
        private double satis;

        public Hisse(String name, double alis, double satis) {
            this.name = name;
            this.alis = alis;
            this.satis = satis;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getAlis() {
            return alis;
        }

        public void setAlis(double alis) {
            this.alis = alis;
        }

        public double getSatis() {
            return satis;
        }

        public void setSatis(double satis) {
            this.satis = satis;
        }
    }

}
