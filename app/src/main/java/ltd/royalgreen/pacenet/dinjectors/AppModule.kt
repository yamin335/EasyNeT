package ltd.royalgreen.pacenet.dinjectors

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import ltd.royalgreen.pacenet.util.LiveDataCallAdapterFactory
import dagger.Module
import dagger.Provides
import ltd.royalgreen.pacenet.network.ApiService
import ltd.royalgreen.pacenet.SHARED_PREFS_KEY
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
@Suppress("unused")
object AppModule {
    @Singleton
    @Provides
    @JvmStatic
    fun provideApiService(): ApiService {

        val interceptor = Interceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader("AuthorizedToken", "cmdsX3NlY3JldF9hcGlfa2V5")
                .build()
            chain.proceed(newRequest)
        }

        val client = OkHttpClient().newBuilder()
            .connectTimeout(7, TimeUnit.SECONDS)
            .callTimeout(7, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()

        return Retrofit.Builder()
            .client(client)
            .baseUrl("http://123.136.26.98:8081")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(ApiService::class.java)
    }

    @Singleton
    @Provides
    @JvmStatic
    fun provideSharedPreferences(app: Application): SharedPreferences {
        return app.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE)
    }

//    @Singleton
//    @JvmStatic
//    @Provides
//    fun provideLoggedUser(preference: SharedPreferences): LoggedUser {
//        val loggedUser = preference.getString("LoggedUser", "{}")
//        return Gson().fromJson(loggedUser, LoggedUser::class.java)
//    }
}
