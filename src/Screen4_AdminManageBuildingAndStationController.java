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
import java.util.LinkedList;
import java.util.List;

public class Screen4_AdminManageBuildingAndStationController {

    @FXML
    private ComboBox<String> buildingNameComboBox;
    @FXML
    private ComboBox<String> stationNameComboBox;

    @FXML
    private TextField buildingTag;

    @FXML
    private TextField capacityUpperBoundTextField;

    @FXML
    private TextField capacityLowerBoundTextField;

    @FXML
    private GridPane buildingGridPane;

    @FXML
    private Text errorText;

    private ToggleGroup radioBtnGroup;

    public void initialize() throws SQLException {
        errorText.setText("");
        // setup buildingName combobox
        String query = "SELECT buildingName FROM cs4400spring2020.Building";
        ResultSet rs = MySQL.table(query);
        buildingNameComboBox.getItems().add("");
        while (rs.next()) {
            buildingNameComboBox.getItems().add(rs.getString("buildingName"));
        }

        // setup stationName combobox
        query = "SELECT stationName FROM cs4400spring2020.Station";
        rs = MySQL.table(query);
        stationNameComboBox.getItems().add("");
        while (rs.next()) {
            stationNameComboBox.getItems().add(rs.getString("stationName"));
        }
    }
    public void filterBtnClicked(ActionEvent actionEvent) throws SQLException {
        // call procedure
        //-- Query #3: ad_filter_building_station [Screen #4 Admin Manage Building & Station]
        String query = "{call ad_filter_building_station(?,?,?,?,?)}";
        CallableStatement cs = MySQL.conn.prepareCall(query);
        cs.setString(1, buildingNameComboBox.getValue());
        cs.setString(2, buildingTag.getText());
        cs.setString(3, stationNameComboBox.getValue());
        if (capacityLowerBoundTextField.getText().isEmpty()) cs.setNull(4, Types.INTEGER);
        else cs.setInt(4, Integer.parseInt(capacityLowerBoundTextField.getText()));
        if (capacityUpperBoundTextField.getText().isEmpty()) cs.setNull(5, Types.INTEGER);
        else cs.setInt(5, Integer.parseInt(capacityUpperBoundTextField.getText()));
        MySQL.procedure(cs);



        // call query
        query = "SELECT buildingName, tags, stationName, capacity, foodTruckNames FROM cs4400spring2020.ad_filter_building_station_result";
        ResultSet rs = MySQL.table(query);
        int counter = 1;
        cleanGridPane();
        radioBtnGroup = new ToggleGroup();
        while (rs.next()) {
            RadioButton radioButton = new RadioButton(rs.getString("buildingName"));
            Text tagsText = new Text(rs.getString("tags"));
            Text stationNameText = new Text(rs.getString("stationName"));
            Text capacityNameText = new Text(rs.getInt("capacity") + "");
            Text foodTruckNamesText = new Text(rs.getString("foodTruckNames"));
            buildingGridPane.add(radioButton, 0, counter);
            radioButton.setToggleGroup(radioBtnGroup);
            buildingGridPane.add(tagsText, 1, counter);
            buildingGridPane.add(stationNameText, 2, counter);
            buildingGridPane.add(capacityNameText, 3, counter);
            buildingGridPane.add(foodTruckNamesText, 4, counter);
            counter++;

        }
    }

    public void cleanGridPane() {
        buildingGridPane.getChildren().clear();
        buildingGridPane.add(new Text("Building"), 0, 0);
        buildingGridPane.add(new Text("Tag(s)"), 1, 0);
        buildingGridPane.add(new Text("Station"), 2, 0);
        buildingGridPane.add(new Text("Capacity"), 3, 0);
        buildingGridPane.add(new Text("Food Truck(s)"), 4, 0);

    }

    public void backBtnClicked(ActionEvent actionEvent) {
        sceneChange(actionEvent, "Screen3_Home.fxml");
    }

    public void createBuildingBtnClicked(ActionEvent actionEvent) {
        sceneChange(actionEvent, "Screen5_CreateBuilding.fxml");
    }

    public void createStationBtnClicked(ActionEvent actionEvent) {
        sceneChange(actionEvent, "Screen7_CreateStation.fxml");
    }

    public void updateBuildingBtnClicked(ActionEvent actionEvent) {
        if (radioBtnGroup == null || radioBtnGroup.getSelectedToggle() == null) {
            errorText.setText("Please select a building");
        } else {
            Configuration.chosenBuildingName = ((RadioButton) radioBtnGroup.getSelectedToggle()).getText();
            sceneChange(actionEvent, "Screen6_UpdateBuilding.fxml");
        }
    }

    public void updateStationBtnClicked(ActionEvent actionEvent) {
        if (radioBtnGroup == null || radioBtnGroup.getSelectedToggle() == null) {
            errorText.setText("Please select a station");
        } else {
            int i = buildingGridPane.getRowIndex((RadioButton) radioBtnGroup.getSelectedToggle());
            Configuration.chosenStationName = ((Text) buildingGridPane.getChildren().get(i * 5 + 2)).getText();
            System.out.println(Configuration.chosenStationName);
            sceneChange(actionEvent, "Screen8_AdminUpdateStation.fxml");
        }
    }

    public void deleteBuildingBtnClicked(ActionEvent actionEvent) throws SQLException {
        if (radioBtnGroup == null || radioBtnGroup.getSelectedToggle() == null) {
            errorText.setText("Please select a building");
        } else {
            // call procedure
            String chosenBuildingName = ((RadioButton) radioBtnGroup.getSelectedToggle()).getText();
            System.out.println(chosenBuildingName);//test again
            //-- Query #4: ad_delete_building [Screen #4 Admin Manage Building & Station]
            String query = "{call ad_delete_building(?)}";
            CallableStatement cs = MySQL.conn.prepareCall(query);
            cs.setString(1, chosenBuildingName);
            MySQL.procedure(cs);
            filterBtnClicked(actionEvent);
        }
    }

    public void deleteStationBtnClicked(ActionEvent actionEvent) throws SQLException {
        if (radioBtnGroup == null || radioBtnGroup.getSelectedToggle() == null) {
            errorText.setText("Please select a station");
        } else {
            // call procedure
            int i = buildingGridPane.getRowIndex((RadioButton) radioBtnGroup.getSelectedToggle());
            String chosenStationName = ((Text) buildingGridPane.getChildren().get(i * 5 + 2)).getText();
            System.out.println(chosenStationName);//test again
            //-- Query #5: ad_delete_station [Screen #4 Admin Manage Building & Station]ad_filter_building_station
            String query = "{call ad_delete_station(?)}";
            CallableStatement cs = MySQL.conn.prepareCall(query);
            cs.setString(1, chosenStationName);
            MySQL.procedure(cs);
            filterBtnClicked(actionEvent);
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
