import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;


public class Screen16_CustomerExploreController {
    @FXML // fx:id="buildindNameComboBox"
    private ComboBox<String> buildingNameComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="stationNameComboBox"
    private ComboBox<String> stationNameComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="buildingNameTag"
    private TextField buildingNameTag; // Value injected by FXMLLoader

    @FXML // fx:id="foodTruckName"
    private TextField foodTruckName; // Value injected by FXMLLoader

    @FXML // fx:id="foodName"
    private TextField foodName; // Value injected by FXMLLoader

    @FXML // fx:id="filterResultGridPane"
    private GridPane filterResultGridPane; // Value injected by FXMLLoader

    @FXML
    private Text errorText;

    private ToggleGroup radioBtnGroup;

    public void initialize() throws SQLException {
        // setup buildingName combobox
        String buildingNameQuery = "SELECT buildingName FROM cs4400spring2020.Building";
        ResultSet rsBuildingName = MySQL.table(buildingNameQuery);
        buildingNameComboBox.getItems().add("");
        while (rsBuildingName.next()) {
            buildingNameComboBox.getItems().add(rsBuildingName.getString("buildingName"));
        }
        buildingNameComboBox.setValue("");

        // setup stationName combobox
        String stationNameQuery = "SELECT stationName FROM cs4400spring2020.Station";
        ResultSet rsStationName = MySQL.table(stationNameQuery);
        stationNameComboBox.getItems().add("");
        while (rsStationName.next()) {
            stationNameComboBox.getItems().add(rsStationName.getString("stationName"));
        }
        stationNameComboBox.setValue("");

        errorText.setText("");
    }

    public void cleanGridPane() {
        filterResultGridPane.getChildren().clear();
        filterResultGridPane.add(new Text("stationName"), 0, 0);
        filterResultGridPane.add(new Text("buildingName"), 1, 0);
        filterResultGridPane.add(new Text("foodTruckNames"), 2, 0);
        filterResultGridPane.add(new Text("foodNames"), 3, 0);
    }

    @FXML
    public void filterBtnClicked(ActionEvent event) throws SQLException {
        // call procedure
        // -- Query #26: cus_filter_explore [Screen #16 Customer Explore]
        // -- call cus_filter_explore("","","","","chickenTacos");
        String cusFilterTagQuery = "call cus_filter_explore(\"" + buildingNameComboBox.getValue() +"\",\"" + stationNameComboBox.getValue() +"\",\"" + buildingNameTag.getText() + "\",\"" + foodTruckName.getText() + "\",\"" + foodName.getText() + "\")";
        //System.out.println(cusFilterTagQuery);
        CallableStatement csCusFilterTag = MySQL.conn.prepareCall(cusFilterTagQuery);
        MySQL.procedure(csCusFilterTag);

        //-- Query #26: cus_filter_explore [Screen #16 Customer Explore]
        //-- call cus_filter_explore("","","","","chickenTacos");
        // call query
        String queryCusFilterResult = "select * from cus_filter_explore_result";
        ResultSet rsCusFilterResult = MySQL.table(queryCusFilterResult);
        int counter = 1;
        cleanGridPane();
        radioBtnGroup = new ToggleGroup();
        while (rsCusFilterResult.next()) {
            RadioButton radioButton = new RadioButton(rsCusFilterResult.getString("stationName"));
            Text buildName = new Text(rsCusFilterResult.getString("buildingName"));
            Text foodTruckNames = new Text(rsCusFilterResult.getString("foodTruckNames"));
            Text foodNames = new Text(rsCusFilterResult.getString("foodNames"));
            filterResultGridPane.add(radioButton, 0, counter);
            radioButton.setToggleGroup(radioBtnGroup);
            filterResultGridPane.add(buildName, 1, counter);
            filterResultGridPane.add(foodTruckNames, 2, counter);
            filterResultGridPane.add(foodNames, 3, counter);
            counter++;
        }
    }

    @FXML
    public void selectBtnClicked(ActionEvent event) throws SQLException{
        if (radioBtnGroup == null || radioBtnGroup.getSelectedToggle() == null) {
            errorText.setText("Please select a station");
        } else {
            Configuration.chosenStationName = ((RadioButton) radioBtnGroup.getSelectedToggle()).getText();
            String query = "{call cus_select_location(?,?)}";
            CallableStatement cs = MySQL.conn.prepareCall(query);
            cs.setString(1, Configuration.username);
            cs.setString(2, Configuration.chosenStationName);
            MySQL.procedure(cs);
            errorText.setText("Success");
        }
    }

    @FXML
    public void backBtnClicked(ActionEvent actionEvent) {
        sceneChange(actionEvent, "Screen3_Home.fxml");
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
