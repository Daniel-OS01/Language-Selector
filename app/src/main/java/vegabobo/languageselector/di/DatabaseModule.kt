package vegabobo.languageselector.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import vegabobo.languageselector.dao.AppInfoDao
import vegabobo.languageselector.dao.AppInfoDb
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppInfoDb(app: Application): AppInfoDb {
        return Room.databaseBuilder(
            app,
            AppInfoDb::class.java,
            "app-info-db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideAppInfoDao(db: AppInfoDb): AppInfoDao {
        return db.appInfoDao()
    }
}