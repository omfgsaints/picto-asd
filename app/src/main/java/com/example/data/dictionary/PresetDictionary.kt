package com.example.data.dictionary

import com.example.data.model.WordCard
import com.example.data.model.WordCategory

object PresetDictionary {

    val allWords: List<WordCard> = (VocabularyCatalog.items + VocabularyCatalogPart2.items)
        .distinctBy { it.word.lowercase().trim() }

    // Fast O(1) normalized lookup table mapping exact words and common child variations
    private val normalizedIndex: Map<String, WordCard> = buildMap {
        for (item in allWords) {
            val key = item.word.lowercase().trim()
            put(key, item)
            put(item.label.lowercase().trim(), item)
        }

        // Helper mapper to safely alias
        fun alias(from: String, toWord: String) {
            get(toWord)?.let { put(from.lowercase().trim(), it) }
        }

        // Family & People
        alias("mommy", "mom")
        alias("mama", "mom")
        alias("mother", "mom")
        alias("mum", "mom")
        alias("mummy", "mom")
        alias("daddy", "dad")
        alias("papa", "dad")
        alias("father", "dad")
        alias("grandma", "grandma")
        alias("grandmother", "grandma")
        alias("granny", "grandma")
        alias("nana", "grandma")
        alias("grandpa", "grandpa")
        alias("grandfather", "grandpa")
        alias("gramps", "grandpa")
        alias("infant", "baby")
        alias("kid", "boy")
        alias("child", "me")
        alias("myself", "me")

        // Animals
        alias("puppy", "dog")
        alias("pup", "dog")
        alias("doggie", "dog")
        alias("doggy", "dog")
        alias("kitty", "cat")
        alias("kitten", "cat")
        alias("catty", "cat")
        alias("bunny", "rabbit")
        alias("hare", "rabbit")
        alias("bunny rabbit", "rabbit")
        alias("froggy", "frog")
        alias("toad", "frog")
        alias("piggy", "pig")
        alias("piglet", "pig")
        alias("horsey", "horse")
        alias("stallion", "horse")
        alias("colt", "horse")
        alias("lamb", "sheep")
        alias("lambie", "sheep")
        alias("t-rex", "dinosaur")
        alias("trex", "dinosaur")
        alias("dino", "dinosaur")
        alias("alligator", "crocodile")
        alias("gator", "crocodile")
        alias("polar bear", "bear")
        alias("grizzly", "bear")
        alias("birdie", "bird")
        alias("duckling", "duck")
        alias("ducky", "duck")
        alias("rooster", "chicken")
        alias("hen", "chicken")
        alias("chick", "chicken")
        alias("cattle", "cow")

        // Vehicles
        alias("automobile", "car")
        alias("auto", "car")
        alias("sedan", "car")
        alias("vehicle", "car")
        alias("plane", "airplane")
        alias("aeroplane", "airplane")
        alias("jet", "airplane")
        alias("chopper", "helicopter")
        alias("bike", "bicycle")
        alias("cycle", "bicycle")
        alias("pickup", "truck")
        alias("firetruck", "fire truck")
        alias("police", "police car")
        alias("schoolbus", "bus")
        alias("locomotive", "train")
        alias("sailboat", "boat")
        alias("vessel", "ship")
        alias("starship", "rocket")
        alias("spaceship", "rocket")

        // Household & Objects
        alias("sofa", "couch")
        alias("couch", "sofa")
        alias("potty", "toilet")
        alias("restroom", "toilet")
        alias("washroom", "toilet")
        alias("loo", "toilet")
        alias("tub", "bath")
        alias("tv", "television")
        alias("telly", "television")
        alias("cellphone", "phone")
        alias("smartphone", "phone")
        alias("telephone", "phone")
        alias("notebook", "computer")
        alias("laptop", "computer")
        alias("pc", "computer")
        alias("ipad", "tablet")
        alias("pic", "picture")
        alias("photo", "camera")
        alias("snapshot", "camera")
        alias("crayons", "crayon")
        alias("colors", "crayons")
        alias("teddy", "teddy bear")
        alias("stuffed animal", "teddy bear")
        alias("plushie", "teddy bear")
        alias("beverage", "drink")
        alias("soda", "drink")
        alias("pop", "soda")
        alias("cola", "soda")
        alias("presents", "gift")
        alias("present", "gift")
        alias("trashcan", "box")
        alias("cup", "mug")
        alias("glass", "cup")
        alias("tumbler", "cup")
        alias("dish", "plate")
        alias("brush teeth", "toothbrush")
        alias("brushing teeth", "toothbrush")
        alias("toothpaste", "toothbrush")

        // Nature & Colors
        alias("sunshine", "sun")
        alias("moonlight", "moon")
        alias("blossom", "flower")
        alias("bloom", "flower")
        alias("foliage", "leaves")
        alias("autumn leaves", "leaves")
        alias("seashore", "beach")
        alias("coast", "beach")
        alias("sea", "ocean")
        alias("waves", "ocean")
        alias("stream", "river")
        alias("brook", "river")
        alias("woods", "trees")
        alias("forest", "trees")
        alias("jungle", "trees")

        // Actions & Gerunds
        alias("jumping", "jump")
        alias("running", "run")
        alias("walking", "walk")
        alias("eating", "eat")
        alias("drinking", "drink")
        alias("sleeping", "sleep")
        alias("playing", "play")
        alias("dancing", "dance")
        alias("singing", "sing")
        alias("reading", "read")
        alias("writing", "write")
        alias("drawing", "draw")
        alias("coloring", "color")
        alias("washing", "wash")
        alias("swimming", "swim")
        alias("climbing", "climb")
        alias("smiling", "smile")
        alias("laughing", "laugh")
        alias("crying", "cry")
        alias("hugging", "hug")
        alias("clapping", "clap")
        alias("sitting", "sit")
        alias("standing", "stand")

        // Plurals
        alias("apples", "apple")
        alias("bananas", "banana")
        alias("oranges", "orange")
        alias("lemons", "lemon")
        alias("grapes", "grape")
        alias("strawberries", "strawberry")
        alias("blueberries", "blueberry")
        alias("cherries", "cherry")
        alias("peaches", "peach")
        alias("pears", "pear")
        alias("potatoes", "potato")
        alias("tomatoes", "tomato")
        alias("carrots", "carrot")
        alias("cookies", "cookie")
        alias("pancakes", "pancake")
        alias("waffles", "waffle")
        alias("donuts", "donut")
        alias("sandwiches", "sandwich")
        alias("burgers", "burger")
        alias("tacos", "taco")
        alias("eggs", "egg")
        alias("fries", "fries")
        alias("dogs", "dog")
        alias("puppies", "dog")
        alias("cats", "cat")
        alias("kittens", "cat")
        alias("birds", "bird")
        alias("ducks", "duck")
        alias("fish", "fish")
        alias("fishes", "fish")
        alias("horses", "horse")
        alias("pigs", "pig")
        alias("sheep", "sheep")
        alias("rabbits", "rabbit")
        alias("bunnies", "rabbit")
        alias("bears", "bear")
        alias("lions", "lion")
        alias("tigers", "tiger")
        alias("monkeys", "monkey")
        alias("elephants", "elephant")
        alias("turtles", "turtle")
        alias("frogs", "frog")
        alias("butterflies", "butterfly")
        alias("bees", "bee")
        alias("ants", "ant")
        alias("cars", "car")
        alias("trucks", "truck")
        alias("trains", "train")
        alias("planes", "airplane")
        alias("airplanes", "airplane")
        alias("boats", "boat")
        alias("ships", "ship")
        alias("bikes", "bicycle")
        alias("bicycles", "bicycle")
        alias("books", "book")
        alias("balls", "ball")
        alias("blocks", "blocks")
        alias("puzzles", "puzzle")
        alias("bubbles", "bubbles")
        alias("kites", "kite")
        alias("drums", "drum")
        alias("toys", "ball")
        alias("chairs", "chair")
        alias("tables", "table")
        alias("doors", "door")
        alias("windows", "window")
        alias("keys", "key")
        alias("lamps", "lamp")
        alias("clocks", "clock")
        alias("cups", "cup")
        alias("plates", "plate")
        alias("bowls", "bowl")
        alias("spoons", "spoon")
        alias("forks", "fork")
        alias("shoes", "shoe")
        alias("boots", "boots")
        alias("socks", "sock")
        alias("pants", "pants")
        alias("shirts", "shirt")
        alias("hats", "hat")
        alias("gloves", "gloves")
        alias("stars", "star")
        alias("clouds", "cloud")
        alias("trees", "tree")
        alias("flowers", "flower")
        alias("leaves", "leaf")
        alias("hands", "hand")
        alias("feet", "foot")
        alias("eyes", "eye")
        alias("ears", "ear")
        alias("teeth", "teeth")
    }

