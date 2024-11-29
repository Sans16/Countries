package com.sanusi.countriesapp.di

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import com.sanusi.countriesapp.data.AppDatabase
import com.sanusi.countriesapp.data.CountryRepositoryImpl
import com.sanusi.countriesapp.domain.CountryRepository
import com.sanusi.countriesapp.utils.Constants.BASE_URL
import com.sanusi.countriesapp.utils.Constants.COUNTRY_ENTITY_NAME
import com.sanusi.countriesapp.utils.Constants.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE $COUNTRY_ENTITY_NAME ADD COLUMN languages TEXT NOT NULL DEFAULT 'no language'")
        }
    }

    private val interceptor = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    private val client =  OkHttpClient
        .Builder()
        .addInterceptor(interceptor)
        .build()


    @Provides
    @Singleton
    fun provideApolloClient() : ApolloClient {
        return ApolloClient
            .Builder()
            .okHttpClient(client)
            .serverUrl(BASE_URL)
            .build()
    }

    @Provides
    @Singleton
    fun providesCountryDatabase(application : Application) : AppDatabase {
        return Room
            .databaseBuilder(application,
                AppDatabase::class.java,
                DATABASE_NAME)
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    @Singleton
    fun provideCountryRepository(apolloClient : ApolloClient,
                                 database : AppDatabase) : CountryRepository {
        return CountryRepositoryImpl(apolloClient,database.countryDao)
    }
}