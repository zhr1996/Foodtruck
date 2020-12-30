import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Screen8_AdminUpdateStationController {

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField capacityTextField;

    @FXML
    private ComboBox<String> sponsoredBuildingComboBox;

    @FXML
    private Text errorText;

    public void initialize() throws SQLException {

        errorText.setText("");

        // call procedure
        //-- Query #12: ad_view_station [Screen #8 Admin Update Station]
        String query = "{call ad_view_station(?)}";
        CallableStatement cs = MySQL.conn.prepareCall(query);
        cs.setString(1, Configuration.chosenStationName);
        MySQL.procedure(cs);

        // call query
        query = "SELECT stationName, capacity, buildingName FROM cs4400spring2020.ad_view_station_result";
        ResultSet rs = MySQL.table(query);
        if (rs.next()) {
            nameTextField.setText(rs.getString("stationName"));
            nameTextField.setDisable(true);
            capacityTextField.setText(rs.getInt("capacity")+"");
            sponsoredBuildingComboBox.setValue(rs.getString("buildingName"));
        }


        // call procedure
        //-- Query #10: ad_get_available_building [Screen #7 Admin Create Station]
        query = "{call ad_get_available_building()}";
        cs = MySQL.conn.prepareCall(query);
        MySQL.procedure(cs);

        // add building names to combo box
        query = "SELECT buildingName FROM cs4400spring2020.ad_get_available_building_result";
        rs = MySQL.table(query);
        while (rs.next()) {
            sponsoredBuildingComboBox.getItems().add(rs.getString("buildingName"));
        }
    }

    public void updateBtnClicked(ActionEvent actionEvent)throws SQLException {
        if(capacityTextField.getText().isEmpty() || Integer.parseInt(capacityTextField.getText())<0){
            errorText.setText("Capacity cannot be empty or negative");
        }
        // call procedure
        //-- Query #13: ad_update_station [Screen #8 Admin Update Station]
        String query = "{call ad_update_station(?,?,?)}";
        CallableStatement cs = MySQL.conn.prepareCall(query);
        cs.setString(1, nameTextField.getText());
        cs.setInt(2,Integer.parseInt(capacityTextField.getText()));
        cs.setString(3, sponsoredBuildingComboBox.getValue());
        MySQL.procedure(cs);

    }

    public void backBtnClicked(ActionEvent actionEvent) {
        sceneChange(actionEvent, "Screen4_AdminManageBuildingAndStation.fxml");
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
