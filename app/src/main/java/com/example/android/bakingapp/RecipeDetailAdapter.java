package com.example.android.bakingapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.model.Ingredient;
import com.example.android.bakingapp.model.Recipe;
import com.example.android.bakingapp.model.Step;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecipeDetailAdapter extends RecyclerView.Adapter<RecipeDetailAdapter.RecipeDetailViewHolder> {

    private Recipe mRecipe;
    private boolean mSelectFirst;

    private final StepAdapterOnClickHandler mClickHandler;

    public interface StepAdapterOnClickHandler {
        void onClick(View view, int stepIndex);
        void onClick(View view, ArrayList<Ingredient> ingredients);
    }

    public RecipeDetailAdapter(StepAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public RecipeDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.step_item, parent, false);
        return new RecipeDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeDetailViewHolder holder, int position) {
        if (position == 0) {
            if (mSelectFirst) {
                holder.itemView.performClick();
                mSelectFirst = false;
            }
            holder.stepDivider.setVisibility(View.GONE);
            holder.stepNumber.setVisibility(View.GONE);
            holder.stepShortDescription.setText(R.string.recipe_ingredients_label);
        } else {
            holder.stepDivider.setVisibility(View.VISIBLE);
            Step step = mRecipe.getSteps().get(position - 1);
            if (step.getId() == 0) {
                holder.stepNumber.setVisibility(View.GONE);
            } else {
                holder.stepNumber.setVisibility(View.VISIBLE);
                holder.stepNumber.setText(String.valueOf(step.getId()));
            }
            holder.stepShortDescription.setText(step.getShortDescription());
        }
    }

    @Override
    public int getItemCount() {
        if ( (mRecipe == null) || (mRecipe.getSteps() == null) ) {
            return 0;
        }
        return mRecipe.getSteps().size() + 1;
    }

    public void setRecipe(Recipe recipe, boolean selectFisrt) {
        mRecipe = recipe;
        mSelectFirst = selectFisrt;
        notifyDataSetChanged();
    }

    public class RecipeDetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView stepNumber;
        final TextView stepShortDescription;
        final View stepDivider;

        public RecipeDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            stepNumber = itemView.findViewById(R.id.step_numer_tv);
            stepShortDescription = itemView.findViewById(R.id.step_short_description_tv);
            stepDivider = itemView.findViewById(R.id.step_divider);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position == 0) {
                mClickHandler.onClick(view, (ArrayList<Ingredient>) mRecipe.getIngredients());
            } else {
                mClickHandler.onClick(view, position - 1);
            }
        }
    }

}
