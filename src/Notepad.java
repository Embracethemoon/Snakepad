import java.util.Date;
import java.awt.event.*;
import java.awt.*;
import javax.swing.event.*;
import javax.swing.*;

public class Notepad implements ActionListener, MenuConstants {

    JFrame frame;
    JTextArea textArea;
    JLabel statusBar;

    /*
    private String searchString, replaceString;
    private int lastSearchIndex;
    */

    FileOperation fileHandler;
    //    FindDialog findReplaceDialog = null;
    JColorChooser backColorChooser = null;
    JColorChooser foreColorChooser = null;
    JDialog backgroundDialog = null;
    JDialog foregroundDialog = null;
    JMenuItem cutItem, copyItem, deleteItem, findItem, findNextItem, replaceItem, gotoItem, selectAllItem;

    /**
    Constructor, initializes attributes for notepad and executes it.
     */
    public Notepad() {
        frame = new JFrame();
        textArea = new JTextArea(30, 60);
        textArea.setFont(new Font("Courier New", Font.PLAIN, 13));
        textArea.setTabSize(4);
        statusBar = new JLabel("||       Ln 1, Col 1  ",JLabel.RIGHT);
        frame.add(new JScrollPane(textArea), BorderLayout.CENTER);
        frame.add(statusBar, BorderLayout.SOUTH);
        frame.add(new JLabel(" "), BorderLayout.EAST);
        frame.add(new JLabel(" "), BorderLayout.WEST);
        createMenuBar(frame);
        frame.pack();
        frame.setLocation(100, 50);
        frame.setVisible(true);
        frame.setLocation(150, 50);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        fileHandler = new FileOperation(this);

        textArea.addCaretListener(
                e -> {
                    int lineNumber = 0, column = 0, pos;

                    try {
                        pos = textArea.getCaretPosition();
                        lineNumber = textArea.getLineOfOffset(pos);
                        column = pos - textArea.getLineStartOffset(lineNumber);
                    } catch (Exception excp) {
                        excp.printStackTrace();
                    }
                    if (textArea.getText().length() == 0) {
                        lineNumber = 0;
                        column = 0;
                    }
                    statusBar.setText("||       Ln " + (lineNumber + 1) + ", Col " + (column + 1));
                }
        );

        DocumentListener myListener = new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                fileHandler.setSaved(false);
            }
            public void removeUpdate(DocumentEvent e) {
                fileHandler.setSaved(false);
            }
            public void insertUpdate(DocumentEvent e) {
                fileHandler.setSaved(false);
            }
        };
        textArea.getDocument().addDocumentListener(myListener);

        WindowListener frameClose = new WindowAdapter() {
            public void windowCLosing() {
                if(fileHandler.confirmSave())
                    System.exit(0);
            }
        };
        frame.addWindowListener(frameClose);
    }

    /**
     * Go to a line with specific line number
     */
    void Goto() {
        int lineNumber;
        try {
            lineNumber = textArea.getLineOfOffset(textArea.getCaretPosition()) + 1;
            String tempStr = JOptionPane.showInputDialog(frame, "Enter Line Number:", "" + lineNumber);
            if (tempStr != null) {
                lineNumber = Integer.parseInt(tempStr);
                textArea.setCaretPosition(textArea.getLineStartOffset(lineNumber - 1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Execute commands according to user's choice in the menu.
     * @param ev - Event performed on menu items
     */
    @Override
    public void actionPerformed(ActionEvent ev) {
        String cmdText = ev.getActionCommand();

        switch (cmdText) {
            case fileNew:
                fileHandler.newFile();
                break;
            case fileOpen:
                fileHandler.openFile();
                break;
            case fileSave:
                fileHandler.saveThisFile();
                break;
            case fileSaveAs:
                fileHandler.saveAsFile();
                break;
            case fileExit:
                if (fileHandler.confirmSave())
                    System.exit(0);
                break;
            case filePrint:
                JOptionPane.showMessageDialog(
                        frame,
                        "Not connected to printer.",
                        "Bad Printer",
                        JOptionPane.INFORMATION_MESSAGE
                );
                break;
//        else if(cmdText.equals(editUndo)){
//        }
            case editCut:
                textArea.cut();
                break;
            case editCopy:
                textArea.copy();
                break;
            case editPaste:
                textArea.paste();
                break;
            case editDelete:
                textArea.replaceSelection("");
                break;
//        else if(cmdText.equals(editFind)) {
//            if(textArea.getText().length() == 0)
//                return;
//            if(findReplaceDialog == null)
//                findReplaceDialog
//        }
            // editFindNext
//        else if(cmdText.equals(editReplace))
            case editGoTo:
                if (textArea.getText().length() != 0) {
                    Goto();
                }
                break;
            case editSelectAll:
                textArea.selectAll();
                break;
            case editTimeDate:
                textArea.insert(new Date().toString(), textArea.getSelectionStart());
                break;
            case formatWordWrap: {
                JCheckBoxMenuItem temp = (JCheckBoxMenuItem) ev.getSource();
                textArea.setLineWrap(temp.isSelected());
                break;
            }
/*
        else if(cmdText.equals(formatFont)) {
            textArea.setFont(new Font("Courier New", Font.PLAIN, 13));
        }
*/
            case formatForeground:
                showForegroundColorDialog();
                break;
            case formatBackground:
                showBackgroundColorDialog();
                break;
            case formatTabWidth:
                int tabWidth = 8;
                textArea.setTabSize(tabWidth);
                break;
            case viewStatusBar: {
                JCheckBoxMenuItem temp = (JCheckBoxMenuItem) ev.getSource();
                statusBar.setVisible(temp.isSelected());
                break;
            }
            case helpAboutNotepad:
                JOptionPane.showMessageDialog(
                        frame, aboutText, "Thanks for choosing us!",
                        JOptionPane.INFORMATION_MESSAGE);
                break;
            default:
                statusBar.setText("This " + cmdText + " command is still being implemented.");
                break;
        }
    }

    /**
     * Show a dialog for user to choose background's color.
     */
    public void showBackgroundColorDialog() {
        if(backColorChooser == null)
            backColorChooser = new JColorChooser();
        if(backgroundDialog == null)
            backgroundDialog = JColorChooser.createDialog(
                    frame,
                    formatBackground,
                    false,
                    backColorChooser,
                    event_2 -> textArea.setBackground(backColorChooser.getColor()),
                    null
            );
        backgroundDialog.setVisible(true);
    }

    /**
     * Show a dialog for user to choose foreground's color.
     */
    public void showForegroundColorDialog() {
        if(foreColorChooser == null)
            foreColorChooser = new JColorChooser();
        if(foregroundDialog == null)
            foregroundDialog = JColorChooser.createDialog(
                    frame,
                    formatForeground,
                    false,
                    foreColorChooser,
                    event_2 -> textArea.setForeground(foreColorChooser.getColor()),
                    null
            );
        foregroundDialog.setVisible(true);
    }

    /**
     * Supporting method to create menu items with a text, keyboard mnemonic, adding it to a menu,
     * and assign an action listener to it.
     * @param str - The text of the menu item
     * @param key - keyboard mnemonic
     * @param toMenu - The menu where the menu item is being added to
     * @param listener - Action listener assigned to this item
     * @return - return a reference to this menu item
     */
    public JMenuItem createMenuItem(String str, int key, JMenu toMenu, ActionListener listener) {
        JMenuItem temp = new JMenuItem(str, key);
        temp.addActionListener(listener);
        toMenu.add(temp);
        return temp;
    }

    /**
     * Supporting method to create menu items with a text, keyboard mnemonic, adding it to a menu,
     * assign a keystroke, and an action listener.
     * @param str - The text of the menu item
     * @param key - keyboard mnemonic
     * @param toMenu - The menu where the menu item is being added to
     * @param listenerKey - Keystroke for modifying the menu item
     * @param listener - Action listener assigned to this item
     * @return - return a reference to this menu item
     */
    public JMenuItem createMenuItem(String str, int key, JMenu toMenu, int listenerKey, ActionListener listener) {
        JMenuItem temp = new JMenuItem(str, key);
        temp.addActionListener(listener);
        temp.setAccelerator(KeyStroke.getKeyStroke(listenerKey, InputEvent.CTRL_MASK));
        toMenu.add(temp);
        return temp;
    }

    public JCheckBoxMenuItem createCheckBoxMenuItem(String str, int key, JMenu toMenu, ActionListener listener) {
        JCheckBoxMenuItem temp = new JCheckBoxMenuItem(str);
        temp.setMnemonic(key);
        temp.addActionListener(listener);
        temp.setSelected(false);
        toMenu.add(temp);
        return temp;
    }

    /**
     * Create a menu with a key and add it to a menuBar.
     * @param str - The text of the menu.
     * @param key - keyboard mnemonic
     * @param toMenuBar - which menuBar it is being added to.
     * @return - return a reference to this new menu.
     */
    public JMenu createMenu(String str, int key, JMenuBar toMenuBar) {
        JMenu temp = new JMenu(str);
        temp.setMnemonic(key);
        toMenuBar.add(temp);
        return temp;
    }

    /**
     * Initialize all menu bars.
     * @param theFrame - the main frame these menu bars being added to.
     */
    public void createMenuBar(JFrame theFrame) {
        JMenuBar mb = new JMenuBar();

        JMenu fileMenu = createMenu(fileText,KeyEvent.VK_F,mb);
        JMenu editMenu = createMenu(editText,KeyEvent.VK_E,mb);
        JMenu formatMenu = createMenu(formatText,KeyEvent.VK_O,mb);
        JMenu viewMenu = createMenu(viewText,KeyEvent.VK_V,mb);
        JMenu helpMenu = createMenu(helpText,KeyEvent.VK_H,mb);

        // fileMenu
        createMenuItem(fileNew,KeyEvent.VK_N,fileMenu,KeyEvent.VK_N,this);
        createMenuItem(fileOpen,KeyEvent.VK_O,fileMenu,KeyEvent.VK_O,this);
        createMenuItem(fileSave,KeyEvent.VK_S,fileMenu,KeyEvent.VK_S,this);
        createMenuItem(fileSaveAs,KeyEvent.VK_A,fileMenu,this);
        fileMenu.addSeparator();

        JMenuItem temp;
        temp = createMenuItem(filePageSetup, KeyEvent.VK_U, fileMenu, this);
        temp.setEnabled(false);
        createMenuItem(filePrint,KeyEvent.VK_P,fileMenu,KeyEvent.VK_P,this);
        fileMenu.addSeparator();
        createMenuItem(fileExit,KeyEvent.VK_X,fileMenu,this);

        // editMenu
        temp = createMenuItem(editUndo,KeyEvent.VK_U,editMenu,KeyEvent.VK_Z,this);
        temp.setEnabled(false);
        editMenu.addSeparator();
        cutItem=createMenuItem(editCut,KeyEvent.VK_T,editMenu,KeyEvent.VK_X,this);
        copyItem=createMenuItem(editCopy,KeyEvent.VK_C,editMenu,KeyEvent.VK_C,this);
        createMenuItem(editPaste,KeyEvent.VK_P,editMenu,KeyEvent.VK_V,this);
        deleteItem=createMenuItem(editDelete,KeyEvent.VK_L,editMenu,this);
        deleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0));
        editMenu.addSeparator();
        findItem=createMenuItem(editFind,KeyEvent.VK_F,editMenu,KeyEvent.VK_F,this);
        findNextItem=createMenuItem(editFindNext,KeyEvent.VK_N,editMenu,this);
        findNextItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3,0));
        replaceItem=createMenuItem(editReplace,KeyEvent.VK_R,editMenu,KeyEvent.VK_H,this);
        gotoItem=createMenuItem(editGoTo,KeyEvent.VK_G,editMenu,KeyEvent.VK_G,this);
        editMenu.addSeparator();
        selectAllItem=createMenuItem(editSelectAll,KeyEvent.VK_A,editMenu,KeyEvent.VK_A,this);
        createMenuItem(editTimeDate,KeyEvent.VK_D,editMenu,this)
                .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5,0));

        // formatMenu
        createCheckBoxMenuItem(formatWordWrap,KeyEvent.VK_W,formatMenu,this);

        createMenuItem(formatFont,KeyEvent.VK_F,formatMenu,this);
        formatMenu.addSeparator();
        createMenuItem(formatForeground,KeyEvent.VK_T,formatMenu,this);
        createMenuItem(formatBackground,KeyEvent.VK_P,formatMenu,this);
        createMenuItem(formatTabWidth,KeyEvent.VK_Y,formatMenu, this);

        // viewMenu
        createCheckBoxMenuItem(viewStatusBar,KeyEvent.VK_S,viewMenu,this).setSelected(true);
    // For look and feel
    //        LookAndFeelMenu.createLookAndFeelMenuItem(viewMenu,this.frame);


        // helpMenu
        temp=createMenuItem(helpHelpTopic,KeyEvent.VK_H,helpMenu,this);
        temp.setEnabled(false);
        helpMenu.addSeparator();
        createMenuItem(helpAboutNotepad,KeyEvent.VK_A,helpMenu,this);

        MenuListener editMenuListener=new MenuListener()
        {
            public void menuSelected(MenuEvent evvvv)
            {
                if(Notepad.this.textArea.getText().length()==0)
                {
                    findItem.setEnabled(false);
                    findNextItem.setEnabled(false);
                    replaceItem.setEnabled(false);
                    selectAllItem.setEnabled(false);
                    gotoItem.setEnabled(false);
                }
                else
                {
                    findItem.setEnabled(true);
                    findNextItem.setEnabled(true);
                    replaceItem.setEnabled(true);
                    selectAllItem.setEnabled(true);
                    gotoItem.setEnabled(true);
                }
                if(Notepad.this.textArea.getSelectionStart()==textArea.getSelectionEnd())
                {
                    cutItem.setEnabled(false);
                    copyItem.setEnabled(false);
                    deleteItem.setEnabled(false);
                }
                else
                {
                    cutItem.setEnabled(true);
                    copyItem.setEnabled(true);
                    deleteItem.setEnabled(true);
                }
            }
            public void menuDeselected(MenuEvent evvvv) {}
            public void menuCanceled(MenuEvent evvvv) {}
        };
        editMenu.addMenuListener(editMenuListener);

        theFrame.setJMenuBar(mb);
    }

}