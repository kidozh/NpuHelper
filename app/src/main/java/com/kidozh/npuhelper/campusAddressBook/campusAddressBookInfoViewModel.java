package com.kidozh.npuhelper.campusAddressBook;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

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
