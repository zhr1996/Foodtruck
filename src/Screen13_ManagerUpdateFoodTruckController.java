import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.event.ActionEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Screen13_ManagerUpdateFoodTruckController {

    @FXML
    private TextField foodtruckname;

    @FXML
    private ComboBox<String> foodComboBox;

    @FXML
    private ComboBox<String> foodComboBox1;

    @FXML
    private TextField pricefield;

    @FXML
    private ComboBox<String> stationcombo;

    @FXML
    private AnchorPane AssignedStaffList;

    @FXML
    private ListView<String> staffListView;

    @FXML
    private Text errorText;

    List<String> selected_staff = new LinkedList<>();
    Set<String> menu = new HashSet<>();
    // food
    Map<String, String> newly_added_item = new HashMap<>();
    Set<String> assigned_staff_set = new HashSet<>();
    public void initialize() throws SQLException {
        errorText.setText("");
        // foodtruck name
        foodtruckname.setText(Configuration.chosenFoodTruck);

        // Station
        String query = "SELECT station_remaining_capacity.stationName FROM cs4400spring2020.station_remaining_capacity WHERE station_remaining_capacity.remaining_capacity > 0";
        ResultSet rs = MySQL.table(query);

        while (rs.next()) {
            stationcombo.getItems().add(rs.getString("stationName"));
        }

        query = "SELECT FoodTruck.stationName FROM cs4400spring2020.foodTruck WHERE FoodTruck.foodTruckName = " +  "\"" + Configuration.chosenFoodTruck + "\"";
        //query = "SELECT FoodTruck.stationName FROM cs4400spring2020.foodTruck WHERE foodTruckName = \"NachoBizness\"";
        rs = MySQL.table(query);
        if (rs.next()){
            //System.out.println(rs.getString("stationName"));
            stationcombo.setValue(rs.getString("stationName"));
        }


        // Staff
        // Unassigned Staff and assign staff
        //-- Query #20a: mn_view_foodTruck_available_staff [Screen #13 Manager Update Food Truck]
        //-- Should show all staff available to be assigned (i.e. all unassigned staff)
        //-- Should be shown as FirstName LastName
        //-- call mn_view_foodTruck_available_staff("LifeIsLikeABoxOfChoco.", "BubbaGumps");
        query = "{call mn_view_foodTruck_available_staff(null, null)}";
        CallableStatement cs = MySQL.conn.prepareCall(query);
        MySQL.procedure(cs);

        query = "SELECT availableStaff FROM cs4400spring2020.mn_view_foodTruck_available_staff_result";
        rs = MySQL.table(query);

        List<String> available_staff = new LinkedList<>();
        while (rs.next()) {
            available_staff.add(rs.getString("availableStaff"));
        }
        //-- Query #20b: mn_view_foodTruck_staff [Screen #13 Manager Update Food Truck]
        //-- Should be shown as FirstName LastName
        //-- call mn_view_foodTruck_staff("JohnJaneAndVenison");
        query = "{call mn_view_foodTruck_staff(?)}";
        cs = MySQL.conn.prepareCall(query);
        cs.setString(1, Configuration.chosenFoodTruck);

        MySQL.procedure(cs);

        query = "SELECT assignedStaff FROM cs4400spring2020.mn_view_foodTruck_staff_result";

        rs = MySQL.table(query);
        List<String> assigned_staff = new LinkedList<>();


        while (rs.next()) {
            assigned_staff.add(rs.getString("assignedStaff"));
            assigned_staff_set.add(rs.getString("assignedStaff"));
        }

        List<String> staff_total = new LinkedList<>();
        staff_total.addAll(assigned_staff);
        staff_total.addAll(available_staff);
        ObservableList<String> items = FXCollections.observableArrayList(staff_total);

        staffListView.setItems(items);
        staffListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        for (int i = 0; i < assigned_staff_set.size(); ++i){
            staffListView.getSelectionModel().select(i);
        }


        staffListView.setOnMouseClicked(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
//                for (int i = 0; i < assigned_staff_set.size(); ++i){
//                    staffListView.getSelectionModel().select(i);
//                }
                ObservableList<String> selectedItems = staffListView.getSelectionModel().getSelectedItems();

                selected_staff.clear();
                for (String item : selectedItems) {
                    selected_staff.add(item);
                }
            }
        });

        // Selected menu-item
        //-- Query #21: mn_view_foodTruck_menu [Screen #13 Manager Update Food Truck]
        //-- call mn_view_foodTruck_menu("NachoBizness");
        query = "{call mn_view_foodTruck_menu(?)}";
        cs = MySQL.conn.prepareCall(query);
        cs.setString(1, Configuration.chosenFoodTruck);
        MySQL.procedure(cs);

        query = "SELECT foodName, CONCAT(foodName , ',' ,price) AS menu_item FROM cs4400spring2020.mn_view_foodTruck_menu_result";
        rs = MySQL.table(query);
        while(rs.next()) {
            foodComboBox.getItems().add(rs.getString("menu_item"));
            menu.add(rs.getString("foodName"));
        }

        // unSelected foodName
        query = "SELECT food.foodName FROM cs4400spring2020.Food";
        rs = MySQL.table(query);
        while (rs.next()) {
            String foodName = rs.getString("foodName");
            if (!menu.contains(foodName)){
                foodComboBox1.getItems().add(foodName);
            }
        }

    }

    public void addBtn2Clicked(ActionEvent event) {
        String foodName = foodComboBox1.getValue();
        String price = pricefield.getText();
        //System.out.println(pricefield.getText());

        if (foodComboBox1.getValue() == null){
            errorText.setText("Please choose a food");
        }
        else if(pricefield.getText().equals("")){
            errorText.setText("Please enter a price");
        }
        else{
            errorText.setText("");

            newly_added_item.put(foodName, price);
            foodComboBox.getItems().add(foodName + "," + price);
            foodComboBox1.getItems().remove(foodName);
            pricefield.setText("");
        }

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

    public void UpdateBtnClicked(ActionEvent actionEvent) throws SQLException{
        String query = "";
        CallableStatement cs;
        ResultSet rs;
//        for (String staff : selected_staff){
//            System.out.println(staff);
//        }
        // station
        if (stationcombo.getValue() == null){
            System.out.println("no station");
            errorText.setText("Please choose a station");
        }
        else {
            errorText.setText("");
            //-- Query #22a: mn_update_foodTruck_station [Screen #13 Manager Update Food Truck]
            //-- added check for capacity
            //-- call mn_update_foodTruck_station("BurgerBird", "Campanile");
            query = "{call mn_update_foodTruck_station(?, ?)}";
            cs = MySQL.conn.prepareCall(query);
            cs.setString(1, Configuration.chosenFoodTruck);
            cs.setString(2, stationcombo.getValue());
            MySQL.procedure(cs);


            // staff
            if (selected_staff.size() != 0) {
                for (String staff_name : selected_staff) {


                    String[] name = staff_name.split(" ");
                    String firstName = name[0];
                    String lastName = name[1];
                    String staff_username = "";
                    //query = "SELECT User.username AS username FROM cs4400spring2020.User";//"/ WHERE `User`.firstName = \"R2D2\" AND 'User'.lastname = \"Droid\"";
                    query = "SELECT `User`.username AS username FROM cs4400spring2020.User WHERE `User`.firstName = " + "\"" + firstName + "\"" + " AND " + "`User`.lastName = " +
                            "\"" + lastName + "\"";
                    rs = MySQL.table(query);
                    if (rs.next()) {
                        staff_username = rs.getString("username");
                        //System.out.println(rs.getString("username"));
                    } else {
                        errorText.setText("staff doesn't exist!");
                    }
                    System.out.println(staff_username);
                    //-- Query #22b: mn_update_foodTruck_staff [Screen #13 Manager Update Food Truck]
                    //-- i_staffName parameter is the Staff's username
                    //-- call mn_update_foodTruck_staff("123", "Staff1");
                    if (assigned_staff_set.contains(staff_name)){

                        query = "{call mn_unassign_foodTruck_staff(?, ?)}";
                        cs = MySQL.conn.prepareCall(query);
                        cs.setString(1, Configuration.chosenFoodTruck);
                        cs.setString(2, staff_username);
                        MySQL.procedure(cs);
                        System.out.println("Succeed");
                    }
                    else {
                        query = "call mn_update_foodTruck_staff(?,?)";
                        cs = MySQL.conn.prepareCall(query);
                        cs.setString(1, Configuration.chosenFoodTruck);
                        cs.setString(2, staff_username);
                        MySQL.procedure(cs);
                    }
                }
            }
            // newly-added-item
            if (newly_added_item.size() != 0){
                for (String food : newly_added_item.keySet()) {
                    //-- Query #22c: mn_update_foodTruck_menu_item [Screen #13 Manager Update Food Truck]
                    //-- This is meant to add a MenuItem to the FoodTruck upon update.
                    //-- call mn_update_foodTruck_menu_item("123", 4.6, "Bagels");
                    query = "{call mn_update_foodTruck_menu_item(?, ?, ?)}";
                    cs = MySQL.conn.prepareCall(query);
                    cs.setString(1, Configuration.chosenFoodTruck);
                    cs.setString(2, newly_added_item.get(food));
                    cs.setString(3, food);
                    MySQL.procedure(cs);
                }
            }
            selected_staff.clear();
            menu.clear();
            newly_added_item.clear();
            foodComboBox1.getItems().clear();
            foodComboBox.getItems().clear();
            stationcombo.getItems().clear();
            assigned_staff_set.clear();

            initialize();
        }
        //initialize();
    }
}
