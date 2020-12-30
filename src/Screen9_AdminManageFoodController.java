import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Screen9_AdminManageFoodController {

    @FXML
    private ComboBox<String> nameComboBox;

    @FXML
    private GridPane foodGridPane;

    @FXML
    private Text errorText;

    private ToggleGroup foodGroup;

    public void initialize() throws SQLException {
        errorText.setText("");
        // call query
        String query = "SELECT foodName FROM cs4400spring2020.Food";
        ResultSet rs = MySQL.table(query);

        // add food to combobox
        nameComboBox.getItems().add("");
        while (rs.next()) {
                nameComboBox.getItems().add(rs.getString("foodName"));
        }

    }

    public void filterBtnClicked(ActionEvent actionEvent)throws SQLException {
        // call procedure
        //-- Query #14: ad_filter_food [Screen #9 Admin Manage Food]
        String query = "{call ad_filter_food(?,?,?)}";
        CallableStatement cs = MySQL.conn.prepareCall(query);
        cs.setString(1, nameComboBox.getValue());
        cs.setString(2, "name");
        cs.setString(3, "ASC");
        MySQL.procedure(cs);


        // call query
        query = "SELECT foodName, menuCount, purchaseCount FROM cs4400spring2020.ad_filter_food_result";
        ResultSet rs = MySQL.table(query);

        updateFoodGridPane(rs);
    }

    public void updateFoodGridPane(ResultSet rs)throws SQLException{
        foodGridPane.getChildren().clear();
        foodGridPane.setGridLinesVisible(true);
        foodGridPane.add(new Text("Name"), 0, 0);
        foodGridPane.add(new Text("Menu Count"), 1, 0);
        foodGridPane.add(new Text("Purchase Count"), 2, 0);
        int counter = 1;
        foodGroup = new ToggleGroup();
        while (rs.next()) {
            // add food to gridpane
            RadioButton foodNameRBtn = new RadioButton(rs.getString("foodName"));
            Text menuCountText = new Text(rs.getInt("menuCount")+"");
            Text purchaseCountText = new Text(rs.getInt("purchaseCount")+"");
            foodNameRBtn.setToggleGroup(foodGroup);
            foodGridPane.add(foodNameRBtn, 0, counter);
            foodGridPane.add(menuCountText, 1, counter);
            foodGridPane.add(purchaseCountText, 2, counter);
            counter++;
        }
    }
    public void backBtnClicked(ActionEvent actionEvent) {
        sceneChange(actionEvent,"Screen3_Home.fxml");
    }

    public void deleteBtnClicked(ActionEvent actionEvent)throws SQLException {
        System.out.println(((RadioButton) foodGroup.getSelectedToggle()).getText());
        if (foodGroup == null || foodGroup.getSelectedToggle() == null) {
            errorText.setText("Please select food");
        } else {
            // call procedure
            //-- Query #15: ad_delete_food [Screen #9 Admin Manage Food]
            String query = "{call ad_delete_food(?)}";
            CallableStatement cs = MySQL.conn.prepareCall(query);
            cs.setString(1, ((RadioButton) foodGroup.getSelectedToggle()).getText());
            MySQL.procedure(cs);
        }
    }

    public void createBtnClicked(ActionEvent actionEvent) {
        sceneChange(actionEvent, "Screen10_AdminCreateFood.fxml");
    }

    public void sceneChange(ActionEvent actionEvent, String fxmlName) {
        try {
            Parent configParent = FXMLLoader.load(getClass().getResource(fxmlName));
            javafx.scene.Scene scene = new javafx.scene.Scene(configParent);
            // get current stage
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            System.out.println("fail to change scenes");
        }
    }
}
