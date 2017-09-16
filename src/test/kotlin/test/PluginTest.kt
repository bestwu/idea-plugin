package test

import cn.bestwu.gradle.idea.IdeaExtension
import cn.bestwu.gradle.idea.IdeaPlugin
import cn.bestwu.gradle.idea.find
import groovy.util.XmlParser
import org.gradle.api.DefaultTask
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test
import java.io.File
import kotlin.test.assertTrue

/**
 *
 * @author Peter Wu
 * @since
 */
class PluginTest {

    @Test
    fun misc() {
        val project = ProjectBuilder.builder().build()

        val miscXml = project.file(".idea/misc.xml")
        File(PluginTest::class.java.getResource("/.idea/misc.xml").file).copyTo(miscXml, true)
        project.pluginManager.apply("cn.bestwu.idea")
        assertTrue(project.plugins.getAt("cn.bestwu.idea") is IdeaPlugin)

        (project.tasks.findByPath("ideSettings") as DefaultTask).execute()

        val idea = project.extensions.getByType(IdeaExtension::class.java)
        val node = XmlParser().parse(miscXml)
        val projectRootManagaer = node.find("component") {
            attribute("name") == "ProjectRootManager"
        }
        val projectRootManagerAttrs = projectRootManagaer?.attributes()!!
        Assert.assertEquals(idea.languageLevel, projectRootManagerAttrs["languageLevel"])
        Assert.assertEquals(idea.jdkName, projectRootManagerAttrs["project-jdk-name"])
        Assert.assertEquals(idea.jdkType, projectRootManagerAttrs["project-jdk-type"])
        Assert.assertEquals(idea.outputUrl, (projectRootManagaer.find("output")?.attribute("url")))
    }

    @Test
    fun gradle() {
        val project = ProjectBuilder.builder().build()

        val gradleXml = project.file(".idea/gradle.xml")
        File(PluginTest::class.java.getResource("/.idea/gradle.xml").file).copyTo(gradleXml, true)
        project.pluginManager.apply("cn.bestwu.idea")
        assertTrue(project.plugins.getAt("cn.bestwu.idea") is IdeaPlugin)
        val idea = project.extensions.getByType(IdeaExtension::class.java)
        idea.gradleJvm = "1.7"

        (project.tasks.findByPath("ideSettings") as DefaultTask).execute()


        val node = XmlParser().parse(gradleXml)
        val gradleOption = node.find("component") {
            attribute("name") == "GradleSettings"
        }?.find("option") {
            attribute("name") == "linkedExternalProjectsSettings"
        }?.find("GradleProjectSettings")
                ?.find("option") {
                    attribute("name") == "gradleJvm"
                }

        Assert.assertEquals(idea.gradleJvm, gradleOption?.attribute("value"))
    }
}