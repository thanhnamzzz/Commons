/* Clone from https://github.com/gayanvoice/android-animations-kotlin */

package common.libs.animationView

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.view.animation.AccelerateInterpolator

class AnimationView() {
    var du: Long = 1000
    private var isLoop: Boolean = false
    private var delayLoop: Long = 1000
    private var isPause: Boolean = false

    lateinit var animatorSet: AnimatorSet

    fun setAnimation(animatorSet: AnimatorSet) {
        this.animatorSet = animatorSet
    }

    fun setDuration(duration: Long) {
        this.du = duration
    }

    fun isLoop(isLoop: Boolean) {
        this.isLoop = isLoop
    }

    fun setDelayLoop(delayLoop: Long) {
        this.delayLoop = delayLoop
    }

    fun start(animationEnd: () -> Unit = {}) {
        animatorSet.removeAllListeners()
        animatorSet.duration = du
        animatorSet.interpolator = AccelerateInterpolator()
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                if (isLoop && !isPause) {
                    animatorSet.startDelay = delayLoop
                    animatorSet.start()
                }
                animationEnd()
            }
        })
        animatorSet.start()
    }

    fun pause() {
        isPause = true
        animatorSet.pause()
    }

    fun resume() {
        isPause = false
        animatorSet.resume()
    }

    fun cancel() {
        isPause = true
        animatorSet.cancel()
    }
}