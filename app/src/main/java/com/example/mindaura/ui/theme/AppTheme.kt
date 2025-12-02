package com.example.mindaura.ui.theme

import android.util.Log
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val darkColorScheme = AppColorScheme(
    background = Color(0xFF464747), //done  #464747
    onBackground = Color(0xFF202121),// #202121
    primary = Color(0xFFebeded), // #ebeded
    onPrimary = Color(0xFF1C1C1C),
    secondary = Color(0xFFA28E6A),
    onSecondary = Color(0xFFF6F4EE)
)
private val lightColorScheme = AppColorScheme(
    background = Color(0xFFF4F3EF),
    onBackground = Color(0xFF2E2E2E),
    primary = Color(0xFF5B8A72),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFFC9B79C),
    onSecondary = Color(0xFF3D3A35)
)

private val typography = AppTypography(
    titleLarge = TextStyle(
        fontFamily = mainFont,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
    ),
    titleNormal = TextStyle(
        fontFamily = mainFont,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),
    body = TextStyle(
        fontFamily = mainFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = mainFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    ),
    labelNormal = TextStyle(
        fontFamily = mainFont,
        fontSize = 14.sp
    ),
    labelSmall = TextStyle(
        fontFamily = mainFont,
        fontSize = 12.sp
    )
)

private val shape = AppShape(
    container = RoundedCornerShape(12.dp),
    button = RoundedCornerShape(50)
)

private val size = AppSize(
    large = 24.dp,
    medium = 16.dp,
    normal = 12.dp,
    small = 8.dp
)

@Composable
fun MindAuraTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
){
    val colorScheme = remember(isDarkTheme){
        if (isDarkTheme) darkColorScheme else lightColorScheme
    }
    val rippleIndication = ripple()
    CompositionLocalProvider(
        LocalAppColorScheme provides colorScheme,
        LocalAppTypography provides typography,
        LocalAppShape provides shape,
        LocalAppSize provides size,
        LocalIndication provides rippleIndication,
    ) {
        content()
    }
}

object MindAuraTheme{
    val colorScheme: AppColorScheme
        @Composable get() = LocalAppColorScheme.current

    val typography: AppTypography
        @Composable get() = LocalAppTypography.current

    val shape: AppShape
        @Composable get() = LocalAppShape.current

    val size: AppSize
        @Composable get() = LocalAppSize.current
}