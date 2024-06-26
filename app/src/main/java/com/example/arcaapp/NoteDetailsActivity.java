package com.example.arcaapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NoteDetailsActivity extends AppCompatActivity {

    EditText titleEditText, contentEditText;
    ImageButton saveNoteBtn;
    TextView pageTitleTextView;
    String title,content,docId;
    boolean isEditMode = false;
    TextView deleteNoteTextViewBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_note_details);
        titleEditText = findViewById(R.id.notes_title_text);
        contentEditText = findViewById(R.id.notes_content_text);
        saveNoteBtn = findViewById(R.id.save_note_btn);
        pageTitleTextView = findViewById(R.id.page_tittle);
        deleteNoteTextViewBtn = findViewById(R.id.delete_note_text_view_btn);

        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        if (docId!=null && !docId.isEmpty()){
           isEditMode = true;
        }

        titleEditText.setText(title);
        contentEditText.setText(content);
        if (isEditMode){
            pageTitleTextView.setText("Edita tu nota");
            deleteNoteTextViewBtn.setVisibility(View.VISIBLE);
        }

        saveNoteBtn.setOnClickListener( (v)-> saveNote());

        deleteNoteTextViewBtn.setOnClickListener((v)-> deleteNoteFromFirebase());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    void saveNote(){
        String noteTitle = titleEditText.getText().toString();
        String noteContent = contentEditText.getText().toString();
        if (noteTitle==null || noteTitle.isEmpty()){
            titleEditText.setError("Se requiere titulo");
            return;
        }
        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());

        saveNoteToFirebase(note);
    }
    void saveNoteToFirebase(Note note){
        DocumentReference documentReference;
        if (isEditMode){
            //actualizar nota
            documentReference = Utility.getColletionReferenceForNotes().document(docId);
        }else{
            //crear nueva nota
            documentReference = Utility.getColletionReferenceForNotes().document();
        }

        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Utility.showToast(NoteDetailsActivity.this, "Nota agregada con exito!");
                    finish();
                }else{
                    Utility.showToast(NoteDetailsActivity.this, "No se pudo agregar tu nota!");
                }
            }
        });
    }

    void deleteNoteFromFirebase(){
        DocumentReference documentReference;
        documentReference = Utility.getColletionReferenceForNotes().document(docId);
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    //nota borrada
                    Utility.showToast(NoteDetailsActivity.this, "Nota eliminada con exito!");
                    finish();
                }else{
                    Utility.showToast(NoteDetailsActivity.this, "No se pudo eliminar tu nota. Volve a intentar!");
                }
            }
        });

    }
}