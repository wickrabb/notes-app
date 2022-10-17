package com.wickrabb.notesly.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.OnConflictStrategy;

import com.wickrabb.notesly.entities.Note;

import java.util.List;

@Dao
public interface NoteDao {

    @Query("SELECT*FROM notes ORDER BY id DESC")
    List<Note> getAllNotes();

    @Delete
    void deleteNote(Note note);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNote(Note note);
}
