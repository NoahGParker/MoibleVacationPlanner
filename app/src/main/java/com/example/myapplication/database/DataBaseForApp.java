package com.example.myapplication.database;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.myapplication.adapters.ExcursionAdapter;
import com.example.myapplication.adapters.VacationAdapter;
import com.example.myapplication.dao.ExcursionDao;
import com.example.myapplication.dao.VacationDao;
import com.example.myapplication.entities.Excursion;
import com.example.myapplication.entities.Vacation;

@Database(entities = {Excursion.class, Vacation.class}, version = 1)
public abstract class DataBaseForApp extends  RoomDatabase{
    public abstract VacationDao vacationDao();
    public abstract ExcursionDao excursionDao();

    private static volatile DataBaseForApp INSTANCE;

    public static DataBaseForApp getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (DataBaseForApp.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    DataBaseForApp.class, "app_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
