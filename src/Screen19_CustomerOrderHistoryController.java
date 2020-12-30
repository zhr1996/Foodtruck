import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Screen19_CustomerOrderHistoryController {
    @FXML
    private GridPane orderHistoryGridPane;

    @FXML
    private Button BackBtn;



    public void cleanGridPane() throws SQLException {
        orderHistoryGridPane.getChildren().clear();
        orderHistoryGridPane.add(new Text("Date"), 0, 0);
        orderHistoryGridPane.add(new Text("OrderID"), 1, 0);
        orderHistoryGridPane.add(new Text("Order Total"), 2, 0);
        orderHistoryGridPane.add(new Text("Food(s)"), 3, 0);
        orderHistoryGridPane.add(new Text("Food Quantity"), 4, 0);
        orderHistoryGridPane.setGridLinesVisible(true); //useless WHY?????????????????????
    }


    public void initialize() throws SQLException {
        // call procedure -- Query #32: cus_order_history
        String orderHistoryProcedure = "{call cus_order_history(\"" + Configuration.username + "\")}";
        CallableStatement csOrderHistory = MySQL.conn.prepareCall(orderHistoryProcedure);
        MySQL.procedure(csOrderHistory);
        // call query
        String orderHistoryQuery = "select * from cus_order_history_result";
        ResultSet rsOrderHistory = MySQL.table(orderHistoryQuery);

        int counter = 1;
        cleanGridPane();
        while (rsOrderHistory.next()) {
            Text Date = new Text(rsOrderHistory.getString("date"));
            Text orderID = new Text(rsOrderHistory.getString("orderID"));
            Text orderTotal = new Text(rsOrderHistory.getDouble("orderTotal") + "");
            Text food = new Text(rsOrderHistory.getString("foodNames") + "");
            Text foodQuantity = new Text(rsOrderHistory.getInt("foodQuantity") + "");
            orderHistoryGridPane.add(Date, 0, counter);
            orderHistoryGridPane.add(orderID, 1, counter);
            orderHistoryGridPane.add(orderTotal, 2, counter);
            orderHistoryGridPane.add(food, 3, counter);
            orderHistoryGridPane.add(foodQuantity, 4, counter);
            counter++;
        }
        //errorText.setText("");
    }


    public void BackBtnClicked(ActionEvent actionEvent) {
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
