package com.kidozh.npuhelper.campusBuildingLoc;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(indices = {@Index(value = {"name"},unique = true)})
public class campusBuildingInfoEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    public String name;

    public String imgUrl;
    public String description;
    public String location;
    public String campus;
    public Date updateAt;

    public campusBuildingInfoEntity(String name,String imgUrl,String description,String location,String campus){
        this.name = name;
        this.imgUrl = imgUrl;
        this.description = description;
        this.location = location;
        this.campus = campus;
        this.updateAt = new Date();

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
