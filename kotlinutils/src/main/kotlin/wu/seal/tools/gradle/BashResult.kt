package wu.seal.tools.gradle

import java.lang.RuntimeException

data class BashResult(val exitCode: Int, val stdout: Iterable<String>, val stderr: Iterable<String>) {
    fun sout() = stdout.joinToString("\n").trim()

    fun serr() = stderr.joinToString("\n").trim()

    fun getOrNull(): String? {
        return if (exitCode == 0) sout() else null
    }

    fun exceptionOrNull(): Throwable? = if (exitCode != 0) RuntimeException(serr()) else null

    fun getOrDefault(defaultValue: String) = if (exitCode == 0) sout() else defaultValue

    fun onFailure(action: BashResult.() -> Unit): BashResult {
        if (exitCode != 0) action()
        return this
    }

    fun onSuccess(action: BashResult.(output: String) -> Unit): BashResult {
        if (exitCode == 0) action(sout())
        return this
    }


}