package com.example.myapplication.game

import kotlin.random.Random

class DiceRoller {
    fun roll(sides: Int, seed: Long = System.currentTimeMillis()): Int {
        return Random(seed).nextInt(1, sides + 1)
    }
}
