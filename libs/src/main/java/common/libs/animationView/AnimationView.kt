/* Clone from https://github.com/gayanvoice/android-animations-kotlin */

package common.libs.animationView

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Interpolator
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class AnimationView : DefaultLifecycleObserver {
    /**
     * Thời gian chạy animation (ms)
     */
    var duration: Long = 1000

    /**
     * Có lặp lại animation hay không
     */
    var isLoop: Boolean = false

    /**
     * Thời gian chờ giữa các lần lặp (ms)
     */
    var delayLoop: Long = 1000

    /**
     * Bộ nội suy (Interpolator) cho animation
     */
    var interpolator: Interpolator = AccelerateInterpolator()

    private var isPause: Boolean = false
    private var internalAnimatorSet: AnimatorSet? = null

    fun isPaused(): Boolean = isPause

    fun setAnimation(animatorSet: AnimatorSet) {
        this.internalAnimatorSet = animatorSet
    }

    /**
     * Bắt đầu chạy animation
     * @param onEnd Callback khi animation kết thúc (nếu không lặp)
     */
    fun start(onEnd: () -> Unit = {}) {
        val animator = internalAnimatorSet ?: return

        animator.removeAllListeners()
        animator.duration = duration
        animator.interpolator = interpolator
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                if (isLoop && !isPause) {
                    animator.startDelay = delayLoop
                    animator.start()
                }
                onEnd()
            }
        })
        isPause = false
        animator.start()
    }

    fun isRunning(): Boolean = internalAnimatorSet?.isRunning ?: false

    fun pause() {
        isPause = true
        internalAnimatorSet?.pause()
    }

    fun resume() {
        isPause = false
        internalAnimatorSet?.resume()
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        pause()
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        resume()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        destroy()
    }

    fun cancel(callback: () -> Unit = {}) {
        isPause = true
        internalAnimatorSet?.cancel()
        callback()
    }

    /**
     * Hủy animation và giải phóng tài nguyên
     */
    fun destroy(callback: () -> Unit = {}) {
        cancel(callback)
        internalAnimatorSet?.removeAllListeners()
        internalAnimatorSet = null
    }

    fun reset(view: View?) {
        view?.apply {
            alpha = 1f
            scaleX = 1f
            scaleY = 1f
            translationX = 0f
            translationY = 0f
            rotation = 0f
            rotationX = 0f
            rotationY = 0f
        }
    }
}

fun View.playAnimation(
    animatorSet: AnimatorSet,
    duration: Long = 1000,
    isLoop: Boolean = false,
    onEnd: () -> Unit = {}
): AnimationView {
    return AnimationView().apply {
        this.setAnimation(animatorSet)
        this.duration = duration
        this.isLoop = isLoop
        this.start(onEnd)
    }
}