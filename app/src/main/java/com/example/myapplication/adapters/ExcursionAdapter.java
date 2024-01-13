package com.example.myapplication.adapters;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import  com.example.myapplication.R;
import com.example.myapplication.dao.ExcursionDao;
import com.example.myapplication.entities.Excursion;

public class ExcursionAdapter extends RecyclerView.Adapter<ExcursionAdapter.ExcursionViewHolder> {
    private List<Excursion> excursions;
    private ExcursionDao excursionDao;
    private OnItemClickListener listener;

    public ExcursionAdapter(List<Excursion> excursions, ExcursionDao excursionDao) {
        this.excursions = excursions;
        this.excursionDao = excursionDao;
    }

    public ExcursionAdapter(ExcursionDao excursionDao) {
        this(new ArrayList<>(), excursionDao);
    }

    public ExcursionAdapter() {
        this(new ArrayList<>(), null);
    }

    public void setExcursions(List<Excursion> excursions) {
        this.excursions = excursions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExcursionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.excursion_item_layout, parent, false);
        return new ExcursionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExcursionViewHolder holder, int position) {
        Excursion excursion = excursions.get(position);
        holder.titleTextView.setText(excursion.getTitle());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(excursion);
            }
        });
    }

    @Override
    public int getItemCount() {
        return excursions.size();
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void loadExcursions(int vacationId) {
        if (excursionDao == null) {
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            List<Excursion> excursions = excursionDao.getAllForVacation(vacationId);
            new Handler(Looper.getMainLooper()).post(() -> {
                setExcursions(excursions);
            });
        });
    }
    public interface OnItemClickListener {
        void onItemClick(Excursion excursion);
    }
    public class ExcursionViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;


        public ExcursionViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textViewExcursion);
        }
    }
}


