package com.food.lite.nckh.detection.sqlite;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import com.food.lite.nckh.detection.MainActivity2;
import com.food.lite.nckh.detection.model.Food;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.Locale;


public class sqlFood extends SQLiteAssetHelper {
    private static final String DB_NAME = "foodlist669.db";
    private static final int DB_VERSION = 1;
    //private static final String TABLE_NAME = "Food";

    public sqlFood(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
        public ArrayList<Food> getFoodId(Integer food_id) { //add

        SQLiteDatabase db = getReadableDatabase();
        String q = "SELECT * FROM listfood2 WHERE ma_mon= " + food_id;
        //String q = "SELECT * FROM listfood WHERE ma_mon= " + food_id ;
        Cursor cursor = db.rawQuery(q, null);

        ArrayList<Food> resultfood = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Food food = new Food();
                food.setIdFood(cursor.getString(cursor.getColumnIndexOrThrow("ma_mon")));
                food.setNameFood(cursor.getString(cursor.getColumnIndexOrThrow("ten_mon")));
                food.setRecipesFood(cursor.getString(cursor.getColumnIndexOrThrow("cach_lam")));
                food.setIntroductionFood(cursor.getString(cursor.getColumnIndexOrThrow("gioi_thieu")));
                food.setIngredientFood(cursor.getString(cursor.getColumnIndexOrThrow("nguyen_lieu")));
                food.setTinh(cursor.getString(cursor.getColumnIndexOrThrow("tinh")));
                food.setImgFood(cursor.getBlob(cursor.getColumnIndexOrThrow("hinh_anh")));
                resultfood.add(food);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return resultfood;
    }

    public ArrayList<Food> getAllFood() {
        SQLiteDatabase db = getReadableDatabase();
        String q;
        MainActivity2 mainActivity2 = new MainActivity2();
        if (mainActivity2.selected == 0) {
            q = "SELECT * FROM listfood2 order by ma_mon asc";
        } else {
            q = "SELECT lf.ma_mon,lf.ten_mon,lf.tinh,lf.gioi_thieu,lf.nguyen_lieu,lf.cach_lam,lf2.hinh_anh " +
                    "FROM listfood as lf join listfood2 as lf2 where lf.ma_mon = lf2.ma_mon order by lf.ma_mon asc";
        }

        Cursor cursor = db.rawQuery(q, null);

        ArrayList<Food> resultfood = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Food food = new Food();
                food.setNameFood(cursor.getString(cursor.getColumnIndexOrThrow("ten_mon")));
                food.setRecipesFood(cursor.getString(cursor.getColumnIndexOrThrow("cach_lam")));
                food.setIntroductionFood(cursor.getString(cursor.getColumnIndexOrThrow("gioi_thieu")));
                food.setIngredientFood(cursor.getString(cursor.getColumnIndexOrThrow("nguyen_lieu")));
                food.setTinh(cursor.getString(cursor.getColumnIndexOrThrow("tinh")));
                food.setImgFood(cursor.getBlob(cursor.getColumnIndexOrThrow("hinh_anh")));
                resultfood.add(food);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return resultfood;
    }

}
