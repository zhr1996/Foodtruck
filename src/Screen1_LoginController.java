import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import java.io.IOException;
import java.sql.*;

public class Screen1_LoginController {
    @FXML
    private TextField userNameTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private Text errorText;

    public void initialize() {
        errorText.setText("");
    }

    public void registerBtnClicked(ActionEvent actionEvent) {
        sceneChange(actionEvent, "Screen2_Register.fxml");
    }


    public void loginBtnClicked(ActionEvent actionEvent) throws SQLException {
        // call procedure
        //-- Query #1: login [Screen #1 Login]
        String query = "{call login(?,?)}";
        CallableStatement cs = MySQL.conn.prepareCall(query);
        cs.setString(1, userNameTextField.getText());
        cs.setString(2, passwordTextField.getText());
        MySQL.procedure(cs);

        // call query
        query = "SELECT userName, userType FROM cs4400spring2020.login_result";
        ResultSet rs = MySQL.table(query);

        // login success
        if (rs.next()) {
            String userName = rs.getString("userName");
            String userType = rs.getString("userType");
            Configuration.username = userName;
            Configuration.userType = userType;
            System.out.println(userType);
            sceneChange(actionEvent,"Screen3_Home.fxml");
        } else {
            errorText.setText("The username/password is incorrect!");
            userNameTextField.setText("");
            passwordTextField.setText("");
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
