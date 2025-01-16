package mobi.sevenwinds.app.budget

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mobi.sevenwinds.app.author.AuthorEntity
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.transaction

object BudgetService {
    suspend fun addRecord(body: BudgetRecord): BudgetRecord = withContext(Dispatchers.IO) {
        return@withContext transaction {
            val entity = BudgetEntity.new {
                this.year = body.year
                this.month = body.month
                this.amount = body.amount
                this.type = body.type
                body.authorId?.let { this.author = AuthorEntity.findById(it) }
            }
            entity.toResponse()
        }
    }

    suspend fun getYearStats(param: BudgetYearParam): BudgetYearStatsResponse = withContext(Dispatchers.IO) {
        val records = transaction {
            BudgetEntity.find { BudgetTable.year eq param.year }
                .orderBy(BudgetTable.month to SortOrder.ASC, BudgetTable.amount to SortOrder.DESC)
                .limit(param.limit, param.offset)
                .map { it.toResponse() }
        }

        val total = transaction {
            BudgetEntity.find { BudgetTable.year eq param.year }.count()
        }

        val totalByType = transaction {
            BudgetEntity.find { BudgetTable.year eq param.year }
                .groupBy { it.type.name }
                .mapValues { it.value.sumOf { record -> record.amount } }
        }

        return@withContext BudgetYearStatsResponse(total, totalByType, records)
    }
}