package com.giis.mobileappproto1.data.sources.local_room


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.giis.mobileappproto1.data.models.EntityLoginCredentialDTO
import com.giis.mobileappproto1.data.models.EntityLoginStampDTO

@Database(entities = [EntityLoginCredentialDTO::class, EntityLoginStampDTO::class], version = 1)
@TypeConverters(Convertors::class)
abstract class CredentialDatabase : RoomDatabase() {

    abstract fun loginCredentialDao(): LoginCredentialDao

//    class Callback @Inject constructor(
//        private val database: Provider<CredentialDatabase>
//    ): RoomDatabase.Callback() {
//        override fun onCreate(db: SupportSQLiteDatabase) {
//            super.onCreate(db)
//            database.get()
//        }
//    }

//    companion object {
//        private var DBINSTANCE: CredentialDatabase? = null
//
//        fun getDatabase(context: Context): CredentialDatabase {
//            if (DBINSTANCE == null) {
//                synchronized(this) {
//                    DBINSTANCE =
//                        Room.databaseBuilder(
//                            context.applicationContext,
//                            CredentialDatabase::class.java,
//                            "AuthDB"
//                        )
//                            .build()
//                }
//            }
//            return DBINSTANCE!!
//        }
//
//    }

}