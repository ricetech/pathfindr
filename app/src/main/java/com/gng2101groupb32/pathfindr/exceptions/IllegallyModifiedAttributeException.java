package com.gng2101groupb32.pathfindr.exceptions;

/**
 * Exception thrown when an attempt is made to modify a read-only Firebase Firestore property.
 */
public class IllegallyModifiedAttributeException extends IllegalStateException {
    public IllegallyModifiedAttributeException(String message) {
        super(message);
    }
}
