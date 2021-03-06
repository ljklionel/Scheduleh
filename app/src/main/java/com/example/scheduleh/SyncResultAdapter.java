package com.example.scheduleh;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class SyncResultAdapter extends RecyclerView.Adapter<SyncResultAdapter.SyncResultViewHolder> {
    private ArrayList<TimeSlot> timeSlotList;
    private OnTimeSlotItemClickListener listener;

    public SyncResultAdapter(ArrayList<TimeSlot> timeSlotList) {
        this.timeSlotList = timeSlotList;
    }

    @NonNull
    @Override
    public SyncResultViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_item, viewGroup, false);
        SyncResultViewHolder holder = new SyncResultViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SyncResultViewHolder syncResultViewHolder, int i) {
        TimeSlot currentSlot = timeSlotList.get(i);

        syncResultViewHolder.textViewStartTime.setText(currentSlot.getStartTime() + "     -");
        syncResultViewHolder.textViewEndTime.setText(currentSlot.getEndTime());
        syncResultViewHolder.textViewDate.setText(currentSlot.getDay() + "/" + currentSlot.getMonth() + "/" + currentSlot.getYear());
        syncResultViewHolder.textViewFreeBusy.setText("Free: " + currentSlot.getFree() + ", Busy: " + currentSlot.getBusy() +
                "\n" + "Busy priority: " + currentSlot.getPriority());
    }

    @Override
    public int getItemCount() {
        return timeSlotList.size();
    }

    class SyncResultViewHolder extends RecyclerView.ViewHolder {
        TextView textViewStartTime;
        TextView textViewEndTime;
        TextView textViewDate;
        TextView textViewFreeBusy;

        public SyncResultViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewStartTime = itemView.findViewById(R.id.event_start_time);
            textViewEndTime = itemView.findViewById(R.id.event_name);
            textViewDate = itemView.findViewById(R.id.event_end_time);
            textViewFreeBusy = itemView.findViewById(R.id.event_openJio_status);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.OnTimeSlotItemClick(position);
                    }
                    return true;
                }
            });

        }
    }

    public interface OnTimeSlotItemClickListener {
        // To send any other info to next activity upon click, can add it as a parameter in this method.
        void OnTimeSlotItemClick(int position);
    }

    public void setOnTimeSlotItemClickListener(OnTimeSlotItemClickListener listener) {
        this.listener = listener;
    }
}
