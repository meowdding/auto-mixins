package me.owdding

import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

abstract class AutoMixinExtension {
   @get:Input abstract val projectName: Property<String>
   @get:Input abstract val plugin: Property<String>
   @get:Input abstract val mixinPackage: Property<String>
   @get:Input abstract val required: Property<Boolean>
   @get:Input abstract val minVersion: Property<String>
   @get:Input abstract val compatibilityLevel: Property<String>
   @get:Input abstract val nameMapping: MapProperty<String, String>
}