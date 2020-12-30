import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.SQLException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Screen1_Login.fxml"));
        primaryStage.setTitle("Food Truck System");
        primaryStage.setScene(new Scene(root, 650, 450));
        primaryStage.show();
    }

    public static void main(String[] args) {
        try{
            MySQL.connectToMySQL();
        } catch (SQLException ex) {
            System.out.println("An error occurred while connecting to MySQL");
        }
        launch(args);
    }


}
