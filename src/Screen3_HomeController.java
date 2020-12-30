import com.sun.tracing.dtrace.FunctionAttributes;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;



public class Screen3_HomeController {
    @FXML
    private Text errorText;

    public void initialize(){
        errorText.setText("");
    }
    public void exploreBtnClicked(ActionEvent actionEvent) {
        if(Configuration.userType.contains("Customer")){
            sceneChange(actionEvent,"Screen16_CustomerExplore.fxml");
        }else{
            errorText.setText("Only customer can click the button");
        }
    }

    public void viewOrderHistoryBtnClicked(ActionEvent actionEvent) {
        if(Configuration.userType.contains("Customer")){
            sceneChange(actionEvent,"Screen19_CustomerOrderHistory.fxml");
        }else {
            errorText.setText("Only customer can click the button");
        }
    }

    public void viewCurrentInformationBtnClicked(ActionEvent actionEvent) {
        if(Configuration.userType.contains("Customer")){
            sceneChange(actionEvent,"Screen17_CustomerCurrentInformation.fxml");
        }else{
            errorText.setText("Only customer can click the button");
        }
    }
    public void manageFoodTruckBtnClicked(ActionEvent actionEvent) {
        if(Configuration.userType.contains("Manager")){
            sceneChange(actionEvent,"Screen11_ManagerManageFoodTruck.fxml");
        }else{
            errorText.setText("Only manager can click the button");
        }

    }

    public void viewFoodTruckSummaryBtnClicked(ActionEvent actionEvent) {
        if(Configuration.userType.contains("Manager")){
            sceneChange(actionEvent,"Screen14_ManagerFoodTruckSummary.fxml");
        }else{
            errorText.setText("Only manager can click the button");
        }
    }

    public void manageBuildingAndStationBtnClicked(ActionEvent actionEvent) {
        if(Configuration.userType.contains("Admin")){
            sceneChange(actionEvent,"Screen4_AdminManageBuildingAndStation.fxml");
        }else{
            errorText.setText("Only admin can click the button");
        }
    }

    public void manageFoodBtnClicked(ActionEvent actionEvent) {
        if(Configuration.userType.contains("Admin")){
            sceneChange(actionEvent,"Screen9_AdminManageFood.fxml");
        }else{
            errorText.setText("Only admin can click the button");
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
