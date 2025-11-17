package com.example.ums;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import java.io.FileInputStream;

public class Firestore {
    public static com.google.cloud.firestore.Firestore getFirestore() throws Exception {
        FileInputStream serviceAccount =
                new FileInputStream("src/main/resources/university-management-sy-9314c-firebase-adminsdk-fbsvc-7e85945818.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }

        return FirestoreClient.getFirestore();
    }
}
