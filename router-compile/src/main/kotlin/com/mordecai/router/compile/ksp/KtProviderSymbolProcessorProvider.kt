package com.mordecai.router.compile.ksp

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

/**
 * .
 *
 * @author 985892345
 * 2023/12/4 14:18
 */
class KtProviderSymbolProcessorProvider : SymbolProcessorProvider {
  override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
    println("options : ${environment.options.size}")
    environment.options.forEach {
      println("key : ${it.key} \t value : ${it.value}")
    }
    return KtProviderSymbolProcess(
      environment.logger,
      environment.codeGenerator,
      Options(environment.options),
    )
  }
}