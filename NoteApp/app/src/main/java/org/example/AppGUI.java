package org.example;

import org.example.lib.Note;
import org.example.lib.NoteManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class AppGUI {
    private JFrame frame;
    private DefaultListModel<Note> listModel;
    private JList<Note> noteList;
    private NoteManager manager;

    public AppGUI() {
        manager = new NoteManager();
        frame = new JFrame("Note App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());

        // Note list
        listModel = new DefaultListModel<>();
        noteList = new JList<>(listModel);
        noteList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        noteList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Note note) {
                    setText(index + ": " + note.getText() + " (File: " + note.getFilePath() + ")");
                }
                return this;
            }
        });
        frame.add(new JScrollPane(noteList), BorderLayout.CENTER);

        // Buttons panel
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(1, 5));

        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton renameBtn = new JButton("Rename");
        JButton deleteBtn = new JButton("Delete");
        JButton refreshBtn = new JButton("Refresh");

        buttons.add(addBtn);
        buttons.add(editBtn);
        buttons.add(renameBtn);
        buttons.add(deleteBtn);
        buttons.add(refreshBtn);

        frame.add(buttons, BorderLayout.SOUTH);

        // Button actions
        addBtn.addActionListener(e -> addNote());
        editBtn.addActionListener(e -> editNote());
        renameBtn.addActionListener(e -> renameNote());
        deleteBtn.addActionListener(e -> deleteNote());
        refreshBtn.addActionListener(e -> refreshList());

        frame.setVisible(true);
    }

    private void addNote() {
        String text = JOptionPane.showInputDialog(frame, "Enter note text:");
        if (text == null || text.isBlank()) return;

        String filename = JOptionPane.showInputDialog(frame, "Enter file name to save (e.g., note1.txt):");
        if (filename == null || filename.isBlank()) return;

        // Check for duplicate file
        Note existing = manager.getNotes().stream()
                .filter(n -> n.getFilePath().equals(filename))
                .findFirst().orElse(null);

        try {
            if (existing != null) {
                existing.setText(text);
                manager.saveNoteToFile(existing);
            } else {
                Note note = new Note(text, filename);
                manager.addNote(note);
                manager.saveNoteToFile(note);
            }
            refreshList();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Failed to save note: " + ex.getMessage());
        }
    }

    private void editNote() {
        int idx = noteList.getSelectedIndex();
        if (idx == -1) {
            JOptionPane.showMessageDialog(frame, "Select a note to edit.");
            return;
        }

        Note note = manager.getNotes().get(idx);
        String newText = JOptionPane.showInputDialog(frame, "Edit note text:", note.getText());
        if (newText != null && !newText.isBlank()) {
            note.setText(newText);
            try {
                manager.saveNoteToFile(note);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Failed to save note: " + e.getMessage());
            }
            refreshList();
        }
    }

    private void renameNote() {
        int idx = noteList.getSelectedIndex();
        if (idx == -1) {
            JOptionPane.showMessageDialog(frame, "Select a note to rename.");
            return;
        }

        Note note = manager.getNotes().get(idx);
        String newFileName = JOptionPane.showInputDialog(frame, "Enter new file name:", note.getFilePath());
        if (newFileName != null && !newFileName.isBlank() && !newFileName.equals(note.getFilePath())) {
            File oldFile = new File(note.getFilePath());
            File newFile = new File(newFileName);
            if (oldFile.exists()) {
                if (!oldFile.renameTo(newFile)) {
                    JOptionPane.showMessageDialog(frame, "Failed to rename file.");
                    return;
                }
            }
            note.setFilePath(newFileName);
            try {
                manager.saveNoteToFile(note);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Failed to update file: " + e.getMessage());
            }
            refreshList();
        }
    }

    private void deleteNote() {
        int idx = noteList.getSelectedIndex();
        if (idx == -1) {
            JOptionPane.showMessageDialog(frame, "Select a note to delete.");
            return;
        }

        Note note = manager.getNotes().get(idx);
        int confirm = JOptionPane.showConfirmDialog(frame, "Delete this note?\n" + note.getText(),
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            File file = new File(note.getFilePath());
            if (file.exists() && !file.delete()) {
                JOptionPane.showMessageDialog(frame, "Failed to delete file.");
                return;
            }
            manager.deleteNote(idx);
            refreshList();
        }
    }

    private void refreshList() {
        listModel.clear();
        for (Note n : manager.getNotes()) {
            listModel.addElement(n);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AppGUI::new);
    }
}
