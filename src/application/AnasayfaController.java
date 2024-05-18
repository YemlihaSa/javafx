package application;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
            String content = new String(Files.readAllBytes(Paths.get("src/application/hisse.json")));
            List<Hisse> hisseList = parseHisse(content);
            setupTableView(hisseList);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private List<Hisse> parseHisse(String content) {
        List<Hisse> hisseList = new ArrayList<>();
        content = content.trim();
        if (content.startsWith("[") && content.endsWith("]")) {
            content = content.substring(1, content.length() - 1);
            String[] hisseJsonArray = content.split("},\\s*\\{");
            for (String hisseJson : hisseJsonArray) {
                hisseJson = hisseJson.replace("{", "").replace("}", "");
                String[] fields = hisseJson.split(",");
                String name = "";
                double alis = 0;
                double satis = 0;
                for (String field : fields) {
                    String[] keyValue = field.split(":");
                    String key = keyValue[0].trim().replace("\"", "");
                    String value = keyValue[1].trim().replace("\"", "");
                    switch (key) {
                        case "name":
                            name = value;
                            break;
                        case "alis":
                            alis = Double.parseDouble(value);
                            break;
                        case "satis":
                            satis = Double.parseDouble(value);
                            break;
                    }
                }
                hisseList.add(new Hisse(name, alis, satis));
            }
        }
        return hisseList;
    }
}
