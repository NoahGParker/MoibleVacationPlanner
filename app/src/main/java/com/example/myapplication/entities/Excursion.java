package com.example.myapplication.entities;
import static androidx.room.ForeignKey.CASCADE;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
@Entity(foreignKeys = @ForeignKey(entity = Vacation.class,
        parentColumns = "id",
        childColumns = "vacationId",
        onDelete = CASCADE),
        indices = {@Index("vacationId")})
public class Excursion implements Parcelable{
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private int vacationId;
    private String date;

    public String getDate() {return date;}

    public void setDate(String date) { this.date = date; }
    public Excursion() {

    }

    protected Excursion(Parcel in) {
        id = in.readInt();
        title = in.readString();

    }

    public static final Parcelable.Creator<Excursion> CREATOR = new Parcelable.Creator<Excursion>() {
        @Override
        public Excursion createFromParcel(Parcel in) {
            return new Excursion(in);
        }

        @Override
        public Excursion[] newArray(int size) {
            return new Excursion[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getVacationId() {
        return vacationId;
    }

    public void setVacationId(int vacationId) {
        this.vacationId = vacationId;
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
    }
}
