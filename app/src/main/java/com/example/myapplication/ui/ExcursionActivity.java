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
public class ExcursionActivity extends AppCompatActivity{
    private EditText editTextExcursionTitle;
    private Button buttonSaveExcursion;
    private Button buttonDeleteExcursion;
    private EditText editTextExcursionDate;

    private int excursionId;
    private ExcursionDao excursionDao;
    private int vacationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion);

        editTextExcursionTitle = findViewById(R.id.editTextExcursionTitle);
        buttonSaveExcursion = findViewById(R.id.buttonSaveExcursion);
        buttonDeleteExcursion = findViewById(R.id.buttonDeleteExcursion);
        editTextExcursionDate = findViewById(R.id.editTextExcursionDate);
        vacationId = getIntent().getIntExtra("vacationId", -1);

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
        if (item.getItemId() == android.R.id.home) {
            finish();
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
                });
            });
        } else {

            Snackbar.make(findViewById(android.R.id.content), "All fields are required.", Snackbar.LENGTH_LONG).show();
        }
    }
}