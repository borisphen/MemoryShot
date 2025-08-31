package extensions

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

val Project.libs
        get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun Project.version(key: String): String = libs
        .findVersion(key)
        .get()
        .requiredVersion

internal fun Project.versionInt(key: String) = version(key).toInt()

internal val Project.ANDROID_COMPILE_SDK_VERSION get() = versionInt("compileSdk")
internal val Project.ANDROID_MIN_SDK_VERSION get() = versionInt("minSdk")
internal val Project.ANDROID_TARGET_SDK_VERSION get() = versionInt("targetSdk")

