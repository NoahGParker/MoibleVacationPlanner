package com.example.myapplication.dao;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import androidx.room.Update;

import com.example.myapplication.entities.Excursion;

import java.util.List;
@Dao
public interface ExcursionDao {
    @Query("SELECT * FROM excursion WHERE vacationId = :vacationId")
    List<Excursion> getAllForVacation(int vacationId);

    @Insert
    void insertAll(Excursion... excursions);

    @Delete
    void delete(Excursion excursion);

    @Insert
    void insert(Excursion excursion);

    @Query("SELECT * FROM excursion WHERE id = :excursionId")
    Excursion getExcursion(int excursionId);

    @Update
    void update(Excursion excursion);
}
