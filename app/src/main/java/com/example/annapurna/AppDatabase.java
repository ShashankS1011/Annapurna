package com.example.annapurna;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// Incremented version from 1 to 2 to trigger migration for new fields
@Database(entities = {RecipeEntity.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract RecipeDao recipeDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "annapurna_database"
                            )
                            // Rebuilds database cleanly when entity columns change
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}