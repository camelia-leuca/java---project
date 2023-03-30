package com.example.lab4.utils.observer;


import com.example.lab4.utils.events.Event;

public interface Observer<E extends Event> {
    void update(E e);
}