package com.dansiwiec.payments.models

data class Sku(val id:String, val name:String, val price:Double) {
    constructor(): this("", "", 0.0)
}
