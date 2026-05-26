package com.example.srlappexperiment.presentation.model

data class VocabularyCardUi(
    val id: String,
    val word: String,
    val translation: String,
    val definition: String,
    val exampleSentence: String,
    val pronunciation: String,
    val difficulty: String,
    val timesReviewed: Int
)
