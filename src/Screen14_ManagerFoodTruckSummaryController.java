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
import java.sql.*;
import java.time.LocalDate;

public class Screen14_ManagerFoodTruckSummaryController {

    @FXML
    private DatePicker date1;

    @FXML
    private DatePicker date2;

    @FXML
    private TextField foodTruckName;

    @FXML
    private ComboBox<String> StationCombo;

    @FXML
    private GridPane foodtruckgridpane;

    @FXML
    private ComboBox<String> OrderCombo;

    @FXML
    private ComboBox<String> sortByCombo;

    @FXML
    private Text errorText;

    private ToggleGroup radioBtnGroup;

    public void initialize() throws SQLException {
        errorText.setText("");
        // Station combo
        //-- Query #23: mn_get_station [Screen #14 Manager Food Truck Summary]
        //-- Get list of stations that have foodTrucks managed by the given manager
        //-- call mn_get_station("doe.jane");
        String query = "{call mn_get_station(?)}";
        CallableStatement cs = MySQL.conn.prepareCall(query);
        // Manager
        cs.setString(1, Configuration.username);
        MySQL.procedure(cs);

        query = "SELECT stationName FROM cs4400spring2020.mn_get_station_result";
        ResultSet rs = MySQL.table(query);
        while(rs.next()){
            StationCombo.getItems().add(rs.getString("stationName"));
        }

        // sorted by combo
        String[] sortby = new String[]{"foodTruckName", "totalOrder", "totalRevenue", "totalCustomer"};
        sortByCombo.getItems().addAll(sortby);

        // order combo
        String[] order = new String[]{"ASC", "DESC"};
        OrderCombo.getItems().addAll(order);
    }
    public void filterbtnclicked(ActionEvent actionEvent) throws SQLException {
        // call procedure
        //-- Query #24: mn_filter_summary [Screen #14 Manager Food Truck Summary]
        //-- call mn_filter_summary("Manager2", null, null, null, null, null, null);
        String query = "{call mn_filter_summary(?,?,?,?,?,?,?)}";
        CallableStatement cs = MySQL.conn.prepareCall(query);
        // Manager
        cs.setString(1, Configuration.username);
        // FoodTruck
        if (foodTruckName.getText().equals("")){
            cs.setNull(2, Types.VARCHAR);
        }
        else{
            cs.setString(2,foodTruckName.getText());
        }
        // StationName
        if (StationCombo.getValue() == null){
            cs.setNull(3, Types.VARCHAR);
        }
        else{
            String stationName = StationCombo.getValue();
            cs.setString(3, stationName);
        }
        // Min_time
        LocalDate min_date = date1.getValue();
        if (min_date == null){
            cs.setNull(4, Types.DATE);
        }
        else{
            cs.setDate(4, Date.valueOf(min_date));
        }

        // Max_time
        LocalDate max_date = date2.getValue();
        if (min_date == null){
            cs.setNull(5, Types.DATE);
        }
        else{
            cs.setDate(5, Date.valueOf(max_date));
        }

        // sortedBy
        String sortedBy = sortByCombo.getValue();
        if (sortByCombo.getValue() == null ){
            cs.setNull(6, Types.VARCHAR);
        }
        else{
            cs.setString(6, sortedBy);
        }
        // sortedDirection
        String order = OrderCombo.getValue();
        if (OrderCombo.getValue() == null){
            cs.setNull(7, Types.VARCHAR);
        }
        else{
            cs.setString(7, order);
        }

        MySQL.procedure(cs);

//        if (capacityLowerBoundTextField.getText().isEmpty()) cs.setNull(4, Types.INTEGER);
//        else cs.setInt(4, Integer.parseInt(capacityLowerBoundTextField.getText()));
//        if (capacityUpperBoundTextField.getText().isEmpty()) cs.setNull(5, Types.INTEGER);
//        else cs.setInt(5, Integer.parseInt(capacityUpperBoundTextField.getText()));
//        MySQL.procedure(cs);

        // call query
        query = "SELECT foodTruckName, totalOrder, totalRevenue, totalCustomer FROM cs4400spring2020.mn_filter_summary_result";
        ResultSet rs = MySQL.table(query);
        int counter = 1;
        cleanGridPane();
        radioBtnGroup = new ToggleGroup();
        while (rs.next()) {
            RadioButton radioButton = new RadioButton(rs.getString("foodTruckName"));
            Text totalOrder = new Text(rs.getString("totalOrder"));
            Text totalRevenue = new Text(rs.getString("totalRevenue"));
            Text totalCustomer = new Text(rs.getInt("totalCustomer") + "");
            foodtruckgridpane.add(radioButton, 0, counter);
            radioButton.setToggleGroup(radioBtnGroup);
            foodtruckgridpane.add(totalOrder, 1, counter);
            foodtruckgridpane.add(totalRevenue, 2, counter);
            foodtruckgridpane.add(totalCustomer, 3, counter);
            counter++;

        }
    }

    public void backbtnclicked(ActionEvent actionEvent) {
        sceneChange(actionEvent, "Screen3_Home.fxml");
    }


    public void detailbtnclicked(ActionEvent actionEvent) {
        if (radioBtnGroup == null || radioBtnGroup.getSelectedToggle() == null) {
            errorText.setText("Please select a food truck");
        } else {
            Configuration.chosenFoodTruck = ((RadioButton) radioBtnGroup.getSelectedToggle()).getText();
            sceneChange(actionEvent, "Screen15_ManagerSummaryDetail.fxml");
        }
    }
    public void cleanGridPane() {
        foodtruckgridpane.getChildren().clear();
        foodtruckgridpane.add(new Text("Food Truck Name"), 0, 0);
        foodtruckgridpane.add(new Text("Total Order"), 1, 0);
        foodtruckgridpane.add(new Text("Total Revenue"), 2, 0);
        foodtruckgridpane.add(new Text("Customer"), 3, 0);
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
