package com.wickrabb.notesly.listeners;

import com.wickrabb.notesly.entities.Note;

public interface NotesListener {
    void onNoteClicked(Note note, int position);
}
