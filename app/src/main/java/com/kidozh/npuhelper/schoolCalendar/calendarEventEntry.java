package com.kidozh.npuhelper.schoolCalendar;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


import java.util.Date;

@Entity(tableName = "calendarEvent")
public class calendarEventEntry {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String moduleResourceString;
    public String content;
    public Date publishAt;
    public Date updateAt;

    public calendarEventEntry(String moduleResourceString,String content,Date publishAt,Date updateAt){
        this.moduleResourceString = moduleResourceString;
        this.content = content;
        this.publishAt = publishAt;
        this.updateAt = updateAt;
    }

    public int getId(){
        return this.id;
    }
}
