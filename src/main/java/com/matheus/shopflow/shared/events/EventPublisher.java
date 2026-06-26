package com.matheus.shopflow.shared.events;

public interface EventPublisher {
    void publish(Object event);
}