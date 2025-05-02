package com.example.clase8.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.clase8.model.Appointment
import com.example.clase8.utils.Constants.NAME_BD

@Database(entities = [Appointment::class], version = 1)
abstract class AppointmentsDB : RoomDatabase() {

    abstract fun appointmentsDao(): AppointmentsDao

    companion object {
        @Volatile
        private var INSTANCE: AppointmentsDB? = null
        //@Volatile:
        // “Si un hilo cambia el valor de INSTANCE,
        // todos los demás hilos deben ver ese nuevo valor de inmediato”

        fun getDatabase(context: Context): AppointmentsDB {
            // synchronized: Evitar que dos hilos creen la base de datos al mismo tiempo.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppointmentsDB::class.java,
                    NAME_BD
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

//abstract class InventoryDB : RoomDatabase() {
//
//    abstract fun inventoryDao(): InventoryDao
//
//companion object{
//        fun getDatabase(context: Context): InventoryDB {
//            return Room.databaseBuilder(
//                context.applicationContext,
//                InventoryDB::class.java,
//                NAME_BD
//            ).build()
//        }
//    }
//}