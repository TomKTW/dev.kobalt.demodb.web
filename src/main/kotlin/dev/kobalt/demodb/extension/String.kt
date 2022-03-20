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

package dev.kobalt.demodb.extension

import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

fun String.decodeBase64(): ByteArray = Base64.getDecoder().decode(this)


fun String.toFile(): File = File(this)

fun String.toLocalDate(format: String): LocalDate = LocalDate.parse(this, DateTimeFormatter.ofPattern(format))

fun String.parentOfPath(count: Int): String {
    var path = this
    repeat(count) { path = path.parentOfPath() }
    return path
}

fun String.parentOfPath(): String = when {
    substringBeforeLast("/", "").isEmpty() -> "/"
    else -> substringBeforeLast("/")
}

fun String.removeSlashSuffix(): String = removeSuffix("/")
fun String.singleSlashSuffix(): String = removeSlashSuffix() + "/"