package com.basketball.backend.prompter

import com.basketball.backend.prompter.WordFetch
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service

// Wrapper for wordfetch
@Service
public class BackendWordFetcher {

    private val wordFetch = WordFetch()

    fun getPrompt(): String = runBlocking {
        wordFetch.getDrawingPrompt()
    }
}