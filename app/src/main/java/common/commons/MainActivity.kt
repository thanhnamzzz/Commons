package common.commons

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import common.libs.extensions.PatternDate
import common.libs.extensions.formatDate
import common.libs.extensions.toastMess
import common.libs.views.MyToast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val m1 = "yyyy-MM-dd HH:mm:ss".formatDate(2024, 9, 27)
        val m2 = PatternDate.EEEE_dd_MM_yyyy.formatDate(2025, 10, 1)

        toastMess(this, getString(R.string.app_name), 0, MyToast.TypeToast.TOAST_SUCCESS)
        Log.d("Namzzz", "MainActivity: onCreate m1 = $m1")
        Log.d("Namzzz", "MainActivity: onCreate m2 = $m2")
    }
}