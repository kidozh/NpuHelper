package com.kidozh.npuhelper.accountAuth;

import java.util.Date;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
@Dao
public interface expensesRecordDao {
    @Query("SELECT * FROM expensesRecordEntity")
    List<expensesRecordEntity> getAllExpenseRecords();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertExpensesRecords(List<expensesRecordEntity> expensesRecordEntities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertExpensesRecord(expensesRecordEntity expensesRecordEntity);

    @Query("SELECT * FROM expensesRecordEntity WHERE payTime BETWEEN :startTime AND :endTime")
    List<expensesRecordEntity> queryRecordBetween(Date startTime, Date endTime);
}
