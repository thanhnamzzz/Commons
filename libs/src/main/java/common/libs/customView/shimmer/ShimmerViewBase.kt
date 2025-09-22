package common.libs.customView.shimmer

/**
 * Shimmer
 * User: romainpiel
 * Date: 10/03/2014
 * Time: 17:33
 */
interface ShimmerViewBase {

	var gradientX: Float
	var isShimmering: Boolean
	var isSetUp: Boolean

	var primaryColor: Int
	var reflectionColor: Int

	fun setAnimationSetupCallback(callback: ShimmerViewHelper.AnimationSetupCallback)
}
