package com.basketball.backend.prompter

import net.sf.extjwnl.dictionary.Dictionary

object WordNetProvider {
    val dictionary: Dictionary by lazy {
        Dictionary.getDefaultResourceInstance()
    }
}