package org.example.model;

import org.bson.types.ObjectId;

public class Note {
    private ObjectId id;
    private String title;
    private String content;
    private boolean exported;

    public Note() {
        // Required for MongoDB codec/POJO
    }

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
        this.exported = false;
    }

    public Note(ObjectId id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.exported = false;
    }

    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public boolean isExported() { return exported; }
    public void setExported(boolean exported) { this.exported = exported; }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + (id != null ? id.toHexString() : "null") +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", exported=" + exported +
                '}';
    }
}
