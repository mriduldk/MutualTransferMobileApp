package com.codingstudio.mutualtransfer.ui.translate

data class TranslateRequest(
    val q: String,
    val source: String = "en",
    val target: String,
    val format: String = "text"
)

data class TranslateResponse(
    val data: TranslationsData
)

data class TranslationsData(
    val translations: List<Translation>
)

data class Translation(
    val translatedText: String
)
