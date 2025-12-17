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
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class LostPetListAdapter extends RecyclerView.Adapter<LostPetListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<LostPetModel> lostPetList;
    public LostPetListAdapter(Context context, ArrayList<LostPetModel> list) {
        this.context = context;
        this.lostPetList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_lost_pet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LostPetModel pet = lostPetList.get(position);

        holder.name.setText(pet.name);
        holder.color.setText("Color: " + pet.color);
        holder.lastSeen.setText("Last Seen: " + pet.lastSeen);

        try {
            byte[] decoded = Base64.decode(pet.imageBase64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
            holder.image.setImageBitmap(bitmap);
        } catch (Exception e) {
            holder.image.setImageResource(R.drawable.placeholder);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, LostPetDetailsActivity.class);
            intent.putExtra("petId", pet.id);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lostPetList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name, color, lastSeen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.lostPetImage);
            name = itemView.findViewById(R.id.lostPetName);
            color = itemView.findViewById(R.id.lostPetColor);
            lastSeen = itemView.findViewById(R.id.lostPetLastSeen);
        }
    }
}
