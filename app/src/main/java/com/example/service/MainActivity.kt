package com.example.service

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.isVisible
import com.example.service.broadcast.BroadcastFragment
import com.example.service.map.GoogleMapFragment
import com.example.service.notification.NotificationFragment
import com.example.service.service.ServiceFragment

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val buttonsContainer = findViewById<LinearLayout>(R.id.buttons_container)

        val buttonClickListener = View.OnClickListener {
            val fragment = when (it.id) {
                R.id.text_broadcast -> BroadcastFragment()
                R.id.text_notifications -> NotificationFragment()
                R.id.text_services -> ServiceFragment()
                R.id.text_map -> GoogleMapFragment()
                else -> return@OnClickListener
            }

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit()
        }

        buttonsContainer
            .children
            .forEach {
                it.setOnClickListener(buttonClickListener)
            }

        supportFragmentManager
            .addOnBackStackChangedListener {
                buttonsContainer.isVisible = supportFragmentManager.backStackEntryCount == 0
            }
    }
}