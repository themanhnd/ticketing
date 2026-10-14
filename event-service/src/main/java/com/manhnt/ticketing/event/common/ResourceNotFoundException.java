// common/ResourceNotFoundException.java
package com.manhnt.ticketing.event.common;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}