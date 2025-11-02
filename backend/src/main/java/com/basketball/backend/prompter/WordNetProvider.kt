package com.example.drawingapp.prompter

import net.sf.extjwnl.dictionary.Dictionary

object WordNetProvider {
    val dictionary: Dictionary by lazy {
        Dictionary.getDefaultResourceInstance()
    }
}