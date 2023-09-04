import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "UserDatabase"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create the user table if it doesn't exist.
        val createTableSQL = "CREATE TABLE IF NOT EXISTS users " +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, email TEXT)"
        db.execSQL(createTableSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Upgrade the database if needed.
        db.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }
}
