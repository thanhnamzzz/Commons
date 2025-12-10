package common.commons

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import common.commons.ui.theme.CommonsTheme
import common.libs.compose.shimmer.LocalShimmerTheme
import common.libs.compose.shimmer.defaultShimmerTheme
import common.libs.compose.shimmer.shimmer
import common.libs.compose.shimmer.shimmerSpec
import common.libs.compose.toast.CToastConfiguration
import common.libs.compose.toast.CToastHost
import common.libs.compose.toast.CToastState
import common.libs.compose.toast.CToastType
import common.libs.compose.toast.LocalCToastConfig
import kotlinx.coroutines.launch

class ToastActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			var useCustomToasts by remember { mutableStateOf(false) }
			var isDarkMode by remember { mutableStateOf(false) }
			var fullBackground by remember { mutableStateOf(true) }
			var showShimmer by remember { mutableStateOf(true) }

			val cToast = CToastState()
			val scope = rememberCoroutineScope()
			val customCToastConfig = remember {
				CToastConfiguration().copy(
					successToastColor = R.color.success_color,
					errorToastColor = R.color.error_color,
					warningToastColor = R.color.warning_color,
					infoToastColor = R.color.info_color,
					// You can add background colors here too if needed
				)
			}
			val defaultCToastConfig = remember { CToastConfiguration() }
			val currentComposeConfig =
				if (useCustomToasts) customCToastConfig else defaultCToastConfig
			CompositionLocalProvider(LocalCToastConfig provides currentComposeConfig) {
				CommonsTheme(darkTheme = isDarkMode) {
					Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
						MainScreen(
							modifier = Modifier.padding(innerPadding),
							useCustom = useCustomToasts,
							onSwitchChange = { isChecked ->
								useCustomToasts = isChecked
							},
							isDarkMode = isDarkMode,
							onDarkModeChecked = { isChecked ->
								isDarkMode = isChecked
							},
							fullBackground = fullBackground,
							onFullBackgroundChecked = { isChecked ->
								fullBackground = isChecked
							},
							onSuccessClick = {
								scope.launch {
									cToast.setAndShow(
										title = getString(R.string.edit_profile),
										message = getString(R.string.your_profile_was_updated_successfully),
										type = CToastType.SUCCESS,
										isDarkMode = isDarkMode,
										isFullBackground = fullBackground
									)
								}
							},
							onErrorClick = {
								scope.launch {
									cToast.setAndShow(
										title = getString(R.string.upload_failed),
										message = getString(R.string.there_was_an_error_uploading_your_file_please_try_again),
										type = CToastType.ERROR,
										isDarkMode = isDarkMode,
										isFullBackground = fullBackground
									)
								}
							},
							onWarningClick = {
								scope.launch {
									cToast.setAndShow(
										title = getString(R.string.low_battery),
										message = getString(R.string.your_battery_is_running_low_please_connect_to_a_charger),
										type = CToastType.WARNING,
										isDarkMode = isDarkMode,
										isFullBackground = fullBackground
									)
								}
							},
							onInfoClick = {
								scope.launch {
									cToast.setAndShow(
										title = getString(R.string.new_update_available),
										message = getString(R.string.a_new_version_of_the_app_is_available_please_update_to_the_latest_version),
										type = CToastType.INFO,
										isDarkMode = isDarkMode,
										isFullBackground = fullBackground
									)
								}
							},
							onDeleteClick = {
								scope.launch {
									cToast.setAndShow(
										title = getString(R.string.delete_account),
										message = getString(R.string.your_account_has_been_deleted_successfully),
										type = CToastType.INFO,
										isDarkMode = isDarkMode,
										isFullBackground = fullBackground
									)
								}
							},
							onNoInternetClick = {
								scope.launch {
									cToast.setAndShow(
										title = getString(R.string.no_internet_connection),
										message = getString(R.string.please_check_your_internet_connection_and_try_again),
										type = CToastType.WARNING,
										isDarkMode = isDarkMode,
										isFullBackground = fullBackground
									)
								}
							},
							showShimmer = showShimmer,
							onShowShimmer = { isChecked ->
								showShimmer = isChecked
							}
						)

						// The Compose Host is ready to show toasts
						CToastHost(hostState = cToast, modifier = Modifier.systemBarsPadding())
					}
				}
			}
		}
	}
}

