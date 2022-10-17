package com.wickrabb.notesly.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.wickrabb.notesly.R;
import com.wickrabb.notesly.adapters.NotesAdapter;
import com.wickrabb.notesly.database.NotesDatabase;
import com.wickrabb.notesly.entities.Note;
import com.wickrabb.notesly.listeners.NotesListener;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NotesListener {

    private RecyclerView notesReyclerView;
    private List<Note> noteList;
    private NotesAdapter notesAdapter;

    private final int SHOW_NOTES = 1;
    private final int ADD_NOTE = 2;
    private final int UPDATE_NOTE = 3;


    ActivityResultLauncher<Intent> createNote;
    ActivityResultLauncher<Intent> editNote;

    private int noteClickedPosition = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNote = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK){
                    getNotes(ADD_NOTE,false);
                }
            }
        });

        editNote = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK){
                   if(result.getData() != null) getNotes(UPDATE_NOTE,result.getData().getBooleanExtra("isNoteDeleted",false));
                }
            }
        });

                ImageView imageAddNoteMain = findViewById(R.id.imageAddNoteMain);
        imageAddNoteMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateNoteActivity.class);
                createNote.launch(intent);
            }
        });

        notesReyclerView = findViewById(R.id.notesRecyclerView);
        notesReyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        noteList = new ArrayList<>();
        notesAdapter = new NotesAdapter(noteList, this);
        notesReyclerView.setAdapter(notesAdapter);
        getNotes(SHOW_NOTES,false);

        EditText inputSearch = findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                 notesAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(noteList.size() != 0){
                    notesAdapter.searchNotes(editable.toString());
                }
            }
        });

    }

    @Override
    public void onNoteClicked(Note note, int position) {
        noteClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(),CreateNoteActivity.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("note",note);
        editNote.launch(intent);
    }

    private void getNotes(final int request, final boolean isNoteDeleted){
        @SuppressLint("StaticFieldLeak")
        class GetNotesTask extends AsyncTask<Void, Void, List<Note>>{

            @Override
            protected List<Note> doInBackground(Void... voids){
                return NotesDatabase.getNotesDatabase(getApplicationContext()).noteDao().getAllNotes();

            }

            @Override
            protected void onPostExecute(List<Note> notes){
                super.onPostExecute(notes);
               if(request == SHOW_NOTES){
                   noteList.addAll(notes);
                   notesAdapter.notifyDataSetChanged();
               }else if(request == ADD_NOTE){
                   noteList.add(0,notes.get(0));
                   notesAdapter.notifyItemInserted(0);
                   notesReyclerView.smoothScrollToPosition(0);
               }else if(request == UPDATE_NOTE){
                  noteList.remove(noteClickedPosition);
                  if(isNoteDeleted){
                      notesAdapter.notifyItemRemoved(noteClickedPosition);
                  }else{
                      noteList.add(noteClickedPosition,notes.get(noteClickedPosition));
                      notesAdapter.notifyItemChanged(noteClickedPosition);
                  }
               }
            }
        }
        new GetNotesTask().execute();
    }
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            getNotes();
        }
    }

 */
}