package com.example.myapplication.dao;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.myapplication.entities.Excursion;
import com.example.myapplication.entities.Vacation;
import com.example.myapplication.entities.VacationExcursions;

import java.util.List;
@Dao
public interface VacationDao {
    @Query("SELECT * FROM vacation")
    List<Vacation> getAll();

    @Transaction
    @Query("SELECT * FROM vacation")
    List<VacationExcursions> getAllWithExcursions();

    @Insert
    void insertAll(Vacation... vacations);

    @Update
    void update(Vacation vacation);

    @Delete
    void delete(Vacation vacation);
    @Insert
    long insert(Vacation vacation);

    @Transaction
    @Query("SELECT * FROM vacation WHERE id = :vacationId")
    VacationExcursions getVacationWithExcursions(int vacationId);

    @Query("SELECT * FROM excursion WHERE vacationId = :vacationId")
    List<Excursion> getAllExcursionsForVacation(int vacationId);
}

