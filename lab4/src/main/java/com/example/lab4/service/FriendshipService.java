package com.example.lab4.service;

import com.example.lab4.domain.Friendship;
import com.example.lab4.domain.Tuple;
import com.example.lab4.domain.User;
import com.example.lab4.repository.Repository;
import com.example.lab4.utils.Constants;
import com.example.lab4.utils.events.ChangeEventType;
import com.example.lab4.utils.events.FriendshipChangeEvent;
import com.example.lab4.utils.observer.Observable;
import com.example.lab4.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class FriendshipService implements Observable<FriendshipChangeEvent> {
    private Repository<Tuple<Long,Long>, Friendship> friendshipRepo;
    private List<Observer<FriendshipChangeEvent>> observers=new ArrayList<>();
    private UserService userService;

    public FriendshipService(Repository<Tuple<Long, Long>, Friendship> friendshipRepo, UserService userService) {
        this.friendshipRepo = friendshipRepo;
        this.userService = userService;
    }

    public Friendship addFriendship(Long firstId, Long secondId){
        Tuple<Long,Long> id = new Tuple<>(firstId, secondId);
        String date = LocalDateTime.now().format(Constants.DATE_FORMATTER);
        Friendship friendship = new Friendship(id, "pending",date);

        if( friendshipRepo.save(friendship).isEmpty()){
            FriendshipChangeEvent event = new FriendshipChangeEvent(ChangeEventType.ADD, friendship);
            notifyObservers(event);
            return null;
        }
        return friendship;
    }

    public Friendship deleteFriendship(Tuple<Long,Long> id){
        Optional<Friendship> friendship= friendshipRepo.delete(id);
        if (friendship.isPresent()) {
            notifyObservers(new FriendshipChangeEvent(ChangeEventType.DELETE, friendship.get()));
            return friendship.get();}
        return null;
    }

    public List<User> getFriendsForUser(Long id){
        List<User> friends = new ArrayList<>();
        Iterable<Friendship> friendships = this.friendshipRepo.findAll();
        for(Friendship f: friendships) {
            if (f.getFirstId() == id && f.getStatus().equals("accepted")) {
                User friend = this.userService.findOne(f.getSecondId());
                friends.add(friend);
            }
            if (f.getSecondId() == id && f.getStatus().equals("accepted")) {
                User friend = this.userService.findOne(f.getFirstId());
                friends.add(friend);
            }
        }
        return friends;
    }

    public Friendship acceptFriendRequest(Long firstId, Long secondId)
    {
        Tuple<Long,Long> id = new Tuple<>(firstId, secondId);
        Friendship friendship = this.friendshipRepo.findOne(id);
        friendship.setStatus("accepted");

        if( friendshipRepo.update(friendship).isEmpty()){
            FriendshipChangeEvent event = new FriendshipChangeEvent(ChangeEventType.UPDATE, friendship);
            notifyObservers(event);
            return null;
        }
        return friendship;
    }

    public List<User> getPendingRequestsForUser(Long id){
        List<User> friends = new ArrayList<>();
        Iterable<Friendship> friendships = this.friendshipRepo.findAll();
        for(Friendship f: friendships) {
            if (f.getFirstId() == id && f.getStatus().equals("pending")) {
                User friend = this.userService.findOne(f.getSecondId());
                friends.add(friend);
            }
            if (f.getSecondId() == id && f.getStatus().equals("pending")) {
                User friend = this.userService.findOne(f.getFirstId());
                friends.add(friend);
            }
        }
        return friends;
    }

    public List<User> getUsersNotFriends(Long id){
        List<User> usersNotFriends = new ArrayList<>();
        List<User> friends = this.getFriendsForUser(id);
        List<User> pendingFriend = this.getPendingRequestsForUser(id);
        Iterable<User> allUsers = this.userService.getAll();
        for(User u: allUsers){
            if(!friends.contains(u) && !pendingFriend.contains(u) && !id.equals(u.getId()))
                usersNotFriends.add(u);
        }
        return usersNotFriends;
    }

    public Iterable<Friendship> getAll(){
        return this.friendshipRepo.findAll();
    }

    @Override
    public void addObserver(Observer<FriendshipChangeEvent> e) {
        observers.add(e);

    }

    @Override
    public void removeObserver(Observer<FriendshipChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(FriendshipChangeEvent t) {
        observers.stream().forEach(x->x.update(t));
    }

}
