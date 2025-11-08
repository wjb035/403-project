package com.basketball.backend.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.context.annotation.Configuration
import java.io.File
import java.io.FileInputStream

@Configuration
class FirebaseConfig {
    init{
        fun initializeFirebase() {
            val credentialsFile = File("GOOGLE_APPLICATION_CREDENTIALS")
                ?: throw IllegalStateException("GOOGLE_APPLICATION_CREDENTIALS env variable not set!")

            FileInputStream(credentialsFile).use { serviceAccount ->
                val options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setStorageBucket("//quickdraw-6323f.firebasestorage.app")
                    .build()

                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseApp.initializeApp(options)
                    println("Firebase initialized successfully with bucket: \${options.storageBucket}.")
                }
            }
        }
    }
}