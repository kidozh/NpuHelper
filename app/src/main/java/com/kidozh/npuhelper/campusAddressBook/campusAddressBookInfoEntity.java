package com.kidozh.npuhelper.campusAddressBook;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = {"phoneNumber"},unique = true)})
public class campusAddressBookInfoEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    public String name;
    public String phoneNumber;
    public String category;
    public String location;
    public String departmentLoc;
    public String job;

    public campusAddressBookInfoEntity(String name,String phoneNumber,String category,String location,String job, String departmentLoc) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.category = category;
        this.job = job;
        this.location = location;
        this.departmentLoc = departmentLoc;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
