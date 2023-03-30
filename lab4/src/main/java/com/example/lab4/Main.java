package com.example.lab4;

import com.example.lab4.controller.UserController;
import com.example.lab4.domain.Friendship;
import com.example.lab4.domain.Tuple;
import com.example.lab4.domain.User;
import com.example.lab4.domain.validators.FriendshipValidator;
import com.example.lab4.domain.validators.UserValidator;
import com.example.lab4.repository.Repository;
import com.example.lab4.repository.dbrepo.FriendshipDbRepository;
import com.example.lab4.repository.dbrepo.UserDbRepository;
import com.example.lab4.service.FriendshipService;
import com.example.lab4.service.UserService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;


public class Main extends Application {

    UserService userService;
    FriendshipService friendshipService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        String username="postgres";
        String pasword="miau";
        String url="jdbc:postgresql://localhost:5432/seminarGradleFX";

        Repository<Long, User> utilizatorRepository =
                new UserDbRepository(url,username, pasword,  new UserValidator());
        Repository<Tuple<Long,Long>, Friendship> friendshipRepository =
                new FriendshipDbRepository(url,username, pasword,  new FriendshipValidator());

        userService =new UserService(utilizatorRepository);
        friendshipService = new FriendshipService(friendshipRepository, userService);

        initView(primaryStage);
        primaryStage.setWidth(800);
        primaryStage.setTitle("Social Network lab4");
        primaryStage.show();
    }

    private void initView(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("views/AllUsersView.fxml"));

        AnchorPane userLayout = fxmlLoader.load();
        primaryStage.setScene(new Scene(userLayout));

        UserController userController = fxmlLoader.getController();
        userController.setFriendshipService(friendshipService);
        userController.setUserService(userService);
    }
}