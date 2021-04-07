package com.google.firebase.example.fireeats;

import java.util.UUID;

public class Utils {

    public static UUID cacheUUID;

    /** Provides a unique UUID for each run */
    public static UUID getCacheUUID() {
        if (cacheUUID == null)
            cacheUUID = UUID.randomUUID();
        return cacheUUID;
    }
}
