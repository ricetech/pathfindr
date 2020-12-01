package com.gng2101groupb32.pathfindr.cdn;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.gng2101groupb32.pathfindr.exceptions.UserNotLoggedInException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * This class is also known as the "Firebase Storage Magic Thingy" according to Jonathan.
 * <p>
 * This class is similar in functionality to db.DBUtils, except it is for Firebase Storage. User
 * permissions are not checked during operations. The responsibility of verifying that the user has
 * sufficient permissions to access the desired content as well as handling errors and displaying
 * outputs falls on the calling fragment or class.b
 *
 * @author Eric Chen, uOttawa 300136076
 */
public class StorageConnector {
    private static final FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final StorageReference storageRef = storage.getReference();

    /**
     * Returns a full Firebase StorageReference by using the provided path anchored at the root of
     * the default FireStore storage bucket.
     * <p>
     * Note that lowerPath should follow the standard path naming conventions, generally
     * /service/uid/id/file or /service/uid/timestamp/file.
     *
     * @param lowerPath - Path to file from root.
     *
     * @return - The StorageReference corresponding to the provided lowerPath.
     */
    public static StorageReference getPath(String lowerPath) {
        return storageRef.child(lowerPath);
    }

    /**
     * Uploads an image to Firebase FireStore. The caller is responsible for: - Ensuring that the
     * user is signed in - Ensuring that the user has sufficient permissions to upload to the given
     * path - Ensuring that the path is empty (no file should exist there) - Ensuring that the
     * Storage upload/download limits are adhered to - Ensuring that the file is valid - Ensuring
     * that the appropriate file management permissions have been requested through Android
     * Permissions
     *
     * @param fileUri           - URI pointing to the file
     * @param destinationFolder - Storage Folder where the file is to be uploaded to
     * @param filename          - Complete name of the file, including the file extension
     * @param successListener   - Evaluates to a TaskSnapshot containing the progress of the
     *                          upload.
     * @param failureListener   - For handling errors.
     * @param currentActivity   - Required for garbage collection.
     */
    public static void uploadImage(
            Uri fileUri, StorageReference destinationFolder,
            String filename,
            OnSuccessListener<UploadTask.TaskSnapshot> successListener,
            OnFailureListener failureListener,
            Activity currentActivity) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Firebase operations are not permitted unless the user is logged in
        if (currentUser == null) {
            failureListener.onFailure(new UserNotLoggedInException("User is not logged in"));
            return;
        }

        StorageReference fileRef = destinationFolder.child(filename);

        fileRef.putFile(fileUri).addOnSuccessListener(currentActivity, successListener)
               .addOnFailureListener(currentActivity, failureListener);
    }

    /**
     * Downloads a file from Firebase Storage. The returned file is a temporary file in the `cache`
     * folder of this application. This file SHOULD NOT be used directly for any long term use.
     *
     * @param currentActivity - Required for garbage collection.
     * @param successListener - The listener to pass the object back through.
     * @param failureListener - The listener to pass any errors through.
     * @param ref             - The Storage Reference to retrieve.
     *
     * @return Returns a FileDownloadTask. This can be used to keep track of the current progress of
     * the download.
     */
    @Nullable
    public static FileDownloadTask downloadFile(
            Activity currentActivity,
            final OnSuccessListener<File> successListener,
            final OnFailureListener failureListener,
            StorageReference ref) {
        String extension = ref.getPath().substring(ref.getPath().lastIndexOf("."));

        try {
            File tempFile = File.createTempFile(UUID.randomUUID().toString(), extension);
            FileDownloadTask task = ref.getFile(tempFile);

            task.addOnSuccessListener(currentActivity, runnable -> {
                if (runnable.getError() == null) {
                    successListener.onSuccess(tempFile);
                } else {
                    failureListener.onFailure(runnable.getError());
                }
            });

            return task;
        } catch (IOException e) {
            failureListener.onFailure(e);
        }

        return null;
    }

    /**
     * Ask Android to open the file in the application matching the file MIME type. This should
     * result in a prompt shown to the user asking which app to use to open the file.
     * <p>
     * IMPORTANT: The file needs to be under a path listed in the `file_paths.xml` file. This allows
     * the file to be passed to another application without crashing the app. The file returned by
     * downloadFile() has this property.
     *
     * @param activity - Required to start another activity
     * @param file     - The file to open
     */
    public static void openFile(Activity activity, File file) {
        MimeTypeMap map = MimeTypeMap.getSingleton();
        String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
        String type = map.getMimeTypeFromExtension(ext);

        if (type == null) {
            type = "*/*";
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);

        Uri data = FileProvider
                .getUriForFile(activity, "com.seg2105group77.servicenovigrad.fileprovider", file);

        intent.setDataAndType(data, type);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        activity.startActivity(intent);
    }
}
