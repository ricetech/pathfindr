package com.gng2101groupb32.pathfindr.db;

import android.app.Activity;

import com.gng2101groupb32.pathfindr.exceptions.UserNotLoggedInException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class containing several Firebase Cloud FireStore related operations.
 * Most of the methods contained here do NOT verify that the user has the correct permissions
 * or that the data (inputted or outputted) is valid. Those responsibilities lie with the caller
 * of these methods.
 *
 * @author Eric Chen, uOttawa 300136076
 */
public class DBUtils {
    /**
     * Method used to create a new object from a Firebase Cloud FireStore document.
     * <p>
     * The responsibility of checking that the current user is permitted to access the document
     * lies with the caller. This method only verifies that the user is logged in.
     * If the user lacks permissions or the document does not exist, Firebase will pass an error
     * through the failureListener.
     *
     * @param currentActivity - Required for garbage collection.
     * @param successListener - The listener to pass the object back through.
     * @param failureListener - The listener to pass any errors through.
     * @param collectionName  - Name of the FireStore collection to get a document from.
     * @param docName         - Name of the document to retrieve from the collection.
     * @param outputClass     - Class desired for the output object. Must have a constructor with 0
     *                        params.
     * @param <T>             - Class desired for the output object. Must have a constructor with 0
     *                        params.
     */
    public static <T extends FireStoreDoc> void getDocument(Activity currentActivity,
                                                            final OnSuccessListener<T> successListener,
                                                            final OnFailureListener failureListener,
                                                            String collectionName, String docName,
                                                            final Class<T> outputClass) {
        // Get Firebase instances
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Firebase operations are not permitted unless the user is logged in
        if (currentUser == null) {
            failureListener.onFailure(new UserNotLoggedInException("User is not logged in"));
            return;
        }

        // Get document from Cloud FireStore
        DocumentReference userDocRef = db.collection(collectionName).document(docName);
        userDocRef.get().addOnSuccessListener(currentActivity,
                documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Create object from FireStore document
                        // outputObj should never be null since documentSnapshot.exists() is a
                        // prereq to creating the outputObj
                        T outputObj = documentSnapshot.toObject(outputClass);
                        // Add FireStore ID to Document Object
                        outputObj.setId(documentSnapshot.getId());
                        // Return Object through Listener
                        successListener.onSuccess(outputObj);
                    }
                }).addOnFailureListener(currentActivity, failureListener);
    }

    /**
     * Method used to create a List containing a new Object of type OutputClass
     * for each document in the provided collection.
     * <p>
     * The responsibility of checking that the current user is permitted to access EVERY document
     * in the collection lies with the caller, as this method only verifies that the user
     * is logged in.
     * If the user lacks permissions for ANY document in the collection or the document does not
     * exist, Firebase will pass an error through the failureListener.
     *
     * @param currentActivity - Required for garbage collection.
     * @param successListener - The listener to pass the List back through.
     * @param failureListener - The listener to pass any errors through.
     * @param collectionName  - Name of the FireStore collection to get a document from.
     * @param outputClass     - Class desired for the output object. Must have a constructor with 0
     *                        params.
     * @param <T>             - Class desired for the output object. Must have a constructor with 0
     *                        params.
     */
    public static <T extends FireStoreDoc> void getCollection(Activity currentActivity,
                                                              final OnSuccessListener<List<T>> successListener,
                                                              final OnFailureListener failureListener,
                                                              String collectionName, final Class<T> outputClass) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Firebase operations are not permitted unless the user is logged in
        if (currentUser == null) {
            failureListener.onFailure(new UserNotLoggedInException("User is not logged in"));
            return;
        }

        // Get Collection from Cloud FireStore
        db.collection(collectionName).get().addOnSuccessListener(currentActivity,
                queryDocumentSnapshots -> {
                    List<T> collection = new ArrayList<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Convert Document into specified class
                        T obj = Objects.requireNonNull(document.toObject(outputClass));
                        // Add FireStore ID to Document Object
                        obj.setId(document.getId());
                        collection.add(obj);
                    }

                    successListener.onSuccess(collection);
                }).addOnFailureListener(currentActivity, failureListener);
    }
}
