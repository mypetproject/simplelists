package com.example.shoplist2;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

/*class ListDataRepository {

    private ListDataDao mListDataDao;
    private LiveData<List<ListData>> mAllLists;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    ListDataRepository(Application application) {
        ListDataDatabase db = ListDataDatabase.getDatabase(application);
        mListDataDao = db.listDataDao();
        mAllLists = mListDataDao.getAlphabetizedLists();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<ListData>> getAllWords() {
        return mAllLists;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    void insert(final ListData listData) {
        ListDataDatabase.databaseWriteExecutor.execute(() -> {
            mListDataDao.insert(listData);
        });
    }

}*/
