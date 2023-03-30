package com.example.lab4.controller;

import com.example.lab4.Main;
import com.example.lab4.domain.Friendship;
import com.example.lab4.domain.Tuple;
import com.example.lab4.domain.User;
import com.example.lab4.domain.exceptions.ValidationException;
import com.example.lab4.service.FriendshipService;
import com.example.lab4.service.UserService;
import com.example.lab4.utils.events.FriendshipChangeEvent;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendshipController implements Observer<FriendshipChangeEvent> {
    FriendshipService friendshipService;
    Long id;
    UserService userService;
    ObservableList<User> model = FXCollections.observableArrayList();

    @FXML
    TableView<User> tableFriends;
    @FXML
    TableColumn<User,String> FriendFirstName;
    @FXML
    TableColumn<User,String> FriendLastName;

    public void setUserService(UserService service) {
        this.userService = service;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFriendshipService(FriendshipService service) {
        this.friendshipService = service;
        service.addObserver(this);
        initModel();
    }

    @FXML
    public void initialize() {
        FriendFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("FirstName"));
        FriendLastName.setCellValueFactory(new PropertyValueFactory<User, String>("LastName"));
        tableFriends.setItems(model);
    }


    private void initModel() {
        List<User> friends = this.friendshipService.getFriendsForUser(this.id);
        List<User> friendsForUSer = StreamSupport.stream(friends.spliterator(), false).toList();
        model.setAll(friendsForUSer);
    }

    @Override
    public void update(FriendshipChangeEvent friendshipChangeEvent) {
        initModel();
    }

    public void handleSendFriendRequest(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("views/SendFriendRequestView.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Trimite o cerere de prietenie");
        SendFriendRequestController sendFriendRequestController = fxmlLoader.getController();
        sendFriendRequestController.setUserID(this.id);
        sendFriendRequestController.setFriendshipService(friendshipService);
        stage.show();
    }

    public void handleShowPendingRequests(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("views/PendingRequestsView.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Cereri de prietenie");
        PendingRequestsController pendingRequestsController = fxmlLoader.getController();
        pendingRequestsController.setUserID(this.id);
        pendingRequestsController.setFriendshipService(friendshipService);
        stage.show();
    }

    public void handleDeleteFriendship(ActionEvent actionEvent) {
        Long id = (Long) tableFriends.getSelectionModel().getSelectedItem().getId();
        try{
            Tuple<Long, Long> friendshipId = new Tuple<>(this.id, id);
            this.friendshipService.deleteFriendship(friendshipId);
        } catch (ValidationException e){
            MessageAlert.showErrorMessage(null, e.getMessage());
            return;
        }
        MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Info", "Prieten sters cu succes!");
        initModel();
    }
}
