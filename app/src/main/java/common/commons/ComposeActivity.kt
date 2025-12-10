package common.commons

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import common.commons.ui.theme.CommonsTheme
import common.libs.compose.extensions.SetNavigationBarContentColor
import common.libs.compose.extensions.SetStatusBarContentColor
import common.libs.compose.functions.HideKeyboard
import common.libs.compose.functions.ShowKeyboard
import common.libs.compose.functions.isConnectedInternet
import common.libs.compose.functions.shareApp
import common.libs.compose.functions.versionApp
import common.libs.compose.toast.CToastHost
import common.libs.compose.toast.CToastState
import common.libs.compose.toast.CToastType
import kotlinx.coroutines.launch

class ComposeActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			window.SetStatusBarContentColor(MaterialTheme.colorScheme.background)
			window.SetNavigationBarContentColor(MaterialTheme.colorScheme.background)
			CommonsTheme {
				Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
					Greeting(
						context = this,
						name = "Android",
						modifier = Modifier
							.padding(innerPadding)
							.windowInsetsPadding(WindowInsets.ime)
					)
				}
			}
		}
	}
}

@Composable
fun Greeting(context: Context, name: String, modifier: Modifier = Modifier) {
	var showKeyboard by remember { mutableStateOf(false) }
	val focusRequester = remember { FocusRequester() }
	val cToast = CToastState()
	val scope = rememberCoroutineScope()
	val version = context.versionApp()
	var text by remember { mutableStateOf("") }
	Column(
		modifier = modifier.fillMaxSize(),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(text = "Hello $name!")
		Button(onClick = {
			scope.launch {
				cToast.setAndShow(
					title = "Version App",
					message = version,
					type = CToastType.SUCCESS,
					isDarkMode = false,
					isFullBackground = false,
					duration = 1000,
				)
			}
		}) {
			Text("Version App")
		}
		TextField(
			value = text,
			onValueChange = { text = it },
			modifier = Modifier.focusRequester(focusRequester)
		)
		Button(onClick = {
			showKeyboard = !showKeyboard
		}) {
			Text(if (showKeyboard) "Keyboard : show" else "Keyboard : hide")
		}
		Button(onClick = { context.shareApp() }) {
			Text("Share App")
		}
		Button(onClick = {
			Log.d("Namzzz", ": Greeting isConnected = ${context.isConnectedInternet()}")
		}) {
			Text("Connect Internet")
		}
	}

	if (showKeyboard) {
		ShowKeyboard(focusRequester)
	} else {
		HideKeyboard()
	}
	CToastHost(hostState = cToast, modifier = Modifier.systemBarsPadding())
}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun GreetingPreview() {
//	CommonsTheme {
//		Greeting(LocalContext.current, "Android")
//	}
//}