package com.shanemaglangit.wakemeup.alarm

import android.content.Intent
import android.provider.AlarmClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AlarmViewModel : ViewModel() {
    private val _interval = MutableLiveData(5)
    val interval: LiveData<Int>
        get() = _interval

    private val _saveAlarm = MutableLiveData<Boolean>()
    val saveAlarm: LiveData<Boolean>
        get() = _saveAlarm

    fun createAlarmWithAlarmClock(
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int
    ) : MutableList<Intent> {
        val intentList = mutableListOf<Intent>()
        val startTimeInMinutes = (startHour * 60) + startMinute
        val endTimeInMinutes = (endHour * 60) + endMinute

        for(x in startTimeInMinutes..endTimeInMinutes step 5) {
            val intent = Intent(AlarmClock.ACTION_SET_ALARM)
            intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true)
            intent.putExtra(AlarmClock.EXTRA_HOUR, x / 60)
            intent.putExtra(AlarmClock.EXTRA_MINUTES, x % 60)
            intentList.add(intent)
        }

        return intentList
    }

    fun updateInterval(newInterval: Int) {
        _interval.value = newInterval * 5 + 5
    }

    fun submitAlarm() {
        _saveAlarm.value = true
    }

    fun alarmSubmitted() {
        _saveAlarm.value = false
    }
}