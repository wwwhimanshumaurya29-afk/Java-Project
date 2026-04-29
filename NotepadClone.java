
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.io.*;


public class NotepadClone extends Frame implements ActionListener {

    private TextArea textArea;
    private String str = "";
    private int fontSize = 18;

    public NotepadClone() {
        setTitle("My Notepad Clone");
        setSize(800, 800);
        setLayout(new BorderLayout());
        // TextArea setup
        textArea = new TextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, fontSize));
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.add(textArea);
        add(scrollPane, BorderLayout.CENTER);

        // MenuBar
        MenuBar menuBar = new MenuBar();

        // File Menu
        Menu fileMenu = new Menu("File");
        fileMenu.setFont(new Font("AERIAL", Font.PLAIN, 16));
        addMenuItem(fileMenu, "New tab");
        addMenuItem(fileMenu, "New window");
        addMenuItem(fileMenu, "Open");
        addMenuItem(fileMenu, "Save");
        addMenuItem(fileMenu, "Print");
        addMenuItem(fileMenu, "Close tab");
        addMenuItem(fileMenu, "Close window");
        addMenuItem(fileMenu, "Exit");

        // Edit Menu
        Menu editMenu = new Menu("Edit");
        editMenu.setFont(new Font("AERIAL", Font.PLAIN, 16));
        addMenuItem(editMenu, "Cut");
        addMenuItem(editMenu, "Copy");
        addMenuItem(editMenu, "Paste");
                addMenuItem(editMenu, "Delete");
        addMenuItem(editMenu, "Select All");

        // View Menu (Zoom)
        Menu viewMenu = new Menu("View");
        viewMenu.setFont(new Font("AERIAL", Font.PLAIN, 16));
        addMenuItem(viewMenu, "Zoom In");
        addMenuItem(viewMenu, "Zoom Out");
        addMenuItem(viewMenu, "Default Zoom");

        // Help Option
//Menu HelpMenu = new Menu("About");
      //  HelpMenu.setFont(new Font("AERIAL", Font.PLAIN, 16));
        //shortcut key

        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        
        setMenuBar(menuBar);


        // Window close event
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                dispose(); // Gracefully closes the current window
            }
        });



        setVisible(true);
    }

    private void addMenuItem(Menu menu, String itemName) {
        MenuItem item = new MenuItem(itemName);
        item.addActionListener(this);
        menu.add(item);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            case "New tab":
                textArea.setText("");
                break;
            case "New window":
                new NotepadClone();
                break;

            case "Open":
                FileDialog openDialog = new FileDialog(this, "Open File", FileDialog.LOAD);
                openDialog.setVisible(true);
                String openDir = openDialog.getDirectory();
                String openFile = openDialog.getFile();
                if (openFile != null) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(openDir + openFile))) {
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                        textArea.setText(sb.toString());
                    } catch (IOException ex) {
                        showError("Error opening file");
                    }
                }
                break;

            case "Save":
                FileDialog saveDialog = new FileDialog(this, "Save File", FileDialog.SAVE);
                saveDialog.setVisible(true);
                String saveDir = saveDialog.getDirectory();
                String saveFile = saveDialog.getFile();
                if (saveFile != null) {
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveDir + saveFile))) {
                        writer.write(textArea.getText());
                    } catch (IOException ex) {
                        showError("Error saving file");
                    }
                }
                break;

            case "Print":
                PrinterJob job = PrinterJob.getPrinterJob();
                job.setPrintable(new Printable() {
                    public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
                        if (pageIndex > 0) {
                            return NO_SUCH_PAGE;
                        }

                        Graphics2D g2d = (Graphics2D) g;
                        g2d.translate(pf.getImageableX(), pf.getImageableY());
                        g2d.setFont(new Font("Monospaced", Font.PLAIN, fontSize));

                        // Split text into lines
                        String[] lines = textArea.getText().split("\n");
                        int y = 100;
                        for (String line : lines) {
                            g2d.drawString(line, 100, y);
                            y += fontSize + 4; // Line spacing
                        }

                        return PAGE_EXISTS;
                    }
                });

                if (job.printDialog()) {
                    try {
                        job.print();
                    } catch (PrinterException ex) {
                        showError("Printing failed: " + ex.getMessage());
                    }
                }
                break;

            case "Close tab":
                dispose() ;
                break;

            case "Close window":
                dispose(); // Closes only the current window
                break;

            case "Exit":
                System.exit(0);
                break;

            case "Cut":
                str= textArea.getSelectedText();
                textArea.replaceRange("", textArea.getSelectionStart(), textArea.getSelectionEnd());
                break;

            case "Copy":
                str= textArea.getSelectedText();
                break;

            case "Paste":
                textArea.insert(str, textArea.getCaretPosition());
                break;
            case "Delete":
                str= textArea.getSelectedText();
                textArea.setText("");
                break;

            case "Select All":
                textArea.selectAll();
                break;

            case "Zoom In":
                fontSize += 4;
                updateFontSize();
                break;

            case "Zoom Out":
                fontSize = Math.max(12, fontSize - 4);
                updateFontSize();
                break;

            case "Default Zoom":
                fontSize = 18;
                updateFontSize();
                break;
        }
    }

    private void updateFontSize() {
        textArea.setFont(new Font("Monospaced", Font.PLAIN, fontSize));
    }

    private void showError(String message) {
        Dialog dialog = new Dialog(this, "Error", true);
        dialog.setLayout(new FlowLayout());
        dialog.setSize(300, 100);
        Label label = new Label(message);
        Button ok = new Button("OK");
        ok.addActionListener(e -> dialog.setVisible(false));
        dialog.add(label);
        dialog.add(ok);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        new NotepadClone();
    }
}

