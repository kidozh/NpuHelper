package com.kidozh.npuhelper.campusAddressBook;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;
@Dao
public interface campusAddressBookInfoDao {
    @Query("SELECT * FROM campusAddressBookInfoEntity")
    List<campusAddressBookInfoEntity> loadAll();

    @Query("SELECT * FROM campusAddressBookInfoEntity WHERE id = :id")
    campusAddressBookInfoEntity getCampusAddressBookInfoById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertInfo(campusAddressBookInfoEntity mCampusAddressBookInfoEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertInfo(List<campusAddressBookInfoEntity> mCampusAddressBookInfoEntities);

    @Delete
    void deleteInfo(campusAddressBookInfoEntity mCampusAddressBookInfoEntity);

    @Query("SELECT * FROM campusAddressBookInfoEntity WHERE name LIKE '%%' || :searchText || '%%' OR category LIKE '%%' || :searchText || '%%' OR phoneNumber LIKE '%%' || :searchText || '%%' OR location LIKE '%%' || :searchText || '%%' OR departmentLoc LIKE '%%' || :searchText || '%%' OR job LIKE '%%' || :searchText || '%%'")
    List<campusAddressBookInfoEntity> getAllRelatedCampusAddressBookInfo(String searchText);

    @Query("SELECT * FROM campusAddressBookInfoEntity")
    LiveData<List<campusAddressBookInfoEntity>> getAllCampusAddressBookInfo();

    @Query("SELECT * FROM campusAddressBookInfoEntity")
    List<campusAddressBookInfoEntity> queryAllCampusAddressBookInfo();
}
