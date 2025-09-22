package common.libs.views

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun Context.loadImage(
	view: ImageView,
	image: Any,
	complete: (complete: Boolean, error: String?) -> Unit = { _, _ -> }
) {
	val exception = CoroutineExceptionHandler { _, throwable ->
		throwable.message?.let {
			(this as Activity).runOnUiThread {
				complete(false, it)
			}
		}
	}
	CoroutineScope(Dispatchers.Main).launch(exception) {
		Glide.with(this@loadImage).load(image).into(view)
		complete(true, null)
	}
}

enum class ImageTrans {
	FIT_CENTER,
	CENTER_CROP,
	CENTER_INSIDE,
	CIRCLE_CROP
}

fun Context.loadImage(
	view: ImageView,
	image: Any,
	trans: ImageTrans,
	complete: (complete: Boolean, error: String?) -> Unit = { _, _ -> }
) {
	val exception = CoroutineExceptionHandler { _, throwable ->
		throwable.message?.let {
			(this as Activity).runOnUiThread {
				complete(false, it)
			}
		}
	}
	CoroutineScope(Dispatchers.Main).launch(exception) {
		when (trans) {
			ImageTrans.FIT_CENTER -> Glide.with(this@loadImage).load(image).fitCenter().into(view)
			ImageTrans.CENTER_CROP -> Glide.with(this@loadImage).load(image).centerCrop().into(view)
			ImageTrans.CENTER_INSIDE -> Glide.with(this@loadImage).load(image).centerInside()
				.into(view)

			ImageTrans.CIRCLE_CROP -> Glide.with(this@loadImage).load(image).circleCrop().into(view)
		}
		complete(true, null)
	}
}

fun Context.loadImage(
	view: ImageView,
	image: Any,
	trans: ImageTrans,
	error: Any,
	complete: (complete: Boolean, error: String?) -> Unit = { _, _ -> }
) {
	val exception = CoroutineExceptionHandler { _, throwable ->
		throwable.message?.let {
			(this as Activity).runOnUiThread {
				complete(false, it)
			}
		}
	}
	CoroutineScope(Dispatchers.Main).launch(exception) {
		when (trans) {
			ImageTrans.FIT_CENTER -> Glide.with(this@loadImage).load(image).fitCenter().error(
				Glide.with(this@loadImage).load(error).centerInside()
			).into(view)

			ImageTrans.CENTER_CROP -> Glide.with(this@loadImage).load(image).centerCrop().error(
				Glide.with(this@loadImage).load(error).centerInside()
			).into(view)

			ImageTrans.CENTER_INSIDE -> Glide.with(this@loadImage).load(image).centerInside().error(
				Glide.with(this@loadImage).load(error).centerInside()
			).into(view)

			ImageTrans.CIRCLE_CROP -> Glide.with(this@loadImage).load(image).circleCrop().error(
				Glide.with(this@loadImage).load(error).centerInside()
			).into(view)
		}
		complete(true, null)
	}
}

fun Context.loadImage(
	view: ImageView,
	image: Any,
	trans: ImageTrans,
	placeHolder: Drawable?,
	error: Any,
	complete: (complete: Boolean, error: String?) -> Unit = { _, _ -> }
) {
	val exception = CoroutineExceptionHandler { _, throwable ->
		throwable.message?.let {
			(this as Activity).runOnUiThread {
				complete(false, it)
			}
		}
	}
	CoroutineScope(Dispatchers.Main).launch(exception) {
		when (trans) {
			ImageTrans.FIT_CENTER -> Glide.with(this@loadImage).load(image).fitCenter()
				.placeholder(placeHolder).error(
					Glide.with(this@loadImage).load(error).centerInside()
				).into(view)

			ImageTrans.CENTER_CROP -> Glide.with(this@loadImage).load(image).centerCrop()
				.placeholder(placeHolder).error(
					Glide.with(this@loadImage).load(error).centerInside()
				).into(view)

			ImageTrans.CENTER_INSIDE -> Glide.with(this@loadImage).load(image).centerInside()
				.placeholder(placeHolder).error(
					Glide.with(this@loadImage).load(error).centerInside()
				).into(view)

			ImageTrans.CIRCLE_CROP -> Glide.with(this@loadImage).load(image).circleCrop()
				.placeholder(placeHolder).error(
					Glide.with(this@loadImage).load(error).centerInside()
				).into(view)
		}
		complete(true, null)
	}
}