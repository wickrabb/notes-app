package com.wickrabb.notesly.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.wickrabb.notesly.dao.NoteDao;
import com.wickrabb.notesly.entities.Note;

@Database(entities = Note.class,version=1,exportSchema = false)
public abstract class NotesDatabase extends RoomDatabase {

    public static NotesDatabase notesDatabase;
    public static synchronized NotesDatabase getNotesDatabase(Context context){
        if(notesDatabase == null){
            notesDatabase = Room.databaseBuilder(context,
                    NotesDatabase.class,
                    "notes_db").build();
        }
        return notesDatabase;
    }

    public abstract NoteDao noteDao();
}
