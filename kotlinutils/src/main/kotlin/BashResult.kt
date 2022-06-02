/**
 * Created by wuseal in 2022-0528
 */

import wu.seal.tools.gradle.KotlinUtilsPlugin.Companion.processOperations
import java.io.File
import java.util.concurrent.TimeUnit

fun String.evalBash(): Result<String> {
    return runCommandInner(0) {
        redirectOutput(ProcessBuilder.Redirect.PIPE)
        redirectInput(ProcessBuilder.Redirect.PIPE)
        redirectError(ProcessBuilder.Redirect.PIPE)
    }.run {
        val stdout = inputStream.reader().readLines().joinToString("\n")
        val stderr = errorStream.reader().readLines().joinToString("\n")
        waitFor(1, TimeUnit.HOURS)
        val exitCode = exitValue()
        if (exitCode == 0) Result.success(stdout)
        else Result.failure(Exception("$exitCode:Execute command failed with exit code $exitCode:\n$stderr))"))
    }
}


internal fun String.runCommandInner(
    timeoutValue: Long = 60,
    timeoutUnit: TimeUnit = TimeUnit.MINUTES,
    processConfig: ProcessBuilder.() -> Unit = {}
): Process {
    ProcessBuilder("/bin/bash", "-c", this).run {
        directory(File("."))
        inheritIO()
        processConfig()
        val process = start()
        if (timeoutValue > 0L) {
            process.waitFor(timeoutValue, timeoutUnit)
        } else if (timeoutValue < 0) {
            process.waitFor()
        }
        return process
    }
}

fun String.runCommand(): Result<Unit> {
    val thisProcessOperations = processOperations
    if (thisProcessOperations != null) {
        val execResult = try {
            thisProcessOperations.exec {
                commandLine("/bin/bash", "-c", this@runCommand)
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
        if (execResult.exitValue == 0) {
            return Result.success(Unit)
        } else {
            return Result.failure(IllegalStateException("Command exit with none 0 code: ${execResult.exitValue}"))
        }
    }
    return runCommandInner(0) {
        redirectOutput(ProcessBuilder.Redirect.PIPE)
        redirectInput(ProcessBuilder.Redirect.PIPE)
        redirectErrorStream(true)
    }.run {
        inputStream.reader().useLines {
            it.forEach { println(it) }
        }
        waitFor()
        if (exitValue() == 0) {
            Result.success(Unit)
        } else {
            Result.failure(IllegalStateException("Command exit with none 0 code: ${exitValue()}"))
        }
    }
}


fun Process.throwIfError(): Process {
    if (this.exitValue() != 0) {
        throw kotlin.RuntimeException("Process exec error ${toString()}")
    }
    return this
}

fun killAllSubProcesses() {
    ProcessHandle.current().descendants().forEach { it.destroy() }
    while (ProcessHandle.current().descendants().anyMatch { it.isAlive }) {
        Thread.sleep(100)
    }
}
