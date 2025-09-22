package common.libs.animationView

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityOptionsCompat

/** Ở activity thứ 2 thì thêm `android:transitionName="sharedElementName"` vào view mà activity 2 muốn được zoom vào */
fun Activity.activityMakeSceneTransition(
	viewTarget: View,
	sharedElementName: String
): Bundle? {
	return ActivityOptionsCompat.makeSceneTransitionAnimation(
		this,
		viewTarget,
		sharedElementName
	).toBundle()
}

fun activityMakeClipRevealAnimation(
	viewTarget: View,
	startX: Int = 0,
	startY: Int = 0
): Bundle? {
	return ActivityOptionsCompat.makeClipRevealAnimation(
		viewTarget, startX, startY,
		viewTarget.width, viewTarget.height
	).toBundle()
}

fun activityMakeScaleUpAnimation(
	viewTarget: View,
	startX: Int = 0,
	startY: Int = 0
): Bundle? {
	return ActivityOptionsCompat.makeScaleUpAnimation(
		viewTarget, startX, startY,
		viewTarget.width, viewTarget.height
	).toBundle()
}

fun Activity.activityMakeCustomAnimation(enterRes: Int, exitRes: Int): Bundle? {
	return ActivityOptionsCompat.makeCustomAnimation(this, enterRes, exitRes).toBundle()
}

fun activityMakeBasicAnimation(): Bundle? {
	return ActivityOptionsCompat.makeBasic().toBundle()
}