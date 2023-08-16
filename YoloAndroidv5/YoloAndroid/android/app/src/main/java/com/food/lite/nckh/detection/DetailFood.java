package com.food.lite.nckh.detection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.food.lite.nckh.detection.model.Food;

import org.tensorflow.lite.examples.detection.R;

public class DetailFood extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_food);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle(R.string.detailfood);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }

        Food food = (Food) bundle.get("object_food");
        byte[] data = food.getImgFood();
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

        TextView tvFoodName = findViewById(R.id.food_name);
        TextView tvTinh = findViewById(R.id.ten_tinh);
        TextView tvCachLam = findViewById(R.id.detail_cachlam);
        TextView tvNguyenLieu = findViewById(R.id.detail_nguyenlieu);
        TextView tvGioiThieu = findViewById(R.id.detail_gioithieu);
        ImageView tvHinhAnh = findViewById(R.id.myimage);

        tvFoodName.setText(food.getNameFood());
        tvTinh.setText(food.getTinh());
        tvCachLam.setText(food.getRecipesFood());
        tvNguyenLieu.setText(food.getIngredientFood());
        tvGioiThieu.setText(food.getIntroductionFood());
        tvHinhAnh.setImageBitmap(bitmap);
    }
}
