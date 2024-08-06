package com.example.mapp.model.util;

/**
 * An interface that forces our implementing entity classes
 * to provide a method to uppercase the entity name on persist/updates
 */
public interface UppercasedEntity {

    void uppercaseName();
}
