package com.example.ojtbadaassignment14.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ojtbadaassignment14.Pref;
import com.example.ojtbadaassignment14.R;
import com.example.ojtbadaassignment14.models.ItemReminderOnListener;
import com.example.ojtbadaassignment14.models.Reminder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.VH> {
    private List<Reminder> reminderList = new ArrayList<>();
    private ItemReminderOnListener itemReminderOnListener;

    public void setItemReminderOnListener(ItemReminderOnListener itemReminderOnListener) {
        this.itemReminderOnListener = itemReminderOnListener;
    }

    @NonNull
    @Override
    public ReminderAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reminder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderAdapter.VH holder, int position) {
        Reminder reminder = reminderList.get(holder.getAdapterPosition());
        holder.tvTitle.setText(reminder.getTitle());
        holder.tvDateTime.setText(Pref.convertDateTime(reminder.getDatetime()));

        String url = "https://image.tmdb.org/t/p/original" + reminder.getPoster();

        Picasso.get().load(url).centerCrop().fit().into(holder.imageView);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                itemReminderOnListener.onLongClick(reminder);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Reminder> allReminders) {
        this.reminderList = allReminders;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void removeItem(Reminder reminder) {
        this.reminderList.remove(reminder);
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvDateTime;
        private ImageView imageView;

        public VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            imageView = itemView.findViewById(R.id.image);
        }
    }
}
