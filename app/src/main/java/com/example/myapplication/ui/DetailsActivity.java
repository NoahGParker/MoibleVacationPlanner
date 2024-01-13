package com.example.myapplication.ui;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.view.Menu;
import android.widget.DatePicker;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapters.ExcursionAdapter;
import com.example.myapplication.dao.ExcursionDao;
import com.example.myapplication.dao.VacationDao;
import com.example.myapplication.database.DataBaseForApp;
import com.example.myapplication.entities.Excursion;
import com.example.myapplication.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.concurrent.Executors;
public class DetailsActivity extends AppCompatActivity {
    private EditText TextTitle;
    private Button buttonSave;
    private Button buttonDelete;
    private FloatingActionButton floatingActionButton;
    private VacationDao vacationDao;
    private ExcursionDao excursionDao;
    private Vacation currentVacation;
    private RecyclerView recyclerView;
    private ExcursionAdapter excursionAdapter;
    private EditText TextHotel;
    private Button DatePickerStart;
    private Button DatePickerEnd;
    private EditText TextStartDate;
    private EditText TextEndDate;
    private Button buttonEdit;
    private static  final int NOTIFICATION_ID_START = 1;
    private static final int NOTIFICATION_ID_END = 2;


    private int vacationId;

    final SimpleDateFormat month_date = new SimpleDateFormat("MMMM", Locale.getDefault());
    final DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_details);

        TextTitle = findViewById(R.id.TextTitle);
        buttonSave = findViewById(R.id.buttonSave);
        buttonDelete = findViewById(R.id.buttonDelete);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        recyclerView = findViewById(R.id.recyclerViewExcursion);
        TextTitle = findViewById(R.id.TextTitle);
        TextHotel = findViewById(R.id.TextHotel);
        DatePickerStart = findViewById(R.id.DatePickerStart);
        DatePickerEnd = findViewById(R.id.DatePickerEnd);
        TextStartDate = findViewById(R.id.TextStartDate);
        TextEndDate = findViewById(R.id.TextEndDate);
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        buttonEdit = findViewById(R.id.buttonEdit);
        TextStartDate = findViewById(R.id.TextStartDate);
        TextEndDate = findViewById(R.id.TextEndDate);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        DataBaseForApp database = DataBaseForApp.getInstance(this);

        vacationDao = database.vacationDao();
        excursionDao = database.excursionDao();

        Intent intent = getIntent();
        currentVacation = intent.getParcelableExtra("vacation");

        if (currentVacation != null) {
            vacationId = currentVacation.getId();
            TextTitle.setText(currentVacation.getTitle());
            TextHotel.setText(currentVacation.getHotel());
            TextStartDate.setText(currentVacation.getStartDate());
            TextEndDate.setText(currentVacation.getEndDate());
            setupRecyclerView();
        }

        TextTitle.setEnabled(false);
        TextHotel.setEnabled(false);
        TextStartDate.setEnabled(false);
        TextEndDate.setEnabled(false);
        DatePickerStart.setEnabled(false);
        DatePickerEnd.setEnabled(false);
        buttonEdit.setVisibility(View.VISIBLE);
        buttonSave.setVisibility(View.GONE);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = TextTitle.getText().toString();
                String hotel = TextHotel.getText().toString();
                String startDate = TextStartDate.getText().toString();
                String endDate = TextEndDate.getText().toString();

                if (title.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
                    Snackbar.make(v, "All fields required!", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (currentVacation == null) {
                    // If there's no currentVacation, create a new one.
                    currentVacation = new Vacation();
                }

                currentVacation.setTitle(title);
                currentVacation.setHotel(hotel);
                currentVacation.setStartDate(startDate);
                currentVacation.setEndDate(endDate);

                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (currentVacation.getId() > 0) {
                            vacationDao.update(currentVacation);
                        } else {
                            long id = vacationDao.insert(currentVacation);
                            currentVacation.setId((int) id);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Disable all fields and show the Edit button
                                TextTitle.setEnabled(false);
                                TextHotel.setEnabled(false);
                                TextStartDate.setEnabled(false);
                                TextEndDate.setEnabled(false);
                                DatePickerStart.setEnabled(false);
                                DatePickerEnd.setEnabled(false);
                                buttonEdit.setVisibility(View.VISIBLE);
                                buttonSave.setVisibility(View.GONE);
                                finish();
                            }
                        });
                    }
                });
            }
        });


        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentVacation != null) {

                    Log.d("VacationDetailsActivity", "Delete button clicked");
                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            List<Excursion> excursions = vacationDao.getAllExcursionsForVacation(currentVacation.getId());
                            if (excursions != null && !excursions.isEmpty()) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Snackbar.make(v, "Cannot delete vacation with excursions!", Snackbar.LENGTH_SHORT).show();
                                        Log.d("VacationDetailsActivity", "Vacation has excursions");
                                    }
                                });
                            } else {

                                Log.d("VacationDetailsActivity", "Deleting vacation");
                                vacationDao.delete(currentVacation);
                                finish();
                            }
                        }
                    });
                } else {

                    finish();
                }
            }
        });
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextTitle.setEnabled(true);
                TextHotel.setEnabled(true);
                TextStartDate.setEnabled(true);
                TextEndDate.setEnabled(true);
                DatePickerStart.setEnabled(true);
                DatePickerEnd.setEnabled(true);

                buttonEdit.setVisibility(View.GONE);
                buttonSave.setVisibility(View.VISIBLE);
            }
        });


        DatePickerStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        DetailsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(year, monthOfYear, dayOfMonth);

                                String newStartDate = dateFormat.format(calendar.getTime());

                                // Check if new start date is after current end date
                                String endDateStr = TextEndDate.getText().toString();
                                if (!endDateStr.isEmpty()) {
                                    try {
                                        Date endDate = dateFormat.parse(endDateStr);
                                        Date chosenStartDate = dateFormat.parse(newStartDate);

                                        if (endDate != null && chosenStartDate != null && chosenStartDate.after(endDate)) {
                                            Snackbar.make(v, "Start date should be before end date", Snackbar.LENGTH_LONG).show();
                                            return;
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }

                                TextStartDate.setText(newStartDate);
                            }
                        },
                        currentYear,
                        currentMonth,
                        currentDay
                );
                datePickerDialog.show();
            }
        });


        DatePickerEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        DetailsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(year, monthOfYear, dayOfMonth);

                                String endDate = dateFormat.format(calendar.getTime());
                                TextEndDate.setText(endDate);
                                String startDateStr = TextStartDate.getText().toString();
                                try {
                                    Date startDate = dateFormat.parse(startDateStr);
                                    Date chosenEndDate = dateFormat.parse(endDate);

                                    if (chosenEndDate.before(startDate)) {
                                        Snackbar.make(v, "End date should be after start date", Snackbar.LENGTH_LONG).show();
                                        TextEndDate.setText("");
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        currentYear,
                        currentMonth,
                        currentDay
                );
                datePickerDialog.show();
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent excursionIntent = new Intent(DetailsActivity.this, ExcursionActivity.class);
                if (currentVacation != null) {
                    excursionIntent.putExtra("vacationId", currentVacation.getId());
                }
                startActivity(excursionIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExcursions();
        loadDataAndUpdateRecyclerView();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        excursionAdapter = new ExcursionAdapter(excursionDao);
        recyclerView.setAdapter(excursionAdapter);
        excursionAdapter.setOnItemClickListener(new ExcursionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Excursion excursion) {
                Intent intent = new Intent(DetailsActivity.this, ExcursionActivity.class);
                intent.putExtra("EXCURSION_ID", excursion.getId());
                intent.putExtra("vacationId", vacationId);
                startActivity(intent);
            }
        });
    }
    private void loadDataAndUpdateRecyclerView() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Excursion> excursions = excursionDao.getAllForVacation(vacationId);
            runOnUiThread(() -> {
                if (excursionAdapter != null) {
                    excursionAdapter.setExcursions(excursions);
                    excursionAdapter.notifyDataSetChanged();
                }
            });
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if ( id == R.id.action_alert){
            showAlert();
            setAlert();
            return true;
        }
        if ( id == R.id.action_share){
            shareVacationDetails();
            return true;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void shareVacationDetails() {
        if (currentVacation != null) {
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    StringBuilder shareBody = new StringBuilder();
                    shareBody.append("Title: ").append(currentVacation.getTitle()).append("\n");
                    shareBody.append("Hotel: ").append(currentVacation.getHotel()).append("\n");
                    shareBody.append("Start Date: ").append(currentVacation.getStartDate()).append("\n");
                    shareBody.append("End Date: ").append(currentVacation.getEndDate()).append("\n");

                    // Add excursion details
                    List<Excursion> excursions = excursionDao.getAllForVacation(currentVacation.getId());
                    if (excursions != null && !excursions.isEmpty()) {
                        shareBody.append("\nExcursions:\n");
                        for (Excursion excursion : excursions) {
                            shareBody.append("Title: ").append(excursion.getTitle()).append("\n");
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                            sharingIntent.setType("text/plain");
                            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Vacation Details");
                            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody.toString());
                            startActivity(Intent.createChooser(sharingIntent, "Share via"));
                        }
                    });
                }
            });
        } else {
            Snackbar.make(TextTitle, "No Vacation Details to Share", Snackbar.LENGTH_SHORT).show();
        }
    }
    private void loadExcursions() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                if(currentVacation != null){
                    List<Excursion> excursions = excursionDao.getAllForVacation(currentVacation.getId());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            excursionAdapter.setExcursions(excursions);
                        }
                    });
                }
            }
        });
    }
    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Alert")
                .setMessage("Alerts set for start date and end date!")
                .setPositiveButton("OK", null)
                .show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacation_details, menu);
        return true;
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
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }
    private void setAlert() {
        String startDateStr = TextStartDate.getText().toString();
        String endDateStr = TextEndDate.getText().toString();
        if (startDateStr.isEmpty() || endDateStr.isEmpty()) {
            return;
        }
        try {
            Date startDate = dateFormat.parse(startDateStr);
            Date endDate = dateFormat.parse(endDateStr);
            Calendar calendarStart = Calendar.getInstance();
            calendarStart.setTime(startDate);
            Calendar calendarEnd = Calendar.getInstance();
            calendarEnd.setTime(endDate);
            scheduleNotification(calendarStart, NOTIFICATION_ID_START, "Today is the beginning of your vacation!");
            scheduleNotification(calendarEnd, NOTIFICATION_ID_END, "Today is the end of your vacation!");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}