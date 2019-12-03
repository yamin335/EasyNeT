package ltd.royalgreen.pacenet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.splash_activity.*
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity() {

    private lateinit var animation: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)

        animation = AnimationUtils.loadAnimation(this, R.anim.logo_animation)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                runBlocking {
                    launch {
                        delay(1500L)
                    }

                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                }
            }

            override fun onAnimationStart(p0: Animation?) {

            }
        })

        logo.startAnimation(animation)
    }
}
