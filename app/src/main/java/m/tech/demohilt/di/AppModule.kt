package m.tech.demohilt.di

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Qualifier
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object AppModule {

    @Provides
    fun provideGlide(
        @ApplicationContext context: Context
    ): RequestManager {
        return Glide.with(context)
            .setDefaultRequestOptions(
                RequestOptions()
                    .format(DecodeFormat.PREFER_RGB_565)
            )
    }

    @String1
    @Provides
    fun provideHelloString(): String {
        return "Hello"
    }

    @String2
    @Provides
    fun provideGoodbyeString(): String {
        return "Goodbye"
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class String1

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class String2











