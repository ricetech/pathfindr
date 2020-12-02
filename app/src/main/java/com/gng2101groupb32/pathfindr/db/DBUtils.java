package com.gng2101groupb32.pathfindr.db;

import android.app.Activity;

import com.gng2101groupb32.pathfindr.exceptions.UserNotLoggedInException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Class containing several Firebase Cloud Firestore related operations. Most of the methods
 * contained here do NOT verify that the user has the correct permissions or that the data (inputted
 * or outputted) is valid. Those responsibilities lie with the caller of these methods.
 *
 * @author Eric Chen, uOttawa 300136076
 */
@SuppressWarnings("RedundantSuppression")
public class DBUtils {
    /**
     * Returns a DocumentReference from the provided collection and document name.
     * Should only be used where necessary - for example, to fill in a 'user' field in
     * ServiceRequest.
     * <p>
     * This method provides no verification whatsoever - a DocumentReference can be returned for
     * nonexistent documents.
     *
     * @param collection - Name of the collection in Firebase Cloud FireStore.
     * @param document   - Name of the document in the collection.
     *
     * @return - A DocumentReference referring to the given document.
     */
    public static DocumentReference getRef(String collection, String document) {
        return FirebaseFirestore.getInstance().collection(collection).document(document);
    }

    /**
     * Returns a CollectionReference from the provided collection name.
     * Should only be used where necessary - for example, to create a Query.
     * <p>
     * This method provides no verification whatsoever - a CollectionReference can be returned for
     * nonexistent collections.
     *
     * @param collection - Name of the collection in Firebase Cloud FireStore.
     *
     * @return - A CollectionReference referring to the given collection.
     */
    public static CollectionReference getCollectionRef(String collection) {
        return FirebaseFirestore.getInstance().collection(collection);
    }

    /**
     * Method used to create a new object from a Firebase Cloud Firestore document using
     * a provided document reference.
     * <p>
     * The responsibility of checking that the current user is permitted to access the document
     * lies with the caller. This method only verifies that the user is logged in.
     * If the user lacks permissions, Firebase will pass an error through the failureListener.
     * If the document doesn't exist, a null object will be returned.
     *
     * @param currentActivity - Required for garbage collection.
     * @param successListener - The listener to pass the object back through.
     * @param failureListener - The listener to pass any errors through.
     * @param docRef          - The Document Reference to retrieve. Should be one that is
     *                        provided by Firebase or from DBUtils.getRef().
     * @param outputClass     - Class desired for the output object. Must have a constructor with 0
     *                        params.
     * @param <T>             - Class desired for the output object. Must have a constructor with 0
     *                        params.
     */
    public static <T extends FireStoreDoc> void getDoc(
            Activity currentActivity,
            final OnSuccessListener<T> successListener, final OnFailureListener failureListener,
            DocumentReference docRef, final Class<T> outputClass) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Firebase operations are not permitted unless the user is logged in
        if (currentUser == null) {
            failureListener.onFailure(new UserNotLoggedInException("User is not logged in"));
            return;
        }

