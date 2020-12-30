import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.util.*;

public class Screen6_UpdateBuildingController {

    @FXML
    private TextField updateBuildingNameTextField;

    @FXML
    private TextField updateBuildingDescriptionTextField;

    @FXML
    private TextField updateTagNameTextField;

    @FXML
    private Text errorText;

    @FXML
    private ComboBox<String> updateTagNameComboBox;

    private Set<String> oldTags = new HashSet<>();
    private Set<String> deleteSet = new HashSet<>();
    private Set<String> tags = new HashSet<>();

    public void initialize() throws SQLException {
        errorText.setText("");

        // selected from Screen4 update Building
        updateBuildingNameTextField.setText(Configuration.chosenBuildingName);

        // call procedure
        //-- Query #8a: ad_view_building_general [Screen #6 Admin Update Building]
        String query = "{call ad_view_building_general(?)}";
        CallableStatement cs = MySQL.conn.prepareCall(query);
        cs.setString(1, updateBuildingNameTextField.getText());
        MySQL.procedure(cs);


        // view building general %query 8a
        String buildingGeneralQuery = "SELECT buildingName, description FROM cs4400spring2020.ad_view_building_general_result";
        ResultSet rs = MySQL.table(buildingGeneralQuery);
        while (rs.next()) {
            String buildingName = rs.getString("buildingName");
            String description = rs.getString("description");
            updateBuildingNameTextField.setText(buildingName);
            updateBuildingDescriptionTextField.setText(description);
        }


        // call procedure
        //-- Query #8b: ad_view_building_tags [Screen #6 Admin Update Building]
        query = "{call ad_view_building_tags(?)}";
        cs = MySQL.conn.prepareCall(query);
        cs.setString(1, updateBuildingNameTextField.getText());
        MySQL.procedure(cs);

        String buildingTagQuery = "select * from cs4400spring2020.ad_view_building_tags_result";
        ResultSet rsBuildingTag = MySQL.table(buildingTagQuery);

        while (rsBuildingTag.next()) {
            updateTagNameComboBox.getItems().add(rsBuildingTag.getString("tag"));
            tags.add(rsBuildingTag.getString("tag"));
            oldTags.add(rsBuildingTag.getString("tag"));
        }
    }

    @FXML
    public void BackBtnClicked(ActionEvent actionEvent) {
        sceneChange(actionEvent, "Screen4_AdminManageBuildingAndStation.fxml");
    }

    @FXML
    void UpdateBtnClicked(ActionEvent actionEvent) throws SQLException {
        // Query #9: ad_update_building [Screen #6 Admin Update Building]
        if (updateBuildingNameTextField.getText().isEmpty()) {
            errorText.setText("Building name cannot be empty");
        } else if (updateBuildingDescriptionTextField.getText().isEmpty()) {
            errorText.setText("Description cannot be empty");
        } else if (tags.isEmpty()) {
            errorText.setText("Must be at least one tag");
        } else {

            // call procedure
            //-- Query #9: ad_update_building [Screen #6 Admin Update Building]
            String query = "{call ad_update_building(?,?,?)}";
            CallableStatement cs = MySQL.conn.prepareCall(query);
            cs.setString(1, Configuration.chosenBuildingName);
            cs.setString(2, updateBuildingNameTextField.getText());
            cs.setString(3, updateBuildingDescriptionTextField.getText());
            MySQL.procedure(cs);
            Configuration.chosenBuildingName = updateBuildingNameTextField.getText();

            for (String s : tags) {
                if (!oldTags.contains(s)) {
                    //-- Query #6a: ad_add_building_tag [Screen #5 Admin Add Building Tag]
                    query = "{call ad_add_building_tag(?,?)}";
                    cs = MySQL.conn.prepareCall(query);
                    cs.setString(1, Configuration.chosenBuildingName);
                    cs.setString(2, s);
                    MySQL.procedure(cs);
                    oldTags.add(s);
                }
            }
            for(String s : oldTags){
                if(!tags.contains(s)){
                    //-- Query #6b: ad_remove_building_tag [Screen #5 Admin Remove Building Tag]
                    query = "{call ad_remove_building_tag(?,?)}";
                    cs = MySQL.conn.prepareCall(query);
                    cs.setString(1, Configuration.chosenBuildingName);
                    cs.setString(2, s);
                    MySQL.procedure(cs);
                    deleteSet.add(s);
                }
            }
            for(String s:deleteSet){
                oldTags.remove(s);
            }
            deleteSet.clear();
            errorText.setText("Success");
        }
    }

    @FXML
    public void updateTagNameAddClicked(ActionEvent event) throws SQLException {
        if (updateTagNameTextField.getText().isEmpty()) {
            errorText.setText("Tag name cannot be empty");
        } else if (tags.contains(updateTagNameTextField.getText())) {
            errorText.setText("The tag is already added");
        } else {
            tags.add(updateTagNameTextField.getText());
            updateTagNameComboBox.getItems().add(updateTagNameTextField.getText());
            updateTagNameTextField.setText("");
        }
    }

    @FXML
    public void updateTagNameRemoveClicked(ActionEvent event) throws SQLException {
        if (tags.size() == 1) {
            errorText.setText("Must remain at least one tag");
        } else {
            tags.remove(updateTagNameComboBox.getValue());
            updateTagNameComboBox.getItems().remove(updateTagNameComboBox.getValue());
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
