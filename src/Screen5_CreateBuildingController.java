import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.SQLException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.util.*;

public class Screen5_CreateBuildingController {

    @FXML
    private TextField BuildingNameTextField;

    @FXML
    private TextField BuildingDescriptionTextField;

    @FXML
    private TextField TagNameAddTextField;

    @FXML
    private ComboBox<String> TagNameComboBox;
    @FXML
    private Text errorText;

    private Set<String> tags = new HashSet<>();

    @FXML
    public void initialize() {
        errorText.setText("");
    }

    @FXML
    public void BackBtnClicked(ActionEvent actionEvent) {
        sceneChange(actionEvent, "Screen4_AdminManageBuildingAndStation.fxml");
    }

    @FXML
    void CreateBtnClicked(ActionEvent actionEvent) throws SQLException {
        if (BuildingNameTextField.getText().isEmpty()) {
            errorText.setText("Building name cannot be empty");
        } else if (BuildingDescriptionTextField.getText().isEmpty()) {
            errorText.setText("Description cannot be empty");
        } else if (tags.isEmpty()) {
            errorText.setText("Must add at least one tag");
        } else {

            // call procedure
            //-- Query #7: ad_create_building [Screen #5 Admin Create Building]
            String query = "{call ad_create_building(?,?)}";
            CallableStatement cs = MySQL.conn.prepareCall(query);
            cs.setString(1, BuildingNameTextField.getText());
            cs.setString(2, BuildingDescriptionTextField.getText());
            MySQL.procedure(cs);

            // call procedure
            for(String tag: tags){
                //-- Query #6a: ad_add_building_tag [Screen #5 Admin Add Building Tag]
                query = "{call ad_add_building_tag(?,?)}";
                cs = MySQL.conn.prepareCall(query);
                cs.setString(1, BuildingNameTextField.getText());
                cs.setString(2, tag);
                MySQL.procedure(cs);
            }

            errorText.setText("Success");
            tags.clear();
            BuildingNameTextField.setText("");
            BuildingDescriptionTextField.setText("");
            TagNameAddTextField.setText("");
            TagNameComboBox.getItems().clear();
        }
    }

    @FXML
    public void TagNameAddBtnClicked(ActionEvent event) {
        if (TagNameAddTextField.getText().isEmpty()) {
            errorText.setText("Tag name cannot be empty");
        } else if (tags.contains(TagNameAddTextField.getText())) {
            errorText.setText("The tag is already added");
        } else {
            tags.add(TagNameAddTextField.getText());
            TagNameComboBox.getItems().add(TagNameAddTextField.getText());
            TagNameAddTextField.setText("");
        }
    }

    @FXML
    public void TagNameRemoveBtnClicked(ActionEvent event) {
        tags.remove(TagNameComboBox.getValue());
        TagNameComboBox.getItems().remove(TagNameComboBox.getValue());
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
