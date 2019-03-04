package com.kidozh.npuhelper.campusAddressBook;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class campusAddressBookInfoViewModel extends AndroidViewModel {
    private LiveData<List<campusAddressBookInfoEntity>> campusAddressBookInfoEntityFullListLiveData;

    public campusAddressBookInfoViewModel(@NonNull Application application){
        super(application);
        campusAddressBookInfoDatabase database = campusAddressBookInfoDatabase.getsInstance(this.getApplication());
        // cache objects
        campusAddressBookInfoEntityFullListLiveData = database.campusAddressBookInfoDao().getAllCampusAddressBookInfo();
    }

    public LiveData<List<campusAddressBookInfoEntity>> getCampusAddressBookInfoEntityFullListLiveData() {
        return campusAddressBookInfoEntityFullListLiveData;
    }
}
