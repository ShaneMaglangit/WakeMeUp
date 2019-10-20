package com.shanemaglangit.wakemeup.alarm

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.shanemaglangit.wakemeup.R
import com.shanemaglangit.wakemeup.databinding.FragmentAlarmBinding

/**
 * A simple [Fragment] subclass.
 */
class AlarmFragment : Fragment() {
    private lateinit var binding: FragmentAlarmBinding
    private lateinit var viewModel: AlarmViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_alarm, container, false)

        viewModel = ViewModelProvider(this).get(AlarmViewModel::class.java)

        viewModel.saveAlarm.observe(this, Observer { if(it) createAlarmWithAlarmClock() })

        binding.seekbarInterval.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(p0: SeekBar?) {} // Don't need this
            override fun onStopTrackingTouch(p0: SeekBar?) {} // Don't need this

            override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.updateInterval(progress)
            }
        })

        binding.alarmViewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    private fun createAlarmWithAlarmClock() {
        val startHour: Int
        val startMinute: Int
        val endHour: Int
        val endMinute: Int

        if(Build.VERSION.SDK_INT >= 23) {
            startHour = binding.timeStart.hour
            startMinute = binding.timeStart.minute
            endHour = binding.timeEnd.hour
            endMinute = binding.timeEnd.minute
        }
        else {
            startHour = binding.timeStart.currentHour
            startMinute = binding.timeStart.currentMinute
            endHour = binding.timeEnd.currentHour
            endMinute = binding.timeEnd.currentMinute
        }

        val alarmIntents = viewModel.createAlarmWithAlarmClock(
            startHour, startMinute, endHour, endMinute
        )

        for(intent in alarmIntents) startActivity(intent)

        viewModel.alarmSubmitted()
    }
}
