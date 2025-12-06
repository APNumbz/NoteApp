package org.example.lib;

public class Note {
    private String text;
    private String filePath;   // The filename

    public Note(String text, String filePath) {
        this.text = text;
        this.filePath = filePath;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return text;
    }
}
