import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.SQLException;

public class Screen10_AdminCreateFoodController {

    @FXML
    private TextField nameTextField;

    @FXML
    private Text errorText;

    public void initialize(){
        errorText.setText("");
    }

    public void createBtnClicked(ActionEvent actionEvent)throws SQLException {
        if(nameTextField.getText().isEmpty()) {
            errorText.setText("Food name cannot be empty");
        }else{
            // call procedure
            //-- Query #16: ad_create_food [Screen #10 Admin Create Food]
            String query = "{call ad_create_food(?)}";
            CallableStatement cs = MySQL.conn.prepareCall(query);
            cs.setString(1, nameTextField.getText());
            MySQL.procedure(cs);
            errorText.setText("Success!");
            nameTextField.setText("");
        }
    }


    public void backBtnClicked(ActionEvent actionEvent){
        sceneChange(actionEvent,"Screen9_AdminManageFood.fxml");
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
