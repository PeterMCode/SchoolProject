package controller;

import com.example.schoolproject.HelloApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Inventory;
import model.Part;
import model.Product;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * controller.ModifyProductController
 * @author Peter Moffett
 */
public class ModifyProductController implements Initializable {
    public TableView allTable;
    public TableColumn allPartIDCol;
    public TableColumn allNameCol;
    public TableColumn allInventoryCol;
    public TableColumn allPriceCol;
    public TableView fewTable;
    public TableColumn fewPartIDCol;
    public TableColumn fewNameCol;
    public TableColumn fewInventoryCol;
    public TableColumn fPriceCol;
    public Button addAssociatedPartB;
    public Button deleteAssociatedPartB;
    public TextField productIDField;
    public TextField productMinField;
    public TextField productNameField;
    public TextField productInventoryField;
    public TextField productPriceField;
    public TextField productMaxField;
    public TextField searchPartString;

    Product selectedProduct;

    private ObservableList<Part> associatedParts = FXCollections.observableArrayList();

    /**
     * Initialize the Controller
     * Populate the text fields with selected product data
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        System.out.println("AddProduct!");
        selectedProduct = MainMenuController.productmodify();
        associatedParts = selectedProduct.getAssociatedParts();

        allTable.setItems(Inventory.getAllParts());
        allPartIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        allNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        allInventoryCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        allPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        fewTable.setItems(MainMenuController.productmodify().getAllAssociatedPart());
        fewPartIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        fewNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        fewInventoryCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        fPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        productIDField.setText(String.valueOf(selectedProduct.getId()));
        productNameField.setText(selectedProduct.getName());
        productInventoryField.setText(String.valueOf(selectedProduct.getStock()));
        productPriceField.setText(String.valueOf(selectedProduct.getPrice()));
        productMaxField.setText(String.valueOf(selectedProduct.getMax()));
        productMinField.setText(String.valueOf(selectedProduct.getMin()));
    }

    /**
     * add selectedparts to the associatedpart table
     * if part wasn't selected, the Warning Dialog box pops up
     *
     * @param actionEvent add button action event
     */
    public void addAssociatedPart(ActionEvent actionEvent) {
        System.out.println("On Add Button!");
        Part selectedItem = (Part) allTable.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("This is an error!");
            alert.setHeaderText("Please select an item to add!");
            alert.setContentText("Part wasn't selected.  Please select the Part");
            alert.showAndWait();
        } else {
            associatedParts.add(selectedItem);
            fewTable.setItems(associatedParts);
        }
    }

    /**
     * remove selectedparts from the associatedTable
     * if part was not selected, the Warning Dialog box popped up
     *
     * @param actionEvent Remove Associated Button event
     */
    public void deleteAssociatedPart(ActionEvent actionEvent) {
        System.out.println("On Remove Button!");
        Part selectedItem = (Part) fewTable.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("This is an error!");
            alert.setHeaderText("Select an item to remove!");
            alert.setContentText("Part wasn't selected.  Please select a part.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("This is an alert!!!");
            alert.setHeaderText("Selected part will be deleted!");
            alert.setContentText("Are you positive you want to delete selected part?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                associatedParts.remove(selectedItem);
                fewTable.setItems(associatedParts);
            }
        }
    }

    /**
     * ModifyProduct closes, and application returns to MainMenu
     *
     * @param actionEvent Cancel Button Action event
     * @throws IOException
     */
    public void onClickCancelProduct(ActionEvent actionEvent) throws IOException {
        //Parent root = FXMLLoader.load(getClass().getResource("/view/mainMenu.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("mainMenu.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load(), 1110, 600);
        stage.setTitle("Main Menu");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Product was modified and returned to Main Menu after validating and logic are checked
     *
     * @param actionEvent Save Button Action event
     * @throws IOException
     */
    public void onClickSaveProduct(ActionEvent actionEvent) throws IOException {
        ObservableList<Product> products = Inventory.getAllProducts();

        int id = selectedProduct.getId();
        String name = productNameField.getText();
        String price = productPriceField.getText();
        String inventory = productInventoryField.getText();
        String max = productMaxField.getText();
        String min = productMinField.getText();

        boolean passed = false;
        try {

            boolean isinventoryvalid = true;
            boolean isminvalid = true;

            if (Integer.parseInt(min) < 0 || Integer.parseInt(max) < 0 || Integer.parseInt(inventory) < 0 || Double.parseDouble(price) < 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("This is an error!");
                alert.setHeaderText("Form contains unacceptable data!");
                alert.setContentText("Negative numbers are invalid!");
                alert.showAndWait();
            }

            if (Integer.parseInt(min) > 0 && Integer.parseInt(max) > 0 && Integer.parseInt(inventory) > 0 && Double.parseDouble(price) > 0) {
                if (Integer.parseInt(min) >= Integer.parseInt(max) || Integer.parseInt(min) <= 0) {
                    isminvalid = false;
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("There is an error!");
                    alert.setHeaderText("Form contains unacceptable data!");
                    alert.setContentText("Min cannot be greater than Max.");
                    alert.showAndWait();
                } else if (Integer.parseInt(inventory) > Integer.parseInt(max) || Integer.parseInt(inventory) < Integer.parseInt(min)) {
                    isinventoryvalid = false;
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("There is an error!");
                    alert.setHeaderText("Form contains unacceptable data!");
                    alert.setContentText("Inventory must fall between Min and Max.");
                    alert.showAndWait();
                }
            } else {
                isminvalid = false;
                isinventoryvalid = false;
            }

            if (isminvalid && isinventoryvalid) {
                Product newProduct = new Product(
                        //Integer.parseInt(productIDField.getText())
                        id
                        , name
                        , Double.parseDouble(price)
                        , Integer.parseInt(inventory)
                        , Integer.parseInt(min)
                        , Integer.parseInt(max));

                for (Part part : associatedParts) {
                    newProduct.addAssociatedPart(part);
                }

                Inventory.addProduct(newProduct);
                Inventory.deleteProduct(selectedProduct);
                passed = true;
            }

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("There is an error!");
            alert.setHeaderText("There is an error adding product!");
            alert.setContentText("Form contains blank fields or unacceptable data.");
            alert.showAndWait();
        }

        if (passed) {
            //Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/mainMenu.fxml")));        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("mainMenu.fxml"));
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("mainMenu.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(fxmlLoader.load(), 1110, 600);
            stage.setTitle("Main Menu");
            stage.setScene(scene);
            stage.show();
        }

    }

    /**
     * search based on value in searchbox textfield
     *
     * @param actionEvent
     */
    /*public void onSearchPart(ActionEvent actionEvent) {
        if (!searchPartString.getText().trim().isEmpty()) {
            try {
                int id = Integer.parseInt(searchPartString.getText());
                for (Part q : Inventory.getAllParts()) {
                    if (q.getId() == id) {
                        allTable.getSelectionModel().select(q);
                    }
                }
            } catch (NumberFormatException e) {
                String Name = (searchPartString.getText());
                for (Part q : Inventory.getAllParts()) {
                    if (q.getName().equals(Name)) {
                        allTable.getSelectionModel().select(q);
                    }
                }
            }
        }
    }
}*/

    public void onSearchPart(ActionEvent actionEvent) {

        String searchText = searchPartString.getText();

        ObservableList<Part> parts = Inventory.lookupPart(searchText);

        if (parts.isEmpty()) {
            try {
                int searchId = Integer.parseInt(searchText);
                Part searchPart = Inventory.lookupPart(searchId);
                if (searchPart != null) {
                    parts.add(searchPart);
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText("No Match");
                    alert.setContentText("No part matches found.");
                    alert.showAndWait();
                }
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("No Match");
                alert.setContentText("No part matches found.");
                alert.showAndWait();
            }
        }

        allTable.setItems(parts);
    }
}