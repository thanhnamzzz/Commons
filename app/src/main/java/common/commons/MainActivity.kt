package common.commons

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import common.commons.blurView.BlurActivity
import common.commons.convert.ConvertActivity
import common.commons.databinding.ActivityMainBinding
import common.libs.SimpleActivity
import common.libs.animationView.AnimationView
import common.libs.animationView.Attention
import common.libs.extensions.PatternDate
import common.libs.extensions.formatDate
import common.libs.extensions.hideSystemNavigationBar
import common.libs.extensions.isQ29Plus
import common.libs.extensions.toastMess
import common.libs.functions.openAppSettingsWifi
import common.libs.functions.openPanelNetwork
import common.libs.functions.versionApp
import common.libs.navigationBar.IslandNavigationBarView
import common.libs.transitionButton.TransitionButton
import common.libs.views.TypeToast

class MainActivity : SimpleActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContentView(binding.root)
		ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
			val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
			v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
			insets
		}
		window.hideSystemNavigationBar()
		val m1 = "yyyy-MM-dd HH:mm:ss".formatDate(2024, 9, 27)
		val m2 = PatternDate.EEEE_dd_MM_yyyy.formatDate(2025, 10, 1)

		toastMess(getString(R.string.app_name), TypeToast.TOAST_SUCCESS)
		Log.d("Namzzz", "MainActivity: onCreate m1 = $m1")
		Log.d("Namzzz", "MainActivity: onCreate m2 = $m2")
		Log.d("Namzzz", "MainActivity: onCreate version App = ${versionApp()}")

		binding.btnCheckNetwork.setOnClickListener {
			if (isQ29Plus()) openPanelNetwork()
			else openAppSettingsWifi(launcherNetwork)
		}

		binding.btnBlurView.setOnClickListener {
			startActivity(Intent(this, BlurActivity::class.java))
		}

		binding.btnConvert.setOnClickListener {
			startActivity(Intent(this, ConvertActivity::class.java))
		}

		binding.bottomBar.apply {
			setOnTabActionListener(object : IslandNavigationBarView.OnTabActionListener {
				override fun onTabSelected(tabId: Int) {

				}

				override fun onTabReselected(tabId: Int) {

				}

				override fun onTabUnselected(tabId: Int) {

				}
			})
		}

		binding.btnAnimation.setOnClickListener {
			AnimationView().apply {
				setAnimation(Attention().Ruberband(binding.btnAnimation))
				isLoop(true)
				start()
			}
		}
		val listener1 = TransitionButton.OnAnimationStopEndListener {
			toastMess("Animation successfully", TypeToast.TOAST_SUCCESS)
			val intent = Intent(this, BlurActivity::class.java).apply {
				addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
			}
			startActivity(intent)
		}
		val listener2 = TransitionButton.OnAnimationStopEndListener {
//			toastMess("Animation successfully", TypeToast.TOAST_ERROR)
//			binding.btnTransition2.setMessageAnimationDuration(1000)
			binding.btnTransition2.showErrorMessage("Animation Failed")
		}
		val listener3 = TransitionButton.OnAnimationStopEndListener {
			binding.btnTransition3.setMessageAnimationDuration(1200)
			binding.btnTransition3.showErrorMessage("Animation Failed")
		}
		binding.btnTransition1.setOnClickListener {
			binding.btnTransition1.startAnimation()

			Handler(Looper.getMainLooper()).postDelayed({
				binding.btnTransition1.stopAnimation(
					TransitionButton.StopAnimationStyle.EXPAND,
					listener1
				)
			}, 2000)
		}
		binding.btnTransition3.setOnClickListener {
			binding.btnTransition3.startAnimation()

			Handler(Looper.getMainLooper()).postDelayed({
				binding.btnTransition3.stopAnimation(
					TransitionButton.StopAnimationStyle.NULL,
					listener3
				)
			}, 2000)
		}
		binding.btnTransition2.setOnClickListener {
			binding.btnTransition2.startAnimation()

			Handler(Looper.getMainLooper()).postDelayed({
				binding.btnTransition2.stopAnimation(
					TransitionButton.StopAnimationStyle.SHAKE,
					listener2
				)
			}, 2000)
		}
	}

	override fun onPause() {
		super.onPause()
		Log.d("Namzzz", "MainActivity: onPause")
	}

	override fun onResume() {
		super.onResume()
		Log.d("Namzzz", "MainActivity: onResume")
		binding.btnTransition1.reset()
	}

	private val launcherNetwork =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
			Log.d("Namzzz", "MainActivity: in launcherNetwork")
		}
}