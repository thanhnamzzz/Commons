package common.commons

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import common.commons.blurView.BlurActivity
import common.commons.databinding.ActivityMainBinding
import common.libs.SimpleActivity
import common.libs.extensions.PatternDate
import common.libs.extensions.formatDate
import common.libs.extensions.hideSystemNavigationBar
import common.libs.extensions.isQ29Plus
import common.libs.extensions.toastMess
import common.libs.functions.openAppSettingsWifi
import common.libs.functions.openPanelNetwork
import common.libs.functions.versionApp
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

        toastMess( getString(R.string.app_name), TypeToast.TOAST_SUCCESS)
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
    }

    override fun onPause() {
        super.onPause()
        Log.d("Namzzz", "MainActivity: onPause")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Namzzz", "MainActivity: onResume")
    }

    private val launcherNetwork = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        Log.d("Namzzz", "MainActivity: in launcherNetwork")
    }
}