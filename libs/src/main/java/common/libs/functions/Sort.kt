package common.libs.functions

fun <T, R : Comparable<R>> sortByProperty(
    list: Iterable<T>,
    isAscending: Boolean = true,
    propertySelector: (T) -> R,
): List<T> {
    return if (isAscending) {
        list.sortedWith(compareBy { propertySelector(it) })
    } else {
        list.sortedWith(compareByDescending { propertySelector(it) })
    }
}