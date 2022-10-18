package com.dansiwiec.customers.models

data class Customer(val id:String, val name:String, val accountId:String, val email:String) {
    constructor(): this("", "", "", "")
}
