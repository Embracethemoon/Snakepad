import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileOperation {
    private final Notepad notepad;
    private boolean saved;
    private boolean newFileFlag;
    private String fileName;
    private final String applicationTitle = "SnakePad Beta";

    File fileRef;
    JFileChooser chooser;

    /**
     * Constructor
     * @param notepad - notepad for reading user input.
     */
    public FileOperation(Notepad notepad) {
        this.notepad = notepad;
        newFileFlag = true;
        fileName = "Untitled";
        fileRef = new File(fileName);
        this.notepad.frame.setTitle(fileName + " - " + applicationTitle);

        chooser = new JFileChooser();
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("Text Files(*.txt)", "txt"));
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("Java Files(*.java)", "java"));
        chooser.setCurrentDirectory(new File("."));
    }

    /**
     * Getter Method for boolean attribute 'saved'.
     * @return - boolean value indicating if the file is saved.
     */
    public boolean isSaved() {
        return saved;
    }

    /**
     * Setter Method for boolean attribute 'saved'.
     * @param saved - new boolean value indicating if the file is saved.
     */
    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    /**
     * Getter Method for String attribute 'fileName'.
     * @return - String 'fileName' attribute for current file.
     */
    public String getFileName(){
        return fileName;
    }

    /**
     * Setter Method for String attribute 'fileName'.
     * @param fileName - new fileName for this file.
     */
    void setFileName(String fileName){
        this.fileName = fileName;
    }

    /**
     * Read the text in textArea and save them to a specified file.
     * Returns a value indicating a successful operation.
     * @param temp - File to save
     * @return - a boolean value to indicate success in saving the file
     */
    public boolean saveFile(File temp) {
        FileWriter fout = null;
        try {
            fout = new FileWriter(temp);
            fout.write(notepad.textArea.getText());
        } catch (IOException e) {
            updateStatus(temp, false);
            return false;
        }
        finally {
            try {
                if(fout != null)
                    fout.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        updateStatus(temp, true);
        return true;
    }

    private void updateStatus(File temp, boolean saved) {
        if(saved){
            this.saved = true;
            fileName = temp.getName();
            if(!temp.canWrite()){
                fileName += "(Read only)";
                newFileFlag = true;
            }
            fileRef = temp;
            notepad.frame.setTitle(fileName + " - " + applicationTitle);
            notepad.statusBar.setText("File : " + temp.getPath() + " saved/opened successfully");
            newFileFlag = false;
        }
        else{
            notepad.statusBar.setText("Failed to save/open : " + temp.getPath());
        }
    }

    /**
     * Save the file in different ways depending on if the file is a new file or not.
     * @return - boolean value indicating if the saving operation is successful.
     */
    public boolean saveThisFile(){
        if(!newFileFlag)
            return saveFile(fileRef);
        return saveAsFile();
    }

    public boolean saveAsFile(){
        File temp;
        chooser.setDialogTitle("Save As...");
        chooser.setApproveButtonText("Save Now");
        chooser.setApproveButtonMnemonic(KeyEvent.VK_S);
        chooser.setApproveButtonToolTipText("Click me to save!");

        do {
           if(chooser.showSaveDialog(this.notepad.frame) != JFileChooser.APPROVE_OPTION)
               return false;
           temp = chooser.getSelectedFile();
           if(!temp.exists())
               break;
           if(JOptionPane.showConfirmDialog(this.notepad.frame, "<html>" + temp.getPath() + " already exists.<br>Do you want to replace it?<html>",
                   "Save As", JOptionPane.YES_NO_CANCEL_OPTION) == JOptionPane.YES_OPTION)
            break;
        }while(true);

        return saveFile(temp);
    }

    public boolean openFile(File temp){
        FileInputStream input = null;
        BufferedReader reader = null;

        try {
            input = new FileInputStream(temp);
            reader = new BufferedReader(new InputStreamReader(input));
            String str;
            while(true){
                str = reader.readLine();
                if(str == null)
                    break;
                this.notepad.textArea.append(str + "\n");
            }

        } catch(IOException ioe){
            updateStatus(temp, false);
            return false;
        }
        finally {
            try {
                if (reader != null) reader.close();
                if(input != null) input.close();

            } catch (IOException excp) {
                excp.printStackTrace();
            }
        }
        updateStatus(temp, true);
        this.notepad.textArea.setCaretPosition(0);
        return true;
    }

    public void openFile(){
        if(!confirmSave())
            return;
        chooser.setDialogTitle("Open File...");
        chooser.setApproveButtonText("Open this");
        chooser.setApproveButtonMnemonic(KeyEvent.VK_O);
        chooser.setApproveButtonToolTipText("Click me to open the selected file.");

        File temp;
        do {
            if(chooser.showOpenDialog(this.notepad.frame) != JFileChooser.APPROVE_OPTION)
                return;
            temp = chooser.getSelectedFile();
            if(temp.exists())
                break;
            JOptionPane.showMessageDialog(this.notepad.frame, "<html>" + temp.getName() + "<br>file not found.<br>" +
                    "Please verify the correct file name was given.<html>",
                    "Open", JOptionPane.INFORMATION_MESSAGE);
        }while(true);
        this.notepad.textArea.setText("");

        if(!openFile(temp)){
            fileName = "Untitled";
            saved = true;
            this.notepad.frame.setTitle(fileName + " - " + applicationTitle);
        }
        if(!temp.canWrite())
            newFileFlag = true;
    }

    public boolean confirmSave(){
        String strMsg = "<html>The text in the "+fileName+" file has been changed.<br>"+
                "Do you want to save the changes?<html>";
        if(!saved){
            int choice = JOptionPane.showConfirmDialog(this.notepad.frame, strMsg, applicationTitle, JOptionPane.YES_NO_CANCEL_OPTION);
            if(choice == JOptionPane.CANCEL_OPTION)
                return false;
            else return choice != JOptionPane.YES_OPTION || saveAsFile();
        }
        return true;
    }

    public void newFile(){
        if(!confirmSave())
            return;
        this.notepad.textArea.setText("");
        fileName = "Untitled";
        fileRef = new File(fileName);
        saved = true;
        newFileFlag = true;
        this.notepad.frame.setTitle(fileName + " - " + applicationTitle);
    }
}
