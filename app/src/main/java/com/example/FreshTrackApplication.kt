package com.example

import android.app.Application
import com.example.security.SecurityAuditor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FreshTrackApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Execute the silent app-launch security audit immediately in a background Coroutine
        CoroutineScope(Dispatchers.IO).launch {
            SecurityAuditor.executeSilentAudit(this@FreshTrackApplication)
        }
    }
}
