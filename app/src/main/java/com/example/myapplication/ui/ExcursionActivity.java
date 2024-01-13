package com.example.myapplication.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.ActionBar;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.ActionBar;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executors;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.dao.ExcursionDao;
import com.example.myapplication.database.DataBaseForApp;
import com.example.myapplication.entities.Excursion;
import com.example.myapplication.entities.Vacation;
import com.example.myapplication.dao.VacationDao;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import java.text.ParseException;
import java.util.Date;
public class ExcursionActivity extends AppCompatActivity{
    private EditText editTextExcursionTitle;
    private Button buttonSaveExcursion;
    private Button buttonDeleteExcursion;
    private EditText editTextExcursionDate;

    private int excursionId;
    private ExcursionDao excursionDao;
    private int vacationId;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
    private VacationDao vacationDao;
    private static final int NOTIFICATION_ID_EXCURSION = 5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion);

        editTextExcursionTitle = findViewById(R.id.editTextExcursionTitle);
        buttonSaveExcursion = findViewById(R.id.buttonSaveExcursion);
        buttonDeleteExcursion = findViewById(R.id.buttonDeleteExcursion);
        editTextExcursionDate = findViewById(R.id.editTextExcursionDate);
        vacationId = getIntent().getIntExtra("vacationId", -1);
        vacationDao = DataBaseForApp.getInstance(this).vacationDao();
        buttonDeleteExcursion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteExcursion();
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        DataBaseForApp database = DataBaseForApp.getInstance(this);
        excursionDao = database.excursionDao();

        excursionId = getIntent().getIntExtra("EXCURSION_ID", -1);
        if (excursionId != -1) {
            Executors.newSingleThreadExecutor().execute(() -> {
                Excursion excursion = excursionDao.getExcursion(excursionId);
                if (excursion != null) {
                    runOnUiThread(() -> {
                        editTextExcursionTitle.setText(excursion.getTitle());
                        editTextExcursionDate.setText(excursion.getDate());
                    });
                }
            });
        }

        editTextExcursionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        buttonSaveExcursion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExcursion();
            }
        });

        loadExcursion();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;

        }
        if ( id == R.id.action_alert){
            setAlert();
            return true;
        }
        if ( id == R.id.action_share){
            shareExcursionDetails();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadExcursion() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Excursion excursion = excursionDao.getExcursion(excursionId);
            runOnUiThread(() -> {
                if (excursion != null) {
                    editTextExcursionTitle.setText(excursion.getTitle());
                    editTextExcursionDate.setText(excursion.getDate());
                }
            });
        });
    }

    private void deleteExcursion() {
        if (excursionId > 0) {
            Executors.newSingleThreadExecutor().execute(() -> {
                Excursion excursion = excursionDao.getExcursion(excursionId);
                if (excursion != null) {
                    excursionDao.delete(excursion);
                    runOnUiThread(() -> {
                        finish();
                    });
                }
            });
        }
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String excursionDate = formatDate(year, month, dayOfMonth);
                        editTextExcursionDate.setText(excursionDate);
                    }
                },
                year,
                month,
                day
        );
        datePickerDialog.show();
    }

    private String formatDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
        return dateFormat.format(calendar.getTime());
    }

    private void saveExcursion() {
        String excursionTitle = editTextExcursionTitle.getText().toString().trim();
        String excursionDate = editTextExcursionDate.getText().toString().trim();

        if (!excursionTitle.isEmpty() && !excursionDate.isEmpty()) {
            Executors.newSingleThreadExecutor().execute(() -> {
                Excursion updatedExcursion;

                if (excursionId > 0) {
                    updatedExcursion = excursionDao.getExcursion(excursionId);
                    if (updatedExcursion == null) {
                        return;
                    }
                } else {
                    updatedExcursion = new Excursion();
                }
                boolean isDateDuringVacation = checkExcursionDateDuringVacation(excursionDate);
                if (!isDateDuringVacation) {
                    runOnUiThread(() -> {
                        Snackbar.make(findViewById(android.R.id.content), "Excursion date is not in range of your stay.", Snackbar.LENGTH_LONG).show();
                    });
                    return;
                }
                updatedExcursion.setVacationId(vacationId);
                updatedExcursion.setTitle(excursionTitle);
                updatedExcursion.setDate(excursionDate);

                if (excursionId > 0) {
                    excursionDao.update(updatedExcursion);
                } else {
                    excursionDao.insert(updatedExcursion);
                }

                runOnUiThread(() -> {
                    finish();
                    Snackbar.make(findViewById(android.R.id.content), "Excursion Saved!", Snackbar.LENGTH_SHORT).show();
                });
            });
        } else {

            Snackbar.make(findViewById(android.R.id.content), "All fields are required.", Snackbar.LENGTH_LONG).show();
        }
    }
    private boolean checkExcursionDateDuringVacation(String excursionDate) {
        // Retrieve the associated vacation details
        Vacation vacation = vacationDao.getVacation(vacationId);
        if (vacation != null) {
            String startDate = vacation.getStartDate();
            String endDate = vacation.getEndDate();

            try {
                Date excursionDateObj = dateFormat.parse(excursionDate);
                Date startDateObj = dateFormat.parse(startDate);
                Date endDateObj = dateFormat.parse(endDate);

                return excursionDateObj.compareTo(startDateObj) >= 0 && excursionDateObj.compareTo(endDateObj) <= 0;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void scheduleNotification(Calendar calendar, int notificationId, String message) {
        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        notificationIntent.putExtra(NotificationReceiver.EXTRA_NOTIFICATION_ID, notificationId);
        notificationIntent.putExtra(NotificationReceiver.EXTRA_MESSAGE, message);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                notificationId,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        }
    }

    private void setAlert() {
        String excursionDateStr = editTextExcursionDate.getText().toString();

        if (excursionDateStr.isEmpty()) {
            return;
        }

        try {
            Date excursionDate = dateFormat.parse(excursionDateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(excursionDate);


            String excursionTitle = editTextExcursionTitle.getText().toString();


            String message = excursionTitle + " is today!";
            scheduleNotification(calendar, NOTIFICATION_ID_EXCURSION, message);

            showAlert();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacation_details, menu);
        return true;
    }

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Alert")
                .setMessage("Alert set for the excursion date!")
                .setPositiveButton("OK", null)
                .show();
    }

    private void shareExcursionDetails() {
        String excursionTitle = editTextExcursionTitle.getText().toString().trim();
        String excursionDate = editTextExcursionDate.getText().toString().trim();

        if (!excursionTitle.isEmpty() && !excursionDate.isEmpty()) {
            StringBuilder shareBody = new StringBuilder();
            shareBody.append("Excursion: ").append(excursionTitle).append("\n");
            shareBody.append("Date: ").append(excursionDate).append("\n");

            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Excursion Details");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody.toString());
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        } else {
            Snackbar.make(editTextExcursionTitle, "Excursion Empty Can't Share.", Snackbar.LENGTH_SHORT).show();
        }
    }


}
