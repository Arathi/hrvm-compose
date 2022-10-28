// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.undsf.hrvm

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.undsf.hrvm.views.MainView
import com.undsf.hrvm.models.Problem
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun gui(problem: Problem = Problem.default) = run {
    application {
        val state = rememberWindowState( size = DpSize(1366.dp, 768.dp) )
        Window(
            title = "人力资源虚拟机 v1.1",
            state = state,
            onCloseRequest = ::exitApplication) {
            MainView(problem, state.size)
        }
    }
}

fun asm(args: Array<String>) = run {

}

fun main(args: Array<String>) = run {
    if (args.isNotEmpty()) {
        val mode = args[0]
        when (mode) {
            "gui" -> {
                try {
                    if (args.size > 1) {
                        val problemId = args[1].toInt()
                        val problem = Problem.Problems[problemId]
                        gui(problem)
                    }
                }
                catch (ex: Exception) {
                    gui()
                }
            }
            "asm" -> asm(args)
        }
    }
    else {
        logger.warn { "没有指定参数，默认打开GUI" }
        gui()
    }
}
