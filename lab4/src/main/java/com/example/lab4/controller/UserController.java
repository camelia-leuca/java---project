package com.example.lab4.controller;


import com.example.lab4.Main;
import com.example.lab4.domain.User;
import com.example.lab4.domain.exceptions.ValidationException;
import com.example.lab4.service.FriendshipService;
import com.example.lab4.service.UserService;
import com.example.lab4.utils.events.UserChangeEvent;
import com.example.lab4.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserController implements Observer<UserChangeEvent> {
    UserService userService;
    FriendshipService friendshipService;
    ObservableList<User> model = FXCollections.observableArrayList();


    @FXML
    TableView<User> tableView;
    @FXML
    TableColumn<User,String> tableColumnFirstName;
    @FXML
    TableColumn<User,String> tableColumnLastName;

    public void setUserService(UserService service) {
        this.userService = service;
        service.addObserver(this);
        initModel();
    }

    public void setFriendshipService(FriendshipService service) {
        this.friendshipService = service;
    }

    @FXML
    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        tableView.setItems(model);
    }

        private void initModel() {
        Iterable<User> allUsers = userService.getAll();
        List<User> users = StreamSupport.stream(allUsers.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(users);
    }

    @Override
    public void update(UserChangeEvent userChangeEvent) {
        initModel();
    }

    public void handleAddUser(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("views/AddUserView.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Adauga un utilizator");
        AddUserController addUserController = fxmlLoader.getController();
        addUserController.setUserService(userService);
        stage.show();
    }

    public void handleDeleteUser(ActionEvent actionEvent) {
        Long id = (Long) tableView.getSelectionModel().getSelectedItem().getId();
        try{
            User saved = userService.deleteUser(id);
        } catch (ValidationException e){
            MessageAlert.showErrorMessage(null, e.getMessage());
            return;
        }
        MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Info", "User sters cu succes!");
        initModel();
    }

    public void handleShowFriends(ActionEvent actionEvent) throws IOException {
        Long id = (Long) tableView.getSelectionModel().getSelectedItem().getId();
        tableView.getSelectionModel().clearSelection();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("views/FriendshipView.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Prietenii utilizatorului");
        FriendshipController friendshipController = fxmlLoader.getController();
        friendshipController.setId(id);
        friendshipController.setUserService(this.userService);
        friendshipController.setFriendshipService(this.friendshipService);
        stage.show();
    }

}
