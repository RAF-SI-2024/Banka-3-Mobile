package com.example.banka_3_mobile.networking.di

data class Environment (
    val production: Boolean,
    val userUrl: String,
    val emailUrl: String,
    val bankUrl: String,
    val stockUrl: String,
)

val environmentProd = Environment(
    production = true,
    userUrl = "https://banka-3.si.raf.edu.rs/user-service/api/",
    emailUrl = "https://",
    bankUrl = "https://banka-3.si.raf.edu.rs/bank-service/api/",
    stockUrl = "https://banka-3.si.raf.edu.rs/stock-service/api/"
)

val environmentLocal = Environment(
    production = false,
    userUrl = "http://localhost:8080/api/",
    emailUrl = "http://localhost:8081/api/",
    bankUrl = "http://localhost:8082/api/",
    stockUrl = "http://localhost:8083/api/",
)

   /* production: true,
    userUrl: 'https://banka-3.si.raf.edu.rs/user-service',
    emailUrl: 'https://',
    bankUrl: 'https://banka-3.si.raf.edu.rs/bank-service',
    stockUrl: 'https://banka-3.si.raf.edu.rs/stock-service',*/
