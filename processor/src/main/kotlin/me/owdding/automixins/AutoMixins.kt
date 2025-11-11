package me.owdding.automixins

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.gson.JsonArray
import com.google.gson.JsonObject

internal class Processor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val context: AutoMixinContext,
) : SymbolProcessor {

    private var ran = false

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (ran) return emptyList()
        ran = true

        val mixinPackage = context.mixinPackage + "."
        val mixinClasses = resolver.getSymbolsWithAnnotation("org.spongepowered.asm.mixin.Mixin")
        val names = mixinClasses.filterIsInstance<KSClassDeclaration>().map { it to it.qualifiedName!!.asString() }
            .filter { (_, name) -> name.startsWith(mixinPackage) }
            .map { (key, name) -> key to name.removePrefix(mixinPackage) }.toList()
        val keys = names.map { (key) -> key }
        val values = names.map { (_, values) -> values }

        val middleSection = if (context.sourceSet.isEmpty()) "" else ("." + context.sourceSet)
        val fileOutputStream = codeGenerator.createNewFile(
            Dependencies(false, *keys.mapNotNull { it.containingFile }.toTypedArray()),
            "",
            "${context.projectName}$middleSection.mixins",
            "json"
        )

        val data = JsonObject()
        data.addProperty("required", context.required)
        data.addProperty("minVersion", context.minVersion)
        data.addProperty("package", context.mixinPackage)
        data.addProperty("compatibilityLevel", context.compatibilityVersion)
        if (context.plugin != null) {
            data.addProperty("plugin", context.plugin)
        }
        data.add("injectors", JsonObject().apply {
            addProperty("defaultRequire", 1)
        })
        data.add("overwrites", JsonObject().apply {
            addProperty("requireAnnotations", true)
        })
        data.add("mixins", JsonArray().apply {
            values.forEach { add(it) }
        })

        fileOutputStream.write(data.toString().toByteArray())

        return keys
    }
}

internal class AutoMixinProvider : SymbolProcessorProvider {
    override fun create(
        environment: SymbolProcessorEnvironment,
    ): SymbolProcessor {
        return Processor(
            environment.codeGenerator,
            environment.logger,
            AutoMixinContext.create(environment.options, environment.logger)
        )
    }
}

internal data class AutoMixinContext(
    val projectName: String,
    val mixinPackage: String,
    val required: Boolean,
    val minVersion: String,
    val compatibilityVersion: String,
    val sourceSet: String,
    val plugin: String?,
) {
    companion object {
        fun create(
            options: Map<String, String>,
            logger: KSPLogger,
        ): AutoMixinContext {
            return AutoMixinContext(
                this.require("project_name", options, logger).let {
                    it.replaceFirstChar { first -> first.uppercase() }
                },
                this.require("mixin_package", options, logger),
                this.require("required", options, logger).toBoolean(),
                this.require("min_version", options, logger),
                this.require("compatibility_level", options, logger),
                this.require("sourceset", options, logger),
                options["meowdding.mixins.plugin"],
            )
        }

        private fun require(option: String, map: Map<String, String>, logger: KSPLogger): String {
            return requireNotNull(map["meowdding.mixins.$option"] ?: map["meowdding.$option"], logger)
        }

        private fun <T> requireNotNull(value: T?, logger: KSPLogger): T {
            if (value == null) {
                throw IllegalStateException("Module processor wasn't configured correctly, please ensure you use the gradle plugin!")
            }
            return value
        }
    }
}