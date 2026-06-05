package common.commons.m3component

import android.view.Gravity
import com.google.android.material.listitem.SwipeableListItem

data class ItemData(
	val text: String = "",
	val swipeState: Int = SwipeableListItem.STATE_CLOSED,
	val swipeGravity: Int = Gravity.END
)
