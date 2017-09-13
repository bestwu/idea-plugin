package cn.bestwu.gradle.idea

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author Peter Wu
 */
class IdeaPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create("ideaProject", IdeaExtension::class.java)

        project.afterEvaluate {
            val miscXml = project.file(".idea/misc.xml")
            if ((miscXml.exists())) {
                val idea = project.extensions.getByType(IdeaExtension::class.java)
                val node = groovy.util.XmlParser().parse(miscXml)
                node.run {
                    (children().find { (it as? groovy.util.Node)?.attribute("name") == "ProjectRootManager" } as groovy.util.Node).run {
                        val attributes = attributes()
                        attributes.put("languageLevel", idea.languageLevel)
                        attributes.put("project-jdk-name", idea.jdkName)
                        attributes.put("project-jdk-type", idea.jdkType)
                        val output = children().find { (it as? groovy.util.Node)?.name() == "output" }
                        if (output == null) {
                            appendNode("output", mapOf(Pair("url", idea.outputUrl)))
                        } else {
                            (output as groovy.util.Node).run {
                                attributes().put("url", idea.outputUrl)
                            }
                        }
                    }
                }
                groovy.xml.XmlUtil.serialize(node, miscXml.printWriter())
            }
        }
    }
}