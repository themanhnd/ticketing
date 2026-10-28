// common/ResourceNotFoundException.java
package com.manhnt.ticketing.inventory.common;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}