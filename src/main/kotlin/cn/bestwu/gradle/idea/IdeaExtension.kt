package cn.bestwu.gradle.idea

/**
 *
 * @author Peter Wu
 * @since
 */
open class IdeaExtension(
        var jdkName: String = "1.7",
        var languageLevel: String = "JDK_1_7",
        var jdkType: String = "JavaSDK",
        var gradleJvm: String = "",
        var outputUrl: String = "file://\$PROJECT_DIR\$/classes"
)