import com.google.gson.Gson

/**
 * create by wuseal on 2022-0528
 * This is a safety json convert tool, will never cause exception and return null if any exception occurs
 */
val prettyJsonGson = Gson().newBuilder().setPrettyPrinting().create()

inline fun <reified T> Gson.fromJson(json: String): T? {
    return try {
        fromJson(json, T::class.java)
    } catch (e: Exception) {
        null
    }
}

internal fun <T> String.fromJson(clazz: Class<T>): T = prettyJsonGson.fromJson<T>(this, clazz)

internal fun <T> T.toJson():String = prettyJsonGson.toJson(this)
