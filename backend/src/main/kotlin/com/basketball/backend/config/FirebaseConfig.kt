package com.basketball.backend.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.StorageClient
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Bean
import org.springframework.boot.CommandLineRunner
import java.io.FileInputStream
import java.nio.file.Paths

@Configuration
class FirebaseConfig {

    @Bean
    fun firebaseApp(): FirebaseApp {
        val serviceAccountFilePath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS")
            ?: throw RuntimeException("GOOGLE_APPLICATION_CREDENTIALS environment variable not set")

        val serviceAccount = FileInputStream(serviceAccountFilePath)

        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setStorageBucket("quickdraw-6323f.firebasestorage.app")
            .setProjectId("quickdraw-6323f")
            .build()

        return if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options).also {
                println("Firebase initialized successfully with project: ${it.options.projectId}, bucket: ${it.options.storageBucket}.")
            }
        } else {
            FirebaseApp.getInstance()
        }
    }

    @Bean
    fun firebaseStartupRunner(firebaseApp: FirebaseApp) = CommandLineRunner {
        println("Attempting to verify Firebase Storage bucket...")
        try {
            val bucket = StorageClient.getInstance(firebaseApp).bucket().name
            println("Firebase Storage bucket verified: $bucket")
        } catch (e: Exception) {
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
            System.err.println("!!!! FATAL ERROR: Failed to verify Firebase Storage Bucket !!!")
            System.err.println("!!!! Exception Type: ${e.javaClass.simpleName} !!!!!!!!!!")
            System.err.println("!!!! Message: ${e.message} !!!!!!!!!!!!!!!!!!!!!!!!!!")
            System.err.println("!!!! Full Stack Trace: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
            e.printStackTrace(System.err) // Print full stack trace to standard error
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
            // Re-throw the exception to stop the application, as before
            throw e
        }
    }
}
