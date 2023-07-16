package com.example.mygymy.Routine;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

// This class extends Android's ContentProvider and provides access to data from the 'exercises'
// table in the database.
public class ExerciseContentProvider extends ContentProvider {
    // Constant for the authority of this content provider, used for building URIs
    public static final String AUTHORITY = "com.example.mygymy.exercisecontentprovider";
    // UriMatcher helps to determine what kind of URI is passed to the content provider
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    // Instance of the custom SQLiteOpenHelper class
    private Exercisedb exercisedb;
    // Static block to add URI patterns to the UriMatcher. The pattern "exercises" is assigned the
    // code 1
    static {
        uriMatcher.addURI(AUTHORITY, "exercises", 1);
    }

    // This method is called when the content provider is created. Here, an instance of the
    // Exercisedb class is created.

    @Override
    public boolean onCreate() {
        exercisedb = new Exercisedb(getContext());
        return true;
    }

    // This method is used to query the database based on the given URI and other parameters
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Handle query for exercise information based on the URI
        SQLiteDatabase db = exercisedb.getReadableDatabase();
        Cursor cursor;
        // Check if the URI matches the 'exercises' pattern
        if (uriMatcher.match(uri) == 1) {
            cursor = db.query("exercises", projection, selection, selectionArgs, null,
                    null, sortOrder);
        } else {
            // If it matches, query the 'exercises' table
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        // Notify the system that the data at this URI could change in the future
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }
    // This method returns the MIME type of data for the content provider URI
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    // This method is used to insert a new row into the database
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        // Get a writable database
        SQLiteDatabase db = exercisedb.getWritableDatabase();

        // Insert the data and get the ID of the inserted row
        long id = db.insert("exercises", null, contentValues);
        Uri insertedUri = Uri.withAppendedPath(uri, Long.toString(id));

        // Notify the system that the data at this URI has changed
        getContext().getContentResolver().notifyChange(uri, null);
        return insertedUri;
    }
    // This method is used to delete rows from the database
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Get a writable database
        SQLiteDatabase db = exercisedb.getWritableDatabase();
        // Delete the data and get the count of deleted rows
        int count = db.delete("exercises", selection, selectionArgs);
        // Notify the system that the data at this URI has changed
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
    // This method is used to update rows in the database
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Get a writable database
        SQLiteDatabase db = exercisedb.getWritableDatabase();
        // Update the data and get the count of updated rows
        int count = db.update("exercises", contentValues, selection, selectionArgs);
        // Notify the system that the data at this URI has changed
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }




}
