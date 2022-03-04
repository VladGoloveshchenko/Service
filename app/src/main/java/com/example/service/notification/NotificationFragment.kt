package com.example.service.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationChannelGroupCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.example.service.MainActivity
import com.example.service.R
import com.example.service.databinding.FragmentNotificationBinding

class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val notificationManager by lazy {
        NotificationManagerCompat.from(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentNotificationBinding.inflate(inflater, container, false)
            .also { _binding = it }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notificationManager.createNotificationChannelGroup(createChannelGroup())
        notificationManager.createNotificationChannel(createChannel())

        with(binding) {
            buttonNotification.setOnClickListener {
                sendNotification()
            }
            buttonGroupNotification.setOnClickListener {
                sendGroupNotification()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun sendNotification() {
        val notification = NotificationCompat.Builder(requireContext(), NOTIFICATION_CHANNEL_NAME)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentText("Hello!")
            .addAction(
                android.R.drawable.ic_menu_edit,
                "Edit",
                requireContext().activityPendingIntent(1)
            )
            .addAction(
                android.R.drawable.ic_menu_view,
                "View",
                requireContext().activityPendingIntent(2)
            )
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun sendGroupNotification() {
        val groupNotificationKey = "groupNotificationKey"

        val summaryNotification = NotificationCompat.Builder(requireContext(), NOTIFICATION_CHANNEL_NAME)
            .setContentTitle("Summary")
            .setContentText("Summary description")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setStyle(NotificationCompat.InboxStyle()
                .addLine("first message")
                .addLine("second message")
                .setBigContentTitle("2 new messages")
                .setSummaryText("sender@example.com"))
            .setGroup(groupNotificationKey)
            .setGroupSummary(true)
            .build()

        val newMessageNotification1 = NotificationCompat.Builder(requireContext(), NOTIFICATION_CHANNEL_NAME)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Title 1")
            .setContentText("Description 1")
            .setGroup(groupNotificationKey)
            .build()

        val newMessageNotification2 = NotificationCompat.Builder(requireContext(), NOTIFICATION_CHANNEL_NAME)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Title 2")
            .setContentText("Description 2")
            .setGroup(groupNotificationKey)
            .build()

        with(notificationManager) {
            notify(1, newMessageNotification1)
            notify(2, newMessageNotification2)
            notify(NOTIFICATION_GROUP_ID, summaryNotification)
        }
    }

    private fun createChannel() =
        NotificationChannelCompat.Builder(
            NOTIFICATION_CHANNEL_NAME,
            NotificationManagerCompat.IMPORTANCE_DEFAULT
        )
            .setName("ChannelName")
            .setDescription("ChannelDescription")
            .setGroup(NOTIFICATION_GROUP_NAME)
            .build()

    private fun createChannelGroup() =
        NotificationChannelGroupCompat.Builder(NOTIFICATION_GROUP_NAME)
            .setName("GroupChannelName")
            .setDescription("GroupChannelDescription")
            .build()

    companion object {
        private const val NOTIFICATION_ID = 111
        private const val NOTIFICATION_GROUP_ID = 222
        private const val NOTIFICATION_CHANNEL_NAME = "NotificationChannel"
        private const val NOTIFICATION_GROUP_NAME = "GroupNotificationChannel"
    }
}

fun Context.activityPendingIntent(requestCode: Int = 0): PendingIntent {
    return PendingIntent.getActivity(
        this,
        requestCode,
        Intent(this, MainActivity::class.java),
        PendingIntent.FLAG_IMMUTABLE
    )
}

fun NotificationManagerCompat.canSendNotification(channelId: String): Boolean {
    if (!areNotificationsEnabled()) return false

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = getNotificationChannel(channelId) ?: return true
        if (channel.importance == NotificationManager.IMPORTANCE_NONE) return false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val channelGroup = channel.group?.let { getNotificationChannelGroup(it) }
            if (channelGroup != null && channelGroup.isBlocked) return false
        }
    }

    return true
}