package common.libs.functions

/**
 * Sắp xếp một Iterable dựa trên một thuộc tính.
 *
 * @param isAscending true nếu sắp xếp tăng dần, false nếu giảm dần.
 * @param propertySelector Hàm chọn thuộc tính để sắp xếp.
 */
//inline fun <T, R : Comparable<R>> sortByProperty(
//	list: Iterable<T>,
//	isAscending: Boolean = true,
//	crossinline propertySelector: (T) -> R,
//): List<T> {
//    return if (isAscending) {
//        list.sortedWith(compareBy { propertySelector(it) })
//    } else {
//        list.sortedWith(compareByDescending { propertySelector(it) })
//    }
//}

inline fun <T, R : Comparable<R>> Iterable<T>.sortByProperty(
	isAscending: Boolean = true,
	crossinline propertySelector: (T) -> R
): List<T> {
	return if (isAscending) {
		this.sortedBy(propertySelector)
	} else {
		this.sortedByDescending(propertySelector)
	}
}