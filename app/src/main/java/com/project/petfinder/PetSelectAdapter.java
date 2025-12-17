package com.project.petfinder;

import android.content.Context;
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

public class PetSelectAdapter extends RecyclerView.Adapter<PetSelectAdapter.ViewHolder> {

    public interface PetClickListener {
        void onPetSelected(String petId);
    }
    Context context;
    ArrayList<PetModel> list;
    PetClickListener listener;

    public PetSelectAdapter(Context ctx, ArrayList<PetModel> list, PetClickListener listener) {
        this.context = ctx;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_pet_select, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        PetModel pet = list.get(pos);

        h.petName.setText(pet.name);
        h.petColor.setText("Color: " + pet.color);

        try {
            byte[] decode = Base64.decode(pet.imageBase64, Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(decode, 0, decode.length);
            h.petImage.setImageBitmap(bmp);
        } catch (Exception e) {
            h.petImage.setImageResource(R.drawable.placeholder);
        }
        h.itemView.setOnClickListener(v -> listener.onPetSelected(pet.petId));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView petImage;
        TextView petName, petColor;

        public ViewHolder(@NonNull View v) {
            super(v);
            petImage = v.findViewById(R.id.petImage);
            petName = v.findViewById(R.id.petName);
            petColor = v.findViewById(R.id.petColor);
        }
    }
}
