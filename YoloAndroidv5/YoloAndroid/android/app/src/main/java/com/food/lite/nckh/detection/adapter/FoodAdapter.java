package com.food.lite.nckh.detection.adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.food.lite.nckh.detection.DetailFood;
import com.food.lite.nckh.detection.RemoveAccents;
import com.food.lite.nckh.detection.model.Food;

import org.tensorflow.lite.examples.detection.R;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> implements Filterable {
    //Goi viewholder trong adapter de ke thua 3 phuong thuc

    //Context la cai activity
    private Context context;
    private List<Food> allFood;
    private List<Food> allFoodOld;

    public FoodAdapter(Context context, List<Food> alFood) {
        this.context = context;
        this.allFood = alFood;
        this.allFoodOld = alFood;
    }

    //Tao doi tuong de hien thi
    @NonNull
    @Override
    public FoodAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        //gan vao card_view
        View view = inflater.inflate(R.layout.test_card, parent, false);
        return new FoodAdapter.ViewHolder(view);
    }

    //Chuyen du lieu vao ViewHolder, su kien thao tac tren tung item viet tai day
    @Override
    public void onBindViewHolder(@NonNull FoodAdapter.ViewHolder holder, int position) {
        final Food food = allFood.get(position);

        byte[] data = allFood.get(position).getImgFood();
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

        holder.name_food.setText(allFood.get(position).getNameFood());
        holder.imageView.setImageBitmap(bitmap);
        //holder.introduction_food.setText(allFood.get(position).getIntroductionFood());
        //holder.imageView.setImageBitmap(bitmap);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickGoToDetail(food);
            }
        });
    }

    private void onClickGoToDetail(Food food) {
        Intent intent = new Intent(context, DetailFood.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_food", food);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    //Truyen vo so luong co trong SQL
    @Override
    public int getItemCount() {
        return allFood.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name_food,introduction_food;
        public ImageView imageView;
        public LinearLayout cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.categoryThumb);
            cardView =  itemView.findViewById(R.id.layoutItem);
            name_food = itemView.findViewById(R.id.idFoodName);
            //introduction_food = itemView.findViewById(R.id.idIntroductionName);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String strSearch = charSequence.toString();

                if(strSearch.isEmpty()) {
                    allFood = allFoodOld;
                } else {
                    List<Food> filterlist = new ArrayList<>();
                    for (Food food : allFoodOld) {
                        if (food.getNameFood().toLowerCase().contains(strSearch.toLowerCase())
                                || RemoveAccents.removeAccent(food.getNameFood().toLowerCase()).contains(strSearch.toLowerCase())) {
                            filterlist.add(food);
                        }
                    }
                    allFood = filterlist;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = allFood;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                allFood = (List<Food>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
