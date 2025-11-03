package com.example.vortex_events;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DbViewModel extends ViewModel {

    private final MutableLiveData<DatabaseWorker> dbWorker;
    public LiveData<DatabaseWorker> getDbWorker() {
        return dbWorker;
    }
    public DbViewModel(DatabaseWorker databaseWorker) {
        dbWorker = new MutableLiveData<>();
        dbWorker.setValue(databaseWorker);

    }


}
