package m.tech.demohilt

import android.util.Log
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
data class Car
@Inject
constructor(
    val engine: Engine,
    val wheels: Wheels
) {
    fun drive() {
        Log.d("AppDebug", "${toString()} Driving...")
    }
}