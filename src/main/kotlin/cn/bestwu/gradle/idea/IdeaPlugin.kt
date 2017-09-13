package cn.bestwu.gradle.idea

import groovy.util.Node
import groovy.util.NodeList
import groovy.util.XmlParser
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author Peter Wu
 */
class IdeaPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create("ideaProject", IdeaExtension::class.java)

        project.afterEvaluate {
            val idea = project.extensions.getByType(IdeaExtension::class.java)
            //misc.xml
            val miscXml = project.file(".idea/misc.xml")
            if ((miscXml.exists())) {
                val node = XmlParser().parse(miscXml)
                node.run {
                    (children().find {
                        val component = it as? Node
                        component?.name() == "component" && component.attribute("name") == "ProjectRootManager"
                    } as? Node)?.run {
                        val attributes = attributes()
                        attributes.put("languageLevel", idea.languageLevel)
                        attributes.put("project-jdk-name", idea.jdkName)
                        attributes.put("project-jdk-type", idea.jdkType)
                        val output = children().find { (it as? Node)?.name() == "output" }
                        if (output == null) {
                            appendNode("output", mapOf(Pair("url", idea.outputUrl)))
                        } else {
                            (output as? Node)?.run {
                                attributes().put("url", idea.outputUrl)
                            }
                        }
                    }
                }
                groovy.xml.XmlUtil.serialize(node, miscXml.printWriter())
            }
            //gradle.xml
            if (idea.gradleJvm.isNotBlank()) {
                val gradleXml = project.file(".idea/gradle.xml")
                if (gradleXml.exists()) {
                    val node = XmlParser().parse(gradleXml)
                    node.run {
                        (((((children().find {
                            val component = it as? Node
                            component?.name() == "component" && component.attribute("name") == "GradleSettings"
                        } as? Node)?.children()?.find {
                            val option = it as? Node
                            option?.name() == "option" && option.attribute("name") == "linkedExternalProjectsSettings"
                        } as? Node)?.get("GradleProjectSettings") as? NodeList)?.get(0) as? Node)?.children()?.find {

                            val option = it as? Node
                            option?.name() == "option" && option.attribute("name") == "gradleJvm"
                        } as? Node)?.run {
                            val attributes = attributes()
                            attributes.put("value", idea.gradleJvm)
                        }
                    }
                    groovy.xml.XmlUtil.serialize(node, gradleXml.printWriter())
                }
            }
        }
    }
}