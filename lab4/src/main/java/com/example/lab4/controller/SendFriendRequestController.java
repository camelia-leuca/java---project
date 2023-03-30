package com.example.lab4.controller;

import com.example.lab4.domain.Friendship;
import com.example.lab4.domain.Tuple;
import com.example.lab4.domain.User;
import com.example.lab4.domain.exceptions.ValidationException;
import com.example.lab4.service.FriendshipService;
import com.example.lab4.utils.events.FriendshipChangeEvent;
import com.example.lab4.utils.events.UserChangeEvent;
import com.example.lab4.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SendFriendRequestController implements Observer<FriendshipChangeEvent> {
    FriendshipService friendshipService;
    Long userId;
    @FXML
    TableView<User> tableFriends;
    @FXML
    TableColumn<User,String> tableColumnFirstName;
    @FXML
    TableColumn<User,String> tableColumnLastName;
    @FXML
    ObservableList<User> model = FXCollections.observableArrayList();

    public void setFriendshipService(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
        initModel();
        friendshipService.addObserver(this);
    }

    public void setUserID(Long userId){
        this.userId = userId;
    }

    @FXML
    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        tableFriends.setItems(model);
    }

    private void initModel() {
        List<User> usersNotFriends = this.friendshipService.getUsersNotFriends(this.userId);
        List<User> list = StreamSupport.stream(usersNotFriends.spliterator(), false).toList();
        model.setAll(list);
    }

    @Override
    public void update(FriendshipChangeEvent friendshipChangeEvent) {
        initModel();
    }

    public void handleSendFriendRequest(ActionEvent actionEvent) {
        Long id = (Long) tableFriends.getSelectionModel().getSelectedItem().getId();
        try{
            this.friendshipService.addFriendship(this.userId,id);
        } catch (ValidationException e){
            MessageAlert.showErrorMessage(null, e.getMessage());
            return;
        }
        MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Info", "Cerere de prietenie trimisa cu succes!");
        initModel();
    }
}
