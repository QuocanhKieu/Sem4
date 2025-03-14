package com.devteria.notification.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.FileInputStream;
import java.io.IOException;
@Configuration
public class FirebaseConfig {

//    @Bean
//    public FirebaseApp firebaseApp() throws IOException {
//        FileInputStream serviceAccount = new FileInputStream("path/to/firebase-key.json");
//        FirebaseOptions options = FirebaseOptions.builder()
//                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                .build();
//        return FirebaseApp.initializeApp(options);
//    }

//    @Bean
//    public void initializeFirebase() throws IOException {
//        FileInputStream serviceAccount = new FileInputStream("src/main/resources/firebase-service-account.json");
//
//        FirebaseOptions options = FirebaseOptions.builder()
//                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                .build();
//
//        if (FirebaseApp.getApps().isEmpty()) {  // Prevent reinitialization
//            FirebaseApp.initializeApp(options);
//        }
//    }
}