package com.shanemaglangit.wakemeup.alarm

import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import java.util.*

class AlarmViewModel : ViewModel() {
    private var job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    private val _interval = MutableLiveData(5)
    val interval: LiveData<Int>
        get() = _interval

    private val _saveAlarm = MutableLiveData<Boolean>()
    val saveAlarm: LiveData<Boolean>
        get() = _saveAlarm

    fun createAlarmWithAlarmClock(
        context: Context,
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int
    ) {
        uiScope.launch {
            val calendar = Calendar.getInstance()
            val startTimeInMinutes = (startHour * 60) + startMinute
            val endTimeInMinutes = (endHour * 60) + endMinute
            val currentTimeInMinutes =
                (calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE)

            if(currentTimeInMinutes in (startTimeInMinutes + 1)..endTimeInMinutes) {
                Toast.makeText(context, "You can only set alarms that triggers within 24 hours ", Toast.LENGTH_SHORT).show()
                _saveAlarm.value = false
            } else {
                _saveAlarm.value =
                    createAlarm(context, startTimeInMinutes, endTimeInMinutes, interval.value!!)
            }
        }
    }

    private suspend fun createAlarm(
        context: Context,
        startTimeInMinutes: Int,
        endTimeInMinutes: Int,
        interval: Int
    ) : Boolean {
        return withContext(Dispatchers.IO) {
            var minutes = startTimeInMinutes
            var passesMidnight = startTimeInMinutes > endTimeInMinutes

            while(minutes <= endTimeInMinutes || passesMidnight) {
                if(minutes > 1440) {
                    minutes = 0
                    passesMidnight = false
                }

                val intent = Intent(AlarmClock.ACTION_SET_ALARM)
                intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true)
                intent.putExtra(AlarmClock.EXTRA_HOUR, minutes / 60)
                intent.putExtra(AlarmClock.EXTRA_MINUTES, minutes % 60)
                context.startActivity(intent)

                minutes += interval
            }

            false
        }
    }

    fun updateInterval(newInterval: Int) {
        _interval.value = newInterval * 5 + 5
    }

    fun submitAlarm() {
        _saveAlarm.value = true
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}