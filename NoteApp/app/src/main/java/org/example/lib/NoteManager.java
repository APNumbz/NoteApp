package org.example.lib;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class NoteManager {

    private final String NOTE_DIR = "notes";
    private List<Note> notes = new ArrayList<>();

    public NoteManager() {
        File folder = new File(NOTE_DIR);
        if (!folder.exists()) {
            folder.mkdir();
        }
        loadAllNotes();
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void addNote(Note note) {
        notes.add(note);
    }

    public void deleteNote(int index) {
        if (index >= 0 && index < notes.size()) {
            notes.remove(index);
        }
    }

    /** Save note content to its file */
    public void saveNoteToFile(Note note) throws IOException {
        File file = new File(NOTE_DIR + "/" + note.getFilePath());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(note.getText());
        }
    }

    /** Load a single note file into a Note object */
    public Note loadNoteFromFile(String filename) throws IOException {
        File file = new File(NOTE_DIR + "/" + filename);
        if (!file.exists()) return null;

        String content = Files.readString(file.toPath());
        return new Note(content, filename);
    }

    /** Load all notes from notes/ folder */
    private void loadAllNotes() {
        notes.clear();
        File folder = new File(NOTE_DIR);
        File[] files = folder.listFiles();

        if (files == null) return;

        for (File file : files) {
            try {
                String content = Files.readString(file.toPath());
                notes.add(new Note(content, file.getName()));
            } catch (IOException e) {
                System.err.println("Failed to load: " + file.getName());
            }
        }
    }
}
