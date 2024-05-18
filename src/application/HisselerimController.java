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

public class HisselerimController {

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
            String content = new String(Files.readAllBytes(Paths.get("src/application/users.json")));
            List<Hisse> hisseList = parseHisselerFromUsers(content);
            tableView.getItems().addAll(hisseList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Hisse> parseHisselerFromUsers(String content) {
        List<Hisse> hisseList = new ArrayList<>();
        content = content.trim();
        if (content.startsWith("[") && content.endsWith("]")) {
            content = content.substring(1, content.length() - 1);
            String[] userJsonArray = content.split("},\\s*\\{");
            for (String userJson : userJsonArray) {
                userJson = userJson.replace("{", "").replace("}", "");
                String[] fields = userJson.split(",");
                for (String field : fields) {
                    String[] keyValue = field.split(":");
                    if (keyValue.length < 2) continue; // Eğer keyValue çift değilse atla
                    String key = keyValue[0].trim().replace("\"", "");
                    String value = keyValue[1].trim().replace("\"", "");
                    if (key.equals("hisseler")) {
                        String hisselerJson = value.replace("[", "").replace("]", "");
                        String[] hisseArray = hisselerJson.split("},\\s*\\{");
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
                            hisseList.add(new Hisse(hisseName, miktar));
                        }
                    }
                }
            }
        }
        return hisseList;
    }

    public static class Hisse {
        private String name;
        private int miktar;

        public Hisse(String name, int miktar) {
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
