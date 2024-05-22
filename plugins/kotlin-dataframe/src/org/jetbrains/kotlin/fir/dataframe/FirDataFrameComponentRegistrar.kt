/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.dataframe

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CliOptionProcessingException
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.caches.FirCache
import org.jetbrains.kotlin.fir.caches.firCachesFactory
import org.jetbrains.kotlin.fir.dataframe.extensions.CandidateInterceptor
import org.jetbrains.kotlin.fir.dataframe.extensions.DataRowSchemaSupertype
import org.jetbrains.kotlin.fir.dataframe.extensions.ExpressionAnalysisAdditionalChecker
import org.jetbrains.kotlin.fir.dataframe.extensions.ExtensionsGenerator
import org.jetbrains.kotlin.fir.dataframe.extensions.IrBodyFiller
import org.jetbrains.kotlin.fir.dataframe.extensions.ReturnTypeBasedReceiverInjector
import org.jetbrains.kotlin.fir.dataframe.extensions.TokenGenerator
import org.jetbrains.kotlin.fir.extensions.FirExtensionApiInternals
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.KotlinTypeFacade
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.io.readJson
import org.jetbrains.kotlinx.dataframe.plugin.PluginDataFrameSchema
import org.jetbrains.kotlinx.dataframe.plugin.toPluginDataFrameSchema

val PATH: CompilerConfigurationKey<String> = CompilerConfigurationKey.create("annotation qualified name")

// listOf("-P", "plugin:org.jetbrains.kotlinx.dataframe:path=/home/nikita/IdeaProjects/run-df")
@OptIn(ExperimentalCompilerApi::class)
class DataFrameCommandLineProcessor : CommandLineProcessor {
    companion object {
        val RESOLUTION_DIRECTORY = CliOption(
            "path", "<path>", "", required = false, allowMultipleOccurrences = false
        )
    }
    override val pluginId: String = "org.jetbrains.kotlinx.dataframe"

    override val pluginOptions: Collection<AbstractCliOption> = listOf(RESOLUTION_DIRECTORY)

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) {
        return when (option) {
            RESOLUTION_DIRECTORY -> configuration.put(PATH, value)
            else -> throw CliOptionProcessingException("Unknown option: ${option.optionName}")
        }
    }
}

class FirDataFrameExtensionRegistrar(
    private val path: String?
) : FirExtensionRegistrar() {
    @OptIn(FirExtensionApiInternals::class)
    override fun ExtensionRegistrarContext.configurePlugin() {
        // if input data schema for refinement is also generated schema, maybe it'll be possible to save names to a set
        +::ExtensionsGenerator
        +::ReturnTypeBasedReceiverInjector
        +{ it: FirSession ->
            CandidateInterceptor(path, it, jsonCache(it))
        }
        +::TokenGenerator
        +::DataRowSchemaSupertype
        +{ it: FirSession ->
            ExpressionAnalysisAdditionalChecker(it, jsonCache(it))
        }
    }

    private fun jsonCache(it: FirSession): FirCache<String, PluginDataFrameSchema, KotlinTypeFacade> =
        it.firCachesFactory.createCache { path: String, context ->
            with(context) {
                DataFrame.readJson(path).schema().toPluginDataFrameSchema()
            }
        }
}

@OptIn(ExperimentalCompilerApi::class)
class FirDataFrameComponentRegistrar : CompilerPluginRegistrar() {

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        FirExtensionRegistrarAdapter.registerExtension(FirDataFrameExtensionRegistrar(configuration.get(PATH)))
        IrGenerationExtension.registerExtension(IrBodyFiller())
    }

    override val supportsK2: Boolean = true
}
