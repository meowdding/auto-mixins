package me.owdding

import com.google.devtools.ksp.gradle.KspAATask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.extraProperties

class AutoMixinsPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create("autoMixins", AutoMixinExtension::class.java)


        extension.nameMapping.set(mutableMapOf())
        extension.projectName.convention(target.name)
        extension.required.convention(true)
        extension.minVersion.convention("0.8")
        extension.compatibilityLevel.convention("JAVA_21")

        target.afterEvaluate {
            if (!target.extraProperties.has("auto-mixins-skip-dependency-registration"))
                target.dependencies.add("ksp", "me.owdding.auto-mixins:processor:$PLUGIN_VERSION")
            target.tasks.withType(KspAATask::class.java).configureEach {
                it.kspConfig.processorOptions.put("meowdding.mixins.project_name", extension.projectName.get())
                it.kspConfig.processorOptions.put("meowdding.mixins.mixin_package", extension.mixinPackage.get())
                it.kspConfig.processorOptions.put(
                    "meowdding.mixins.required",
                    extension.required.map { it.toString() }.get()
                )
                it.kspConfig.processorOptions.put("meowdding.mixins.min_version", extension.minVersion.get())
                extension.plugin.orNull?.let { value ->
                    it.kspConfig.processorOptions.put(
                        "meowdding.mixins.plugin",
                        value
                    )
                }
                it.kspConfig.processorOptions.put(
                    "meowdding.mixins.compatibility_level",
                    extension.compatibilityLevel.get()
                )
                val name = it.name.lowercase().removePrefix("ksp").removeSuffix("kotlin")
                it.kspConfig.processorOptions.put(
                    "meowdding.mixins.sourceset",
                    extension.nameMapping.getting(name).orElse(name)
                )
            }
        }
    }
}