        docRef.get().addOnSuccessListener(
                currentActivity,
                documentSnapshot -> {
                    if (documentSnapshot
                            .exists()) //noinspection RedundantSuppression
                    {
                        // Create object from Firestore document
                        // outputObj should never be null since documentSnapshot.exists() is a
                        // prereq to creating the outputObj
                        T outputObj =
                                documentSnapshot.toObject(outputClass);
                        // Add FireStore ID to Document Object
                        //noinspection ConstantConditions
                        outputObj.setId(documentSnapshot.getId());
                        // Return Object through Listener
                        successListener.onSuccess(outputObj);
                    } else {
                        successListener.onSuccess(null);
                    }
                }).addOnFailureListener(currentActivity, failureListener);
    }

    /**
     * Creates a document in Firebase Firestore. The object's id will be updated with the
     * Firestore auto-generated id after a successful creation.
     * Prerequisites:
     * - User is logged into Firebase Auth with a valid email and has been assigned a UID.
     * - The document does not exist. If it already exists, use DBUtils.updateDoc().
     *
     * @param currentActivity - The current activity. Required so that the listener can be stopped
     *                        if its attached activity is stopped to avoid memory leaks.
     * @param successListener - Listener evaluating to void if the creation is successful.
     * @param failureListener - Listener for handling errors, such as user not logged in.
     * @param collection      - The collection to create the document in.
     * @param document        - Object containing the document.
     *
     * @throws UserNotLoggedInException - If the user is not logged in. Thrown through
     *                                  failureListener.
     */
    public static <T extends FireStoreDoc> void createDoc(
            Activity currentActivity,
            final OnSuccessListener<Void> successListener, final OnFailureListener failureListener,
            String collection, T document) {
        // Get Firebase instances
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Verify user is logged in
        if (currentUser == null) {
            failureListener.onFailure(new UserNotLoggedInException("User is not logged in"));
            return;
        }

        CollectionReference docRef = db.collection(collection);

        // Push data to Firestore
        docRef.add(document)
              .addOnSuccessListener(currentActivity, newDoc -> {
                  // Set this object id to the appropriate one
                  document.setId(newDoc.getId());

                  successListener.onSuccess(null);
              })
              .addOnFailureListener(currentActivity, failureListener);
    }

    /**
     * Sets a document at the FireStore location defined by docRef.
     * This will create a new document at that location if it does not exist, or
     * will replace/override the document at that location if it already exists,
     * DELETING the previous document at that docRef AND its fields.
     * To update only some of a document's fields, use DBUtils.updateFields().
     *
     * @param currentActivity - The current activity. Required so that the listener can be stopped
     *                        if its attached activity is stopped to avoid memory leaks.
     * @param successListener - Listener evaluating to void if the overwrite is successful.
     * @param failureListener - Listener for handling errors, such as user not logged in.
     * @param docRef          - Document Reference of the Document to be set.
     * @param document        - Object containing the Document to be set.
     *
     * @throws UserNotLoggedInException - If the user is not logged in.
     *                                  Thrown through failureListener.
     * @throws IllegalStateException    - If the id attribute is null.
     */
    public static <T extends FireStoreDoc> void setDoc(
            Activity currentActivity,
            final OnSuccessListener<Void> successListener, final OnFailureListener failureListener,
            DocumentReference docRef, T document) {

        // Verify user is logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            failureListener.onFailure(new UserNotLoggedInException("User is not logged in"));
            return;
        }

        // Check for empty ID
        if (document.getId() == null) {
            failureListener.onFailure(new IllegalStateException("id is null. " +
                                                                        "Make sure that the document exists in the database."));
        }

        // Update the data. Override whatever is currently in the database with this object.
        docRef.set(document)
              .addOnSuccessListener(currentActivity, successListener)
              .addOnFailureListener(currentActivity, failureListener);
    }

    /**
     * Updates the fields of a document at the FireStore location defined by docRef.
     * This will only update the specified fields of the document,
     * leaving the rest of the fields in their original state.
     * To overwrite an entire document, use DBUtils.setDoc().
     *
     * @param currentActivity - The current activity. Required so that the listener can be stopped
     *                        if its attached activity is stopped to avoid memory leaks.
     * @param successListener - Listener evaluating to void if the update is successful.
     * @param failureListener - Listener for handling errors, such as user not logged in.
     * @param docRef          - Document Reference of the Document to be updated.
     * @param fields          - Map containing the Fields to be updated.
     *
     * @throws UserNotLoggedInException - If the user is not logged in.
     *                                  Thrown through failureListener.
     * @throws IllegalStateException    - If the id attribute is null.
     */
    public static void updateFields(
            Activity currentActivity,
            final OnSuccessListener<Void> successListener, final OnFailureListener failureListener,
            DocumentReference docRef, Map<String, Object> fields) {

        // Verify user is logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            failureListener.onFailure(new UserNotLoggedInException("User is not logged in"));
            return;
        }

        // Update the data. Will only overwrite the fields specified.
        docRef.update(fields)
              .addOnSuccessListener(currentActivity, successListener)
              .addOnFailureListener(currentActivity, failureListener);
    }

    /**
     * Method used to create a List containing a new Object of type OutputClass
     * for each document in the provided collection.
     * <p>
     * The caller is responsible for:
     * - Ensuring that the User is logged in
     * - Ensuring that the User has sufficient permissions to access EVERY Document in the
     * collection. If permissions are insufficient for even ONE Document,
     * the ENTIRE operation will fail.
     *
     * @param currentActivity - Required for garbage collection.
     * @param successListener - The listener to pass the List back through.
     * @param failureListener - The listener to pass any errors through.
     * @param collectionName  - Name of the Firestore collection to get a document from.
     * @param outputClass     - Class desired for the output object. Must have a constructor with 0
     *                        params.
     * @param <T>             - Class desired for the output object. Must have a constructor with 0
     *                        params.
     */
    public static <T extends FireStoreDoc> void getCollection(
            Activity currentActivity,
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
        db.collection(collectionName).get().addOnSuccessListener(
                currentActivity,
                queryDocumentSnapshots -> {
                    List<T> collection =
                            new ArrayList<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Convert Document into specified class
                        T obj =
                                Objects.requireNonNull(
                                        document.toObject(
                                                outputClass));
                        // Add FireStore ID to Document Object
                        obj.setId(
                                document.getId());
                        collection.add(obj);
                    }

                    successListener
                            .onSuccess(collection);
                })
          .addOnFailureListener(currentActivity, failureListener);
    }

    /**
     * Method used to create a live-updating List containing a new Object of type OutputClass
     * for each document in the provided collection.
     * The difference between this and getCollection() is that the returned List will be
     * continuously updated with all new changes provided the eventListener is still active.
     * <p>
     * The caller is responsible for:
     * - Ensuring that the User is logged in
     * - Ensuring that the User has sufficient permissions to access EVERY Document in the
     * collection. If permissions are insufficient for even ONE Document,
     * the ENTIRE operation will fail.
     *
     * @param eventListener  - The listener to pass the List and any errors back through.
     * @param collectionName - Name of the Firestore collection to get a document from.
     * @param outputClass    - Class desired for the output object. Must have a constructor with 0
     *                       params.
     * @param <T>            - Class desired for the output object. Must have a constructor with 0
     *                       params.
     */
    public static <T extends FireStoreDoc> void getLiveCollection(
            EventListener<List<T>> eventListener,
            String collectionName, final Class<T> outputClass) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //noinspection RedundantSuppression
        db.collection(collectionName)
          .addSnapshotListener((value, e) -> {

              List<T> collection = new ArrayList<>();

              //noinspection ConstantConditions
              for (QueryDocumentSnapshot document : value) {
                  // Convert Document into specified class
                  T obj = Objects.requireNonNull(document.toObject(outputClass));
                  // Add FireStore ID to Document Object
                  obj.setId(document.getId());
                  collection.add(obj);
              }

              eventListener.onEvent(collection, e);
          });
    }

    /**
     * Creates a FireStore Query on the provided CollectionReference using the provided criteria.
     * NOTES: The proper format must be followed. See the parameter requirements for operator and
     * arg. The caller is responsible for: - Ensuring that the User is logged in - Ensuring that the
     * User has sufficient permissions to access EVERY Document that could be returned by the Query.
     * If permissions are insufficient for even ONE Document, the ENTIRE Query will fail. - Limiting
     * the Query using Query.limit() to avoid having too many Documents returned at once.
     *
     * @param collection - Name of the FireStore collection to Query.
     * @param operator   - The operator to use on the Collection. Must be one of the following:
     *                   {'==', '<', '<=', '>', '>=', '!=', 'array-contains', 'in', 'not-in',
     *                   'array-contains-any'}
     * @param field      - The field to test.
     * @param arg        - The argument to apply to the field using the operator. Must be a non-null
     *                   and non-empty boolean, int or String for all operators except 'in',
     *                   'not-in', and 'array-contains-any'. For those, must be a List of non-null
     *                   and non-empty booleans, ints or Strings.
     *
     * @return - A FireStore Query.
     */
    public static Query getQuery(
            String collection, String operator,
            String field, Object arg) {
        // Check for and create type-casted List argument, if necessary for the operator
        List<?> listArg = new ArrayList<>();
        if (Arrays.asList("in", "not-in", "array-contains-any").contains(operator)) {
            // Check that argument is a list and assign it to listArg
            if (arg instanceof List<?>) {
                listArg = (List<?>) arg;
            } else {
                throw new IllegalArgumentException("The argument for a 'in', 'not-in' or " +
                                                           "'array-contains-any' query must be of type List<? extends Object>.");
            }
            // Check if list argument is empty
            if (listArg.isEmpty()) {
                throw new IllegalArgumentException("List argument cannot be empty");
            }
        }

        // Get Collection Reference
        CollectionReference collectionRef = getCollectionRef(collection);

        // Return the corresponding Query
        switch (operator) {
            case "==":
                return collectionRef.whereEqualTo(field, arg);
            case "<":
                return collectionRef.whereLessThan(field, arg);
            case "<=":
                return collectionRef.whereLessThanOrEqualTo(field, arg);
            case ">":
                return collectionRef.whereGreaterThan(field, arg);
            case ">=":
                return collectionRef.whereGreaterThanOrEqualTo(field, arg);
            case "!=":
                return collectionRef.whereNotEqualTo(field, arg);
            case "array-contains":
                return collectionRef.whereArrayContains(field, arg);
            case "in":
                return collectionRef.whereIn(field, listArg);
            case "not-in":
                return collectionRef.whereNotIn(field, listArg);
            case "array-contains-any":
                return collectionRef.whereArrayContainsAny(field, listArg);
            default:
                throw new IllegalArgumentException(
                        "Provided option was not a valid FireStore" +
                                "Query Operator. Expected one of the following:\n" +
                                "{'==', '<', '<=', '>', '>=', '!=', 'array-contains', 'in', 'not-in', " +
                                "'array-contains-any'}\nGot: " +
                                operator);
        }
    }

    /**
     * Queries a FireStore collection using the provided data. The Query results will be returned
     * through a List passed through the eventListener that is updated live, along with any errors.
     * If you already have a Query object, use the overloaded method. If you need to order or
     * rate-limit the Query, use one of the other query methods. The caller is responsible for: -
     * Ensuring that the Query parameters are valid - Ensuring that 'field' exists for all Documents
     * in the Collection - Ensuring that the User is logged in - Ensuring that the User has
     * sufficient permissions to access EVERY Document that could be returned by the Query. If
     * permissions are insufficient for even ONE Document, the ENTIRE Query will fail. -
     * Disconnecting the EventListener in the Activity's onClose() method - Limiting the Query using
     * Query.limit() to avoid having too many Documents returned at once.
     *
     * @param eventListener - The listener to pass the List and any errors back through.
     * @param collection    - Name of the FireStore collection to Query.
     * @param operator      - The operator to use on the Collection. Must be one of the following:
     *                      {'==', '<', '<=', '>', '>=', '!=', 'array-contains', 'in', 'not-in',
     *                      'array-contains-any'}
     * @param field         - The field to test.
     * @param arg           - The argument to apply to the field using the operator. Must be a
     *                      non-null and non-empty boolean, int or String for all operators except
     *                      'in', 'not-in', and 'array-contains-any'. For those, must be a List of
     *                      non-null and non-empty booleans, ints or Strings.
     * @param outputClass   - Class desired for the output objects. Must have a constructor with 0
     *                      params.
     * @param <T>           - Class desired for the output objects. Must have a constructor with 0
     *                      params.
     */
    public static <T extends FireStoreDoc> void queryLive(
            EventListener<List<T>> eventListener, String collection,
            String operator, String field, Object arg,
            final Class<T> outputClass) {
        Query query = getQuery(collection, operator, field, arg);
        queryLive(eventListener, query, outputClass);
    }

    /**
     * Queries a FireStore collection using the provided data. The Query results will be returned
     * through a List passed through the eventListener that is updated live, along with any errors.
     * This list will be sorted by orderByField. If you already have a Query object or don't need to
     * sort the output, use DBUtils.queryLive(). If you need to limit the output, use one of the
     * other query methods. The caller is responsible for: - Ensuring that the Query parameters are
     * valid - Ensuring that 'field' exists for all Documents in the Collection - Ensuring that the
     * User is logged in - Ensuring that 'orderByField' exists for all Documents in the Collection -
     * Ensuring that 'orderByField' contains sortable values - Ensuring that the User has sufficient
     * permissions to access EVERY Document that could be returned by the Query. If permissions are
     * insufficient for even ONE Document, the ENTIRE Query will fail. - Disconnecting the
     * EventListener in the Activity's onClose() method - Limiting the Query using Query.limit() to
     * avoid having too many Documents returned at once.
     *
     * @param eventListener - The listener to pass the List and any errors back through.
     * @param collection    - Name of the FireStore collection to Query.
     * @param operator      - The operator to use on the Collection. Must be one of the following:
     *                      {'==', '<', '<=', '>', '>=', '!=', 'array-contains', 'in', 'not-in',
     *                      'array-contains-any'}
     * @param field         - The field to test.
     * @param arg           - The argument to apply to the field using the operator. Must be a
     *                      non-null and non-empty boolean, int or String for all operators except
     *                      'in', 'not-in', and 'array-contains-any'. For those, must be a List of
     *                      non-null and non-empty booleans, ints or Strings.
     * @param orderByField  - The field to sort the output list by.
     * @param descending    - If the list should be sorted in descending order. If false, the list
     *                      will be sorted in ascending order.
     * @param outputClass   - Class desired for the output objects. Must have a constructor with 0
     *                      params.
     * @param <T>           - Class desired for the output objects. Must have a constructor with 0
     *                      params.
     */
    public static <T extends FireStoreDoc> void queryLiveOrdered(
            EventListener<List<T>> eventListener, String collection,
            String operator, String field, Object arg,
            String orderByField, boolean descending,
            final Class<T> outputClass) {
        Query.Direction direction =
                descending ? Query.Direction.DESCENDING : Query.Direction.ASCENDING;
        Query query = getQuery(collection, operator, field, arg)
                .orderBy(orderByField, direction);
        queryLive(eventListener, query, outputClass);
    }

    /**
     * Queries a FireStore collection using the provided data. The Query results will be returned
     * through a List passed through the eventListener that is updated live, along with any errors.
     * This list will be limited to 'limit' results. If you already have a Query object or don't
     * need to limit the output, use DBUtils.queryLive(). If you need to sort the output, use one of
     * the other query methods. The caller is responsible for: - Ensuring that the Query parameters
     * are valid - Ensuring that 'field' exists for all Documents in the Collection - Ensuring that
     * the User is logged in - Ensuring that the User has sufficient permissions to access EVERY
     * Document that could be returned by the Query. If permissions are insufficient for even ONE
     * Document, the ENTIRE Query will fail. - Disconnecting the EventListener in the Activity's
     * onClose() method
     *
     * @param eventListener - The listener to pass the List and any errors back through.
     * @param collection    - Name of the FireStore collection to Query.
     * @param operator      - The operator to use on the Collection. Must be one of the following:
     *                      {'==', '<', '<=', '>', '>=', '!=', 'array-contains', 'in', 'not-in',
     *                      'array-contains-any'}
     * @param field         - The field to test.
     * @param arg           - The argument to apply to the field using the operator. Must be a
     *                      non-null and non-empty boolean, int or String for all operators except
     *                      'in', 'not-in', and 'array-contains-any'. For those, must be a List of
     *                      non-null and non-empty booleans, ints or Strings.
     * @param limit         - Limits the List to contain this many results.  Must be non-negative.
     * @param outputClass   - Class desired for the output objects. Must have a constructor with 0
     *                      params.
     * @param <T>           - Class desired for the output objects. Must have a constructor with 0
     *                      params.
     */
    public static <T extends FireStoreDoc> void queryLiveLimited(
            EventListener<List<T>> eventListener, String collection,
            String operator, String field, Object arg,
            long limit,
            final Class<T> outputClass) {
        Query query = getQuery(collection, operator, field, arg)
                .limit(limit);
        queryLive(eventListener, query, outputClass);
    }

    /**
     * Queries a FireStore collection using the provided data. The Query results will be returned
     * through a List passed through the eventListener that is updated live, along with any errors.
     * This list will be sorted by 'orderByField' and will be limited to 'limit' results. If you
     * already have a Query object or don't need to sort or limit the output, use
     * DBUtils.queryLive(). If you only need to either sort or limit the output, use one of the
     * other query methods. The caller is responsible for: - Ensuring that the Query parameters are
     * valid - Ensuring that 'field' exists for all Documents in the Collection - Ensuring that
     * 'orderByField' exists for all Documents in the Collection - Ensuring that 'orderByField'
     * contains sortable values - Ensuring that the User is logged in - Ensuring that the User has
     * sufficient permissions to access EVERY Document that could be returned by the Query. If
     * permissions are insufficient for even ONE Document, the ENTIRE Query will fail. -
     * Disconnecting the EventListener in the Activity's onClose() method
     *
     * @param eventListener - The listener to pass the List and any errors back through.
     * @param collection    - Name of the FireStore collection to Query.
     * @param operator      - The operator to use on the Collection. Must be one of the following:
     *                      {'==', '<', '<=', '>', '>=', '!=', 'array-contains', 'in', 'not-in',
     *                      'array-contains-any'}
     * @param field         - The field to test.
     * @param arg           - The argument to apply to the field using the operator. Must be a
     *                      non-null and non-empty boolean, int or String for all operators except
     *                      'in', 'not-in', and 'array-contains-any'. For those, must be a List of
     *                      non-null and non-empty booleans, ints or Strings.
     * @param orderByField  - The field to sort the output list by.
     * @param descending    - If the list should be sorted in descending order. If false, the list
     *                      will be sorted in ascending order.
     * @param limit         - Limits the List to contain this many results. Must be non-negative.
     * @param outputClass   - Class desired for the output objects. Must have a constructor with 0
     *                      params.
     * @param <T>           - Class desired for the output objects. Must have a constructor with 0
     *                      params.
     */
    public static <T extends FireStoreDoc> void queryLiveOrderedLimited(
            EventListener<List<T>> eventListener, String collection,
            String operator, String field, Object arg,
            String orderByField, boolean descending,
            long limit,
            final Class<T> outputClass) {
        Query.Direction direction =
                descending ? Query.Direction.DESCENDING : Query.Direction.ASCENDING;
        Query query = getQuery(collection, operator, field, arg)
                .orderBy(orderByField, direction).limit(limit);
        queryLive(eventListener, query, outputClass);
    }

    /**
     * Queries a FireStore collection using the provided Query. The Query results will be returned
     * through a List passed through the eventListener that is updated live, along with any errors.
     * No collection is needed as the Query should be based off of a collection. If you have not
     * created a Query object, either use DBUtils.getQuery() or call one of the other query methods.
     * The caller is responsible for: - Ensuring that the Query is valid - Ensuring that the User is
     * logged in - Ensuring that the User has sufficient permissions to access EVERY Document that
     * could be returned by the Query. If permissions are insufficient for even ONE Document, the
     * ENTIRE Query will fail. - Disconnecting the EventListener in the Activity's onClose() method
     * - Limiting the Query using Query.limit() to avoid having too many Documents returned at
     * once.
     *
     * @param eventListener - The listener to pass the List and any errors back through.
     * @param query         - The Query to call.
     * @param outputClass   - Class desired for the output objects. Must have a constructor with 0
     *                      params.
     * @param <T>           - Class desired for the output objects. Must have a constructor with 0
     *                      params.
     */
    public static <T extends FireStoreDoc> void queryLive(
            EventListener<List<T>> eventListener,
            Query query, final Class<T> outputClass) {
        //noinspection RedundantSuppression
        query.addSnapshotListener((value, error) -> {
            List<T> collection = new ArrayList<>();

            //noinspection ConstantConditions
            for (QueryDocumentSnapshot document : value) {
                // Convert Document into specified class
                T obj = Objects.requireNonNull(document.toObject(outputClass));
                // Add FireStore ID to Document Object
                obj.setId(document.getId());
                collection.add(obj);
            }

            eventListener.onEvent(collection, error);
        });
    }

    /**
     * Deletes the document specified by its document reference.
     *
     * @param currentActivity - The current activity. Required so that the listener can be stopped
     *                        if its attached activity is stopped to avoid memory leaks.
     * @param successListener - Listener evaluating to void if the delete is successful.
     * @param failureListener - Listener for handling errors, such as user not logged in.
     * @param docRef          - The FireStore Document Reference of the document to delete
     */
    public static void deleteDoc(
            Activity currentActivity,
            final OnSuccessListener<Void> successListener, final OnFailureListener failureListener,
            DocumentReference docRef) {
        // Firebase operations are not permitted unless the user is logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            failureListener.onFailure(new UserNotLoggedInException("User is not logged in"));
            return;
        }

        docRef.delete()
              .addOnSuccessListener(currentActivity, successListener)
              .addOnFailureListener(currentActivity, failureListener);
    }

    /**
     * Returns the date nicely formatted as a string. Depending on the time that has passed since
     * the date, a different string will be returned (ex: "Just Now").
     *
     * @param date - The date to format
     *
     * @return The formatted date as a string
     */
    public static String timeSince(Date date) {
        Date now = new Date();
        long timeSince;
        String units;
        double secondsPast = ((double) now.getTime() - (double) date.getTime()) / 1000;

        if (secondsPast < 60 && secondsPast >= 0) {
            return "Just now";
        } else if (secondsPast < 3600) {
            timeSince = Math.round(secondsPast / 60);
            units = timeSince == 1 ? " min " : " mins ";
        } else if (secondsPast < 86400) {
            timeSince = Math.round(secondsPast / 3600);
            units = timeSince == 1 ? " hour " : " hours ";
        } else if (secondsPast < 604800) {
            timeSince = Math.round(secondsPast / 86400);
            units = timeSince == 1 ? " day " : " days ";
        } else {
            return date.toString();
        }
        return timeSince + units + "ago";
    }
}
