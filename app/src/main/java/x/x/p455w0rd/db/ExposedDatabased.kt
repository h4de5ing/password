package x.x.p455w0rd.db

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import x.x.p455w0rd.app.App
import java.sql.Connection

fun connectToDatabase(): Database {
    val dbPath = App.application?.obbDir?.absolutePath
    val db = Database.connect(
        "jdbc:h2:${dbPath}/h2database",
        driver = "org.h2.Driver",
        user = "root",
        password = "password"
    )
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    return db
}

fun upgradeDatabase() = loggedTransaction {
    SchemaUtils.createMissingTablesAndColumns(Item)
}

fun <T> loggedTransaction(execute: Transaction.() -> T): T {
    return transaction {
        addLogger(StdOutSqlLogger)
        execute()
    }
}