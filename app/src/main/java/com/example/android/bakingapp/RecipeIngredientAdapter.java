package com.example.android.bakingapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.android.bakingapp.model.Ingredient;

import java.text.DecimalFormat;
import java.util.List;

class RecipeIngredientAdapter extends BaseAdapter {

    private final List<Ingredient> mIngredients;

    public RecipeIngredientAdapter(List<Ingredient> ingredients) {
        mIngredients = ingredients;
    }

    @Override
    public int getCount() {
        if (mIngredients == null) {
            return 0;
        }
        return mIngredients.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            view = inflater.inflate(R.layout.recipe_ingredient, null);
        }
        TextView quantityTextView = view.findViewById(R.id.quantity_tv);
        String quantity = new DecimalFormat("#.##").format(mIngredients.get(position).getQuantity());
        quantityTextView.setText(quantity);
        TextView measureTextView = view.findViewById(R.id.measure_tv);
        measureTextView.setText(mIngredients.get(position).getMeasure());
        TextView ingredientTextView = view.findViewById(R.id.ingredient_tv);
        ingredientTextView.setText(mIngredients.get(position).getDescription());
        return view;
    }
}
