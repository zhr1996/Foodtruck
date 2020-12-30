import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class Screen11_ManagerManageFoodTruckController {

    @FXML
    private ComboBox<String> stationNameComboBox;

    @FXML
    private TextField staffCountUpperBoundTextField;

    @FXML
    private TextField staffCountLowerBoundTextField;

    @FXML
    private TextField foodTruckNameTextField;

    @FXML
    private CheckBox hasRemainingCapacityCheckBox;

    @FXML
    private GridPane foodTruckGridPane;

    @FXML
    private Text errorText;

    private ToggleGroup radioBtnGroup;

    public void initialize() throws SQLException {
        errorText.setText("");
        // setup stationName combobox
        String query = "SELECT stationName FROM cs4400spring2020.Station";
        ResultSet rs = MySQL.table(query);
        stationNameComboBox.getItems().add("");
        while (rs.next()) {
            stationNameComboBox.getItems().add(rs.getString("stationName"));
        }
    }

    public void filterBtnClicked(ActionEvent actionEvent) throws SQLException {
        // call procedure
        //-- 1. INPUT is null
        //-- CALL mn_filter_foodTruck('Manager1', null, null, null, null, FALSE);
        String query = "{call mn_filter_foodTruck(?,?,?,?,?,?)}";
        CallableStatement cs = MySQL.conn.prepareCall(query);
        cs.setString(1, Configuration.username);
        cs.setString(2, foodTruckNameTextField.getText());
        if (stationNameComboBox.getValue() == null) cs.setNull(3, Types.CHAR);
        else cs.setString(3, stationNameComboBox.getValue());
        if (staffCountLowerBoundTextField.getText().isEmpty()) cs.setNull(4, Types.INTEGER);
        else cs.setInt(4, Integer.parseInt(staffCountLowerBoundTextField.getText()));
        if (staffCountUpperBoundTextField.getText().isEmpty()) cs.setNull(5, Types.INTEGER);
        else cs.setInt(5, Integer.parseInt(staffCountUpperBoundTextField.getText()));
        cs.setBoolean(6, hasRemainingCapacityCheckBox.isSelected());
        MySQL.procedure(cs);

        // call query
        query = "SELECT foodTruckName, stationName, remainingCapacity, staffCount, menuItemCount FROM cs4400spring2020.mn_filter_foodTruck_result";
        ResultSet rs = MySQL.table(query);
        int counter = 1;
        cleanGridPane();
        radioBtnGroup = new ToggleGroup();
        while (rs.next()) {
            RadioButton radioButton = new RadioButton(rs.getString("foodTruckName"));
            Text stationNameText = new Text(rs.getString("stationName"));
            Text capacityText = new Text(rs.getInt("remainingCapacity") + "");
            Text staffText = new Text(rs.getInt("staffCount") + "");
            Text menuItemText = new Text(rs.getInt("menuItemCount") + "");
            foodTruckGridPane.add(radioButton, 0, counter);
            radioButton.setToggleGroup(radioBtnGroup);
            foodTruckGridPane.add(stationNameText, 1, counter);
            foodTruckGridPane.add(capacityText, 2, counter);
            foodTruckGridPane.add(staffText, 3, counter);
            foodTruckGridPane.add(menuItemText, 4, counter);
            counter++;
        }
    }

    public void cleanGridPane() {
        foodTruckGridPane.getChildren().clear();
        foodTruckGridPane.add(new Text("Food Truck Name"), 0, 0);
        foodTruckGridPane.add(new Text("Station Name"), 1, 0);
        foodTruckGridPane.add(new Text("Remaining Capacity"), 2, 0);
        foodTruckGridPane.add(new Text("Staff(s)"), 3, 0);
        foodTruckGridPane.add(new Text("#Menu Item"), 4, 0);
    }

    public void backBtnClicked(ActionEvent actionEvent) {
        sceneChange(actionEvent, "Screen3_Home.fxml");
    }

    public void createBtnClicked(ActionEvent actionEvent) {
        sceneChange(actionEvent, "Screen12_ManagerCreateFoodTruck.fxml");
    }

    public void updateBtnClicked(ActionEvent actionEvent) {
        if (radioBtnGroup == null || radioBtnGroup.getSelectedToggle() == null) {
            errorText.setText("Please select a food truck");
        } else {
            Configuration.chosenFoodTruck = ((RadioButton) radioBtnGroup.getSelectedToggle()).getText();
            sceneChange(actionEvent, "Screen13_ManagerUpdateFoodTruck.fxml");
        }
    }

    public void deleteBtnClicked(ActionEvent actionEvent) throws SQLException {
        if (radioBtnGroup == null || radioBtnGroup.getSelectedToggle() == null) {
            errorText.setText("Please select a food truck");
        } else {
            // call procedure
            String chosenFoodTruckName = ((RadioButton) radioBtnGroup.getSelectedToggle()).getText();
            System.out.println(chosenFoodTruckName);//test again
            //-- Query #18: mn_delete_foodTruck [Screen #11 Manager Manage Food Truck]
            String query = "{call mn_delete_foodTruck(?)}";
            CallableStatement cs = MySQL.conn.prepareCall(query);
            cs.setString(1, chosenFoodTruckName);
            MySQL.procedure(cs);
        }
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
