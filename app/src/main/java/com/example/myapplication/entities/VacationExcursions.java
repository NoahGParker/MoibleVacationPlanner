package com.example.myapplication.entities;
import androidx.room.Embedded;
import androidx.room.Relation;
import java.util.List;
public class VacationExcursions {
    @Embedded public Vacation vacation;
    @Relation(
            parentColumn = "id",
            entityColumn = "vacationId"
    )
    public List<Excursion> excursions;
}
