package de.klimek.kingdombuilder.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Stats(
    @PrimaryKey
    val month: Int = 0,
    val economy: Int = 0,
    val loyalty: Int = 0,
    val stability: Int = 0,
    val unrest: Int = 0,
    val consumption: Int = 0,
    val treasury: Int = 0,
    val size: Int = 0,
    val income: Int = 0
    // TODO magic items?
)