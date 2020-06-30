package com.example.android.bakingapp;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import timber.log.Timber;

public class RecipeWidgetProvider extends AppWidgetProvider {

    private static final String TAG = "WidgetProvider";

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        if (BuildConfig.DEBUG) {
            if (Timber.treeCount() == 0) {
                Timber.plant(new Timber.DebugTree());
            }
        }

        Timber.tag(TAG).d("updateAppWidget: starts");

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_grid_view);

        Intent intent = new Intent(context, RecipeGridWidgetService.class);
        views.setRemoteAdapter(R.id.widget_grid_view, intent);
        Timber.tag(TAG).d("updateAppWidget: setRemoteAdapter() done");

        Intent appIntent = new Intent(context, RecipeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_grid_view, pendingIntent);

        views.setEmptyView(R.id.widget_grid_view, R.id.empty_view);

        appWidgetManager.updateAppWidget(appWidgetId, views);

        Timber.tag(TAG).d("updateAppWidget: updateAppWidget() done");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Timber.tag(TAG).d("onUpdate: starts");
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

