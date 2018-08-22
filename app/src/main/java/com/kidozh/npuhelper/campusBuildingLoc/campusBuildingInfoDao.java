package com.kidozh.npuhelper.campusBuildingLoc;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface campusBuildingInfoDao {
    @Query("SELECT * FROM campusBuildingInfoEntity")
    List<campusBuildingInfoEntity> getAll();

    @Query("SELECT * FROM campusBuildingInfoEntity WHERE id = :id")
    campusBuildingInfoEntity getCampusBuildingInfoById(int id);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertInfos(List<campusBuildingInfoEntity> campusBuildingInfoEntities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertInfo(campusBuildingInfoEntity mCampusBuildingInfoEntity);

    @Delete
    void deleteInfo(campusBuildingInfoEntity mCampusBuildingInfoEntity);

    @Query("SELECT * FROM campusBuildingInfoEntity WHERE name LIKE '%%' || :searchText || '%%' OR description LIKE '%%' || :searchText || '%%'")
    List<campusBuildingInfoEntity> getRelatedCampusBuildingInfo(String searchText);
}
