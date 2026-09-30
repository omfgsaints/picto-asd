package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.dictionary.PresetDictionary
import com.example.data.local.AppDatabase
import com.example.data.model.WordCategory
import com.example.data.network.OnlineImageFetcher
import com.example.data.repository.WordRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `main activity launches without crash`() {
        val controller = org.robolectric.Robolectric.buildActivity(MainActivity::class.java).setup()
        assertNotNull(controller.get())
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("PictoWord", appName)
    }

    @Test
    fun `preset dictionary finds exact and plural words`() {
        val apple = PresetDictionary.findOrGenerate("apple")
        assertEquals("apple", apple.word)
        assertEquals("Apple", apple.label)
        assertEquals("🍎", apple.emoji)
        assertEquals(WordCategory.FOOD, apple.category)

        val apples = PresetDictionary.findOrGenerate("apples")
        assertEquals("apple", apples.word)

        val dog = PresetDictionary.findOrGenerate("dog")
        assertEquals("dog", dog.word)
        assertEquals("🐶", dog.emoji)

        val puppy = PresetDictionary.findOrGenerate("puppy")
        assertEquals("🐶", puppy.emoji)

        val cat = PresetDictionary.findOrGenerate("cat")
        assertEquals("cat", cat.word)
        assertEquals("🐱", cat.emoji)
    }

    @Test
    fun `crucial words that previously collided now match correctly`() {
        val car = PresetDictionary.findOrGenerate("car")
        assertEquals("car", car.word)
        assertEquals("🚗", car.emoji)

        val carrot = PresetDictionary.findOrGenerate("carrot")
        assertEquals("carrot", carrot.word)
        assertEquals("🥕", carrot.emoji)

        val piano = PresetDictionary.findOrGenerate("piano")
        assertEquals("piano", piano.word)
        assertEquals("🎹", piano.emoji)

        val dragon = PresetDictionary.findOrGenerate("dragon")
        assertEquals("dragon", dragon.word)
        assertEquals("🐉", dragon.emoji)

        val watermelon = PresetDictionary.findOrGenerate("watermelon")
        assertEquals("watermelon", watermelon.word)
        assertEquals("🍉", watermelon.emoji)

        assertEquals("🌳", PresetDictionary.findOrGenerate("tree").emoji)
        assertEquals("🌸", PresetDictionary.findOrGenerate("flower").emoji)
        assertEquals("🪑", PresetDictionary.findOrGenerate("chair").emoji)
        assertEquals("🪵", PresetDictionary.findOrGenerate("table").emoji)
        assertEquals("🚪", PresetDictionary.findOrGenerate("door").emoji)
        assertEquals("🪟", PresetDictionary.findOrGenerate("window").emoji)
        assertEquals("☀️", PresetDictionary.findOrGenerate("sun").emoji)
        assertEquals("🌙", PresetDictionary.findOrGenerate("moon").emoji)
        assertEquals("⭐", PresetDictionary.findOrGenerate("star").emoji)
        assertEquals("👟", PresetDictionary.findOrGenerate("shoes").emoji)
        assertEquals("🧢", PresetDictionary.findOrGenerate("hat").emoji)
        assertEquals("🚌", PresetDictionary.findOrGenerate("bus").emoji)
        assertEquals("🏊", PresetDictionary.findOrGenerate("swimming").emoji)
        assertEquals("🏃", PresetDictionary.findOrGenerate("running").emoji)
        assertEquals("🚶", PresetDictionary.findOrGenerate("walking").emoji)
    }

    @Test
    fun `offline repository enriches card seamlessly`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = AppDatabase.getDatabase(context)
        val fetcher = OnlineImageFetcher(context)
        val repo = WordRepository(db.wordCardDao(), fetcher)

        val card = PresetDictionary.findOrGenerate("apple")
        val enriched = repo.enrichWithLocalOfflineImage(card)
        assertNotNull(enriched)
        assertEquals("apple", enriched.word)
    }
}
