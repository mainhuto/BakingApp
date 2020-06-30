package com.example.android.bakingapp.util;

import com.example.android.bakingapp.model.Ingredient;
import com.example.android.bakingapp.model.Recipe;
import com.example.android.bakingapp.model.Step;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class RecipeUtil {

    private static final String TAG = "RecipeUtil";

    private static final String JSON_FILE_NAME = "baking.json";
    private static final String ENCODING = "UTF-8";
    private static final String ID_KEY = "id";
    private static final String NAME_KEY = "name";
    private static final String STEPS_KEY = "steps";
    private static final String SHORT_DESCRIPTION_KEY = "shortDescription";
    private static final String DESCRIPTION_KEY = "description";
    private static final String VIDEO_URL_KEY = "videoURL";
    private static final String THUMBNAIL_URL_KEY = "thumbnailURL";

    private static final String INGREDIENTS_KEY = "ingredients";
    private static final String QUANTITY_KEY = "quantity";
    private static final String MEASURE_KEY = "measure";
    private static final String INGREDIENT_KEY = "ingredient";


    public static List<Recipe> createRecipeList(String jsonString) {

        if ( (jsonString == null) || (jsonString.isEmpty()) ) {
            return null;
        }

        List<Recipe> recipes = new ArrayList<>();

        try {
            JSONArray  recipesJSON = new JSONArray (jsonString);
            for (int index = 0; index < recipesJSON.length(); index++) {
                JSONObject jsonRecipe = recipesJSON.getJSONObject(index);
                int id = jsonRecipe.optInt(ID_KEY);
                String name = jsonRecipe.optString(NAME_KEY);
                Recipe recipe = new Recipe(id, name);
                JSONArray  stepsJSON = jsonRecipe.getJSONArray(STEPS_KEY);
                addRecipeSteps(recipe, stepsJSON);
                JSONArray  ingredientsJSON = jsonRecipe.getJSONArray(INGREDIENTS_KEY);
                addRecipeIngredients(recipe, ingredientsJSON);
                recipes.add(recipe);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return recipes;
    }

    public static Recipe getRecipeWithId(String jsonString, int recipeId) {

        if ( (jsonString == null) || (jsonString.isEmpty()) ) {
            return null;
        }

        Recipe recipe = null;

        try {
            JSONArray  recipesJSON = new JSONArray (jsonString);
            for (int index = 0; index < recipesJSON.length(); index++) {
                JSONObject jsonRecipe = recipesJSON.getJSONObject(index);
                int id = jsonRecipe.optInt(ID_KEY);
                if (id == recipeId) {
                    String name = jsonRecipe.optString(NAME_KEY);
                    recipe = new Recipe(id, name);
                    JSONArray  stepsJSON = jsonRecipe.getJSONArray(STEPS_KEY);
                    addRecipeSteps(recipe, stepsJSON);
                    JSONArray  ingredientsJSON = jsonRecipe.getJSONArray(INGREDIENTS_KEY);
                    addRecipeIngredients(recipe, ingredientsJSON);
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return recipe;
    }

    public static void addRecipeSteps(Recipe recipe, JSONArray  stepsJSON) {
        if (stepsJSON == null) {
            return;
        }
        try {
            for (int index = 0; index < stepsJSON.length(); index++) {
                JSONObject jsonStep = stepsJSON.getJSONObject(index);
                int id = jsonStep.optInt(ID_KEY);
                String shortDescription = jsonStep.optString(SHORT_DESCRIPTION_KEY);
                String description = jsonStep.optString(DESCRIPTION_KEY);
                String videoURL = jsonStep.optString(VIDEO_URL_KEY);
                String thumbnailURL = jsonStep.optString(THUMBNAIL_URL_KEY);
                recipe.addStep(new Step(id, shortDescription, description, videoURL, thumbnailURL));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void addRecipeIngredients(Recipe recipe, JSONArray  ingredientsJSON) {
        if (ingredientsJSON == null) {
            return;
        }
        try {
            for (int index = 0; index < ingredientsJSON.length(); index++) {
                JSONObject jsonIngredient = ingredientsJSON.getJSONObject(index);
                double quantity = jsonIngredient.optDouble(QUANTITY_KEY);
                String measure = jsonIngredient.optString(MEASURE_KEY);
                String description = jsonIngredient.optString(INGREDIENT_KEY);
                String videoURL = jsonIngredient.optString(VIDEO_URL_KEY);
                String thumbnailURL = jsonIngredient.optString(THUMBNAIL_URL_KEY);
                recipe.addIngredient(new Ingredient(description, quantity, measure));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        Timber.tag("JSONLoad").d("getResponseFromHttpUrl: starts");

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        String result = "";
        try {
            int code = urlConnection.getResponseCode();

            Timber.tag("JSONLoad").d("getResponseFromHttpUrl: connection open");
            urlConnection.setConnectTimeout(5000);

            if (code == 200) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                Timber.tag("JSONLoad").d("getResponseFromHttpUrl: getInputStream() done");
                if (in != null) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                    String line = "";

                    while ((line = bufferedReader.readLine()) != null)
                        result += line;
                }
                in.close();
            }
        } catch (java.net.SocketTimeoutException e) {
            return null;
        } catch (java.io.IOException e) {
            return null;
        } finally {
            urlConnection.disconnect();
        }
        return result;
    }

}
