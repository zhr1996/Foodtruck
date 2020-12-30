import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Screen12ManagerCreateFoodTruckController {

    @FXML
    private TextField nameTextField;

    @FXML
    private ListView<String> staffListView;

    @FXML
    private TextField nameTextField1;

    @FXML
    private ComboBox<String> stationComboBox;

    @FXML
    private ComboBox<String> foodComboBox;


    @FXML
    private ComboBox<String> foodComboBox1;

    @FXML
    private Text errorText;

    List<String> selected_staff = new LinkedList<>();
    Map<String, Double> menu = new HashMap<>();

    public void initialize() throws SQLException {
        errorText.setText("");

        // Station
        String query = "SELECT station_remaining_capacity.stationName FROM cs4400spring2020.station_remaining_capacity WHERE station_remaining_capacity.remaining_capacity > 0";
        ResultSet rs = MySQL.table(query);
        while (rs.next()) {
            stationComboBox.getItems().add(rs.getString("stationName"));
        }
        // Food
        // Not implemented thoroughly because any time it will give all the foods in the menu
        query = "SELECT food.foodName FROM cs4400spring2020.Food";
        rs = MySQL.table(query);
        while (rs.next()) {
            foodComboBox.getItems().add(rs.getString("foodName"));
            //foodComboBox1.getItems().add(rs.getString("foodName"));
        }
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
        while(rs.next()){
            available_staff.add(rs.getString("availableStaff"));
        }

        ObservableList<String> items = FXCollections.observableArrayList(available_staff);

        staffListView.setItems(items);
        staffListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        staffListView.setOnMouseClicked(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                ObservableList<String> selectedItems =
                        staffListView.getSelectionModel().getSelectedItems();

                selected_staff.clear();
                for (String s : selectedItems) {
                    selected_staff.add(s);
                }
            }
        });
    }

    public void addBtn1Clicked(ActionEvent actionEvent) {
        String food_name = foodComboBox.getValue();
        if (nameTextField1.getText().equals("")){
            errorText.setText("Please enter a price");
        }
        else {
            errorText.setText("");
            Double price = Double.parseDouble(nameTextField1.getText());

            if (!menu.containsKey(food_name)) {
                foodComboBox1.getItems().add(food_name);
            }
            menu.put(food_name, price);
        }
    }

    public void minusBtn2Clicked(ActionEvent actionEvent) {
        String food_name = foodComboBox1.getValue();
        if (food_name != null) {
            menu.remove(food_name);
            foodComboBox1.getItems().remove(food_name);
        }
    }

    public void createBtnClicked(ActionEvent actionEvent) throws SQLException {
        if (nameTextField.getText().equals("")){
            errorText.setText("Please enter a food truck name");
        }
        else if (stationComboBox.getValue() == null){
            errorText.setText("Please select at least one station.");
        }
        else if (menu.size() < 1 ){
            errorText.setText("Please select at least one menu item");
        }
        else if(selected_staff.size() < 1) {
            errorText.setText("Please select at least one staff");
        }
        else {
            errorText.setText("");
            String query = "";
            CallableStatement cs;
            ResultSet rs;
            // Assign food truck to station
            //-- Query #19a: mn_create_foodTruck_add_station [Screen #12 Manager Create Food Truck]
            //-- call mn_create_foodTruck_add_station("123", "Campanile", "doe.jane");
            query = "{call mn_create_foodTruck_add_station(?,?,?)}";
            cs = MySQL.conn.prepareCall(query);
            cs.setString(1, nameTextField.getText());
            cs.setString(2, stationComboBox.getValue());
            cs.setString(3, Configuration.username);
            MySQL.procedure(cs);

            // Assign staff
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
                //-- Query #19b: mn_create_foodTruck_add_staff [Screen #12 Manager Create Food Truck]
                //-- i_staffName parameter is the Staff's username
                //-- call mn_create_foodTruck_add_staff("123", "2Cool_not_todoschool");
                query = "call mn_create_foodTruck_add_staff(?,?)";
                cs = MySQL.conn.prepareCall(query);
                cs.setString(1, nameTextField.getText());
                cs.setString(2, staff_username);
                MySQL.procedure(cs);
            }

            // Assign food
            for (String food : menu.keySet()) {
                Double price = menu.get(food);
                //-- Query #19c: mn_create_foodTruck_add_menu_item [Screen #12 Manager Create Food Truck]
                //-- call mn_create_foodTruck_add_menu_item("BurgerBird","1.67345" , "MargheritaPizza");
                query = "{call mn_create_foodTruck_add_menu_item(?,?,?)}";
                cs = MySQL.conn.prepareCall(query);
                System.out.println(nameTextField.getText());
                System.out.println(price);
                System.out.println(food);
                cs.setString(1, nameTextField.getText());
                cs.setString(2, String.valueOf(price));
                cs.setString(3, food);
                System.out.println(cs);
                MySQL.procedure(cs);
            }
            nameTextField.setText("");
            nameTextField1.setText("");
            menu.clear();
            selected_staff.clear();
            initialize();
        }
    }

    public void backBtnClicked(ActionEvent actionEvent) {
        sceneChange(actionEvent, "Screen11_ManagerManageFoodTruck.fxml");
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
