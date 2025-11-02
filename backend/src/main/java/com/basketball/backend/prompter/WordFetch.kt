package com.example.drawingapp.prompter

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.sf.extjwnl.data.POS
import net.sf.extjwnl.dictionary.Dictionary
import kotlin.random.Random

class WordFetch(private val dictionary: Dictionary = Dictionary.getDefaultResourceInstance()){

    // Get a nonblacklisted word, will try up to 10 times
    private fun getGoodWord(pos: POS): String? {
        repeat(10) {
            val word = getRandomWord(pos)
            if (word != null && !isBlacklisted(word)) return word
        }
        return null
    }
    suspend fun getDrawingPrompt(): String = withContext(Dispatchers.Default) {
        // gets a noun
        val noun = getGoodWord(POS.NOUN) ?: "null"
        val verbOrAdj = if (Random.Default.nextBoolean()) {
            getGoodWord(POS.VERB)?.let { verb ->
                // add ing to the verb
                makeVerbReadable(verb)
            }
        } else {
            getGoodWord(POS.ADJECTIVE)
        } ?: "null"
        "Draw: $verbOrAdj $noun"
    }

    private fun getRandomWord(pos: POS): String? {
        val synsetIterator = dictionary.getSynsetIterator(pos)

        // 2 pass algorithm, reworked from ground up
        // STEP 1: calculate the weight
        var totalWeight = 0L
        val maxWeight = 30
        val validCandidates = mutableListOf<Pair<String, Int>>() // much smaller than all synsets (saves memory)

        // iterates over the synsets, filters out unused words and multi word phrases
        while (synsetIterator.hasNext()) {
            val synset = synsetIterator.next()
            for (word in synset.words) {
                val lemma = word.lemma.lowercase()
                if (!lemma.contains(" ") && word.useCount > 5 && !lemma.all { it.isDigit()} ) {
                    val weight = minOf(word.useCount, maxWeight)
                    validCandidates.add(lemma to weight)
                    totalWeight += weight
                }
            }
        }
        if (totalWeight == 0L || validCandidates.isEmpty()) return null


        // Cutoff
        val randomValue = Random.nextLong(totalWeight)

        // Pass 2: go through until the cutoff is reached, pick the word
        var runningSum = 0L
        for ((word, weight) in validCandidates) {
            runningSum += weight
            if (randomValue < runningSum) {
                return word
            }
        }
        return null
    }

    private fun makeVerbReadable(verb: String): String {
        return when {
            verb.endsWith("e")  && verb[verb.length - 2] !in "e" -> verb.dropLast(1) + "ing"
            verb.endsWith("ie") -> verb.dropLast(n=2) + "ying"
            verb.endsWith("c") -> verb + "king"
            verb.length > 2 &&
                    verb.last().isLetter() &&
                    verb.last() !in "wxy" &&
                    verb[verb.length - 2] in "aeiou" &&
                    verb[verb.length - 3] !in "aeiou" &&
                    !verb.endsWith("en")
                        -> verb + "${verb.last()}ing"
            else -> verb + "ing"
        }

    }

    // BLACKLIST OF WEIRD OUTLIERS
    private fun isBlacklisted(word: String): Boolean {
        val blacklist = setOf("bing", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "person", "stravinsky", "thing",
            "go", "first", "second", "third", "more", "make", "fifty", "put", "consider", "word", "particular",
            "keep", "exceed", "suppose")
        val lower = word.lowercase()
        return lower in blacklist || (lower.endsWith("ing") && lower.dropLast(3) in blacklist)
    }
}