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

public class PendingRequestsController implements Observer<FriendshipChangeEvent> {
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
        List<User> pendingRequests = this.friendshipService.getPendingRequestsForUser(this.userId);
        List<User> list = StreamSupport.stream(pendingRequests.spliterator(), false).toList();
        model.setAll(list);
    }

    @Override
    public void update(FriendshipChangeEvent friendshipChangeEvent) {
        initModel();
    }
    public void handleAcceptRequest(ActionEvent actionEvent) {
        Long id = (Long) tableFriends.getSelectionModel().getSelectedItem().getId();
        try{
            this.friendshipService.acceptFriendRequest(this.userId,id);
        } catch (ValidationException e){
            MessageAlert.showErrorMessage(null, e.getMessage());
            return;
        }
        MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Info", "Prieten adaugat cu succes!");
        initModel();
    }

    public void handleDeleteRequest(ActionEvent actionEvent) {
        Long id = (Long) tableFriends.getSelectionModel().getSelectedItem().getId();
        try{
            Tuple<Long, Long> friendshipId = new Tuple<>(this.userId, id);
            this.friendshipService.deleteFriendship(friendshipId);
        } catch (ValidationException e){
            MessageAlert.showErrorMessage(null, e.getMessage());
            return;
        }
        MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Info", "Cerere stearsa!");
        initModel();
    }
}
