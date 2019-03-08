package com.kidozh.npuhelper.accountAuth;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = {"id"},unique = true)})
public class expensesRecordEntity implements Comparable<expensesRecordEntity>, Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    public float amount;
    public float balance;
    public String location;
    public Date payTime;
    @Ignore
    public expensesRecordEntity(int id, float amount, float balance, String location, Date payTime){
        this.id = id;
        this.amount = amount;
        this.balance = balance;
        this.location = location;
        this.payTime = payTime;
    }

    public expensesRecordEntity(String location, float amount, float balance, Date payTime){
        this.location = location;
        this.amount = amount;
        this.balance = balance;
        this.payTime = payTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPayTimeMonth(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd", Locale.getDefault());
        return dateFormat.format(this.payTime);
    }

    @Override
    public int compareTo(expensesRecordEntity o) {
        if(this.payTime.before(o.payTime)){
            return -1;
        }
        else if(this.payTime.after(o.payTime)){
            return  1;
        }
        else {
            return 0;
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeFloat(amount);
        dest.writeFloat(balance);
        dest.writeString(location);
        dest.writeLong(payTime.getTime());

    }

    public static final Parcelable.Creator<expensesRecordEntity> CREATOR = new Creator<expensesRecordEntity>() {
        @Override
        public expensesRecordEntity createFromParcel(Parcel source) {
            int id = source.readInt();
            float amount = source.readFloat();
            float balance = source.readFloat();
            String location = source.readString();
            Date payTime = new Date(source.readLong());

            expensesRecordEntity expensesRecordEntity = new expensesRecordEntity(id,amount,balance,location,payTime);
            return expensesRecordEntity;
        }

        @Override
        public expensesRecordEntity[] newArray(int size) {
            return new expensesRecordEntity[size];
        }
    };
}
