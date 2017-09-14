package test

import cn.bestwu.gradle.idea.IdeaExtension
import cn.bestwu.gradle.idea.IdeaPlugin
import groovy.util.Node
import groovy.util.NodeList
import groovy.util.XmlParser
import org.gradle.api.internal.project.DefaultProject
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
        (project as DefaultProject).evaluate()

        val idea = project.extensions.getByType(IdeaExtension::class.java)
        val node = XmlParser().parse(miscXml)
        val projectRootManagaer = node.children().find {
            val component = it as? Node
            component?.name() == "component" && component.attribute("name") == "ProjectRootManager"
        } as? Node
        val projectRootManagerAttrs = projectRootManagaer?.attributes()!!
        Assert.assertEquals(idea.languageLevel, projectRootManagerAttrs["languageLevel"])
        Assert.assertEquals(idea.jdkName, projectRootManagerAttrs["project-jdk-name"])
        Assert.assertEquals(idea.jdkType, projectRootManagerAttrs["project-jdk-type"])
        Assert.assertEquals(idea.outputUrl, (projectRootManagaer.children().find { (it as? Node)?.name() == "output" } as Node).attribute("url"))
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

        (project as DefaultProject).evaluate()

        val node = XmlParser().parse(gradleXml)
        val gradleOption = ((((node.children().find {
            val component = it as? Node
            component?.name() == "component" && component.attribute("name") == "GradleSettings"
        } as? Node)?.children()?.find {
            val option = it as? Node
            option?.name() == "option" && option.attribute("name") == "linkedExternalProjectsSettings"
        } as? Node)?.get("GradleProjectSettings") as? NodeList)?.get(0) as? Node)?.children()?.find {

            val option = it as? Node
            option?.name() == "option" && option.attribute("name") == "gradleJvm"
        } as? Node

        Assert.assertEquals(idea.gradleJvm, gradleOption?.attribute("value"))
    }
}