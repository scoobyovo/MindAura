package com.example.mindaura.screens.Cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mindaura.model.QuoteData
import com.example.mindaura.ui.theme.MindAuraTheme

@Composable
fun QuoteCard(
    quote: QuoteData,
    isDaily: Boolean = false,
    onClick: () -> Unit
) {
    val randomColor = remember {
        val r = (150..255).random()
        val g = (150..255).random()
        val b = (150..255).random()
        Color(r, g, b)
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp,
        ),
        colors = CardDefaults.cardColors(containerColor = randomColor),
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clickable { onClick() }
            .padding(vertical = 4.dp)

    ){
        Column(modifier = Modifier
            .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween) {
            if (isDaily) Text(text = "Quote of the day ~", style = MindAuraTheme.typography.labelLarge)
            Text(text = "'${quote.quote}'" ?: "Not available", style = MindAuraTheme.typography.titleNormal)
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "- ${quote.author}", style = MindAuraTheme.typography.labelLarge)
        }
    }
}