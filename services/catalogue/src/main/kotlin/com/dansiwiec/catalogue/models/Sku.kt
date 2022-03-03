package com.dansiwiec.catalogue.models

data class Sku(val id:String, val name:String, val category:String, val price:Double) {
    constructor(): this("", "", "", 0.0)
}
