package com.dlmanage.dlmanage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.ArrayList;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.*;
import java.util.Objects;

public class FileManageController {

    File mainDirectory;
    File[] fileList;
    ArrayList<File> directoryList = new ArrayList<>();

    String audioPath = "C:\\Users\\caleb\\Downloads\\Audio";
    String mcPath = "C:\\Users\\caleb\\Downloads\\Minecraft";
    String zipPath = "C:\\Users\\caleb\\Downloads\\Zips";
    String picturePath = "C:\\Users\\caleb\\Downloads\\Pictures";
    String videoPath = "C:\\Users\\caleb\\Downloads\\Videos";
    String pdfPath = "C:\\Users\\caleb\\Downloads\\PDFs";

    @FXML
    private ListView<File> listView;
    public ListView<File> dListView;

    public Label nameLabel;
    public Label lastMod;
    public Label fileType;

    @FXML
    public MenuItem openButton;
    public MenuItem openDestButton;
    public Button moveButton;

    public void openFile(ActionEvent event) {
        DirectoryChooser fileChooser = new DirectoryChooser();
        mainDirectory = fileChooser.showDialog(null);
        loadDirectory();
    }

    public void loadDirectory() {
        if (mainDirectory != null) {
            fileList = mainDirectory.listFiles();
            for (File f : fileList) {
                listView.getItems().add(f.getAbsoluteFile());
            }
        } else {
            System.out.println("none");
        }
    }

    public void openDestFile(ActionEvent actionEvent) {
        DirectoryChooser fileChooser = new DirectoryChooser();
        File newDir = fileChooser.showDialog(null);

        if (newDir.exists()) {
            directoryList.add(newDir);
            dListView.getItems().clear();
            for (int i = 0; i < directoryList.size(); i++)
                dListView.getItems().add(directoryList.get(i));
        } else
            System.out.println("Error with file");
    }

    public void moveFiles(ActionEvent actionEvent) throws IOException {
        for (File file : fileList) {
            System.out.println(file.getAbsolutePath());
            String ext = getExtension(file.getName());
            System.out.println(ext);

            if (ext.equals("mp3") || ext.equals("wav") || ext.equals("ogg")) {
                moveFile(file.getAbsolutePath(), audioPath + "\\" + file.getName());
            }
            else if (ext.equals("jar")) {
                moveFile(file.getAbsolutePath(), mcPath + "\\" + file.getName());
            }
            else if (ext.equals("zip")) {
                moveFile(file.getAbsolutePath(), zipPath + "\\" + file.getName());
            }
            else if (ext.equals("png") || ext.equals("jpg") || ext.equals("gif") || ext.equals("jpeg")) {
                moveFile(file.getAbsolutePath(), picturePath + "\\" + file.getName());
            }
            else if (ext.equals("mov") || ext.equals("mp4")) {
                moveFile(file.getAbsolutePath(), videoPath + "\\" + file.getName());
            }
            else if (ext.equals("pdf")) {
                moveFile(file.getAbsolutePath(), pdfPath + "\\" + file.getName());
            }
            listView.getItems().clear();
            loadDirectory();
        }
    }

    public String getExtension(String filename) {
        String extension = "";
        int i = filename.lastIndexOf('.');
        if (i > 0) extension = filename.substring(i+1);
        return extension;
    }

    public void moveFile(String sourcePath, String targetPath) {
        try {
            Files.move(Paths.get(sourcePath), Paths.get(targetPath), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listSelected(MouseEvent fileEditEvent) {
        if (!listView.getItems().isEmpty()) {
            int index = listView.getSelectionModel().getSelectedIndex();
            nameLabel.setText(fileList[index].getName());
            lastMod.setText(String.valueOf(fileList[index].lastModified()));
            String ext = getExtension(fileList[index].getName());
            if (!ext.isEmpty())
                fileType.setText(getExtension(fileList[index].getName()));
            else fileType.setText("directory");
        }
    }
}