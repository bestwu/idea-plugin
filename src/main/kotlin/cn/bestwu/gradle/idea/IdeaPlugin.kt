package cn.bestwu.gradle.idea

import groovy.util.Node
import groovy.util.XmlParser
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author Peter Wu
 */
class IdeaPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create("ideSettings", IdeaExtension::class.java)
        project.tasks.create("ideSettings").run {
            doLast {
                val idea = project.extensions.getByType(IdeaExtension::class.java)
                //misc.xml
                val miscXml = project.file(".idea/misc.xml")
                if ((miscXml.exists())) {
                    val node = XmlParser().parse(miscXml)
                    node.find("component") {
                        attribute("name") == "ProjectRootManager"
                    }?.run {
                        val attributes = attributes()
                        attributes.put("languageLevel", idea.languageLevel)
                        attributes.put("project-jdk-name", idea.jdkName)
                        attributes.put("project-jdk-type", idea.jdkType)
                        val output = find("output")
                        if (output == null) {
                            appendNode("output", mapOf("url" to idea.outputUrl))
                        } else {
                            (output as? Node)?.run {
                                attributes().put("url", idea.outputUrl)
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
                        node.find("component") {
                            attribute("name") == "GradleSettings"
                        }?.find("option") {
                            attribute("name") == "linkedExternalProjectsSettings"
                        }?.find("GradleProjectSettings")
                                ?.find("option") {
                                    attribute("name") == "gradleJvm"
                                }?.run {
                            attributes().put("value", idea.gradleJvm)
                        }

                        groovy.xml.XmlUtil.serialize(node, gradleXml.printWriter())
                    }
                }
            }
        }
    }

}

internal fun Node.find(name: String, filter: Node.() -> Boolean = { true }): Node? {
    return children().find {
        val node = it as? Node
        node?.name() == name && node.filter()
    } as? Node
}