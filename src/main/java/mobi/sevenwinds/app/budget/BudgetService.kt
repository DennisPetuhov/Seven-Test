package mobi.sevenwinds.app.budget

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mobi.sevenwinds.app.author.AuthorEntity
import mobi.sevenwinds.app.author.AuthorTable
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.select
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
        transaction {
            val query = BudgetTable
                .join(AuthorTable, JoinType.LEFT, additionalConstraint = { BudgetTable.authorId eq AuthorTable.id })
                .select { BudgetTable.year eq param.year }

            if (!param.authorName.isNullOrBlank()) {
                query.andWhere { AuthorTable.fullName.lowerCase() like "%${param.authorName.toLowerCase()}%" }
            }

            val total = query.count()
            val data = BudgetEntity.wrapRows(query).map { it.toResponse() }

            val sumByType = data.groupBy { it.type.name }.mapValues { it.value.sumOf { v -> v.amount } }

            return@transaction BudgetYearStatsResponse(
                total = total,
                totalByType = sumByType,
                items = data
            )
        }
    }
//    suspend fun getYearStats(param: BudgetYearParam): BudgetYearStatsResponse = withContext(Dispatchers.IO) {
//        transaction {
//            val query = BudgetTable
//                .select { BudgetTable.year eq param.year }
//                .limit(param.limit, param.offset)
//
//            val total = query.count()
//            val data = BudgetEntity.wrapRows(query).map { it.toResponse() }
//
//            val sumByType = data.groupBy { it.type.name }.mapValues { it.value.sumOf { v -> v.amount } }
//
//            return@transaction BudgetYearStatsResponse(
//                total = total,
//                totalByType = sumByType,
//                items = data
//            )
//        }
//    }
}