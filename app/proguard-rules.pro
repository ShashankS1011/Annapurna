# ProGuard / R8 Rules for Annapurna Production Builds

# Keep Room Entities and DAOs
-keep class com.example.annapurna.RecipeEntity { *; }
-keep interface com.example.annapurna.RecipeDao { *; }
-keep class * extends androidx.room.RoomDatabase

# Keep Gson Serialized Data Models
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }

# Keep Retrofit API Interface
-keep interface com.example.annapurna.data.remote.RecipeApiService { *; }

# Keep Media3 ExoPlayer Components
-keep class androidx.media3.** { *; }
