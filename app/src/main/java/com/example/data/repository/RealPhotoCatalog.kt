package com.example.data.repository

import com.example.R

object RealPhotoCatalog {

    fun getDrawableForWord(word: String): Int? {
        val clean = word.lowercase().trim()
        return when (clean) {
            "doctor" -> R.drawable.photo_doctor
            "hospital" -> R.drawable.photo_hospital
            "teacher" -> R.drawable.photo_teacher
            "firefighter" -> R.drawable.photo_firefighter
            "fire truck" -> R.drawable.photo_fire_truck
            "chef" -> R.drawable.photo_chef
            "pizza" -> R.drawable.photo_pizza
            "dog", "puppy" -> R.drawable.photo_dog
            "cat", "kitten" -> R.drawable.photo_cat
            "sleeping", "sleep" -> R.drawable.photo_sleeping
            "bed" -> R.drawable.photo_bed
            "rocket", "spaceship" -> R.drawable.photo_rocket
            "book", "read", "reading" -> R.drawable.photo_book
            "bird" -> R.drawable.photo_bird
            "apple" -> R.drawable.photo_apple
            "banana" -> R.drawable.photo_banana
            "jumping", "jump" -> R.drawable.photo_jumping
            "milk" -> R.drawable.photo_milk
            "water", "drink", "drinking" -> R.drawable.photo_water
            "frog" -> R.drawable.photo_frog
            "police car" -> R.drawable.photo_police_car
            "bus", "school bus" -> R.drawable.photo_bus
            "bicycle", "bike" -> R.drawable.photo_bicycle
            "sandwich" -> R.drawable.photo_sandwich
            "salad" -> R.drawable.photo_salad
            "cookie", "cookies" -> R.drawable.photo_cookie
            "watermelon" -> R.drawable.photo_watermelon
            "running", "run" -> R.drawable.photo_running
            "swimming", "swim" -> R.drawable.photo_swimming
            "dancing", "dance" -> R.drawable.photo_dancing
            "airplane", "plane" -> R.drawable.photo_airplane
            "boat", "ship" -> R.drawable.photo_boat
            "train" -> R.drawable.photo_train
            "pencil", "write", "writing" -> R.drawable.photo_pencil
            "crayons", "crayon", "color" -> R.drawable.photo_crayons
            "backpack", "bag" -> R.drawable.photo_backpack
            "butterfly" -> R.drawable.photo_butterfly
            "turtle" -> R.drawable.photo_turtle
            "duck" -> R.drawable.photo_duck
            "fish" -> R.drawable.photo_fish
            "happy", "smile" -> R.drawable.photo_happy
            "sad", "crying" -> R.drawable.photo_sad
            "yes" -> R.drawable.photo_yes
            "no" -> R.drawable.photo_no
            "help" -> R.drawable.photo_help
            "more" -> R.drawable.photo_more
            "stop" -> R.drawable.photo_stop
            "all done", "done", "finished" -> R.drawable.photo_all_done
            "play", "toy", "toys" -> R.drawable.photo_play
            "car", "automobile" -> R.drawable.photo_car
            "egg", "eggs" -> R.drawable.photo_egg
            "cheese" -> R.drawable.photo_cheese
            "sun", "sunny" -> R.drawable.photo_sun
            "moon", "night" -> R.drawable.photo_moon
            "star", "stars" -> R.drawable.photo_star
            "ball" -> R.drawable.photo_ball
            "shirt", "clothes" -> R.drawable.photo_shirt
            "baby" -> R.drawable.photo_baby
            "mom", "mommy", "mother" -> R.drawable.photo_mom
            "dad", "daddy", "father" -> R.drawable.photo_dad
            "shoes", "shoe" -> R.drawable.photo_shoes
            "tree" -> R.drawable.photo_tree
            "flower", "flowers" -> R.drawable.photo_flower
            else -> null
        }
    }
}
