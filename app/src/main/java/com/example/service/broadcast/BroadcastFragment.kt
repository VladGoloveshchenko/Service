package com.example.service.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.service.databinding.FragmentBroadcastBinding
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class BroadcastFragment : Fragment() {

    private var _binding: FragmentBroadcastBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val messageBroadcast = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Toast.makeText(
                context,
                intent.getStringExtra(MESSAGE_BROADCAST_KEY),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val airplaneFlow: Flow<Boolean> = callbackFlow {

        val initialState = Settings.Global.getInt(
            requireContext().contentResolver,
            Settings.Global.AIRPLANE_MODE_ON,
            0) != 0
        trySend(initialState)

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val state = intent.getBooleanExtra("state", false)
                trySend(state)
            }
        }

        requireContext().registerReceiver(
            receiver,
            IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        )

        awaitClose {
            requireContext().unregisterReceiver(receiver)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentBroadcastBinding.inflate(inflater, container, false)
            .also { _binding = it }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

            airplaneFlow
                .onEach { state ->
                    textModeResult.text = "Airplane mode: $state"
                }
                .launchIn(viewLifecycleOwner.lifecycleScope)

            buttonSend.setOnClickListener {
                // local broadcast
                val intent = Intent(MESSAGE_BROADCAST_ACTION)
                    .putExtra(MESSAGE_BROADCAST_KEY, editText.text.toString())
                it.context.sendBroadcast(intent)

                // global broadcast in manifest
                val intentGlobal = Intent(requireContext(), MessageGlobalBroadcast::class.java)
                    .setAction(MESSAGE_GLOBAL_BROADCAST_ACTION)
                it.context.sendBroadcast(intentGlobal)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        requireContext().registerReceiver(
            messageBroadcast,
            IntentFilter(MESSAGE_BROADCAST_ACTION)
        )
    }

    override fun onStop() {
        super.onStop()
        requireContext().unregisterReceiver(messageBroadcast)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        // local broadcast
        private const val MESSAGE_BROADCAST_ACTION = "com.example.service.broadcast.MESSAGE"
        private const val MESSAGE_BROADCAST_KEY = "key_message"

        // global broadcast in Manifest
        private const val MESSAGE_GLOBAL_BROADCAST_ACTION =
            "com.example.service.broadcast.GLOBAL_MESSAGE"
    }
}