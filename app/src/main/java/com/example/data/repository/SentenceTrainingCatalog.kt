package com.example.data.repository

import com.example.data.dictionary.PresetDictionary
import com.example.data.model.SentenceExercise
import com.example.data.model.SentenceTrainingLevel
import com.example.data.model.WordCard

object SentenceTrainingCatalog {

    val levels: List<SentenceTrainingLevel> = listOf(
        SentenceTrainingLevel(
            levelNumber = 1,
            title = "Level 1: I Want & Requests",
            description = "Learn essential carrier phrases for asking for food, toys, and help.",
            icon = "👉",
            exercises = listOf(
                SentenceExercise(
                    id = "req_1",
                    levelNumber = 1,
                    title = "Asking for an Apple",
                    promptInstruction = "Can you ask for a red apple?",
                    promptAudioText = "I want apple",
                    emoji = "🍎",
                    targetWords = listOf("i want", "apple"),
                    distractorWords = listOf("cat", "ball", "sleep"),
                    slotHints = listOf("Carrier", "Food")
                ),
                SentenceExercise(
                    id = "req_2",
                    levelNumber = 1,
                    title = "Asking for Water",
                    promptInstruction = "When you are thirsty, ask for water!",
                    promptAudioText = "I want water",
                    emoji = "🥤",
                    targetWords = listOf("i want", "water"),
                    distractorWords = listOf("dog", "car", "stop"),
                    slotHints = listOf("Carrier", "Drink")
                ),
                SentenceExercise(
                    id = "req_3",
                    levelNumber = 1,
                    title = "Wanting to Play",
                    promptInstruction = "Tell someone you are ready to play!",
                    promptAudioText = "I want play",
                    emoji = "🧩",
                    targetWords = listOf("i want", "play"),
                    distractorWords = listOf("bread", "tired", "shoe"),
                    slotHints = listOf("Carrier", "Action")
                ),
                SentenceExercise(
                    id = "req_4",
                    levelNumber = 1,
                    title = "More Bubbles",
                    promptInstruction = "Ask for more shiny bubbles to pop!",
                    promptAudioText = "more bubbles",
                    emoji = "🫧",
                    targetWords = listOf("more", "bubbles"),
                    distractorWords = listOf("apple", "sleep", "cat"),
                    slotHints = listOf("Quantity", "Toy")
                ),
                SentenceExercise(
                    id = "req_5",
                    levelNumber = 1,
                    title = "Asking for Help",
                    promptInstruction = "Say please when you need a hand!",
                    promptAudioText = "help please",
                    emoji = "🤝",
                    targetWords = listOf("help", "please"),
                    distractorWords = listOf("banana", "run", "car"),
                    slotHints = listOf("Action", "Polite")
                ),
                SentenceExercise(
                    id = "req_6",
                    levelNumber = 1,
                    title = "All Done Lunch",
                    promptInstruction = "Let everyone know you finished eating!",
                    promptAudioText = "all done lunch",
                    emoji = "👐",
                    targetWords = listOf("all done", "sandwich"),
                    distractorWords = listOf("more", "dog", "jump"),
                    slotHints = listOf("Finished", "Meal")
                ),
                SentenceExercise(
                    id = "req_7",
                    levelNumber = 1,
                    title = "Wanting a Cookie",
                    promptInstruction = "Ask nicely for a yummy sweet cookie!",
                    promptAudioText = "I want cookie",
                    emoji = "🍪",
                    targetWords = listOf("i want", "cookie"),
                    distractorWords = listOf("book", "wash", "bus"),
                    slotHints = listOf("Carrier", "Treat")
                )
            )
        ),
        SentenceTrainingLevel(
            levelNumber = 2,
            title = "Level 2: Feelings & Needs",
            description = "Practice expressing how you feel inside and what your body needs.",
            icon = "💭",
            exercises = listOf(
                SentenceExercise(
                    id = "feel_1",
                    levelNumber = 2,
                    title = "Feeling Happy",
                    promptInstruction = "Show that you are smiling and joyful!",
                    promptAudioText = "I feel happy",
                    emoji = "😊",
                    targetWords = listOf("i feel", "happy"),
                    distractorWords = listOf("apple", "car", "angry"),
                    slotHints = listOf("Feeling", "Emotion")
                ),
                SentenceExercise(
                    id = "feel_2",
                    levelNumber = 2,
                    title = "Feeling Calm",
                    promptInstruction = "Take a deep breath and share your peaceful calm.",
                    promptAudioText = "I feel calm",
                    emoji = "😌",
                    targetWords = listOf("i feel", "calm"),
                    distractorWords = listOf("ball", "run", "water"),
                    slotHints = listOf("Feeling", "State")
                ),
                SentenceExercise(
                    id = "feel_3",
                    levelNumber = 2,
                    title = "Feeling Tired",
                    promptInstruction = "Tell someone you are sleepy and need to rest.",
                    promptAudioText = "I feel tired",
                    emoji = "😴",
                    targetWords = listOf("i feel", "tired"),
                    distractorWords = listOf("happy", "jump", "train"),
                    slotHints = listOf("Feeling", "State")
                ),
                SentenceExercise(
                    id = "feel_4",
                    levelNumber = 2,
                    title = "I Need Help",
                    promptInstruction = "Let someone know you need their assistance.",
                    promptAudioText = "I need help",
                    emoji = "🤝",
                    targetWords = listOf("i need", "help"),
                    distractorWords = listOf("pizza", "toy", "stop"),
                    slotHints = listOf("Need", "Assistance")
                ),
                SentenceExercise(
                    id = "feel_5",
                    levelNumber = 2,
                    title = "Feeling Hungry",
                    promptInstruction = "When your tummy is rumbling, ask for food!",
                    promptAudioText = "I feel hungry",
                    emoji = "😋",
                    targetWords = listOf("i feel", "hungry"),
                    distractorWords = listOf("calm", "pencil", "duck"),
                    slotHints = listOf("Feeling", "Tummy")
                ),
                SentenceExercise(
                    id = "feel_6",
                    levelNumber = 2,
                    title = "Feeling Sad",
                    promptInstruction = "It is okay to express sadness when you need comfort.",
                    promptAudioText = "I feel sad",
                    emoji = "😢",
                    targetWords = listOf("i feel", "sad"),
                    distractorWords = listOf("sun", "truck", "eating"),
                    slotHints = listOf("Feeling", "Emotion")
                )
            )
        ),
        SentenceTrainingLevel(
            levelNumber = 3,
            title = "Level 3: Who is Doing What?",
            description = "Build Subject + Action sentences (Colourful Semantics: Orange + Yellow).",
            icon = "🏃",
            exercises = listOf(
                SentenceExercise(
                    id = "who_1",
                    levelNumber = 3,
                    title = "Dog Running",
                    promptInstruction = "Build: The dog is running!",
                    promptAudioText = "Dog is running",
                    emoji = "🐶",
                    targetWords = listOf("dog", "is", "running"),
                    distractorWords = listOf("sleeping", "car", "apple"),
                    slotHints = listOf("Who", "Linking", "Action")
                ),
                SentenceExercise(
                    id = "who_2",
                    levelNumber = 3,
                    title = "Cat Sleeping",
                    promptInstruction = "Look at the cute kitty resting softly!",
                    promptAudioText = "Cat is sleeping",
                    emoji = "🐱",
                    targetWords = listOf("cat", "is", "sleeping"),
                    distractorWords = listOf("jumping", "water", "ball"),
                    slotHints = listOf("Who", "Linking", "Action")
                ),
                SentenceExercise(
                    id = "who_3",
                    levelNumber = 3,
                    title = "Boy Eating",
                    promptInstruction = "The boy is enjoying his meal!",
                    promptAudioText = "Boy is eating",
                    emoji = "👦",
                    targetWords = listOf("boy", "is", "eating"),
                    distractorWords = listOf("running", "bird", "book"),
                    slotHints = listOf("Who", "Linking", "Action")
                ),
                SentenceExercise(
                    id = "who_4",
                    levelNumber = 3,
                    title = "Girl Reading",
                    promptInstruction = "She is looking at the storybook pages!",
                    promptAudioText = "Girl is reading",
                    emoji = "👧",
                    targetWords = listOf("girl", "is", "reading"),
                    distractorWords = listOf("swimming", "train", "juice"),
                    slotHints = listOf("Who", "Linking", "Action")
                ),
                SentenceExercise(
                    id = "who_5",
                    levelNumber = 3,
                    title = "Bird Singing",
                    promptInstruction = "Listen to the cheerful tunes in the trees!",
                    promptAudioText = "Bird is singing",
                    emoji = "🐦",
                    targetWords = listOf("bird", "is", "singing"),
                    distractorWords = listOf("sleeping", "pizza", "car"),
                    slotHints = listOf("Who", "Linking", "Action")
                ),
                SentenceExercise(
                    id = "who_6",
                    levelNumber = 3,
                    title = "Fish Swimming",
                    promptInstruction = "The little fish is gliding in the water!",
                    promptAudioText = "Fish is swimming",
                    emoji = "🐟",
                    targetWords = listOf("fish", "is", "swimming"),
                    distractorWords = listOf("dancing", "banana", "hat"),
                    slotHints = listOf("Who", "Linking", "Action")
                )
            )
        ),
        SentenceTrainingLevel(
            levelNumber = 4,
            title = "Level 4: Complete Sentences",
            description = "Combine starters, colors, actions, and objects for complete thoughts.",
            icon = "🌟",
            exercises = listOf(
                SentenceExercise(
                    id = "full_1",
                    levelNumber = 4,
                    title = "I See a Car",
                    promptInstruction = "Look out the window! What do you see?",
                    promptAudioText = "I see a car",
                    emoji = "🚗",
                    targetWords = listOf("i see", "a", "car"),
                    distractorWords = listOf("cookie", "jump", "dog"),
                    slotHints = listOf("Starter", "Article", "Object")
                ),
                SentenceExercise(
                    id = "full_2",
                    levelNumber = 4,
                    title = "I See Red Ball",
                    promptInstruction = "Describe the toy you spotted!",
                    promptAudioText = "I see red ball",
                    emoji = "🔴",
                    targetWords = listOf("i see", "red", "ball"),
                    distractorWords = listOf("blue", "cat", "sleeping"),
                    slotHints = listOf("Starter", "Color", "Toy")
                ),
                SentenceExercise(
                    id = "full_3",
                    levelNumber = 4,
                    title = "Cat Drinks Milk",
                    promptInstruction = "What is the kitty drinking from the bowl?",
                    promptAudioText = "Cat drinks milk",
                    emoji = "🥛",
                    targetWords = listOf("cat", "drinks", "milk"),
                    distractorWords = listOf("running", "apple", "bread"),
                    slotHints = listOf("Subject", "Action", "Drink")
                ),
                SentenceExercise(
                    id = "full_4",
                    levelNumber = 4,
                    title = "Boy Eats Pizza",
                    promptInstruction = "A warm, cheesy slice for lunch!",
                    promptAudioText = "Boy eats pizza",
                    emoji = "🍕",
                    targetWords = listOf("boy", "eats", "pizza"),
                    distractorWords = listOf("water", "book", "reading"),
                    slotHints = listOf("Subject", "Action", "Food")
                ),
                SentenceExercise(
                    id = "full_5",
                    levelNumber = 4,
                    title = "Mom Gives Hug",
                    promptInstruction = "A warm gentle squeeze from mom!",
                    promptAudioText = "Mom gives hug",
                    emoji = "🤗",
                    targetWords = listOf("mom", "hug"),
                    distractorWords = listOf("banana", "stop", "car"),
                    slotHints = listOf("Person", "Action")
                )
            )
        )
    )

    fun resolveCards(wordKeys: List<String>): List<WordCard> {
        return wordKeys.map { PresetDictionary.findOrGenerate(it) }
    }

    fun getExerciseChoices(exercise: SentenceExercise): List<WordCard> {
        val targets = resolveCards(exercise.targetWords)
        val distractors = resolveCards(exercise.distractorWords)
        // Combine and shuffle deterministically or nicely so all targets appear
        return (targets + distractors).distinctBy { it.word.lowercase() }.shuffled()
    }
}
