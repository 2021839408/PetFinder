package com.project.petfinder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ReportedListAdapter extends RecyclerView.Adapter<ReportedListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ReportedPetModel> list;

    public ReportedListAdapter(Context context, ArrayList<ReportedPetModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_reported_pet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {

        ReportedPetModel pet = list.get(position);

        h.title.setText(pet.type);

        String date = new SimpleDateFormat("dd MMM yyyy, hh:mm a")
                .format(new Date(pet.timestamp));
        h.timestamp.setText(date);

        try {
            byte[] decoded = Base64.decode(pet.imageBase64, Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
            h.photo.setImageBitmap(bmp);
        } catch (Exception e) {
            h.photo.setImageResource(R.drawable.placeholder);
        }

        h.lastSeen.setText(
                ReportedListActivity.getAddressFromLatLng(
                        (AppCompatActivity) context,
                        pet.latitude,
                        pet.longitude
                )
        );

        h.itemView.setOnClickListener(v -> {

            if (pet.type.equals("Lost Pet")) {

                Intent i = new Intent(context, LostPetDetailsActivity.class);
                i.putExtra("petId", pet.petId);
                context.startActivity(i);

            } else if (pet.type.equals("Found Pet")) {

                Intent i = new Intent(context, FoundPetDetailsActivity.class);
                i.putExtra("petId", pet.petId);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView photo;
        TextView title, timestamp, lastSeen;

        public ViewHolder(@NonNull View v) {
            super(v);

            photo = v.findViewById(R.id.petPhoto);
            title = v.findViewById(R.id.titleType);
            timestamp = v.findViewById(R.id.timeStamp);
            lastSeen = v.findViewById(R.id.lastSeenLocation);
        }
    }
}
