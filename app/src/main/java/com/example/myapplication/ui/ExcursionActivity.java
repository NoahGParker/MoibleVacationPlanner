package com.example.myapplication.ui;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.ActionBar;
import java.util.Objects;
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

    private int excursionId;
    private ExcursionDao excursionDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion);

        editTextExcursionTitle = findViewById(R.id.editTextExcursionTitle);
        buttonSaveExcursion = findViewById(R.id.buttonSaveExcursion);
        buttonDeleteExcursion = findViewById(R.id.buttonDeleteExcursion);

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

        // Get the excursion ID from the intent.
        excursionId = getIntent().getIntExtra("EXCURSION_ID", -1);
        if (excursionId != -1) {
            Executors.newSingleThreadExecutor().execute(() -> {
                Excursion excursion = excursionDao.getExcursion(excursionId);
                if (excursion != null) {
                    runOnUiThread(() -> {
                        editTextExcursionTitle.setText(excursion.getTitle());

                    });
                }
            });
        }

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

    private void saveExcursion() {
        String excursionTitle = editTextExcursionTitle.getText().toString().trim();
        if (!excursionTitle.isEmpty()) {
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    Excursion updatedExcursion;

                    if (excursionId > 0) {
                        updatedExcursion = excursionDao.getExcursion(excursionId);
                        if (updatedExcursion == null) {
                            return;
                        }
                    } else {
                        updatedExcursion = new Excursion();
                    }

                    updatedExcursion.setTitle(excursionTitle);

                    Intent intent = getIntent();
                    int vacationId = intent.getIntExtra("vacationId", -1);
                    if (vacationId != -1) {
                        updatedExcursion.setVacationId(vacationId);

                        if (excursionId > 0) {
                            excursionDao.update(updatedExcursion);
                        } else {
                            excursionDao.insert(updatedExcursion);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        });
                    }
                }
            });
        }
    }
    private void loadExcursion() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Excursion excursion = excursionDao.getExcursion(excursionId);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (excursion != null) {
                            editTextExcursionTitle.setText(excursion.getTitle());
                        }
                    }
                });
            }
        });
    }

    private void deleteExcursion() {
        if (excursionId > 0) {
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    Excursion excursion = excursionDao.getExcursion(excursionId);
                    if (excursion != null) {
                        excursionDao.delete(excursion);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        });
                    }
                }
            });
        }
    }


}