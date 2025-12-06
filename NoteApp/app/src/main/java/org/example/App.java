package org.example;

import org.example.lib.Note;
import org.example.lib.NoteManager;
import java.util.Scanner;
import java.io.IOException;
import java.io.File;

public class App {
    public static void main(String[] args) {
        NoteManager manager = new NoteManager();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Note App ---");
            System.out.println("1. Add Note");
            System.out.println("2. View Notes");
            System.out.println("3. Delete Note");
            System.out.println("4. Open/Edit Note from file (Replace old & rename)");
            System.out.println("5. Exit");
            System.out.print("Select: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1": // Add new note
                    System.out.print("Enter note text: ");
                    String text = scanner.nextLine();
                    System.out.print("Enter file name to save (e.g., note1.txt): ");
                    String fileName = scanner.nextLine();

                    // Check for existing note with same file name
                    Note existingNote = null;
                    for (Note n : manager.getNotes()) {
                        if (n.getFilePath().equals(fileName)) {
                            existingNote = n;
                            break;
                        }
                    }

                    if (existingNote != null) {
                        // Overwrite the old note
                        existingNote.setText(text);
                        try {
                            manager.saveNoteToFile(existingNote);
                            System.out.println("Note updated and saved to " + fileName);
                        } catch (IOException e) {
                            System.out.println("Failed to save note: " + e.getMessage());
                        }
                    } else {
                        // Add new note
                        Note note = new Note(text, fileName);
                        manager.addNote(note);
                        try {
                            manager.saveNoteToFile(note);
                            System.out.println("Note added and saved to " + fileName);
                        } catch (IOException e) {
                            System.out.println("Failed to save note: " + e.getMessage());
                        }
                    }
                    break;

                case "2": // View notes
                    System.out.println("\nYour notes:");
                    for (int i = 0; i < manager.getNotes().size(); i++) {
                        Note n = manager.getNotes().get(i);
                        System.out.println(i + ": " + n.getText() + " (File: " + n.getFilePath() + ")");
                    }
                    break;

                case "3": // Delete note
                    System.out.print("Enter index to delete: ");
                    int idx = Integer.parseInt(scanner.nextLine());
                    manager.deleteNote(idx);
                    System.out.println("Deleted.");
                    break;

                case "4": // Open/Edit note from file
                    System.out.print("Enter file name to open: ");
                    String path = scanner.nextLine();

                    // Check if note exists in memory
                    Note loadedNote = null;
                    for (Note n : manager.getNotes()) {
                        if (n.getFilePath().equals(path)) {
                            loadedNote = n;
                            break;
                        }
                    }

                    try {
                        if (loadedNote == null) {
                            loadedNote = manager.loadNoteFromFile(path);
                        }

                        System.out.println("Current content:\n" + loadedNote.getText());
                        System.out.println("Enter new content (Replaces old content; leave blank to keep unchanged):");
                        String updated = scanner.nextLine();
                        if (!updated.isBlank()) {
                            loadedNote.setText(updated);
                        }

                        System.out.println("Current file name: " + loadedNote.getFilePath());
                        System.out.print("Enter new file name to rename (leave blank to keep unchanged): ");
                        String newFileName = scanner.nextLine();

                        if (!newFileName.isBlank() && !newFileName.equals(loadedNote.getFilePath())) {
                            // Rename file on disk
                            File oldFile = new File(loadedNote.getFilePath());
                            File newFile = new File(newFileName);
                            if (oldFile.exists()) {
                                if (!oldFile.renameTo(newFile)) {
                                    System.out.println("Failed to rename file. Keeping original name.");
                                } else {
                                    loadedNote.setFilePath(newFileName);
                                }
                            } else {
                                loadedNote.setFilePath(newFileName);
                            }
                        }

                        // Save updated content
                        manager.saveNoteToFile(loadedNote);
                        System.out.println("Note updated and saved.");
                    } catch (IOException e) {
                        System.out.println("Error reading/writing file: " + e.getMessage());
                    }
                    break;

                case "5":
                    System.out.println("Notes Closed");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
