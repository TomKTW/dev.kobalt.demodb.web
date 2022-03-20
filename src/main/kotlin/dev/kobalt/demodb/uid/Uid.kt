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

package dev.kobalt.demodb.uid

import java.nio.ByteBuffer
import java.util.*

@JvmInline
value class Uid(val value: UUID) {

    companion object {
        val none = UUID(0L, 0L).toUid()

        fun random(): Uid {
            return UUID.randomUUID().toString().replace("-", "").toUid()!!
        }
    }

    override fun toString(): String {
        // return value.toString().replace("-", "")
        val bb: ByteBuffer = ByteBuffer.wrap(ByteArray(16))
        bb.putLong(value.mostSignificantBits)
        bb.putLong(value.leastSignificantBits)
        return Base64.getUrlEncoder().encodeToString(bb.array()).replace("=", "")
    }


}

fun String.toUid(): Uid? {
    return runCatching {
        val d = Base64.getUrlDecoder().decode(this.toByteArray())
        val bb = ByteBuffer.wrap(d)
        Uid(UUID(bb.long, bb.long))
    }.getOrNull()
    /*return runCatching {
        val part1 = substring(0, 8)
        val part2 = substring(8, 12)
        val part3 = substring(12, 16)
        val part4 = substring(16, 20)
        val part5 = substring(20, 32)
        UUID.fromString("$part1-$part2-$part3-$part4-$part5").toUid()
    }.getOrNull()*/
}


fun UUID.toUid(): Uid {
    return Uid(this)
}