package com.example.android.bakingapp.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;

public class NetworkExecutor {

    private static final Object LOCK = new Object();
    private static NetworkExecutor sInstance;
    private final Executor networkIO;

    private NetworkExecutor(Executor networkIO) {
        this.networkIO = networkIO;
    }

    public static NetworkExecutor getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new NetworkExecutor(Executors.newFixedThreadPool(3));
            }
        }
        return sInstance;
    }

    public Executor networkIO() {
        return networkIO;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }

}
