package org.example.model;

import org.bson.types.ObjectId;

public class Note {
    private ObjectId id;  // MongoDB document _id
    private String title;
    private String content;

    public Note() {
        // Default constructor needed for MongoDB codec/POJO mapping
    }

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Note(ObjectId id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + (id != null ? id.toHexString() : "null") +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
