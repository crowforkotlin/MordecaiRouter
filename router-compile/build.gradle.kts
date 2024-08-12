plugins {
  alias(libs.plugins.kotlin.jvm)
}

dependencies {
  implementation(projects.providerApi)
  compileOnly(libs.ksp.api)
  // https://square.github.io/kotlinpoet/
  implementation(libs.kotlinpoet)
}