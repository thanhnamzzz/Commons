package common.commons

import android.app.Application
import com.google.android.material.color.DynamicColors

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Áp dụng Dynamic Color (Material You) cho toàn bộ Activity trong app
        // Tính năng này tự động thay đổi bảng màu của app dựa trên hình nền của người dùng (từ Android 12+)
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}
