import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class ComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

        // Пробуем найти Application или Library и конфигурируем
        pluginManager.withPlugin("com.android.application") {
            extensions.configure<ApplicationExtension> {
                enableCompose(this)
            }
        }
        pluginManager.withPlugin("com.android.library") {
            extensions.configure<LibraryExtension> {
                enableCompose(this)
            }
        }

        dependencies {
            add("implementation", platform(libs.findLibrary("androidx-compose-bom").get()))
            add("implementation", libs.findLibrary("androidx-ui").get())
            add("implementation", libs.findLibrary("androidx-ui-graphics").get())
            add("implementation", libs.findLibrary("androidx-ui-tooling-preview").get())
            add("implementation", libs.findLibrary("androidx-foundation").get())
            add("implementation", libs.findLibrary("material3").get())
            add("implementation", libs.findLibrary("material").get())
            add("debugImplementation", libs.findLibrary("androidx-ui-tooling").get())
        }
    }

    private fun Project.enableCompose(common: CommonExtension<*, *, *, *, *, *>) {
        common.apply {
            buildFeatures.compose = true
            composeOptions.kotlinCompilerExtensionVersion =
                libs.findVersion("composeCompiler").get().toString()
        }
    }

    val Project.libs
        get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")
}
