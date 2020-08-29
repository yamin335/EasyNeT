package ltd.royalgreen.pacenet

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.splash_fragment.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ltd.royalgreen.pacenet.dinjectors.Injectable
import javax.inject.Inject

class SplashFragment : Fragment(), Injectable {

    @Inject
    lateinit var preferences: SharedPreferences

    private lateinit var animation: Animation
    private var windowConfig: Int? = null

    override fun onDestroyView() {
        super.onDestroyView()
//        if (windowConfig != null) {
//            requireActivity().window.decorView.systemUiVisibility = windowConfig!!
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Hide the status bar.
        windowConfig = requireActivity().window.decorView.systemUiVisibility
        requireActivity().window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.splash_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (preferences.getBoolean("goToLogin", false)) {
            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
        }

        animation = AnimationUtils.loadAnimation(requireContext(), R.anim.logo_animation)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                runBlocking {
                    launch {
                        if (preferences.getBoolean("isLoggedIn", false)) {
                            delay(1500L)
                            startActivity(Intent(requireActivity(), MainActivity::class.java))
                            requireActivity().finish()
                        } else {
                            preferences.edit().apply {
                                putBoolean("goToLogin", true)
                                apply()
                            }
                            delay(1500L)
                            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
                        }
                    }
                }
            }

            override fun onAnimationStart(p0: Animation?) {

            }
        })

        logo.startAnimation(animation)
    }
}
