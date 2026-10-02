package com.example.data.repository

import com.example.data.dictionary.PresetDictionary
import com.example.data.model.SentenceIdentifyItem
import com.example.data.model.WordCard

object SentenceIdentifyCatalog {

    val items: List<SentenceIdentifyItem> = listOf(
        SentenceIdentifyItem(
            id = "ident_1",
            spokenSentence = "A doctor is working in the hospital.",
            promptInstruction = "Listen: \"A doctor is working in the hospital.\"",
            targetCardKey = "doctor",
            optionCardKeys = listOf("doctor", "teacher", "firefighter", "chef"),
            explanation = "A doctor helps people feel better in the hospital! 🩺"
        ),
        SentenceIdentifyItem(
            id = "ident_2",
            spokenSentence = "The red fire truck is rushing with loud sirens.",
            promptInstruction = "Listen: \"The red fire truck is rushing with loud sirens.\"",
            targetCardKey = "fire truck",
            optionCardKeys = listOf("fire truck", "police car", "bus", "bicycle"),
            explanation = "The firefighter drives the big red fire truck! 🚒"
        ),
        SentenceIdentifyItem(
            id = "ident_3",
            spokenSentence = "The chef is cooking a warm cheesy pizza.",
            promptInstruction = "Listen: \"The chef is cooking a warm cheesy pizza.\"",
            targetCardKey = "pizza",
            optionCardKeys = listOf("pizza", "apple", "sandwich", "salad"),
            explanation = "A hot and cheesy delicious pizza! 🍕"
        ),
        SentenceIdentifyItem(
            id = "ident_4",
            spokenSentence = "The little puppy is wagging its tail and playing with the ball.",
            promptInstruction = "Listen: \"The little puppy is wagging its tail and playing with the ball.\"",
            targetCardKey = "dog",
            optionCardKeys = listOf("dog", "cat", "bird", "fish"),
            explanation = "The playful puppy loves to play! 🐶"
        ),
        SentenceIdentifyItem(
            id = "ident_5",
            spokenSentence = "The fluffy cat is sleeping softly on the warm rug.",
            promptInstruction = "Listen: \"The fluffy cat is sleeping softly on the warm rug.\"",
            targetCardKey = "sleeping",
            optionCardKeys = listOf("sleeping", "running", "dancing", "swimming"),
            explanation = "The sleepy cat is resting quietly! 💤"
        ),
        SentenceIdentifyItem(
            id = "ident_6",
            spokenSentence = "The space rocket is zooming past the stars and moon.",
            promptInstruction = "Listen: \"The space rocket is zooming past the stars and moon.\"",
            targetCardKey = "rocket",
            optionCardKeys = listOf("rocket", "airplane", "boat", "train"),
            explanation = "The rocket flies high into outer space! 🚀"
        ),
        SentenceIdentifyItem(
            id = "ident_7",
            spokenSentence = "The teacher is reading a story from a big colorful book.",
            promptInstruction = "Listen: \"The teacher is reading a story from a big colorful book.\"",
            targetCardKey = "book",
            optionCardKeys = listOf("book", "pencil", "crayons", "backpack"),
            explanation = "Turning the pages of a wonderful storybook! 📖"
        ),
        SentenceIdentifyItem(
            id = "ident_8",
            spokenSentence = "A sweet bird is singing in the tall green tree.",
            promptInstruction = "Listen: \"A sweet bird is singing in the tall green tree.\"",
            targetCardKey = "bird",
            optionCardKeys = listOf("bird", "frog", "butterfly", "turtle"),
            explanation = "The cheerful little bird is singing melodies! 🐦"
        ),
        SentenceIdentifyItem(
            id = "ident_9",
            spokenSentence = "The boy is eating a crunchy red apple for his healthy snack.",
            promptInstruction = "Listen: \"The boy is eating a crunchy red apple for his healthy snack.\"",
            targetCardKey = "apple",
            optionCardKeys = listOf("apple", "banana", "cookie", "watermelon"),
            explanation = "A crisp and sweet red apple! 🍎"
        ),
        SentenceIdentifyItem(
            id = "ident_10",
            spokenSentence = "The playful child is jumping high on the bouncy trampoline.",
            promptInstruction = "Listen: \"The playful child is jumping high on the bouncy trampoline.\"",
            targetCardKey = "jumping",
            optionCardKeys = listOf("jumping", "sleeping", "writing", "sitting"),
            explanation = "Bouncing and jumping high into the air! 🦘"
        ),
        SentenceIdentifyItem(
            id = "ident_11",
            spokenSentence = "The baby is smiling happily and drinking a bottle of milk.",
            promptInstruction = "Listen: \"The baby is smiling happily and drinking a bottle of milk.\"",
            targetCardKey = "milk",
            optionCardKeys = listOf("milk", "water", "juice", "soup"),
            explanation = "Refreshing and wholesome cold milk! 🥛"
        ),
        SentenceIdentifyItem(
            id = "ident_12",
            spokenSentence = "The green frog is hopping across the cool blue pond.",
            promptInstruction = "Listen: \"The green frog is hopping across the cool blue pond.\"",
            targetCardKey = "frog",
            optionCardKeys = listOf("frog", "duck", "fish", "dog"),
            explanation = "Ribbit! The little green frog hops! 🐸"
        )
    )

    fun resolveItemOptions(item: SentenceIdentifyItem): List<WordCard> {
        return item.optionCardKeys.map { PresetDictionary.findOrGenerate(it) }.shuffled()
    }

    fun getTargetCard(item: SentenceIdentifyItem): WordCard {
        return PresetDictionary.findOrGenerate(item.targetCardKey)
    }
}
