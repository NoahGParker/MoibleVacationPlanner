package com.example.myapplication.ui;
import android.widget.DatePicker;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.util.Calendar;
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
    private EditText editTextTitle;
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
    private int vacationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_details);


        editTextTitle = findViewById(R.id.TextTitle);
        buttonSave = findViewById(R.id.buttonSave);
        buttonDelete = findViewById(R.id.buttonDelete);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        recyclerView = findViewById(R.id.recyclerViewExcursion);
        findViewById(R.id.floatingActionButton);
        recyclerView = findViewById(R.id.recyclerViewExcursion);
        editTextTitle = findViewById(R.id.TextTitle);
        TextHotel = findViewById(R.id.TextHotel);
        DatePickerStart = findViewById(R.id.DatePickerStart);
        DatePickerEnd = findViewById(R.id.DatePickerEnd);
        TextStartDate = findViewById(R.id.TextStartDate);
        TextEndDate = findViewById(R.id.TextEndDate);
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
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
            editTextTitle.setText(currentVacation.getTitle());
            TextHotel.setText(currentVacation.getHotel());
            TextStartDate.setText(currentVacation.getStartDate());
            TextEndDate.setText(currentVacation.getEndDate());
            setupRecyclerView();
        }

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTitle.getText().toString();
                String hotel = TextHotel.getText().toString();
                String startDate = TextStartDate.getText().toString();
                String endDate = TextEndDate.getText().toString();

                if (title.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
                    Snackbar.make(v, "Cannot have a empty field!", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (currentVacation == null) {
                   
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

        DatePickerStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        DetailsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String startDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
                                TextStartDate.setText(startDate);
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
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String endDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
                                TextEndDate.setText(endDate);
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
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        excursionAdapter = new ExcursionAdapter(excursionDao);
        recyclerView.setAdapter(excursionAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
}
