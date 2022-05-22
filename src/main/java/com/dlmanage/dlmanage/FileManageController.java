package com.dlmanage.dlmanage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.*;

public class FileManageController {

    File mainDirectory;
    File[] fileList;
    //ArrayList<File> directoryList = new ArrayList<>();
    HashMap<File,String[]> directoryList = new HashMap<>();

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

    public Label nameLabelDest;
    public Label lastModDest;
    public TextField destExtInput;
    public Button destExtButton;
    public Text extensions;

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
            if (fileList != null) {
                for (File f : fileList) {
                    listView.getItems().add(f.getAbsoluteFile());
                }
            }
        } else {
            System.out.println("none");
        }
    }

    public void openDestFile(ActionEvent actionEvent) {
        DirectoryChooser fileChooser = new DirectoryChooser();
        File newDir = fileChooser.showDialog(null);
        File[] files = newDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().equals("config.txt")) {
                    String[] args = checkConfig(file);
                    directoryList.put(newDir, args);
                }
            }
        } else {
            directoryList.put(newDir, null);
        }

        dListView.getItems().clear();
        directoryList.forEach((k, v) -> dListView.getItems().add(k));
    }

    public String[] checkConfig(File file) {
        String[] args = {};
        try {
            Scanner myReader = new Scanner(file);
            while (myReader.hasNextLine())
                args = myReader.nextLine().split("\\s+");
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return args;
    }

    public void moveFiles(ActionEvent actionEvent) throws IOException {
        for (File file : fileList) {
            System.out.println(file.getAbsolutePath());
            String ext = getExtension(file.getName());
            System.out.println(ext);

            /*
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
            */

            directoryList.forEach((k, v) ->
            {
                if (k != null) {
                    for (int i = 0; i < v.length; i++) {
                        if (ext.equals(v[i])) {
                            moveFile(file.getAbsolutePath(), k.getAbsolutePath() + "\\" + file.getName());
                        }
                    }
                }
            });

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
        if (!listView.getItems().isEmpty() && listView.getSelectionModel().getSelectedIndex() != -1) {
            int index = listView.getSelectionModel().getSelectedIndex();
            nameLabel.setText(fileList[index].getName());
            lastMod.setText(String.valueOf(fileList[index].lastModified()));
            String ext = getExtension(fileList[index].getName());
            if (!ext.isEmpty())
                fileType.setText(getExtension(fileList[index].getName()));
            else fileType.setText("directory");
        }
    }

    public void destListSelected(MouseEvent mouseEvent) {
        if (!dListView.getItems().isEmpty() && dListView.getSelectionModel().getSelectedIndex() != -1) {
            File selectedItem = dListView.getSelectionModel().getSelectedItem();
            nameLabelDest.setText(selectedItem.getName());
            lastModDest.setText(String.valueOf(selectedItem.lastModified()));
            extensions.setText(Arrays.toString(directoryList.get(selectedItem)));
        }
    }

    public void destinationExtSubmit(ActionEvent actionEvent) {
        if (!dListView.getItems().isEmpty() && !listView.getItems().isEmpty()) {
            File selectedItem = dListView.getSelectionModel().getSelectedItem();
            String[] extInput = destExtInput.getText().split("\\s+");
            directoryList.put(selectedItem, extInput);
        }
    }
}