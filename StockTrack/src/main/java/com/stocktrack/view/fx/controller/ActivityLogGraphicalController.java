package com.stocktrack.view.fx.controller;

import com.stocktrack.bean.ActivityLogBean;
import com.stocktrack.controller.ActivityLogController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ActivityLogGraphicalController {
    @FXML private TableView<ActivityLogBean> activityTable;
    @FXML private TableColumn<ActivityLogBean, String> timestampCol;
    @FXML private TableColumn<ActivityLogBean, String> usernameCol;
    @FXML private TableColumn<ActivityLogBean, String> actionCol;
    @FXML private TableColumn<ActivityLogBean, String> descriptionCol;

    @FXML
    public void initialize() {
        timestampCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        actionCol.setCellValueFactory(new PropertyValueFactory<>("actionType"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        activityTable.setItems(FXCollections.observableArrayList());
    }

    public void loadData() {
        ActivityLogController controller = new ActivityLogController();
        try {
            activityTable.setItems(FXCollections.observableArrayList(controller.getRecentActivities()));
        } catch (Exception e) {
            showAlert(e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        loadData();
    }

    private void showAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore caricamento attivita");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
