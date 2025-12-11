package com.example.mindaura.screens

import android.graphics.Paint
import android.os.Build
import android.util.Log
import android.widget.ToggleButton
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mindaura.JournalUiState
import com.example.mindaura.toDayMillis
import com.example.mindaura.ui.theme.MindAuraTheme
import com.example.mindaura.vm.JournalViewModel
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Locale
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarView(modifier: Modifier = Modifier,
                 navController: NavController,
                 journalVM : JournalViewModel,
                 userId : String
) {
    journalVM.loadAllJournaledDays(userId = userId)
    val uiState by journalVM.uiState.collectAsState()
    val journaledDays = uiState.journaledDays ?: emptyList()
    var currentView by remember { mutableStateOf("WEEK") } // default view is week
    val today = LocalDate.now()

    // Week setup
    val startOfWeek = today.with(DayOfWeek.MONDAY)
    val weekDays = (0..6).map { startOfWeek.plusDays(it.toLong()) }
    val dayOfWeekHeaders = DayOfWeek.values().toList()

    // Month calendar setup
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { YearMonth.now().minusMonths(12) }
    val endMonth = remember { YearMonth.now().plusMonths(12) }
    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = DayOfWeek.MONDAY
    )

    Column(modifier = modifier.fillMaxSize().padding(8.dp)) {

        // --- Month Header ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            MonthHeader(yearMonth = currentMonth)
            Switch(
                checked = currentView == "MONTH",
                onCheckedChange = { currentView = if (it) "MONTH" else "WEEK" },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFFBFA1FF),
                    uncheckedThumbColor = Color.LightGray
                ),
                modifier = Modifier
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Calendar Container
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    dayOfWeekHeaders.forEach { day ->
                        Text(
                            text = day.name.take(3),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (currentView == "WEEK") {
                    // Week view
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        weekDays.forEach { date ->
                            WeekDayBox(date,
                                isToday = date == today,
                                hasEntry = journaledDays.contains(date.toDayMillis()),
                                navController = navController)
                        }
                    }
                } else {
                    // Month view
                    HorizontalCalendar(
                        state = state,
                        dayContent = { day ->
                            WeekDayBox(day.date,
                                isToday = day.date == today,
                                hasEntry = journaledDays.contains(day.date.toDayMillis()),
                                navController = navController)
                        },
                        userScrollEnabled = true
                    )
                }
            }
        }
    }
}

@Composable
fun WeekDayBox(day: LocalDate, isToday: Boolean = false, navController: NavController, hasEntry : Boolean = false) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isToday) Color(0xFFBFA1FF) else Color(0xFFF7F4FF))
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .clickable {
                val millis = day.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

                navController.navigate("journalEntry/${millis}") },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.dayOfMonth.toString(),
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
            color = if (isToday) Color.White else Color.DarkGray
        )

        if(hasEntry){
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(6.dp)
                    .background(Color(0xFF6C68B9), CircleShape)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthHeader(yearMonth: YearMonth) {
    Text(
        text = "${yearMonth.month.getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault())} ${yearMonth.year}",
        style = MindAuraTheme.typography.titleNormal
    )
}
