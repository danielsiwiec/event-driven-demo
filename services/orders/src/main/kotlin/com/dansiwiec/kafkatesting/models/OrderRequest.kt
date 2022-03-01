package com.dansiwiec.kafkatesting.models

import javax.validation.constraints.NotEmpty

data class OrderRequest(@field:NotEmpty val items: List<LineItem>) {
    constructor(): this(emptyList())
}