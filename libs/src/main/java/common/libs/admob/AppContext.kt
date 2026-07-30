package common.libs.admob

import android.content.Context

object AppContext {
	private lateinit var appContext: Context

	fun init(context: Context) {
		appContext = context
	}

	fun getContext(): Context {
		return appContext
	}
}