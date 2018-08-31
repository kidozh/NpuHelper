package com.kidozh.npuhelper.campusAddressBook;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(indices = {@Index(value = {"name"},unique = true)})
public class campusAddressBookInfoEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    public String name;
    public String phoneNumber;
    public String category;

    public campusAddressBookInfoEntity(String name,String phoneNumber,String category) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
