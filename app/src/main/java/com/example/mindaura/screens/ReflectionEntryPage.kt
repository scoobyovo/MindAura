package com.example.mindaura.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mindaura.model.QuoteData
import com.example.mindaura.model.Reflection
import com.example.mindaura.ui.theme.MindAuraTheme
import com.example.mindaura.vm.QuoteViewModel

@Composable
fun ReflectionEntryPage(quote: QuoteData?,
                        userId : String,
                        navController: NavController,
                        quoteVM : QuoteViewModel,
                        existingReflection: Reflection?) {

    val uiState by quoteVM.uiState.collectAsState()
    var reflectionText by remember(uiState.openedReflection?.text) {
        mutableStateOf(uiState.openedReflection?.text ?: "")
    }
    val editing = if(existingReflection != null) true else false

    Log.i("REFLECTION ENTRY PAGE", "edit mode : ${editing}, \nquote - ${quote?.quote}\n" +
            "reflection to edit - ${existingReflection?.text}")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F4FF))
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF7A5CBD)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (editing) "Edit Reflection" else "New Reflection",
                style = MindAuraTheme.typography.labelNormal,
                color = Color(0xFF7A5CBD)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ---------- Quote Card ----------
        Card(
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier.padding(20.dp)
            ) {
                Text(
                    text = if(editing) existingReflection?.quote_text?: "" else quote?.quote ?: "",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 26.sp
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = if(editing) "- ${existingReflection?.quote_author ?: ""}"
                    else "- ${quote?.author ?: ""}" ,
                    fontSize = 16.sp,
                    color = Color(0xFF7A7A7A)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // ---------- Text Input Box ----------
        OutlinedTextField(
            value = reflectionText,
            onValueChange = { reflectionText = it },
            label = { Text("Write your reflection…") },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            maxLines = 8
        )

        Spacer(Modifier.height(24.dp))

        // ---------- Save Button ----------
        Button(
            onClick = {
                if(editing){quoteVM.updateReflection(existingReflection!!, reflectionText)}
                else {
                    quoteVM.saveReflection(quote = quote!!, text = reflectionText, userId = userId)
                }
                navController.popBackStack()
            },
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFAD93EE)
            )
        ) {
            Text(
                "Save Reflection",
                style = MindAuraTheme.typography.labelNormal,
                fontSize = 18.sp
            )
        }
    }
}