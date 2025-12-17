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

public class PetListAdapter extends RecyclerView.Adapter<PetListAdapter.ViewHolder> {

    Context context;
    ArrayList<PetModel> list;

    public PetListAdapter(Context context, ArrayList<PetModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_pet_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        PetModel pet = list.get(position);

        h.petName.setText(pet.name);
        h.petType.setText(pet.type);

        try {
            byte[] decoded = Base64.decode(pet.imageBase64, Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
            h.petImage.setImageBitmap(bmp);
        } catch (Exception e) {
            h.petImage.setImageResource(R.drawable.placeholder);
        }

        h.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PetDetailsActivity.class);
            intent.putExtra("petId", pet.petId); // only send ID
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView petImage;
        TextView petName, petType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            petImage = itemView.findViewById(R.id.petImage);
            petName = itemView.findViewById(R.id.petName);
            petType = itemView.findViewById(R.id.petType);
        }
    }
}
