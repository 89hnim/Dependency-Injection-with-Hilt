package m.tech.demohilt

import android.util.Log
import javax.inject.Inject

class Wheels
@Inject
constructor(){
    init {
        Log.d("AppDebug", "Wheels init: ")
    }
}