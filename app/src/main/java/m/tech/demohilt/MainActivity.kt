package m.tech.demohilt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import m.tech.demohilt.di.String1
import m.tech.demohilt.di.String2
import javax.inject.Inject

const val TAG = "AppDebug"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var car: Car

    @Inject
    lateinit var glide: RequestManager

    @String2
    @Inject
    lateinit var helloString: String

    @String1
    @Inject
    lateinit var goodbyeString: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate: ${car.hashCode()}")
//        car.drive()
//
//        Log.d("AppDebug", "onCreate: ${glide.hashCode()}")
//        Log.d(TAG, "onCreate: $helloString")
//        Log.d(TAG, "onCreate: $goodbyeString")

//        val engine = Engine()
//        val wheels = Wheels()
//        val car = Car(engine, wheels, 123)
    }
}