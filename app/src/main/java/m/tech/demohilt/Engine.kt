package m.tech.demohilt

import android.util.Log
import javax.inject.Inject

class Engine
@Inject
constructor() {
    init {
        Log.d("AppDebug", "Engine init: ")
    }
}