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

    File mainDirectory; //File for the source directory
    File[] fileList; //List of all the files in the source directory.

    //Map of all destination files and their corresponding extensions.
    HashMap<File,String[]> destinationMap = new HashMap<>();

    @FXML
    private ListView<File> listView; //List view for source directory
    public ListView<File> dListView; //List view for destination directories.

    public Label nameLabel; //Label for source directory selected file names
    public Label lastMod; //Label for source directory selected file's last modification
    public Label fileType; //Label for source directory selected file types

    public Label nameLabelDest; //Label for destination selected directory file names
    public Label lastModDest; //Label for destination selected directory file's last modification

    public TextField destExtInput; //Text field for input of file extensions
    //Button to submit file extensions to be assigned to a destination directory in the destination map
    public Button destExtButton;
    public Text extensions; //Label to list selected destination file extensions

    @FXML
    public MenuItem openButton; //Menu button to open source directory
    public MenuItem openDestButton; //Menu button to open destination directory
    public Button moveButton; //Button to start the process of moving files

    /**
     * Opens directory chooser and loads file.
     */
    public void openFile(ActionEvent event) {
        DirectoryChooser fileChooser = new DirectoryChooser();
        mainDirectory = fileChooser.showDialog(null);
        loadDirectory();
    }

    /**
     * Loads all the files from the source directory into the list view.
     */
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

    /**
     * Opens directory chooser, and if directory is chosen, it loads the directory into the destination map.  If config
     * file is included, it will set the extensions of the directory to those in the config file.
     */
    public void openDestFile(ActionEvent actionEvent) {
        DirectoryChooser fileChooser = new DirectoryChooser();
        File newDir = fileChooser.showDialog(null);
        File[] files = newDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().equals("config.txt")) {
                    String[] args = checkConfig(file);
                    destinationMap.put(newDir, args);
                }
            }
        } else {
            destinationMap.put(newDir, null);
        }

        dListView.getItems().clear();
        destinationMap.forEach((k, v) -> dListView.getItems().add(k));
    }

    /**
     * Checks the first line of the config file and returns an array of all the extensions.
     * @param file File to be checked
     * @return String array of file extensions
     */
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

    /**
     * When the move button is pressed, all the files in the source directory will have their extensions checked
     * and compared to the destination map. Those files will then be moved if the file extension is to be moved.
     * Source directory list view is then refreshed.
     */
    public void moveButtonPressed(ActionEvent actionEvent) throws IOException {
        for (File file : fileList) {
            //System.out.println(file.getAbsolutePath());
            String ext = getExtension(file.getName());
            //System.out.println(ext);

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

            destinationMap.forEach((k, v) -> //k = file, v = extension array
            {
                if (k != null) {
                    for (int i = 0; i < v.length; i++) {
                        if (ext.equals(v[i])) { //compares the extensions of file to those of the destination's ext array
                            moveFile(file.getAbsolutePath(), k.getAbsolutePath() + "\\" + file.getName());
                        }
                    }
                }
            });

            listView.getItems().clear();
            loadDirectory();
        }
    }

    /**
     * Returns the file extension of a given file name.
     * @param filename String of file name
     * @return String of file extension
     */
    public String getExtension(String filename) {
        String extension = "";
        int i = filename.lastIndexOf('.');
        if (i > 0) extension = filename.substring(i+1);
        return extension;
    }

    /**
     * Moves a file from source to target.
     * @param sourcePath String of the source path
     * @param targetPath String of the target path
     */
    public void moveFile(String sourcePath, String targetPath) {
        try {
            Files.move(Paths.get(sourcePath), Paths.get(targetPath), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * When an item in the source directory list view is selected, the information for that item will be displayed
     * on the right.
     */
    public void listSelected(MouseEvent mouseEvent) {
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


    /**
     * When an item in the destination list view is selected, the information for that item will be displayed
     * on the right.
     */
    public void destListSelected(MouseEvent mouseEvent) {
        if (!dListView.getItems().isEmpty() && dListView.getSelectionModel().getSelectedIndex() != -1) {
            File selectedItem = dListView.getSelectionModel().getSelectedItem();
            nameLabelDest.setText(selectedItem.getName());
            lastModDest.setText(String.valueOf(selectedItem.lastModified()));
            extensions.setText(Arrays.toString(destinationMap.get(selectedItem)));
        }
    }


    /**
     * When the submit button is pressed, the contents inside the text box above the button, will be designated
     * to the selected directory in the destination list view.
     */
    public void destinationExtSubmit(ActionEvent actionEvent) {
        if (!dListView.getItems().isEmpty() && !listView.getItems().isEmpty()) {
            File selectedItem = dListView.getSelectionModel().getSelectedItem();
            String[] extInput = destExtInput.getText().split("\\s+");
            destinationMap.put(selectedItem, extInput);
        }
    }
}