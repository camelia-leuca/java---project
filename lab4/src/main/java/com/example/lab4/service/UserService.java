package com.example.lab4.service;

import com.example.lab4.domain.User;
import com.example.lab4.repository.Repository;
import com.example.lab4.utils.events.ChangeEventType;
import com.example.lab4.utils.events.UserChangeEvent;
import com.example.lab4.utils.observer.Observable;
import com.example.lab4.utils.observer.Observer;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserService implements Observable<UserChangeEvent> {
    private Repository<Long, User> userRepo;
    private List<Observer<UserChangeEvent>> observers=new ArrayList<>();

    public UserService(Repository<Long, User> userRepo) {
        this.userRepo = userRepo;
    }


    public User addUser(User user) {
        if(userRepo.save(user).isEmpty()){
            UserChangeEvent event = new UserChangeEvent(ChangeEventType.ADD, user);
            notifyObservers(event);
            return null;
        }
        return user;
    }

    public User deleteUser(Long id){
        Optional<User> user= userRepo.delete(id);
        if (user.isPresent()) {
            notifyObservers(new UserChangeEvent(ChangeEventType.DELETE, user.get()));
            return user.get();}
        return null;
    }

    public User updateUser(User updatedUser){
        Optional<User> user= userRepo.update(updatedUser);
        if (user.isPresent()) {
            notifyObservers(new UserChangeEvent(ChangeEventType.UPDATE, user.get()));
            return user.get();}
        return null;
    }

    public Iterable<User> getAll(){
        return userRepo.findAll();
    }

    public User findOne(Long id){
        return this.userRepo.findOne(id);
    }

    @Override
    public void addObserver(Observer<UserChangeEvent> e) {
        observers.add(e);

    }

    @Override
    public void removeObserver(Observer<UserChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(UserChangeEvent t) {
        observers.stream().forEach(x->x.update(t));
    }

}
