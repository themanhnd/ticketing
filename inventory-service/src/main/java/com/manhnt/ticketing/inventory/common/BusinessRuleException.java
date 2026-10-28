// common/BusinessRuleException.java
package com.manhnt.ticketing.inventory.common;

public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
