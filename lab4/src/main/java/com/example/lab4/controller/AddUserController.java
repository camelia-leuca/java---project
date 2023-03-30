package com.example.lab4.controller;

import com.example.lab4.domain.User;
import com.example.lab4.domain.exceptions.ValidationException;
import com.example.lab4.service.UserService;
import com.example.lab4.utils.events.UserChangeEvent;
import com.example.lab4.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AddUserController implements Observer<UserChangeEvent> {
    UserService userService;
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    ObservableList<User> model = FXCollections.observableArrayList();

    public void setUserService(UserService userService) {
        this.userService = userService;
        initModel();
        userService.addObserver(this);
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

    public void handleAddUser(ActionEvent actionEvent) {
        try {
            String firstName = firstNameTextField.getText();
            String lastName = lastNameTextField.getText();
            User newUser = new User(firstName, lastName);
            userService.addUser(newUser);
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "INFO", "Adaugare cu succes!");
        } catch (ValidationException e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }
}
