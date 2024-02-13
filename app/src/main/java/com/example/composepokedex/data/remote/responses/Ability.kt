package com.example.composepokedex.data.remote.responses

import com.example.composepokedex.data.remote.responses.AbilityX

data class Ability(
    val ability: AbilityX,
    val is_hidden: Boolean,
    val slot: Int
)