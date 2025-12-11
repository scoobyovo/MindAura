package com.example.mindaura.screens.Cards

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mindaura.model.Reflection
import com.example.mindaura.toDateString
import com.example.mindaura.ui.theme.MindAuraTheme
import com.example.mindaura.db.vm.QuoteViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReflectionCard(
    reflection: Reflection,
    quoteVM : QuoteViewModel,
    navController: NavController
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ){
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            SmallDropdown(
                onViewEdit = {
                    quoteVM.openReflection(reflection.id)
                    navController.navigate("quoteEntry/${reflection.user_id}?reflectionId=${reflection.id}")
                },
                onDelete = { quoteVM.deleteReflection(reflection) }
            )
        }
        Column(modifier = Modifier.padding(
            end = 16.dp,
            top = 0.dp,
            bottom = 16.dp,
            start = 16.dp)) {
            Text(text = "${reflection.text}",
                style = MindAuraTheme.typography.titleNormal,
                color = Color.DarkGray,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Text(text = "'${reflection.quote_text}'", style = MindAuraTheme.typography.labelNormal)
            Text(text = "- ${reflection.quote_author}", style = MindAuraTheme.typography.labelNormal)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "${reflection.date.toDateString()}", textAlign = TextAlign.End)
        }
    }
}

@Composable
fun SmallDropdown(
    onViewEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box (modifier = Modifier.padding(5.dp)){
        Button(
            onClick = { expanded = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE8F0FF)
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .height(16.dp)
                .width(40.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text("•••", fontSize = 10.sp, color = Color.DarkGray)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = Color(0xFFFDFBFF),
            tonalElevation = 8.dp,
            modifier = Modifier.background(
                shape = RoundedCornerShape(10.dp),
                color = Color.White
            )
        ) {
            DropdownMenuItem(
                text = { Text("View / Edit", style = MindAuraTheme.typography.labelSmall) },
                onClick = {
                    expanded = false
                    onViewEdit()
                }
            )
            DropdownMenuItem(
                text = { Text("Delete", color = Color(0xFFDA3A3A),
                    style = MindAuraTheme.typography.labelSmall) },
                onClick = {
                    expanded = false
                    onDelete()
                }
            )
        }
    }
}
