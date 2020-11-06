package com.gng2101groupb32.pathfindr.db;

/**
 * Interface defining required methods for initializing the document ID during
 * Firebase Cloud FireStore operations.
 * <p>
 * FireStore does not add its internal document ID to the document by default, so it must be
 * requested and added manually through setId().
 */
public interface FireStoreDoc {
    String getId();

    void setId(String id);
}
