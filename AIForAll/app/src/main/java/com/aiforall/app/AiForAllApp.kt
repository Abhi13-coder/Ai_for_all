package com.aiforall.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry point. @HiltAndroidApp triggers Hilt's code generation,
 * giving every Activity/ViewModel/Repository in the app access to the
 * dependency graph defined under `di/`.
 */
@HiltAndroidApp
class AiForAllApp : Application()
