package org.example.service;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;
import org.example.model.Note;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class MongoService {
    private final MongoCollection<Note> noteCollection;

    public MongoService() {
        MongoClient client = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase db = client.getDatabase("notes_db");
        CodecRegistry pojoCodecRegistry = org.bson.codecs.configuration.CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                org.bson.codecs.configuration.CodecRegistries.fromProviders(
                        org.bson.codecs.pojo.PojoCodecProvider.builder().automatic(true).build()
                )
        );
        this.noteCollection = db.getCollection("notes", Note.class).withCodecRegistry(pojoCodecRegistry);
    }

    public void addNote(Note note) {
        Document doc = new Document();
        doc.put("title", note.getTitle());
        doc.put("content", note.getContent());
        doc.put("exported", note.isExported());
        noteCollection.insertOne(note);
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        noteCollection.find().iterator().forEachRemaining(notes::add);
        return notes;
    }

    public void deleteNoteById(ObjectId id) {
        noteCollection.deleteOne(eq("_id", id));
    }

    public void updateNote(Note note) {
        noteCollection.replaceOne(eq("_id", note.getId()), note);
    }

    public void markNoteAsExported(ObjectId id) {
        Note note = noteCollection.find(eq("_id", id)).first();
        if (note != null) {
            note.setExported(true);
            updateNote(note);
        }
    }
}
