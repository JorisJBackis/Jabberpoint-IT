import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

/**
 * This class handles all the menus in the application.
 * It creates the menu structure and manages what happens when you click on menu items.
 * It also keeps track of the presentation to update menu items (like enabling/disabling
 * the Next button when you reach the last slide).
 */
public class MenuController extends MenuBar implements Observer {

    private final Frame parent;
    private final Presentation presentation;
    private MenuItem nextMenuItem;
    private MenuItem prevMenuItem;
    private MenuItem saveMenuItem;

    /**
     * We create a new MenuController with the main window and the presentation.
     * This sets up all the menus and connects them to the right actions.
     */
    public MenuController(Frame frame, Presentation pres) {
        parent = frame;
        presentation = pres;
        presentation.addObserver(this);
        setupMenus();
    }

    /**
     * This creates all the menus and menu items with their actions.
     * We create three main menus:
     * - File menu (Open, New, Save, Exit)
     * - View menu (Next, Prev, Go to)
     * - Help menu (About)
     */
    private void setupMenus() {
        Menu fileMenu = new Menu("File");
        MenuItem openItem = new MenuItem("Open", new MenuShortcut('O'));
        openItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "XML Files", "xml");
                fileChooser.setFileFilter(filter);

                // Try to start in the user's desktop directory for easier access
                try {
                    String userHome = System.getProperty("user.home");
                    File desktop = new File(userHome, "Desktop");
                    if (desktop.exists()) {
                        fileChooser.setCurrentDirectory(desktop);
                    }
                } catch (Exception ex) {
                    // If there's any error setting the directory, just use the default
                    System.err.println("Could not set file chooser to Desktop: " + ex.getMessage());
                }

                int returnVal = fileChooser.showOpenDialog(parent);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    presentation.clear();
                    try {
                        File file = fileChooser.getSelectedFile();

                        if (!file.exists()) {
                            JOptionPane.showMessageDialog(
                                    parent,
                                    "The file '" + file.getName() + "' does not exist.",
                                    "File Not Found",
                                    JOptionPane.ERROR_MESSAGE
                            );
                            return;
                        }

                        System.out.println("Attempting to open file: " + file.getAbsolutePath());
                        XMLAccessor xmlAccessor = new XMLAccessor();
                        xmlAccessor.loadFile(presentation, file.getAbsolutePath());
                        presentation.setSlideNumber(0);
                        System.out.println("Successfully opened presentation with " +
                                presentation.getSize() + " slides");
                    } catch (IOException exc) {
                        System.err.println("ERROR: " + exc.getMessage());
                        JOptionPane.showMessageDialog(
                                parent,
                                "Could not load the file:\n" + exc.getMessage(),
                                "Load Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                    parent.repaint();
                }
            }
        });
        fileMenu.add(openItem);

        MenuItem newItem = new MenuItem("New", new MenuShortcut('N'));
        newItem.addActionListener(e -> {
            presentation.clear();
            parent.repaint();
        });
        fileMenu.add(newItem);

        saveMenuItem = new MenuItem("Save", new MenuShortcut('S'));
        saveMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "XML Files", "xml");
                fileChooser.setFileFilter(filter);
                int returnVal = fileChooser.showSaveDialog(parent);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        File file = fileChooser.getSelectedFile();
                        String path = file.getAbsolutePath();
                        // Ensure file has .xml extension
                        if (!path.toLowerCase().endsWith(".xml")) {
                            path += ".xml";
                        }
                        XMLAccessor xmlAccessor = new XMLAccessor();
                        xmlAccessor.saveFile(presentation, path);
                    } catch (IOException exc) {
                        JOptionPane.showMessageDialog(parent, "IO Exception: " + exc,
                                "Save Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        fileMenu.add(saveMenuItem);

        fileMenu.addSeparator();
        MenuItem exitItem = new MenuItem("Exit", new MenuShortcut('E'));
        exitItem.addActionListener(e -> presentation.exit(0));
        fileMenu.add(exitItem);
        add(fileMenu);

        Menu viewMenu = new Menu("View");
        nextMenuItem = new MenuItem("Next", new MenuShortcut('N', true));
        nextMenuItem.addActionListener(e -> presentation.nextSlide());
        viewMenu.add(nextMenuItem);

        prevMenuItem = new MenuItem("Prev", new MenuShortcut('P', true));
        prevMenuItem.addActionListener(e -> presentation.prevSlide());
        viewMenu.add(prevMenuItem);

        MenuItem gotoItem = new MenuItem("Go to", new MenuShortcut('G'));
        gotoItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String pageNumberStr = JOptionPane.showInputDialog("Page number?");
                if (pageNumberStr == null) {
                    return;
                }
                try {
                    if (pageNumberStr == null || pageNumberStr.trim().isEmpty()) {
                        return; // User canceled or entered nothing
                    }

                    int pageNumber = Integer.parseInt(pageNumberStr);
                    if (pageNumber < 1 || pageNumber > presentation.getSize()) {
                        JOptionPane.showMessageDialog(parent, 
                            "Invalid slide number: " + pageNumber + "\nValid range is 1-" + presentation.getSize(), 
                            "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    presentation.setSlideNumber(pageNumber - 1);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(parent, "Invalid number: " + pageNumberStr, 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        viewMenu.add(gotoItem);
        add(viewMenu);

        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About", new MenuShortcut('A'));
        aboutItem.addActionListener(e -> AboutBox.show(parent));
        helpMenu.add(aboutItem);
        setHelpMenu(helpMenu);

        updateMenuState();
    }

    /**
     * This updates the menu items based on where we are in the presentation.
     * For example, the Next button is disabled when we're on the last slide,
     * and the Prev button is disabled on the first slide.
     */
    private void updateMenuState() {
        int slideNumber = presentation.getSlideNumber();
        int slideCount = presentation.getSize();

        // More detailed diagnostic info
        System.out.println("MenuController: Updating menu state - Current slide: " + (slideNumber + 1) +
                " of " + slideCount);
        System.out.println("  - Next button enabled: " + (slideNumber < slideCount - 1));
        System.out.println("  - Prev button enabled: " + (slideNumber > 0));

        nextMenuItem.setEnabled(slideNumber < slideCount - 1);
        prevMenuItem.setEnabled(slideNumber > 0);
        saveMenuItem.setEnabled(slideCount > 0);
    }

    /**
     * This gets called whenever the presentation changes.
     * When that happens, we need to update our menu items.
     */
    @Override
    public void update() {
        updateMenuState();
    }
} 