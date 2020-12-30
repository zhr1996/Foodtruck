import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;


public class Screen18_CustomerOrderController {
    @FXML
    private Text foodtruck;

    @FXML
    private GridPane orderGridPane;

    @FXML
    private DatePicker orderdate;

    @FXML
    private Text errorText;



    List<CheckBox> checkBoxList = new LinkedList<>();
    List<Text> priceTextList = new LinkedList<>();
    List<TextField> textFieldList = new LinkedList<>();
    public void initialize() throws SQLException{
        errorText.setText("");
        // Foodtruck name
        foodtruck.setText(Configuration.chosenFoodTruck);

        // GirdPane
        //-- Query #32: cus_order_history [Screen #19 Customer Order History]
        String orderHistoryProcedure = "{call cus_order_history(\"" + Configuration.username + "\")}";
        CallableStatement csOrderHistory = MySQL.conn.prepareCall(orderHistoryProcedure);
        MySQL.procedure(csOrderHistory);

        //-- Query #21: mn_view_foodTruck_menu [Screen #13 Manager Update Food Truck]
        String query = "{call mn_view_foodTruck_menu(?)}";
        CallableStatement cs = MySQL.conn.prepareCall(query);
        cs.setString(1,Configuration.chosenFoodTruck);
        MySQL.procedure(cs);

        // GridPane

        //-- Query #21: mn_view_foodTruck_menu [Screen #13 Manager Update Food Truck]
        //-- call mn_view_foodTruck_menu("NachoBizness");
        query = "SELECT foodName, price FROM cs4400spring2020.mn_view_foodTruck_menu_result";
        ResultSet rs = MySQL.table(query);
        int counter = 1;
        cleanGridPane();

        while (rs.next()) {
            CheckBox checkbox = new CheckBox(rs.getString("foodName"));
            Text priceText = new Text(rs.getString("price"));
            TextField quantity = new TextField();
            orderGridPane.add(checkbox, 0, counter);
            orderGridPane.add(priceText,1, counter);
            orderGridPane.add(quantity, 2, counter);
            // checkbox list
            checkBoxList.add(checkbox);
            // price list
            priceTextList.add(priceText);
            // quantity text field list
            textFieldList.add(quantity);
            counter++;
        }
    }
    public void cleanGridPane() {
        orderGridPane.getChildren().clear();
        orderGridPane.add(new Text("Food"), 0, 0);
        orderGridPane.add(new Text("Price"), 1, 0);
        orderGridPane.add(new Text("Purchase Quantity"), 2, 0);

    }

    public void backBtnClicked(ActionEvent actionEvent) {
        sceneChange(actionEvent, "Screen17_CustomerCurrentInformation.fxml");
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


    public void submitBtnClicked(ActionEvent actionEvent) throws SQLException {
        System.out.println("clicked");
        // date
        if (orderdate.getValue() == null){
            errorText.setText("Please choose a date");
        }
        else {
            double total_price = 0;
            int count = 0;
            // check quantity
            int flag = 1;
            for (int i = 0; i < checkBoxList.size(); ++i) {
                if (checkBoxList.get(i).isSelected()) {
                    String quantity = textFieldList.get(i).getText();
                    count++;
                    try {
                        errorText.setText("");
                        int quan = Integer.parseInt(quantity);
                        if (quan <= 0){
                            flag = 0;
                            errorText.setText("Please enter a correct quantity");
                            break;
                        }
                        Double price = Double.parseDouble(priceTextList.get(i).getText());
                        total_price += price * quan;
                    } catch (NumberFormatException e) {
                        errorText.setText("Please enter a correct quantity");
                        flag = 0;
                        break;
                    }
                }
            }
            // at least one item
            if (count == 0) {
                errorText.setText("Please select at least one item");
            }
            // quantity is correct
            else if (flag == 1) {
                errorText.setText("");
                String query = "SELECT balance FROM Customer WHERE username = \"" + Configuration.username + "\"";
                ResultSet rs = MySQL.table(query);
                double balance = 0;
                if (rs.next()) {
                    balance = rs.getDouble("balance");
                }
                // balance < total_price
                if (balance < total_price) {
                    errorText.setText("Balance in not enough");
                }
                // place the order
                else {
                    //-- Query #30: cus_order [Screen #18 Customer Order]
                    //-- call cus_order("2020-1-20", "customer1");
                    query = "{call cus_order(?,?)}";
                    CallableStatement cs = MySQL.conn.prepareCall(query);
                    cs.setDate(1, Date.valueOf(orderdate.getValue()));
                    cs.setString(2, Configuration.username);
                    MySQL.procedure(cs);
                    // Get OrderID
                    int orderID = -1;

                    //-- Query #30: cus_order [Screen #18 Customer Order]
                    //-- call cus_order("2020-1-20", "customer1");
                    query = "SELECT orderID FROM cs4400spring2020.cus_order_result";
                    rs = MySQL.table(query);

                    if(rs.next()) {
                        orderID = rs.getInt("orderID");
                    }
                    System.out.println(orderID);
                    // System.out.println(orderID);
                    // Call add_item_to_order
                    for (int i = 0; i < checkBoxList.size(); ++i) {
                        if (checkBoxList.get(i).isSelected()) {
                            System.out.println("added");
                            String quantity = textFieldList.get(i).getText();

                            query = "{call cus_add_item_to_order(?,?,?,?)}";
                            cs = MySQL.conn.prepareCall(query);
                            cs.setString(1, Configuration.chosenFoodTruck);
                            cs.setString(2, checkBoxList.get(i).getText());
                            cs.setInt(3, Integer.parseInt(quantity));
                            cs.setInt(4, orderID);
                            MySQL.procedure(cs);
                        }
                    }
                    checkBoxList.clear();
                    priceTextList.clear();
                    textFieldList.clear();
                    orderdate.setValue(null);
                    sceneChange(actionEvent, "Screen17_CustomerCurrentInformation.fxml");
                }

            }
        }
    }
}
