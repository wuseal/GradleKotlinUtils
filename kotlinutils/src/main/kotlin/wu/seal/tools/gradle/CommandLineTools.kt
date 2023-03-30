package wu.seal.tools.gradle

/**
 * Created by wuseal in 2022-0528
 */

import org.gradle.process.ExecSpec
import wu.seal.tools.gradle.KotlinUtilsPlugin.Companion.processOperations
import java.io.File
import java.util.concurrent.TimeUnit

@JvmName("evalBashForKotlinStringExtension")
fun String.evalBash(showOutput: Boolean = false, wd: File? = null): BashResult {
    return evalBash(this, showOutput, wd)
}

fun evalBash(cmd: String, showOutput: Boolean = false, wd: File? = null): BashResult {
    return cmd.runCommand(0) {
        redirectOutput(ProcessBuilder.Redirect.PIPE)
        redirectInput(ProcessBuilder.Redirect.PIPE)
        redirectError(ProcessBuilder.Redirect.PIPE)
        wd?.let { directory(it) }
    }.run {
        val stdout = inputStream.reader().readLines()
        val stderr = errorStream.reader().readLines()
        waitFor(1, TimeUnit.HOURS)
        val exitCode = exitValue()
        BashResult(exitCode, stdout, stderr).also {
            if (showOutput) {
                if (exitCode == 0) {
                    println(it.sout())
                } else {
                    println(it.serr())
                }
            }
        }
    }
}

fun String.evalBash(
    timeoutValue: Long = 60,
    timeoutUnit: TimeUnit = TimeUnit.MINUTES,
    processConfig: ProcessBuilder.() -> Unit
): BashResult {
    //same like evalBash but with more config
    return this.runCommand(timeoutValue, timeoutUnit) {
        redirectOutput(ProcessBuilder.Redirect.PIPE)
        redirectInput(ProcessBuilder.Redirect.PIPE)
        redirectError(ProcessBuilder.Redirect.PIPE)
        processConfig()
    }.run {
        val stdout = inputStream.reader().readLines()
        val stderr = errorStream.reader().readLines()
        if (timeoutValue > 0L) {
            waitFor(timeoutValue, timeoutUnit)
        } else {
            waitFor()
        }
        val exitCode = exitValue()
        BashResult(exitCode, stdout, stderr)
    }
}

fun BashResult.throwIfError(): BashResult {
    if (this.exitCode != 0) {
        throw kotlin.RuntimeException("Process exec error ${toString()}")
    }
    return this
}

fun String.runCommand(
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

fun String.runCommandWithGradle(config: ExecSpec.() -> Unit): Result<Unit> {
    val thisProcessOperations = processOperations
    if (thisProcessOperations != null) {
        val execResult = try {
            thisProcessOperations.exec {
                commandLine("/bin/bash", "-c", this@runCommandWithGradle)
                config()
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
        if (execResult.exitValue == 0) {
            return Result.success(Unit)
        } else {
            return Result.failure(IllegalStateException("Command exit with none 0 code: ${execResult.exitValue}"))
        }
    } else {
        throw IllegalStateException("You are not apply this plugin, please apply it before you use [runCommandWithGradle] function")
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
