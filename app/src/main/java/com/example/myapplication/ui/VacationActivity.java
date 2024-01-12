package com.example.myapplication.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.adapters.VacationAdapter;
import com.example.myapplication.dao.VacationDao;
import com.example.myapplication.database.DataBaseForApp;
import com.example.myapplication.entities.VacationExcursions;
import com.example.myapplication.adapters.VacationAdapter;
import com.example.myapplication.dao.VacationDao;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.concurrent.Executors;

public class VacationActivity extends AppCompatActivity implements VacationAdapter.VacationClickListener{
    private RecyclerView recyclerView;
    private VacationAdapter adapter;
    private VacationDao vacationDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VacationAdapter(this);
        recyclerView.setAdapter(adapter);
      FloatingActionButton fabAddVacation = findViewById(R.id.fabAddVacation);
        fabAddVacation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VacationActivity.this, DetailsActivity.class);
                startActivity(intent);
            }
        });

        // Get an instance of the AppDatabase
        DataBaseForApp database = DataBaseForApp.getInstance(this);

        // Get the VacationDao from the AppDatabase
        vacationDao = database.vacationDao();

        updateRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateRecyclerView();
    }

    private void updateRecyclerView() {
        // Retrieve all vacations from the database
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                final List<VacationExcursions> vacations = vacationDao.getAllWithExcursions();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Set the retrieved vacations to the adapter
                        adapter.setVacations(vacations);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }


    @Override
    public void onVacationClick(VacationExcursions vacation) {
        Intent intent = new Intent(VacationActivity.this, DetailsActivity.class);
        intent.putExtra("vacation", vacation.vacation);
        startActivity(intent);
    }

    @Override
    public void onVacationLongClick(VacationExcursions vacation) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                // Check if the vacation has excursions
                if (vacation.excursions != null && !vacation.excursions.isEmpty()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Show a Snackbar message indicating that the vacation cannot be deleted
                            Snackbar.make(recyclerView, "Cannot delete vacation with excursions", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    vacationDao.delete(vacation.vacation);
                    final List<VacationExcursions> vacations = vacationDao.getAllWithExcursions();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.setVacations(vacations);
                            adapter.notifyDataSetChanged();

                            // Show a Snackbar message indicating that the vacation was deleted
                            Snackbar.make(recyclerView, "Vacation deleted", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
