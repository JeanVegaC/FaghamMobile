package com.example.faghamsac.modules.invoice.model

import java.math.BigDecimal

data class InvoicePagination (
    val count: Int,
    val result: List<Invoice>,
    val total: BigDecimal
)