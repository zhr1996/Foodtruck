import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.regex.Pattern;

public class Screen2_RegisterController {

    @FXML
    private TextField userNameTextField;

    @FXML
    private TextField firstNameTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private TextField balanceTextField;

    @FXML
    private TextField emailTextField;

    @FXML
    private TextField lastNameTextField;

    @FXML
    private PasswordField confirmPasswordTextField;

    @FXML
    private RadioButton managerRBtn;

    @FXML
    private RadioButton staffRBtn;

    @FXML
    private RadioButton adminRBtn;

    @FXML
    private Text errorText;

    String regex = "^(.+)@(.+).(.+)$";
    Pattern pattern = Pattern.compile(regex);

    public void initialize() {
        errorText.setText("");
    }

    public void registerBtnClicked(ActionEvent actionEvent) throws SQLException {
        if (userNameTextField.getText().isEmpty()) {
            errorText.setText("Username cannot be empty");
        } else if(!pattern.matcher(emailTextField.getText()).matches()){
            errorText.setText("Email is invalid");
        }else if (firstNameTextField.getText().isEmpty()) {
            errorText.setText("First name cannot be empty");
        } else if (lastNameTextField.getText().isEmpty()) {
            errorText.setText("Last name cannot be empty");
        } else if (passwordTextField.getText().length() < 8) {
            errorText.setText("Password should be at least 8 characters");
        } else if (passwordTextField.getText().compareTo(confirmPasswordTextField.getText()) != 0) {
            errorText.setText("Passwords do not match");
        } else if (!balanceTextField.getText().isEmpty() && Double.parseDouble(balanceTextField.getText()) <= 0) {
            errorText.setText("Balance should be greater than 0");
        } else {
            // call procedure
            String userType = null;
            if (adminRBtn.isSelected()) {
                userType = "Admin";
            } else if (managerRBtn.isSelected()) {
                userType = "Manager";
            } else if (staffRBtn.isSelected()) {
                userType = "Staff";
            }

            //-- call register('FFFFFFFF','QQQ','QQQQ', 'DDDDDD', '12345678', 1.00, 'Admin');
            //
            //-- Query #2: register  [Screen #2 Register]
            //-- Don't need to check email format (XXX@XXX.XXX)
            //-- Make sure you check balance > 0 for customer
            //-- Make sure you check password length >= 8
            //
            //-- CALL register('securityLeak', 'phish_me@gatech.eds', 'Easily', 'Hacked', 'hahaha', 20, 'Admin');
            String query = "{call register(?,?,?,?,?,?,?)}";
            CallableStatement cs = MySQL.conn.prepareCall(query);
            cs.setString(1, userNameTextField.getText());
            cs.setString(2, emailTextField.getText().isEmpty()? null : emailTextField.getText());
            cs.setString(3, firstNameTextField.getText());
            cs.setString(4, lastNameTextField.getText());
            cs.setString(5, passwordTextField.getText());
            if(balanceTextField.getText().isEmpty()) cs.setNull(6, Types.NULL);
            else cs.setDouble(6, Double.parseDouble(balanceTextField.getText()));
            cs.setString(7, userType); // bug
            MySQL.procedure(cs);
            sceneChange(actionEvent, "Screen1_Login.fxml");
        }

    }

    public void backBtnClicked(ActionEvent actionEvent) {
        sceneChange(actionEvent, "Screen1_Login.fxml");
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
