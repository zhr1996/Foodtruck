import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Screen17_CustomerCurrentInformationController {

    @FXML // fx:id="stationName"
    private Text stationName; // Value injected by FXMLLoader

    @FXML // fx:id="buildingTag"
    private Text buildingTag; // Value injected by FXMLLoader

    @FXML // fx:id="buildingName"
    private Text buildingName; // Value injected by FXMLLoader

    @FXML // fx:id="buildingDescription"
    private Text buildingDescription; // Value injected by FXMLLoader

    @FXML // fx:id="balance"
    private Text balance; // Value injected by FXMLLoader

    private ToggleGroup radioBtnGroup;

    @FXML
    private GridPane currentInformationGridPane; // Value injected by FXMLLoader

    @FXML
    private GridPane foodTruckGridPane;

    @FXML
    private Text errorText;

    public void cleanGridPane() {
        foodTruckGridPane.getChildren().clear();
        foodTruckGridPane.add(new Text("food Truck"), 0, 0);
        foodTruckGridPane.add(new Text("Manager"), 1, 0);
        foodTruckGridPane.add(new Text("Food(s)"), 2, 0);
    }

    public void initialize() throws SQLException {
        errorText.setText("");
        //-- Query #28: cus_current_information_basic [Screen #17 Customer Current Information]
        //-- call cus_current_information_basic("4400_thebestclass");
        String cusCurrentInformationProcedure = "call cus_current_information_basic(\"" + Configuration.username + "\")";
        //System.out.println(cusCurrentInformationProcedure);
        CallableStatement csCuscurrentInformation = MySQL.conn.prepareCall(cusCurrentInformationProcedure);
        MySQL.procedure(csCuscurrentInformation);

        String cusCurrentInformationQuery = "select * from cus_current_information_basic_result;";
        ResultSet rsCusCurrentInformation = MySQL.table(cusCurrentInformationQuery);

        while (rsCusCurrentInformation.next()) {
            Text stationName = new Text(rsCusCurrentInformation.getString("stationName"));
            stationName.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
            stationName.setUnderline(true);
            Text buildName = new Text(rsCusCurrentInformation.getString("buildingName"));
            buildName.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
            buildName.setUnderline(true);
            Text buildingTag = new Text(rsCusCurrentInformation.getString("tags"));
            buildingTag.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
            buildingTag.setUnderline(true);
            Text buildingDescription = new Text(rsCusCurrentInformation.getString("description"));
            buildingDescription.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
            buildingDescription.setUnderline(true);
            Text balance = new Text(rsCusCurrentInformation.getString("balance"));
            balance.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
            balance.setUnderline(true);
            currentInformationGridPane.add(stationName, 1, 0);
            currentInformationGridPane.add(buildName, 1, 1);
            currentInformationGridPane.add(buildingTag, 1, 2);
            currentInformationGridPane.add(buildingDescription, 1, 3);
            currentInformationGridPane.add(balance, 1, 4);
        }

        //-- Query #29: cus_current_information_foodTruck [Screen #17 Customer Current Information]
        //-- call cus_current_information_foodTruck("4400_thebestclass");
        //-- call cus_current_information_basic("customer1");
        String cusCurrentInformationFoodTruckProcedure = "call cus_current_information_foodTruck(\"" + Configuration.username + "\")";
        //System.out.println(cusCurrentInformationFoodTruckProcedure);
        CallableStatement csCuscurrentInformationFoodTruck = MySQL.conn.prepareCall(cusCurrentInformationFoodTruckProcedure);
        MySQL.procedure(csCuscurrentInformationFoodTruck);

        String cusCurrentInformationFoodTruckQuery = "select * from cus_current_information_foodTruck_result;";
        ResultSet rsCusCurrentInformationFoodTruck = MySQL.table(cusCurrentInformationFoodTruckQuery);
        int counter = 1;
        cleanGridPane();
        radioBtnGroup = new ToggleGroup();
        while (rsCusCurrentInformationFoodTruck.next()) {
            RadioButton radioButton = new RadioButton(rsCusCurrentInformationFoodTruck.getString("foodTruckName"));
            Text managerName = new Text(rsCusCurrentInformationFoodTruck.getString("managerName"));
            Text foodNames = new Text(rsCusCurrentInformationFoodTruck.getString("foodNames"));
            foodTruckGridPane.add(radioButton, 0, counter);
            radioButton.setToggleGroup(radioBtnGroup);
            foodTruckGridPane.add(managerName, 1, counter);
            foodTruckGridPane.add(foodNames, 2, counter);
            counter++;
        }
    }

    @FXML
    public void orderBtnClicked(ActionEvent event) throws SQLException {
        if (radioBtnGroup == null || radioBtnGroup.getSelectedToggle() == null) {
            errorText.setText("Please select a station");
        } else {
            Configuration.chosenFoodTruck = ((RadioButton) radioBtnGroup.getSelectedToggle()).getText();
            sceneChange(event, "Screen18_CustomerOrder.fxml");
        }
    }

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
