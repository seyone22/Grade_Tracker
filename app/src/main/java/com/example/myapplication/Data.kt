package com.example.myapplication

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import java.io.Serializable

@Entity
data class Module(
    @PrimaryKey var id: String,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "grade") var grade: String,
    @ColumnInfo(name = "mark") var mark: Int = 0,
    @ColumnInfo(name = "semester") var semester: Int = 1,
    @ColumnInfo(name = "year") var year: Int = 1) : Serializable {}

@Dao
interface ModuleDao {
    @Query("SELECT * FROM module")
    fun getAll(): List<Module>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertModule(vararg module: Module)

    @Delete
    fun deleteModule(vararg module: Module)

    @Update
    fun updateModule(vararg module:Module)
}

@Database(entities = [Module::class], version = 1)
public abstract class AppDatabase : RoomDatabase() {
    abstract fun moduleDao(): ModuleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).allowMainThreadQueries().build()
                INSTANCE = instance
                instance
            }
        }
    }
}