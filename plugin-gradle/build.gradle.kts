plugins {
  alias(libs.plugins.kotlinJvm)
  alias(libs.plugins.buildconfig)
  `java-gradle-plugin`
}

dependencies {
  compileOnly(kotlin("gradle-plugin-api"))
  compileOnly(libs.ksp.gradlePlugin)
}

gradlePlugin {
  plugins {
    create("version") {
    version = "1.0.0-alpha01-SNAPSHOT"
      // Plugin id.
      id = "com.mordecai.router"
      // Plugin implementation.
      implementationClass = "com.mordecai.router.plugin.gradle.KtProviderGradlePlugin"
    }
  }
}


buildConfig {
  packageName("com.mordecai.router.plugin.gradle")
  buildConfigField("String", "VERSION", "\"${gradle.parent?.rootProject!!.properties["VERSION"].toString()}\"")
  buildConfigField("String", "GROUP", "\"null\"")
}



