package com.example.srlappexperiment.presentation.model

import com.example.srlappexperiment.data.local.database.entities.GameResult as EntityGameResult
import com.example.srlappexperiment.data.local.database.entities.VocabularyCard as EntityVocabularyCard

fun EntityVocabularyCard.toUi(): VocabularyCardUi = VocabularyCardUi(
    id = id,
    word = word,
    translation = translation,
    definition = definition,
    exampleSentence = exampleSentence,
    pronunciation = pronunciation,
    difficulty = difficulty,
    timesReviewed = timesReviewed
)

fun List<EntityVocabularyCard>.toUi(): List<VocabularyCardUi> = map { it.toUi() }

fun EntityGameResult.toUi(): GameResultUi = GameResultUi(
    score = score,
    difficulty = difficulty,
    timestamp = timestamp
)

@JvmName("gameResultListToUi")
fun List<EntityGameResult>.toUi(): List<GameResultUi> = map { it.toUi() }