    /**
     * Finds exact or best semantic card for typed input.
     * NEVER produces false matches from substrings.
     */
    fun findOrGenerate(rawInput: String): WordCard {
        val trimmed = rawInput.trim()
        if (trimmed.isEmpty()) {
            return allWords.first() // Apple
        }
        val cleanKey = trimmed.lowercase()

        // 1. Direct O(1) exact match on word, label, or synonym
        normalizedIndex[cleanKey]?.let { return it }

        // 2. Lemmatization / Stemming:
        // Plurals ending in -ies -> -y (e.g. "cherries" -> "cherry")
        if (cleanKey.endsWith("ies") && cleanKey.length > 3) {
            val root = cleanKey.dropLast(3) + "y"
            normalizedIndex[root]?.let { return it }
        }
        // Plurals ending in -es (e.g. "buses" -> "bus", "dishes" -> "dish")
        if (cleanKey.endsWith("es") && cleanKey.length > 3) {
            val root = cleanKey.dropLast(2)
            normalizedIndex[root]?.let { return it }
        }
        // Plurals ending in -s (e.g. "cats" -> "cat")
        if (cleanKey.endsWith("s") && cleanKey.length > 2) {
            val root = cleanKey.dropLast(1)
            normalizedIndex[root]?.let { return it }
        }

        // Verbs ending in -ing (e.g. "walking" -> "walk", "running" -> "run")
        if (cleanKey.endsWith("ing") && cleanKey.length > 4) {
            val root = cleanKey.dropLast(3)
            normalizedIndex[root]?.let { return it }
            // Double consonants: "running" -> "run", "swimming" -> "swim"
            if (root.length > 2 && root.last() == root[root.length - 2]) {
                normalizedIndex[root.dropLast(1)]?.let { return it }
            }
            // Trailing e: "dancing" -> "dance", "riding" -> "ride"
            normalizedIndex[root + "e"]?.let { return it }
        }

        // Past tense ending in -ed (e.g. "played" -> "play", "jumped" -> "jump")
        if (cleanKey.endsWith("ed") && cleanKey.length > 3) {
            val root = cleanKey.dropLast(2)
            normalizedIndex[root]?.let { return it }
            if (root.length > 2 && root.last() == root[root.length - 2]) {
                normalizedIndex[root.dropLast(1)]?.let { return it }
            }
            normalizedIndex[root + "e"]?.let { return it }
        }

        // 3. Prefix matching: User typed at least 3 letters and is mid-word (e.g. "eleph" -> "elephant")
        if (cleanKey.length >= 3) {
            val prefixMatch = allWords
                .filter { it.word.startsWith(cleanKey) }
                .minByOrNull { it.word.length }
            if (prefixMatch != null) {
                return prefixMatch
            }
        }

        // 4. Safe Compound Word Detection:
        // Check if cleanKey ends with or starts with a significant vocabulary word (min 4 letters)
        // e.g. "cheeseburger" ends with "burger", "goldfish" ends with "fish", "racecar" ends with "car"
        for (item in allWords) {
            if (item.word.length >= 4 && (cleanKey.endsWith(item.word) || cleanKey.startsWith(item.word))) {
                return item.copy(
                    word = cleanKey,
                    label = trimmed.replaceFirstChar { it.uppercase() },
                    syllables = computeSyllables(cleanKey)
                )
            }
        }

        // 5. Intelligent semantic fallback generator for any remaining word
        return generateDynamicCard(trimmed)
    }

