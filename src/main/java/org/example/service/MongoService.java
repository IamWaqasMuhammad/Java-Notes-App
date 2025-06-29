package org.example.service;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.example.model.Note;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoService {

    private final MongoClient client;
    private final MongoDatabase database;
    private final MongoCollection<Note> noteCollection;

    public MongoService() {
        // Register PojoCodecProvider
        CodecRegistry pojoCodecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(org.bson.codecs.pojo.PojoCodecProvider.builder().automatic(true).build())
        );

        // Create client with codec
        MongoClientSettings settings = MongoClientSettings.builder()
                .codecRegistry(pojoCodecRegistry)
                .build();

        client = MongoClients.create(settings);
        database = client.getDatabase("notesdb").withCodecRegistry(pojoCodecRegistry);
        noteCollection = database.getCollection("notes", Note.class);
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        noteCollection.find().forEach(notes::add);
        return notes;
    }

    public void addNote(Note note) {
        noteCollection.insertOne(note);
    }

    public void updateNote(Note note) {
        noteCollection.replaceOne(eq("_id", note.getId()), note);
    }

    public void deleteNoteById(org.bson.types.ObjectId id) {
        noteCollection.deleteOne(eq("_id", id));
    }

    public void close() {
        client.close();
    }
}
