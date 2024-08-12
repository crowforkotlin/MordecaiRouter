package com.g985892345.provider.plugin.gradle

import com.g985892345.provider.plugin.gradle.extensions.KtRouterExtensions
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.plugins.PluginContainer
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetContainer

class KtRouterGradlePlugin : Plugin<Project> {
  
  override fun apply(target: Project) {
    target.extensions.create("ktProvider", KtRouterExtensions::class.java, target)
  }
  
  private fun config(project: Project) {
    val ktProvider = project.extensions.getByType(KtRouterExtensions::class.java)
    // configDependencies(project, ktProvider)
    transmitKsp(project, ktProvider)
  }
  
  private fun configDependencies(project: Project, ktProvider: KtRouterExtensions) {
    project.plugins.withId("org.jetbrains.kotlin.multiplatform") {
      project.extensions
        .getByType(KotlinSourceSetContainer::class.java)
        .sourceSets
        .named("commonMain") {
          it.dependencies {
            implementation(ktProvider.api)
          }
        }
    }
    project.plugins.withAnyId(
      "org.jetbrains.kotlin.android",
      "org.jetbrains.kotlin.jvm",
    ) {
      project.dependencies.add(
        "implementation",
        ktProvider.api.replace("provider-api", "provider-api-jvm")
      )
    }
    project.plugins.withAnyId(
      "org.jetbrains.kotlin.js",
      "kotlin2js",
    ) {
      project.dependencies.add(
        "implementation",
        ktProvider.api.replace("provider-api", "provider-api-js")
      )
    }
  }
  
  private fun transmitKsp(project: Project, ktProvider: KtRouterExtensions) {
    val ktProviderRouterPackageName = KtRouterExtensions.getPackageName(project)
    val ktProviderRouterClassName = "${KtRouterExtensions.getClassNameSuffix(project)}KtProviderRouter"
    val selfInitializerClass = KtRouterExtensions.getInitializerClass(project)
    val initializerPackageName = selfInitializerClass.substringBeforeLast(".")
    val initializerClassName = selfInitializerClass.substringAfterLast(".")
    project.extensions.configure(KspExtension::class.java) {
      it.arg("ktProviderRouterPackageName", ktProviderRouterPackageName)
      it.arg("ktProviderRouterClassName", ktProviderRouterClassName)
      it.arg("ktProviderInitializerPackageName", initializerPackageName)
      it.arg("ktProviderInitializerClassName", initializerClassName)
    }
    project.afterEvaluate {
      project.afterEvaluate {
        project.extensions.configure(KspExtension::class.java) {
          it.arg(
            "ktProviderDependModuleProjects",
            getDependModulePaths(project, ktProvider).joinToString("&") { dependProject ->
              KtRouterExtensions.getInitializerClass(dependProject)
            }
          )
        }
      }
    }
  }
  
  private fun PluginContainer.withAnyId(vararg ids: String, action: Action<in Plugin<*>>) {
    ids.forEach { withId(it, action) }
  }
  
  // Retrieve the paths of all dependent modules.
  private fun getDependModulePaths(project: Project, ktProvider: KtRouterExtensions): List<Project> {
    val dependProjects = mutableListOf<Project>()
    ktProvider.configurations.mapNotNull {
      project.configurations.findByName(it)
    }.forEach { config ->
      config.dependencies.forEach { dependency ->
        if (dependency is ProjectDependency) {
          val dependProject = dependency.dependencyProject
          dependProjects.add(dependProject)
        }
      }
    }
    return dependProjects
  }
}