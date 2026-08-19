package com.example.minibrowser8.model

import androidx.compose.ui.graphics.Color

enum class AppLanguage(val code: String, val displayName: String) {
    UKRAINIAN("ua", "Українська"),
    ENGLISH("en", "English"),
    RUSSIAN("ru", "Русский")
}

enum class AppThemeStyle(
    val id: String,
    val titleEn: String,
    val titleUa: String,
    val titleRu: String,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val cardHeader: Color,
    val primary: Color,
    val accent: Color,
    val border: Color,
    val textPrimary: Color,
    val textSecondary: Color
) {
    CYBERPUNK_NEON(
        id = "neon",
        titleEn = "⚡ Cyberpunk Neon",
        titleUa = "⚡ Кіберпанк Неон",
        titleRu = "⚡ Киберпанк Неон",
        background = Color(0xFF090D16),
        surface = Color(0xFF131B2E),
        surfaceVariant = Color(0xFF1E293B),
        cardHeader = Color(0xFF1E293B),
        primary = Color(0xFF0284C7),
        accent = Color(0xFF38BDF8),
        border = Color(0xFF0284C7),
        textPrimary = Color(0xFFF8FAFC),
        textSecondary = Color(0xFF94A3B8)
    ),
    MIDNIGHT_EMERALD(
        id = "emerald",
        titleEn = "🌿 Midnight Emerald",
        titleUa = "🌿 Опівнічний Смарагд",
        titleRu = "🌿 Полуночный Изумруд",
        background = Color(0xFF050C0A),
        surface = Color(0xFF0D1C17),
        surfaceVariant = Color(0xFF132A23),
        cardHeader = Color(0xFF132A23),
        primary = Color(0xFF059669),
        accent = Color(0xFF34D399),
        border = Color(0xFF10B981),
        textPrimary = Color(0xFFF0FDF4),
        textSecondary = Color(0xFF86EFAC)
    ),
    SUNSET_CRIMSON(
        id = "sunset",
        titleEn = "🔥 Sunset Crimson",
        titleUa = "🔥 Багряний Захід",
        titleRu = "🔥 Багровый Закат",
        background = Color(0xFF140A0F),
        surface = Color(0xFF26121D),
        surfaceVariant = Color(0xFF3B1A2C),
        cardHeader = Color(0xFF3B1A2C),
        primary = Color(0xFFE11D48),
        accent = Color(0xFFFB7185),
        border = Color(0xFFF43F5E),
        textPrimary = Color(0xFFFFF1F2),
        textSecondary = Color(0xFFFDA4AF)
    ),
    AMETHYST_PURPLE(
        id = "amethyst",
        titleEn = "🔮 Amethyst Void",
        titleUa = "🔮 Аметистовий Простір",
        titleRu = "🔮 Аметистовая Бездна",
        background = Color(0xFF0F0A1A),
        surface = Color(0xFF1E1333),
        surfaceVariant = Color(0xFF2E1C4E),
        cardHeader = Color(0xFF2E1C4E),
        primary = Color(0xFF7C3AED),
        accent = Color(0xFFA78BFA),
        border = Color(0xFF8B5CF6),
        textPrimary = Color(0xFFFAF5FF),
        textSecondary = Color(0xFFDDD6FE)
    ),
    MINIMAL_LIGHT(
        id = "light",
        titleEn = "☀️ Minimalist Light",
        titleUa = "☀️ Мінімалістичний Світлий",
        titleRu = "☀️ Минималистичный Светлый",
        background = Color(0xFFF1F5F9),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFE2E8F0),
        cardHeader = Color(0xFFE2E8F0),
        primary = Color(0xFF2563EB),
        accent = Color(0xFF3B82F6),
        border = Color(0xFF94A3B8),
        textPrimary = Color(0xFF0F172A),
        textSecondary = Color(0xFF475569)
    )
}
