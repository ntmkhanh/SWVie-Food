package com.food.lite.nckh.detection.model;

import java.io.Serializable;

public class Food implements Serializable {
    public String idFood;
    public String nameFood;
    public String introductionFood,ingredientFood,recipesFood;
    public String tinh;
    public byte[] imgFood;

    public Food(String idFood, String nameFood, String introductionFood, String ingredientFood, String recipesFood, String tinh, byte[] imgFood) {
        this.idFood = idFood;
        this.nameFood = nameFood;
        this.introductionFood = introductionFood;
        this.ingredientFood = ingredientFood;
        this.recipesFood = recipesFood;
        this.tinh = tinh;
        this.imgFood = imgFood;
    }

    public Food() {
    }

    public byte[] getImgFood() {
        return imgFood;
    }

    public void setImgFood(byte[] imgFood) {
        this.imgFood = imgFood;
    }

    public String getTinh() {
        return tinh;
    }

    public void setTinh(String tinh) {
        this.tinh = tinh;
    }

    public String getIdFood() {
        return idFood;
    }

    public void setIdFood(String idFood) {
        this.idFood = idFood;
    }

    public String getNameFood() {
        return nameFood;
    }

    public void setNameFood(String nameFood) {
        this.nameFood = nameFood;
    }

    public String getIntroductionFood() {
        return introductionFood;
    }

    public void setIntroductionFood(String introductionFood) {
        this.introductionFood = introductionFood;
    }

    public String getIngredientFood() {
        return ingredientFood;
    }

    public void setIngredientFood(String ingredientFood) {
        this.ingredientFood = ingredientFood;
    }

    public String getRecipesFood() {
        return recipesFood;
    }

    public void setRecipesFood(String recipesFood) {
        this.recipesFood = recipesFood;
    }
}
