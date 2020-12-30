import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.xml.transform.Result;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Screen15_ManagerSummaryDetailController {
    @FXML
    private Text foodtruck;

    @FXML
    private GridPane summarygrid;



    public void initialize() throws SQLException {
        foodtruck.setText(Configuration.chosenFoodTruck);
        //-- Query #25: mn_summary_detail [Screen #15 Manager Summary Detail]
        //-- call mn_summary_detail("Manager1", "CrazyPies");
        String query = "{call mn_summary_detail(?,?)}";
        CallableStatement cs = MySQL.conn.prepareCall(query);
        // Manager
        cs.setString(1, Configuration.username);
        // FoodTruckName
        cs.setString(2, Configuration.chosenFoodTruck);
        MySQL.procedure(cs);

        query = "SELECT * FROM mn_summary_detail_result";
        ResultSet rs = MySQL.table(query);
        int counter = 1;
        cleanGridPane();
        while (rs.next()) {
            Text Date = new Text(rs.getString("date"));
            Text customerName = new Text(rs.getString("customerName"));
            Text totalPurchase = new Text(rs.getString("totalPurchase"));
            Text orderCount = new Text(rs.getString("orderCount"));
            Text foodNames = new Text(rs.getString("foodNames"));

            summarygrid.add(Date, 0, counter);
            summarygrid.add(customerName, 1, counter);
            summarygrid.add(totalPurchase, 2, counter);
            summarygrid.add(orderCount, 3, counter);
            summarygrid.add(foodNames, 4, counter);
            counter++;
        }

    }

    public void cleanGridPane() {
        summarygrid.getChildren().clear();
        summarygrid.add(new Text("Date"), 0, 0);
        summarygrid.add(new Text("Customer"), 1, 0);
        summarygrid.add(new Text("Total Purchase"), 2, 0);
        summarygrid.add(new Text("#Orders"), 3, 0);
        summarygrid.add(new Text("Food(s)"), 4, 0);
    }
    public void backbtnclicked(ActionEvent actionEvent) {
        sceneChange(actionEvent, "Screen14_ManagerFoodTruckSummary.fxml");
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
