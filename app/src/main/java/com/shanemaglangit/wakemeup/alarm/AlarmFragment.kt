package com.shanemaglangit.wakemeup.alarm

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
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

        viewModel.saveAlarm.observe(this, Observer {
            if(it) {
                binding.buttonSave.visibility = View.GONE
                showProgressBar(true)
                createAlarmWithAlarmClock()
            }
            else {
                binding.buttonSave.visibility = View.VISIBLE
                showProgressBar(false)
            }
        })

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

        viewModel
            .createAlarmWithAlarmClock(this.context!!, startHour, startMinute, endHour, endMinute)
    }

    private fun showProgressBar(show: Boolean) {
        val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        layoutParams.startToStart = R.id.seekbar_interval
        layoutParams.endToEnd = R.id.seekbar_interval
        layoutParams.topToBottom = R.id.seekbar_interval
        layoutParams.bottomToBottom = LayoutParams.PARENT_ID
        layoutParams.setMargins(8,8,8,8)

        val progressBar = ProgressBar(this.context)
        progressBar.id = R.id.progress_save
        progressBar.layoutParams = layoutParams

        if(show) binding.constraintLayout.addView(progressBar)
        else binding.constraintLayout.getViewById(R.id.progress_save).visibility = View.GONE
    }
}
