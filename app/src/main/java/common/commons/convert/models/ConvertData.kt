package common.commons.convert.models

data class ConvertData(
    val value: String,
    val result: String,
    val from: Int,
    val to: Int,
) {
    fun swap() = copy(
        value = result, result = value,
        from = to, to = from
    )
}
