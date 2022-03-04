package com.example.service.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.service.databinding.FragmentServiceBinding

class ServiceFragment : Fragment() {

    private var _binding: FragmentServiceBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val stopwatchReceiver = StopwatchReceiver()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentServiceBinding.inflate(inflater, container, false)
            .also { _binding = it }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            buttonStart.setOnClickListener {
                sendCommandToForegroundService(StopwatchState.START)
            }
            buttonStop.setOnClickListener {
                if (StopwatchService.isServiceRunning) {
                    sendCommandToForegroundService(StopwatchState.PAUSE)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        requireContext().registerReceiver(
            stopwatchReceiver,
            IntentFilter(StopwatchService.INTENT_ACTION)
        )
    }

    override fun onStop() {
        super.onStop()
        requireContext().unregisterReceiver(stopwatchReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        if (StopwatchService.isServiceRunning) {
            sendCommandToForegroundService(StopwatchState.STOP)
        }
    }

    private fun sendCommandToForegroundService(stopwatchState: StopwatchState) {
        val intent = Intent(requireContext(), StopwatchService::class.java)
            .putExtra(StopwatchService.STOPWATCH_SERVICE_COMMAND, stopwatchState)
        ContextCompat.startForegroundService(requireContext(), intent)
    }

    private fun updateUi(elapsedTime: Int) {
        binding.textResult.text = elapsedTime.secondsToTime()
    }

    inner class StopwatchReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == StopwatchService.INTENT_ACTION) {
                updateUi(intent.getIntExtra(StopwatchService.STOPWATCH_VALUE, 0))
            }
        }
    }
}