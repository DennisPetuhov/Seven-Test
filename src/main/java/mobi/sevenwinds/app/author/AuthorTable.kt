package mobi.sevenwinds.app.author

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.CurrentDateTime

object AuthorTable : IntIdTable("author") {
    val fullName = varchar("full_name", 255)
    val creationDate = datetime("creation_date").defaultExpression(CurrentDateTime())
}

class AuthorEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AuthorEntity>(AuthorTable)

    var fullName by AuthorTable.fullName
    var creationDate by AuthorTable.creationDate
    fun toResponse(): AuthorRecord {
        return AuthorRecord(fullName = this.fullName)
    }
}