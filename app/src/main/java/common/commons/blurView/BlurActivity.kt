package common.commons.blurView

import android.os.Bundle
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import common.commons.data.ViewAdapter
import common.commons.databinding.ActivityBlurBinding
import common.libs.SimpleActivity
import common.libs.blurView.setupBlurBackground

class BlurActivity : SimpleActivity<ActivityBlurBinding>(ActivityBlurBinding::inflate) {
	private lateinit var viewAdapter: ViewAdapter
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContentView(binding.root)
		ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
			val systemBars = insets.getInsets(
				WindowInsetsCompat.Type.systemBars()
						or WindowInsetsCompat.Type.displayCutout()
			)
			v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
			insets
		}
		viewAdapter = ViewAdapter(this)
		val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
		binding.rvList.addItemDecoration(itemDecoration)
		binding.rvList.adapter = viewAdapter

		val float = 2f
		window.apply {
			this.setupBlurBackground(binding.blurView, binding.blurTarget, float, false)
			this.setupBlurBackground(binding.blurViewV3, binding.blurTarget, float)
		}
		binding.seekbarBlur.progress = float.toInt()
		binding.seekbarBlur.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
			override fun onProgressChanged(
				p0: SeekBar?,
				p1: Int,
				p2: Boolean,
			) {
				if (p1 > 0) {
					binding.blurView.setBlurRadius(p1.toFloat())
					binding.blurViewV3.setBlurRadius(p1.toFloat())
				}
			}

			override fun onStartTrackingTouch(p0: SeekBar?) {

			}

			override fun onStopTrackingTouch(p0: SeekBar?) {

			}
		})
	}
}