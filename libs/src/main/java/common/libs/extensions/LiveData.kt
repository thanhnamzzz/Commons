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