package com.example.data.repository

import com.example.data.dictionary.PresetDictionary
import com.example.data.model.LetterBankTile
import com.example.data.model.SpellingWordItem
import com.example.data.model.WordCategory
import kotlin.random.Random

object SpellingCatalog {

    val items: List<SpellingWordItem> = listOf(
        // Level 1: 3-Letter Core Words
        SpellingWordItem("spell_dog", "DOG", "Dog", WordCategory.ANIMALS, PresetDictionary.findOrGenerate("dog"), 1, "A faithful furry puppy! 🐶"),
        SpellingWordItem("spell_cat", "CAT", "Cat", WordCategory.ANIMALS, PresetDictionary.findOrGenerate("cat"), 1, "A sweet gentle kitten! 🐱"),
        SpellingWordItem("spell_car", "CAR", "Car", WordCategory.ROUTINE, PresetDictionary.findOrGenerate("car"), 1, "Vroom! A car with wheels! 🚗"),
        SpellingWordItem("spell_bed", "BED", "Bed", WordCategory.ROUTINE, PresetDictionary.findOrGenerate("bed"), 1, "A cozy place to sleep! 🛏️"),
        SpellingWordItem("spell_bus", "BUS", "Bus", WordCategory.ROUTINE, PresetDictionary.findOrGenerate("bus"), 1, "The big yellow school bus! 🚌"),
        SpellingWordItem("spell_sun", "SUN", "Sun", WordCategory.GENERAL, PresetDictionary.findOrGenerate("sun"), 1, "Warm and bright in the sky! ☀️"),
        SpellingWordItem("spell_egg", "EGG", "Egg", WordCategory.FOOD, PresetDictionary.findOrGenerate("egg"), 1, "A healthy breakfast egg! 🥚"),

        // Level 2: 4-Letter Words
        SpellingWordItem("spell_book", "BOOK", "Book", WordCategory.ROUTINE, PresetDictionary.findOrGenerate("book"), 2, "Pages full of wonderful stories! 📖"),
        SpellingWordItem("spell_duck", "DUCK", "Duck", WordCategory.ANIMALS, PresetDictionary.findOrGenerate("duck"), 2, "Quack quack! Swimming in the pond! 🦆"),
        SpellingWordItem("spell_frog", "FROG", "Frog", WordCategory.ANIMALS, PresetDictionary.findOrGenerate("frog"), 2, "Ribbit! Hopping on lily pads! 🐸"),
        SpellingWordItem("spell_milk", "MILK", "Milk", WordCategory.FOOD, PresetDictionary.findOrGenerate("milk"), 2, "Fresh and healthy cold milk! 🥛"),
        SpellingWordItem("spell_bird", "BIRD", "Bird", WordCategory.ANIMALS, PresetDictionary.findOrGenerate("bird"), 2, "Singing cheerful melodies! 🐦"),
        SpellingWordItem("spell_fish", "FISH", "Fish", WordCategory.ANIMALS, PresetDictionary.findOrGenerate("fish"), 2, "Splish splash in the water! 🐟"),
        SpellingWordItem("spell_star", "STAR", "Star", WordCategory.GENERAL, PresetDictionary.findOrGenerate("star"), 2, "Twinkling high in the night! ⭐"),
        SpellingWordItem("spell_moon", "MOON", "Moon", WordCategory.GENERAL, PresetDictionary.findOrGenerate("moon"), 2, "Glowing softly in the night sky! 🌙"),
        SpellingWordItem("spell_boat", "BOAT", "Boat", WordCategory.ROUTINE, PresetDictionary.findOrGenerate("boat"), 2, "Sailing gently across the waves! ⛵"),
        SpellingWordItem("spell_ball", "BALL", "Ball", WordCategory.TOYS, PresetDictionary.findOrGenerate("ball"), 2, "Bouncing high on the grass! ⚽"),
        SpellingWordItem("spell_tree", "TREE", "Tree", WordCategory.GENERAL, PresetDictionary.findOrGenerate("tree"), 2, "Tall green branches and leaves! 🌳"),

        // Level 3: 5+ Letter Words
        SpellingWordItem("spell_apple", "APPLE", "Apple", WordCategory.FOOD, PresetDictionary.findOrGenerate("apple"), 3, "Crunchy and sweet red fruit! 🍎"),
        SpellingWordItem("spell_pizza", "PIZZA", "Pizza", WordCategory.FOOD, PresetDictionary.findOrGenerate("pizza"), 3, "Warm and cheesy delicious slice! 🍕"),
        SpellingWordItem("spell_train", "TRAIN", "Train", WordCategory.ROUTINE, PresetDictionary.findOrGenerate("train"), 3, "Choo choo down the tracks! 🚂"),
        SpellingWordItem("spell_banana", "BANANA", "Banana", WordCategory.FOOD, PresetDictionary.findOrGenerate("banana"), 3, "Peel and eat a yellow banana! 🍌"),
        SpellingWordItem("spell_rocket", "ROCKET", "Rocket", WordCategory.TOYS, PresetDictionary.findOrGenerate("rocket"), 3, "Zooming high into outer space! 🚀")
    )

    fun generateLetterBank(word: String, level: Int): List<LetterBankTile> {
        val wordChars = word.uppercase().toList()
        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toList()

        // Distractor count based on level
        val distractorCount = when (level) {
            1 -> 3 // 3 target + 3 distractors = 6 tiles
            2 -> 4 // 4 target + 4 distractors = 8 tiles
            else -> 4 // 5-6 target + 4 distractors
        }

        val distractors = (alphabet - wordChars.toSet())
            .shuffled(Random(word.hashCode()))
            .take(distractorCount)

        val combined = (wordChars + distractors).shuffled(Random(word.hashCode() + 42))

        return combined.mapIndexed { index, char ->
            LetterBankTile(id = index, char = char, isUsed = false)
        }
    }
}
