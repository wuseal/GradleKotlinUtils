/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package wu.seal.tools.gradle

import evalBash
import fromJson
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.internal.ProcessOperations
import org.gradle.api.plugins.ExtensionAware
import org.gradle.internal.service.ServiceRegistry
import org.gradle.kotlin.dsl.support.serviceOf
import runCommand
import toJson

/**
 * Add Utils plugin, and use directly in setting.gradle.kts
 * if you use in other script apply from settings.gradle
 * Can declare them like this:
 *
val fromJson: String.(Class<*>) -> Any by extensions
val toJson: Any.() -> String by extensions
val runCommand: String.() -> Result<Unit> by extensions
val evalBash: String.() -> Result<String> by extensions
 */

class KotlinUtilsPlugin : Plugin<ExtensionAware> {
    //serviceOf<ServiceRegistry>().get(org.gradle.api.internal.ProcessOperations::class.java)
    companion object {
        var processOperations: ProcessOperations? = null
    }

    override fun apply(extensionAware: ExtensionAware) {
        if (extensionAware is Settings) {
            processOperations = extensionAware.serviceOf<ServiceRegistry>().get(ProcessOperations::class.java)
        } else if (extensionAware is Project) {
            processOperations = extensionAware.serviceOf<ServiceRegistry>().get(ProcessOperations::class.java)
        }

        extensionAware.extensions.apply {
            //add Json Convert Util
            val fromJsonFunType: String.(Class<Any>) -> Any = String::fromJson
            val toJsonFunType: Any.() -> String = Any::toJson
            add("fromJson", fromJsonFunType)
            add("toJson", toJsonFunType)

            //add run Bash Util
            val runCommandFunType: String.() -> Result<Unit> = String::runCommand
            add("runCommand", runCommandFunType)

            //add eval bash util
            val evalBashFunType: String.() -> Result<String> = String::evalBash
            add("evalBash", evalBashFunType)
        }
    }
}
