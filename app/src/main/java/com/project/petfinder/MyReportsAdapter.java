package com.project.petfinder;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyReportsAdapter extends RecyclerView.Adapter<MyReportsAdapter.ViewHolder> {

    Context context;
    ArrayList<ReportedPetModel> list;

    public MyReportsAdapter(Context context, ArrayList<ReportedPetModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_my_report, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        ReportedPetModel pet = list.get(pos);

        h.type.setText(pet.type);
        h.time.setText(new SimpleDateFormat("dd MMM yyyy, hh:mm a").format(new Date(pet.timestamp)));

        try {
            byte[] decoded = Base64.decode(pet.imageBase64, Base64.DEFAULT);
            h.photo.setImageBitmap(BitmapFactory.decodeByteArray(decoded, 0, decoded.length));
        } catch (Exception e) {
            h.photo.setImageResource(R.drawable.placeholder);
        }

        h.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context,
                    pet.type.equals("Lost Pet")
                            ? LostPetDetailsActivity.class
                            : FoundPetDetailsActivity.class);

            intent.putExtra("petId", pet.petId);
            context.startActivity(intent);
        });

        h.deleteBtn.setOnClickListener(v ->
                ((MyReportsActivity) context).deleteReportDialog(pet.type, pet.petId));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView photo;
        TextView type, time;
        Button deleteBtn;

        public ViewHolder(@NonNull View v) {
            super(v);
            photo = v.findViewById(R.id.petPhoto);
            type = v.findViewById(R.id.reportType);
            time = v.findViewById(R.id.reportTime);
            deleteBtn = v.findViewById(R.id.btnDeleteReport);
        }
    }
}
