package common.commons.blurView

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import common.commons.R
import common.commons.databinding.ActivityBlur2Binding
import common.libs.SimpleActivity
import common.libs.extensions.isS31Plus


class Blur2Activity : SimpleActivity<ActivityBlur2Binding>(ActivityBlur2Binding::inflate) {
	private val mBackgroundBlurRadius = 80
	private val mBlurBehindRadius = 20

	// We set a different dim amount depending on whether window blur is enabled or disabled
	private val mDimAmountWithBlur = 0.1f
	private val mDimAmountNoBlur = 0.4f

	// We set a different alpha depending on whether window blur is enabled or disabled
	private val mWindowBackgroundAlphaWithBlur = 170
	private val mWindowBackgroundAlphaNoBlur = 255

	// Use a rectangular shape drawable for the window background. The outline of this drawable
	// dictates the shape and rounded corners for the window background blur area.
	private var mWindowBackgroundDrawable: Drawable? = null
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContentView(binding.root)
		ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
			val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
			v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
			insets
		}
		mWindowBackgroundDrawable = AppCompatResources.getDrawable(this, R.mipmap.background_home_1)
		window.setBackgroundDrawable(mWindowBackgroundDrawable)

		if (isS31Plus()) {
			window.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
			setupWindowBlurListener()
		} else {
			updateWindowForBlurs()
		}
		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
	}

	@RequiresApi(Build.VERSION_CODES.S)
	private fun setupWindowBlurListener() {
		val windowBlurEnabledListener = this::updateWindowForBlurs
		window.decorView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
			override fun onViewAttachedToWindow(p0: View) {
				windowManager.addCrossWindowBlurEnabledListener(windowBlurEnabledListener)
			}

			override fun onViewDetachedFromWindow(p0: View) {
				windowManager.removeCrossWindowBlurEnabledListener(windowBlurEnabledListener)
			}
		})
	}

	private fun updateWindowForBlurs(bool: Boolean = false) {
		mWindowBackgroundDrawable!!.alpha =
			if (bool && mBackgroundBlurRadius > 0) mWindowBackgroundAlphaWithBlur else mWindowBackgroundAlphaNoBlur
		window.setDimAmount(if (bool && mBlurBehindRadius > 0) mDimAmountWithBlur else mDimAmountNoBlur)
		if (isS31Plus()) {
			window.setBackgroundBlurRadius(mBackgroundBlurRadius)
			window.attributes.blurBehindRadius = mBlurBehindRadius
			window.attributes = window.attributes
		}
	}
}