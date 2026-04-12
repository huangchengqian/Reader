package com.localreader

import android.app.Application
import com.localreader.data.database.AppDatabase

class LocalReaderApp : Application() {
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }
}
