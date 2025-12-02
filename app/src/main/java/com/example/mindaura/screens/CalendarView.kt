package com.example.mindaura.screens

import android.graphics.Paint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.mindaura.ui.theme.MindAuraTheme
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale
import java.time.format.DateTimeFormatter
//import com.kizitonwose.calendar.compose.rememberCalendarState


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarView(modifier : Modifier = Modifier, viewMode : String)
{

    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    val startMonth = remember {YearMonth.now().minusMonths(12)}
    val endMonth = remember { YearMonth.now().plusMonths(12) }
    val mondayFirst = DayOfWeek.MONDAY
    val daysOfWeek = remember {generateDaysOfWeek(mondayFirst)}

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = mondayFirst
    )

    Column(modifier = modifier.fillMaxSize().padding(8.dp)){

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ){
            Text(
                text = "<",
                fontSize = 24.sp,
                modifier = Modifier.clickable{
                    currentMonth = currentMonth.minusMonths(1)
                }
            )
            Text(
                text = currentMonth.month.getDisplayName(
                    java.time.format.TextStyle.FULL, Locale.getDefault()) + " " + currentMonth.year,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = ">",
                fontSize = 24.sp,
                modifier = Modifier.clickable {
                    currentMonth = currentMonth.plusMonths(1)
                }
            )
        }

        // day of week header
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly){
            daysOfWeek.forEach { dayOfWeek ->
                Text(
                    text = dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT , Locale.getDefault()),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                    color = Color.Black
                )
                Log.i("KS_CALENDAR", "day created - ${dayOfWeek}")
            }
        }

        HorizontalCalendar(
            state = state,
            dayContent = {day -> Day(day)},
            monthHeader = {},
            userScrollEnabled = false
        )
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Day(day: CalendarDay) {
    val isToday = day.date == LocalDate.now()
// DAY.DATE TO GET THE EXACT DATE (2025-12-16 ex)
    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(48.dp)
            .clickable {
                Log.i("CalendarScreen", "Clicked on day: ${day.date}")
            }
            .background(
                color = if (isToday){
                    Color.LightGray
                } else if (!isToday && day.date.isBefore(LocalDate.now())) {
                    Color.Green
                } else Color.Transparent,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            color = if (isToday) Color.Black else Color.DarkGray
        )
    }
}

private fun generateDaysOfWeek(startDay : DayOfWeek) : List<DayOfWeek>
{
    val allDays = DayOfWeek.entries.toList()
    val startIndex = allDays.indexOf(startDay)
    return allDays.drop(startIndex) + allDays.take(startIndex)
}