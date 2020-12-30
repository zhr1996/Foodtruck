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

public class Screen7_CreateStationController {
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
        //-- Query #10: ad_get_available_building [Screen #7 Admin Create Station]
        String query = "{call ad_get_available_building()}";
        CallableStatement cs = MySQL.conn.prepareCall(query);
        MySQL.procedure(cs);

        // add building names to combo box
        query = "SELECT buildingName FROM cs4400spring2020.ad_get_available_building_result";
        ResultSet rs = MySQL.table(query);
        while (rs.next()) {
            sponsoredBuildingComboBox.getItems().add(rs.getString("buildingName"));
        }
    }

    public void backBtnClicked(ActionEvent actionEvent) {
        sceneChange(actionEvent, "Screen4_AdminManageBuildingAndStation.fxml");
    }

    public void createBtnClicked(ActionEvent actionEvent) throws SQLException {

        if (nameTextField.getText().isEmpty()) {
            errorText.setText("Name cannot be empty");
        } else if (capacityTextField.getText().isEmpty() || Integer.parseInt(capacityTextField.getText()) < 0) {
            errorText.setText("Capacity cannot be empty or negative");
        } else if (sponsoredBuildingComboBox.getValue() == null) {
            errorText.setText("Sponsored Building cannot be empty");
        } else {
            //-- Query #11: ad_create_station [Screen #7 Admin Create Station]
            String query = "{call ad_create_station(?,?,?)}";
            CallableStatement cs = MySQL.conn.prepareCall(query);
            cs.setString(1, nameTextField.getText());
            cs.setString(2, sponsoredBuildingComboBox.getValue());
            cs.setInt(3, Integer.parseInt(capacityTextField.getText()));
            MySQL.procedure(cs);
            nameTextField.setText("");
            capacityTextField.setText("");
            // call procedure
            //-- Query #10: ad_get_available_building [Screen #7 Admin Create Station]
            query = "{call ad_get_available_building()}";
            cs = MySQL.conn.prepareCall(query);
            MySQL.procedure(cs);

            // add building names to combo box
            sponsoredBuildingComboBox.getItems().clear();
            query = "SELECT buildingName FROM cs4400spring2020.ad_get_available_building_result";
            ResultSet rs = MySQL.table(query);
            while (rs.next()) {
                sponsoredBuildingComboBox.getItems().add(rs.getString("buildingName"));
            }
            errorText.setText("Success!");
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


