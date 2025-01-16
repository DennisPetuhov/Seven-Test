package mobi.sevenwinds.app.author

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object AuthorService {
    suspend fun addAuthor(body: AuthorRecord): AuthorRecord = withContext(Dispatchers.IO) {
        return@withContext transaction {
            val newAuthor = AuthorEntity.new {
                fullName = body.fullName
                creationDate = DateTime.now()
            }
            newAuthor.toResponse()
        }
    }
}