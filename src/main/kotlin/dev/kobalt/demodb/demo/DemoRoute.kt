/*
 * dev.kobalt.demodb
 * Copyright (C) 2022 Tom.K
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.kobalt.demodb.demo

import dev.kobalt.demodb.extension.pageArticle
import dev.kobalt.demodb.extension.pageLink
import dev.kobalt.demodb.markdown.fromMarkdown
import dev.kobalt.demodb.uid.toUid
import dev.kobalt.demodb.extension.respondHtmlContent
import dev.kobalt.demodb.credit.credit
import dev.kobalt.demodb.demo.DemoRepository
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import kotlinx.css.CssBuilder
import kotlinx.html.*

@ContextDsl
fun Route.demoRoute() = route(DemoRepository.pageRoute) {
    get {
        call.respondHtmlContent(
            title = DemoRepository.pageTitle,
            description = DemoRepository.pageSubtitle
        ) {
            pageArticle(
                DemoRepository.pageTitle,
                DemoRepository.pageSubtitle
            ) {
                DemoRepository.selectList().takeIf { it.isNotEmpty() }?.forEach { entity ->
                    pageLink(entity.title, null, "${entity.uid}/")
                } ?: run {
                    p { text(DemoRepository.pageEmpty) }
                }
            }
        }
    }
    route("{uid}/") {
        get {
            val emulator = call.request.queryParameters["emulator"].orEmpty().toUid()
            call.parameters["uid"]?.toUid()?.let { DemoRepository.selectItem(it) }?.let { entity ->
                call.respondHtmlContent(
                    title = "${entity.title} - $emulator",
                    description = null,
                    head = {
                        when (emulator) {
                            DemoRepository.pcx86uid -> {
                                link { href = "../../demo/pcx86/components.css"; rel = "stylesheet"; media = "all" }
                                style {
                                    unsafe {
                                        +CssBuilder().apply {
                                            rule(".pcjs-key") {
                                                put("color", "#000000")
                                            }
                                        }.toString().replace("\n", "")
                                    }
                                }
                                script { attributes["type"] = "text/javascript"; src = "../../demo/pcx86/engine.js" }
                            }
                            DemoRepository.v86uid -> {
                                link { href = "../../demo/v86/components.css"; rel = "stylesheet"; media = "all" }
                                style {
                                    unsafe {
                                        +CssBuilder().apply {
                                            DemoRepository.fontBase64.let {
                                                fontFace {
                                                    put("font-family", "\"IBM VGA 9x16\"")
                                                    put("src", "url('data:font/x-font-ttf;base64,$it')")
                                                }
                                                rule("div#screen *") {
                                                    put("font-family", "\"IBM VGA 9x16\"")
                                                }
                                                rule("div#demoContainer input") {
                                                    put("width", "inherit")
                                                }
                                            }
                                        }.toString().replace("\n", "")
                                    }
                                }
                                script { attributes["type"] = "text/javascript"; src = "../../demo/v86/engine.js" }
                            }
                        }
                    }
                ) {
                    pageArticle(
                        entity.title,
                        null
                    ) {
                        if (emulator != null) {
                            div {
                                id = "startContainer"
                                unsafe { +DemoRepository.infoText.fromMarkdown() }
                                input(classes = "button") {
                                    id = "start"
                                    this.type = InputType.button
                                    value = "Start"
                                    val imageSuffix = when (emulator) {
                                        DemoRepository.pcx86uid -> "pcx86"
                                        DemoRepository.v86uid -> "v86"
                                        else -> ""
                                    }
                                    onClick = when (emulator) {
                                        DemoRepository.pcx86uid -> DemoRepository.pcx86RunScript
                                        DemoRepository.v86uid -> DemoRepository.v86RunScript
                                        else -> ""
                                    }.replace("\$entryImage", "../../${entity.entryImage}.img")
                                        .replace("\$systemImage", "../../${entity.systemImage}${imageSuffix}.img")
                                        .replace("\r\n", "")
                                }
                            }
                            when (emulator) {
                                DemoRepository.pcx86uid -> {
                                    div {
                                        id = "demoContainer"
                                        style = "display: none;"
                                        div { attributes["id"] = "screen_container" }
                                    }
                                }
                                DemoRepository.v86uid -> {
                                    div {
                                        id = "demoContainer"
                                        style = "display: none;"
                                        div {
                                            id = "container"
                                            div {
                                                id = "runtime_options_top"
                                                style = "display: block; float: right; margin-bottom: 16px;"
                                                label {
                                                    style =
                                                        "font-family: 'DejaVu Sans', Arial, Helvetica, sans-serif; color: #ffffff;"
                                                    text("Scale: ")
                                                    input(classes = "button") {
                                                        id = "scale"
                                                        min = "0.25"
                                                        step = "0.25"
                                                        style = "width: 50px"
                                                        type = InputType.number
                                                        value = "1.0"
                                                    }
                                                }
                                                text(" ")
                                                label {
                                                    style =
                                                        "font-family: 'DejaVu Sans', Arial, Helvetica, sans-serif; color: #ffffff;"
                                                    text("Stretch: ")
                                                    input(classes = "button") {
                                                        id = "stretch"
                                                        type = InputType.checkBox
                                                        value = "false"
                                                    }
                                                }
                                                text(" ")
                                                label {
                                                    input(classes = "button") {
                                                        id = "fullscreen"
                                                        type = InputType.button
                                                        value = "Full Screen"
                                                    }
                                                }
                                            }
                                            div {
                                                style = "clear: both;"
                                                p {
                                                    id = "loading"
                                                    style =
                                                        "padding: 4px; background: black; white-space: pre; font: 14px monospace; line-height: 14px"
                                                }
                                                div {
                                                    id = "screen_container"
                                                    div {
                                                        id = "screen"
                                                        style =
                                                            "white-space: pre; font-size: 16px; font-family: \"IBM VGA 9x16\", monospace; font-weight: normal; line-height: 16px"
                                                    }
                                                    canvas {
                                                        id = "vga"
                                                        style = "display: none"
                                                    }
                                                }
                                                div {
                                                    style = "clear: both;"
                                                }
                                                div {
                                                    id = "runtime_options_bottom_right"
                                                    style = "display: block; float: right; margin-top: 16px;"
                                                    label {
                                                        input(classes = "button") {
                                                            id = "run"
                                                            type = InputType.button
                                                            value = "Run"
                                                        }
                                                    }
                                                    text(" ")
                                                    label {
                                                        input(classes = "button") {
                                                            id = "reset"
                                                            type = InputType.button
                                                            value = "Reset"
                                                        }
                                                    }
                                                }
                                                div {
                                                    id = "runtime_options_bottom_left"
                                                    style = "display: block; float: left; margin-top: 16px;"
                                                    label {
                                                        input(classes = "button") {
                                                            id = "ctrlaltdel"
                                                            type = InputType.button
                                                            value = "Ctrl-Alt-Del"
                                                        }
                                                    }
                                                }
                                                div {
                                                    style = "clear: both;"
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            when (emulator) {
                                DemoRepository.pcx86uid -> credit(DemoRepository.pcx86Credit)
                                DemoRepository.v86uid -> credit(DemoRepository.v86Credit)
                            }
                        } else {
                            h2 { text("Select emulator")}
                            a("?emulator=${DemoRepository.pcx86uid}") { p { text("PCx86")}}
                            a("?emulator=${DemoRepository.v86uid}") { p { text("v86")}}
                        }
                        /*script {
                            unsafe {
                                +(when (entity.type) {
                                    "PCx86" -> DemoRepository.pcx86RunScript
                                    "v86" -> DemoRepository.v86RunScript
                                    else -> throw Exception()
                                }.replace("\$entryImage", entity.entryImage)
                                    .replace("\$systemImage", entity.systemImage)
                                    .replace("\r\n", ""))
                            }
                        }*/
                    }
                }
            } ?: call.respond(HttpStatusCode.NotFound)
        }
    }/*
    get {
        call.respondHtml {
            body {
                DemoRepository.selectList().takeIf { it.isNotEmpty() }?.forEach {
                    a {
                        href = "/item/${it.uid}"
                        p {
                            +it.title
                        }
                    }

                } ?: run {
                    article {
                        p { text("There are no entries.") }
                    }
                }
            }
        }
    }
    get("item/{uid}") {
        call.parameters["uid"]?.toUid()?.let { DemoRepository.selectItem(it) }?.let { entity ->
            call.respondHtml {
                head {
                    demoHead(entity.type)
                }
                body {
                    demoInfoContainer(entity.type, entity.entryImage, entity.systemImage)
                    demoContainer(entity.type)
                    when (entity.type) {
                        "PCx86" -> credit(DemoRepository.pcx86Credit)
                        "v86" -> credit(DemoRepository.v86Credit)
                    }
                    script {
                        unsafe {
                            +(when (entity.type) {
                                "PCx86" -> DemoRepository.pcx86RunScript
                                "v86" -> DemoRepository.v86RunScript
                                else -> throw Exception()
                            }.replace("\$entryImage", entity.entryImage)
                                .replace("\$systemImage", entity.systemImage)
                                .replace("\r\n", ""))
                        }
                    }
                }
            }
        }
    }*/
}