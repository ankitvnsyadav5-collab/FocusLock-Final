package com.focuslock.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.focuslock.data.local.database.FocusLockDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "focuslock_prefs")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FocusLockDatabase =
        Room.databaseBuilder(context, FocusLockDatabase::class.java, "focuslock.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides @Singleton
    fun provideBlockedAppDao(db: FocusLockDatabase) = db.blockedAppDao()

    @Provides @Singleton
    fun provideBlockedWebsiteDao(db: FocusLockDatabase) = db.blockedWebsiteDao()

    @Provides @Singleton
    fun provideFocusSessionDao(db: FocusLockDatabase) = db.focusSessionDao()

    @Provides @Singleton
    fun provideScheduleDao(db: FocusLockDatabase) = db.scheduleDao()

    @Provides @Singleton
    fun provideAppUsageStatDao(db: FocusLockDatabase) = db.appUsageStatDao()

    @Provides @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.dataStore
}
