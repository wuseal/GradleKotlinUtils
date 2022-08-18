package wu.seal.tools.gradle

import java.lang.RuntimeException


fun Process.exceptionOrNull(): Throwable? =
    if (exitValue() != 0) RuntimeException("Program exit with code ${exitValue()}") else null


fun Process.onFailure(action: Process.() -> Unit): Process {
    if ( exitValue()!= 0) action()
    return this
}

fun Process.onSuccess(action: Process.() -> Unit): Process {
    if (exitValue() == 0) action()
    return this
}