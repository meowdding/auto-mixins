plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "auto-mixins"

includeBuild("plugin")
include("processor")
include("test-project")