package common.libs.extensions

import androidx.lifecycle.MutableLiveData

/** Extensions cho MutableLiveData<List> (immutable list) **/
// Thêm 1 item
fun <T> MutableLiveData<List<T>>.addItem(item: T) {
	value = (value ?: emptyList()) + item
}

// Thêm nhiều item
fun <T> MutableLiveData<List<T>>.addItems(items: List<T>) {
	value = (value ?: emptyList()) + items
}

// Xóa 1 item (dựa trên equals)
fun <T> MutableLiveData<List<T>>.removeItem(item: T) {
	value = value?.filter { it != item }
}

// Xóa nhiều item
fun <T> MutableLiveData<List<T>>.removeItems(items: List<T>) {
	value = value?.filter { it !in items }
}

// Cập nhật 1 item (theo predicate)
fun <T> MutableLiveData<List<T>>.updateItem(predicate: (T) -> Boolean, newItem: T) {
	value = value?.map { if (predicate(it)) newItem else it }
}

// Cập nhật nhiều item
fun <T> MutableLiveData<List<T>>.updateItems(predicate: (T) -> Boolean, newItems: List<T>) {
	val mutable = value?.toMutableList() ?: mutableListOf()
	newItems.forEach { newItem ->
		val index = mutable.indexOfFirst { predicate(it) }
		if (index >= 0) mutable[index] = newItem
	}
	value = mutable.toList()
}

// Lấy item đầu tiên thỏa điều kiện
fun <T> MutableLiveData<List<T>>.firstItem(predicate: (T) -> Boolean): T? {
	return value?.firstOrNull(predicate)
}

// Lấy item cuối cùng thỏa điều kiện
fun <T> MutableLiveData<List<T>>.lastItem(predicate: (T) -> Boolean): T? {
	return value?.lastOrNull(predicate)
}

// Tìm index đầu tiên thỏa điều kiện
fun <T> MutableLiveData<List<T>>.indexOfFirstItem(predicate: (T) -> Boolean): Int {
	return value?.indexOfFirst(predicate) ?: -1
}

// Lọc ra list mới thỏa điều kiện
fun <T> MutableLiveData<List<T>>.filterList(predicate: (T) -> Boolean): List<T> {
	return value?.filter(predicate) ?: emptyList()
}

// Lọc ra list mới, map sang kiểu khác
fun <T, R> MutableLiveData<List<T>>.mapList(transform: (T) -> R): List<R> {
	return value?.map(transform) ?: emptyList()
}

// Kiểm tra tồn tại item thỏa điều kiện
fun <T> MutableLiveData<List<T>>.anyItem(predicate: (T) -> Boolean): Boolean {
	return value?.any(predicate) == true
}

// Kiểm tra tất cả item thỏa điều kiện
fun <T> MutableLiveData<List<T>>.allItems(predicate: (T) -> Boolean): Boolean {
	return value?.all(predicate) == true
}

// Lấy danh sách copy ra MutableList
fun <T> MutableLiveData<List<T>>.toMutableList(): MutableList<T> {
	return value?.toMutableList() ?: mutableListOf()
}

/** Extensions cho MutableLiveData<MutableList> (mutable list) **/
// Thêm 1 item
fun <T> MutableLiveData<MutableList<T>>.addItemMutable(item: T) {
	val list = value ?: mutableListOf()
	list.add(item)
	value = list
}

// Thêm nhiều item
fun <T> MutableLiveData<MutableList<T>>.addItemsMutable(items: List<T>) {
	val list = value ?: mutableListOf()
	list.addAll(items)
	value = list
}

// Xóa 1 item
fun <T> MutableLiveData<MutableList<T>>.removeItemMutable(item: T) {
	val list = value ?: mutableListOf()
	list.remove(item)
	value = list
}

// Xóa nhiều item
fun <T> MutableLiveData<MutableList<T>>.removeItemsMutable(items: List<T>) {
	val list = value ?: mutableListOf()
	list.removeAll(items)
	value = list
}

// Cập nhật 1 item
fun <T> MutableLiveData<MutableList<T>>.updateItemMutable(predicate: (T) -> Boolean, newItem: T) {
	val list = value ?: mutableListOf()
	val index = list.indexOfFirst { predicate(it) }
	if (index >= 0) list[index] = newItem
	value = list
}

// Cập nhật nhiều item
fun <T> MutableLiveData<MutableList<T>>.updateItemsMutable(predicate: (T) -> Boolean, newItems: List<T>) {
	val list = value ?: mutableListOf()
	newItems.forEach { newItem ->
		val index = list.indexOfFirst { predicate(it) }
		if (index >= 0) list[index] = newItem
	}
	value = list
}

// Lấy item đầu tiên
fun <T> MutableLiveData<MutableList<T>>.firstItemMutable(predicate: (T) -> Boolean): T? {
	return value?.firstOrNull(predicate)
}

// Lấy item cuối cùng
fun <T> MutableLiveData<MutableList<T>>.lastItemMutable(predicate: (T) -> Boolean): T? {
	return value?.lastOrNull(predicate)
}

// Tìm index
fun <T> MutableLiveData<MutableList<T>>.indexOfFirstItemMutable(predicate: (T) -> Boolean): Int {
	return value?.indexOfFirst(predicate) ?: -1
}

// Lọc ra list mới (không thay đổi LiveData)
fun <T> MutableLiveData<MutableList<T>>.filterListMutable(predicate: (T) -> Boolean): List<T> {
	return value?.filter(predicate) ?: emptyList()
}

// Map sang kiểu khác
fun <T, R> MutableLiveData<MutableList<T>>.mapListMutable(transform: (T) -> R): List<R> {
	return value?.map(transform) ?: emptyList()
}

// Kiểm tra tồn tại item
fun <T> MutableLiveData<MutableList<T>>.anyItemMutable(predicate: (T) -> Boolean): Boolean {
	return value?.any(predicate) == true
}

// Kiểm tra tất cả item
fun <T> MutableLiveData<MutableList<T>>.allItemsMutable(predicate: (T) -> Boolean): Boolean {
	return value?.all(predicate) == true
}

// Copy ra MutableList
fun <T> MutableLiveData<MutableList<T>>.toMutableListCopy(): MutableList<T> {
	return value?.toMutableList() ?: mutableListOf()
}

/** Ví dụ **/
//data class User(val id: Int, val name: String)
//
//val usersLiveData = MutableLiveData<List<User>>()
//
//// Thêm 1 user
//usersLiveData.addItem(User(1, "Nam"))
//
//// Thêm nhiều user
//usersLiveData.addItems(listOf(User(2, "Linh"), User(3, "An")))
//
//// Xóa user
//usersLiveData.removeItem(User(1, "Nam"))
//
//// Update user theo id
//usersLiveData.updateItem({ it.id == 2 }, User(2, "Linh Updated"))


//val usersLiveData = MutableLiveData<List<User>>(listOf(
//	User(1, "Nam"),
//	User(2, "Linh"),
//	User(3, "An")
//))
//
//// Tìm user theo id
//val user2 = usersLiveData.firstItem { it.id == 2 }
//
//// Lọc ra những user có tên dài hơn 2 ký tự
//val longNames = usersLiveData.filterList { it.name.length > 2 }
//
//// Map sang tên user
//val names = usersLiveData.mapList { it.name }
//
//// Kiểm tra có user nào tên "Nam" không
//val hasNam = usersLiveData.anyItem { it.name == "Nam" }