@Composable
fun MainScreen(
	modifier: Modifier = Modifier,
	useCustom: Boolean,
	isDarkMode: Boolean,
	onDarkModeChecked: (Boolean) -> Unit,
	fullBackground: Boolean,
	onFullBackgroundChecked: (Boolean) -> Unit,
	onSwitchChange: (Boolean) -> Unit,
	showShimmer: Boolean,
	onShowShimmer: (Boolean) -> Unit,
	onSuccessClick: () -> Unit,
	onErrorClick: () -> Unit,
	onWarningClick: () -> Unit,
	onInfoClick: () -> Unit,
	onDeleteClick: () -> Unit,
	onNoInternetClick: () -> Unit,
) {
	Log.d("Namzzz", ": MainScreen showShimmer $showShimmer")
//	var shimmerTheme by remember { mutableStateOf(defaultShimmerTheme) }
	val shimmerTheme = defaultShimmerTheme.copy(
		animationSpec = infiniteRepeatable(
			animation = shimmerSpec(
				durationMillis = 1_800,
				delayMillis = 700,
				easing = LinearEasing,
			),
		),
		blendMode = BlendMode.Overlay,
		rotation = 45f,
		shaderColors = listOf(
			Color.White.copy(alpha = 0.3f),
			Color.DarkGray.copy(alpha = 1f),
//			Color(255, 68, 68).copy(alpha = 1f),
			Color.White.copy(alpha = 0.3f),
		),
		shaderColorStops = null,
		shimmerWidth = 400.dp,
	)
	CompositionLocalProvider(LocalShimmerTheme provides shimmerTheme) {
		Box(
			modifier = modifier
				.fillMaxSize()
				.background(color = MaterialTheme.colorScheme.background),
		) {
			val scrollState = rememberScrollState()

			Column(
				horizontalAlignment = Alignment.CenterHorizontally,
				modifier = Modifier
					.padding(start = 72.dp, end = 72.dp, top = 32.dp)
					.verticalScroll(scrollState)
					.then(
						if (showShimmer) Modifier.shimmer()
						else Modifier
					)
			) {
				Row(
					modifier = Modifier
						.fillMaxWidth(),
					verticalAlignment = Alignment.CenterVertically
				) {
					Switch(
						checked = useCustom,
						onCheckedChange = onSwitchChange
					)
					Text(
						text = stringResource(R.string.custom_colors),
						modifier = Modifier.padding(start = 8.dp),
						color = MaterialTheme.colorScheme.onBackground
					)
				}

				Row(
					modifier = Modifier.fillMaxWidth(),
					verticalAlignment = Alignment.CenterVertically
				) {
					Checkbox(
						checked = fullBackground,
						onCheckedChange = onFullBackgroundChecked
					)
					Text(
						text = stringResource(R.string.full_background),
						modifier = Modifier.padding(start = 8.dp),
						color = MaterialTheme.colorScheme.onBackground
					)
				}

				Row(
					modifier = Modifier.fillMaxWidth(),
					verticalAlignment = Alignment.CenterVertically
				) {
					Checkbox(
						checked = isDarkMode,
						onCheckedChange = onDarkModeChecked
					)
					Text(
						text = stringResource(R.string.dark_mode),
						modifier = Modifier.padding(start = 8.dp),
						color = MaterialTheme.colorScheme.onBackground
					)
				}

				Button(
					onClick = onSuccessClick,
					shape = RectangleShape,
					colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
					modifier = Modifier.fillMaxWidth()
				) {
					Text(
						text = stringResource(R.string.success).uppercase(),
						color = MaterialTheme.colorScheme.onPrimary
					)
				}
				Button(
					onClick = onErrorClick,
					shape = RectangleShape,
					colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error),
					modifier = Modifier.fillMaxWidth()
				) {
					Text(
						text = stringResource(R.string.error).uppercase(),
						color = MaterialTheme.colorScheme.onError
					)
				}
				Button(
					onClick = onWarningClick,
					shape = RectangleShape,
					colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.tertiary),
					modifier = Modifier.fillMaxWidth()
				) {
					Text(
						text = stringResource(R.string.warning).uppercase(),
						color = MaterialTheme.colorScheme.onTertiary
					)
				}
				Button(
					onClick = onInfoClick,
					shape = RectangleShape,
					colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary),
					modifier = Modifier.fillMaxWidth()
				) {
					Text(
						text = stringResource(R.string.info).uppercase(),
						color = MaterialTheme.colorScheme.onSecondary
					)
				}
				Button(
					onClick = onDeleteClick,
					shape = RectangleShape,
					colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.errorContainer),
					modifier = Modifier.fillMaxWidth()
				) {
					Text(
						text = stringResource(R.string.delete).uppercase(),
						color = MaterialTheme.colorScheme.onErrorContainer
					)
				}
				Button(
					onClick = onNoInternetClick,
					shape = RectangleShape,
					colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.tertiary),
					modifier = Modifier.fillMaxWidth()
				) {
					Text(
						text = stringResource(R.string.no_internet).uppercase(),
						color = MaterialTheme.colorScheme.onTertiary
					)
				}
			}
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.align(Alignment.BottomCenter),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.Center
			) {
				Switch(
					checked = showShimmer,
					onCheckedChange = {
						onShowShimmer(it)
//						if (it) {
//							shimmerTheme = shimmerTheme.copy(
//								shimmerWidth = 200.dp,
//								animationSpec = infiniteRepeatable(
//									animation = shimmerSpec(
//										durationMillis = 550,
//										delayMillis = 300,
//									),
//									repeatMode = RepeatMode.Restart
//								),
//								shaderColors = listOf(
//									Color.Gray.copy(alpha = 1.00f),
//									Color.White.copy(alpha = 1.00f),
//									Color.Gray.copy(alpha = 1.00f),
//								)
//							)
//						}
					}
				)
				Text(
					text = "Show Shimmer",
					modifier = Modifier.padding(start = 8.dp),
					color = MaterialTheme.colorScheme.onBackground
				)
			}
		}
	}
}