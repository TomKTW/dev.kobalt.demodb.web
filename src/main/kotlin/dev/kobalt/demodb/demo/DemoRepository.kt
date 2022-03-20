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

import dev.kobalt.demodb.database.DatabaseRepository
import dev.kobalt.demodb.extension.encodeToBase64String
import dev.kobalt.demodb.extension.transaction
import dev.kobalt.demodb.uid.Uid
import dev.kobalt.demodb.uid.toUid
import dev.kobalt.demodb.credit.CreditEntity
import dev.kobalt.demodb.demo.DemoEntity
import dev.kobalt.demodb.demo.DemoTable
import dev.kobalt.demodb.demo.toDemoEntity
import dev.kobalt.demodb.resource.ResourceRepository
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import java.util.UUID

object DemoRepository {

    val pageTitle = "Entries"
    val pageSubtitle = "List of entries."
    val pageRoute = "entry/"
    val pageEmpty = "There are no entries."

    val database get() = DatabaseRepository.demo

    val pcx86uid = UUID.fromString("a6ff4d61-6aba-429d-8c66-6c7194dade26").toUid()
    val v86uid = UUID.fromString("5fe0eea0-b033-4619-8f88-2e8db2f1ba80").toUid()

    val pcx86Credit = CreditEntity(
        product = "PCx86",
        productLink = "https://www.pcjs.org",
        version = "2.00",
        year = "2012",
        author = "Jeff Parsons",
        authorLink = "https://jeffpar.com",
        license = "MIT",
        licenseLink = "https://www.pcjs.org/LICENSE.txt"
    )

    val v86Credit = CreditEntity(
        product = "v86",
        productLink = "https://copy.sh/v86/",
        version = "2018.07.15.14.07.00",
        year = "2017",
        author = "Fabian Hemmer",
        authorLink = "https://copy.sh/",
        license = "BSD-2-Clause",
        licenseLink = "https://github.com/copy/v86/blob/master/LICENSE"
    )

    fun selectList(): List<DemoEntity> = database.transaction {
        DemoTable.select { DemoTable.visible eq true }.orderBy(DemoTable.title, SortOrder.ASC).map { it.toDemoEntity() }
    }

    fun selectItem(uid: Uid): DemoEntity? = database.transaction {
        DemoTable.select { (DemoTable.uid eq uid) and (DemoTable.visible eq true) }
            .singleOrNull()?.toDemoEntity()
    }

    val pcx86RunScript by lazy { ResourceRepository.getText("pcx86/run.js").orEmpty() }
    val v86RunScript by lazy { ResourceRepository.getText("v86/run.js").orEmpty() }
    val infoText by lazy { ResourceRepository.getText("info.md").orEmpty() }
    val fontBase64 by lazy { ResourceRepository.getBytes("font/PxPlus_IBM_VGA_9x16.ttf")?.encodeToBase64String() }

}