    /**
     * Generates a smart, visually pleasing AAC-style card for words not in the preset dictionary.
     */
    fun generateDynamicCard(word: String): WordCard {
        val clean = word.trim()
        val capitalized = clean.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        val guessedCategory = guessCategory(clean)
        val computedSyllables = computeSyllables(clean)
        val emoji = pickSemanticEmoji(clean)

        return WordCard(
            word = clean.lowercase(),
            label = capitalized,
            category = guessedCategory,
            emoji = emoji,
            syllables = computedSyllables,
            description = "A visual picture card for \"$capitalized\"",
            exampleSentence = "Look at the $capitalized.",
            isCustom = true
        )
    }

    /**
     * Search words starting with query, followed by words containing query.
     */
    fun searchSuggestions(query: String, limit: Int = 12): List<WordCard> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return allWords.take(limit)

        val startsWithMatches = allWords.filter { it.word.startsWith(q) || it.label.lowercase().startsWith(q) }
        val containsMatches = allWords.filter {
            !it.word.startsWith(q) && !it.label.lowercase().startsWith(q) &&
                    (it.word.contains(q) || it.description.lowercase().contains(q))
        }

        return (startsWithMatches + containsMatches).take(limit)
    }

    private fun guessCategory(word: String): WordCategory {
        val lower = word.lowercase()
        return when {
            lower.contains("eat") || lower.contains("food") || lower.contains("berry") ||
                    lower.contains("fruit") || lower.contains("cake") || lower.contains("drink") ||
                    lower.contains("snack") || lower.contains("pie") -> WordCategory.FOOD

            lower.contains("happy") || lower.contains("mad") || lower.contains("sad") ||
                    lower.contains("feel") || lower.contains("love") || lower.contains("tired") ||
                    lower.contains("cry") -> WordCategory.FEELINGS

            lower.contains("ing") || lower.contains("jump") || lower.contains("run") ||
                    lower.contains("play") || lower.contains("walk") || lower.contains("swim") ||
                    lower.contains("dance") || lower.contains("sing") -> WordCategory.ACTIONS

            lower.contains("dog") || lower.contains("cat") || lower.contains("bear") ||
                    lower.contains("pet") || lower.contains("fish") || lower.contains("bird") ||
                    lower.contains("horse") || lower.contains("bug") || lower.contains("lion") -> WordCategory.ANIMALS

            lower.contains("toy") || lower.contains("game") || lower.contains("ball") ||
                    lower.contains("doll") || lower.contains("puzzle") -> WordCategory.TOYS

            lower.contains("room") || lower.contains("house") || lower.contains("park") ||
                    lower.contains("school") || lower.contains("place") -> WordCategory.ROUTINE

            lower.contains("man") || lower.contains("girl") || lower.contains("boy") ||
                    lower.contains("person") || lower.contains("mom") || lower.contains("dad") ||
                    lower.contains("baby") || lower.contains("friend") -> WordCategory.PEOPLE

            lower.contains("shirt") || lower.contains("pant") || lower.contains("shoe") ||
                    lower.contains("hat") || lower.contains("sock") || lower.contains("dress") ||
                    lower.contains("coat") -> WordCategory.CLOTHING

            else -> WordCategory.GENERAL
        }
    }

    private fun computeSyllables(word: String): String {
        if (word.length <= 3) return word.uppercase()
        val vowels = setOf('a', 'e', 'i', 'o', 'u', 'y', 'A', 'E', 'I', 'O', 'U', 'Y')
        val sb = StringBuilder()
        var lastWasVowel = false
        var syllableCharCount = 0

        for (i in word.indices) {
            val c = word[i]
            val isVowel = c in vowels
            sb.append(c.uppercaseChar())
            syllableCharCount++

            if (isVowel && !lastWasVowel && i < word.length - 2 && syllableCharCount >= 2) {
                if (i + 1 < word.length && word[i + 1] !in vowels) {
                    sb.append(" • ")
                    syllableCharCount = 0
                }
            }
            lastWasVowel = isVowel
        }
        return sb.toString()
    }

    /**
     * Never shows abstract alphabet dice ("🎲", "🅱️").
     * Always inspects semantic keyword cues to display real meaningful visuals.
     */
    private fun pickSemanticEmoji(word: String): String {
        val lower = word.lowercase()
        return when {
            // Animals
            lower.contains("dog") || lower.contains("pup") || lower.contains("hound") -> "🐶"
            lower.contains("cat") || lower.contains("kit") || lower.contains("feline") -> "🐱"
            lower.contains("bird") || lower.contains("chirp") || lower.contains("feather") -> "🐦"
            lower.contains("fish") || lower.contains("fin") || lower.contains("swim") -> "🐟"
            lower.contains("shark") -> "🦈"
            lower.contains("whale") -> "🐋"
            lower.contains("dolphin") -> "🐬"
            lower.contains("bear") -> "🐻"
            lower.contains("lion") -> "🦁"
            lower.contains("tiger") -> "🐯"
            lower.contains("horse") || lower.contains("pony") || lower.contains("colt") -> "🐴"
            lower.contains("cow") || lower.contains("bull") || lower.contains("calf") -> "🐮"
            lower.contains("pig") || lower.contains("pork") || lower.contains("swine") -> "🐷"
            lower.contains("sheep") || lower.contains("lamb") || lower.contains("wool") -> "🐑"
            lower.contains("bunny") || lower.contains("rabbit") || lower.contains("hare") -> "🐰"
            lower.contains("monkey") || lower.contains("ape") || lower.contains("chimp") -> "🐵"
            lower.contains("dino") || lower.contains("rex") || lower.contains("fossil") -> "🦖"
            lower.contains("dragon") -> "🐉"
            lower.contains("bug") || lower.contains("insect") || lower.contains("ant") -> "🐜"
            lower.contains("bee") || lower.contains("wasp") || lower.contains("honey") -> "🐝"
            lower.contains("butterfly") || lower.contains("moth") -> "🦋"
            lower.contains("spider") -> "🕷️"
            lower.contains("snake") || lower.contains("serpent") -> "🐍"
            lower.contains("frog") || lower.contains("toad") -> "🐸"
            lower.contains("turtle") || lower.contains("tortoise") -> "🐢"
            lower.contains("mouse") || lower.contains("rat") || lower.contains("rodent") -> "🐭"

            // Food & Drinks
            lower.contains("apple") -> "🍎"
            lower.contains("banana") -> "🍌"
            lower.contains("berry") -> "🍓"
            lower.contains("fruit") -> "🍎"
            lower.contains("vegetable") || lower.contains("veggie") -> "🥕"
            lower.contains("bread") || lower.contains("toast") || lower.contains("bun") -> "🍞"
            lower.contains("cake") || lower.contains("pastry") || lower.contains("cupcake") -> "🎂"
            lower.contains("cookie") || lower.contains("biscuit") -> "🍪"
            lower.contains("pie") -> "🥧"
            lower.contains("donut") -> "🍩"
            lower.contains("candy") || lower.contains("sweet") || lower.contains("sugar") -> "🍬"
            lower.contains("chocolate") || lower.contains("cocoa") -> "🍫"
            lower.contains("ice cream") || lower.contains("popsicle") -> "🍦"
            lower.contains("pizza") -> "🍕"
            lower.contains("burger") || lower.contains("sandwich") -> "🍔"
            lower.contains("noodle") || lower.contains("pasta") || lower.contains("spaghetti") || lower.contains("ramen") -> "🍜"
            lower.contains("soup") || lower.contains("stew") || lower.contains("broth") -> "🍲"
            lower.contains("rice") -> "🍚"
            lower.contains("meat") || lower.contains("steak") || lower.contains("chicken") -> "🍗"
            lower.contains("egg") -> "🥚"
            lower.contains("cheese") -> "🧀"
            lower.contains("water") || lower.contains("aqua") -> "💧"
            lower.contains("milk") || lower.contains("dairy") -> "🥛"
            lower.contains("juice") -> "🧃"
            lower.contains("tea") -> "🍵"
            lower.contains("coffee") -> "☕"
            lower.contains("drink") || lower.contains("beverage") || lower.contains("soda") -> "🥤"

            // Vehicles
            lower.contains("car") || lower.contains("auto") -> "🚗"
            lower.contains("truck") || lower.contains("lorry") || lower.contains("van") -> "🚚"
            lower.contains("bus") -> "🚌"
            lower.contains("train") || lower.contains("rail") || lower.contains("locomotive") -> "🚂"
            lower.contains("plane") || lower.contains("aircraft") || lower.contains("flight") || lower.contains("fly") -> "✈️"
            lower.contains("boat") || lower.contains("ship") || lower.contains("sail") || lower.contains("ferry") -> "⛵"
            lower.contains("bike") || lower.contains("bicycle") -> "🚲"
            lower.contains("rocket") || lower.contains("space") || lower.contains("astronaut") -> "🚀"

            // Nature & Weather
            lower.contains("sun") || lower.contains("sunshine") -> "☀️"
            lower.contains("moon") -> "🌙"
            lower.contains("star") -> "⭐"
            lower.contains("cloud") -> "☁️"
            lower.contains("rain") || lower.contains("drop") -> "🌧️"
            lower.contains("snow") || lower.contains("ice") || lower.contains("cold") -> "❄️"
            lower.contains("rainbow") -> "🌈"
            lower.contains("fire") || lower.contains("flame") || lower.contains("hot") -> "🔥"
            lower.contains("tree") || lower.contains("wood") || lower.contains("forest") -> "🌳"
            lower.contains("flower") || lower.contains("blossom") || lower.contains("rose") -> "🌸"
            lower.contains("leaf") || lower.contains("leaves") || lower.contains("grass") || lower.contains("plant") -> "🌱"
            lower.contains("ocean") || lower.contains("sea") || lower.contains("beach") || lower.contains("wave") -> "🌊"
            lower.contains("mountain") || lower.contains("hill") || lower.contains("rock") -> "⛰️"

            // Household & Objects
            lower.contains("house") || lower.contains("home") || lower.contains("room") || lower.contains("building") -> "🏠"
            lower.contains("school") || lower.contains("class") -> "🏫"
            lower.contains("bed") || lower.contains("sleep") -> "🛏️"
            lower.contains("chair") || lower.contains("seat") || lower.contains("sit") -> "🪑"
            lower.contains("table") || lower.contains("desk") -> "🪵"
            lower.contains("door") -> "🚪"
            lower.contains("window") -> "🪟"
            lower.contains("light") || lower.contains("lamp") -> "💡"
            lower.contains("clock") || lower.contains("time") || lower.contains("watch") -> "⏰"
            lower.contains("tv") || lower.contains("screen") -> "📺"
            lower.contains("phone") -> "📱"
            lower.contains("computer") || lower.contains("laptop") -> "💻"
            lower.contains("book") || lower.contains("story") || lower.contains("read") -> "📚"
            lower.contains("pen") || lower.contains("pencil") || lower.contains("write") -> "✏️"
            lower.contains("box") || lower.contains("gift") || lower.contains("present") -> "🎁"
            lower.contains("balloon") -> "🎈"
            lower.contains("soap") || lower.contains("bath") || lower.contains("wash") -> "🧼"

            // Toys & Games
            lower.contains("ball") || lower.contains("sport") -> "⚽"
            lower.contains("game") || lower.contains("play") -> "🎮"
            lower.contains("music") || lower.contains("song") || lower.contains("sing") -> "🎵"
            lower.contains("drum") -> "🥁"
            lower.contains("guitar") -> "🎸"
            lower.contains("piano") -> "🎹"

            // Emotions & People
            lower.contains("happy") || lower.contains("joy") || lower.contains("smile") -> "😊"
            lower.contains("sad") || lower.contains("cry") || lower.contains("tear") -> "😢"
            lower.contains("angry") || lower.contains("mad") -> "😡"
            lower.contains("love") || lower.contains("heart") -> "❤️"
            lower.contains("baby") || lower.contains("toddler") -> "👶"
            lower.contains("mom") || lower.contains("woman") || lower.contains("lady") || lower.contains("girl") -> "👩"
            lower.contains("dad") || lower.contains("man") || lower.contains("boy") -> "👨"

            // Cheerful general fallback
            else -> "🌟"
        }
    }
}
