package com.dansiwiec.customers.models

import javax.print.attribute.standard.MediaSize.NA

data class Customer(val id:String, val name:String, val accountNumber:String, val email:String, val state: State) {
    constructor(): this("", "", "", "", State.NA)
}

enum class State {
    MN, CA, IL, NV, OR, NA, NY, ME
}
