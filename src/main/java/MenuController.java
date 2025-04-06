import java.awt.MenuBar;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

// No longer need to import XMLAccessor

/**
 * This class handles all the menus in the application.
 * It creates the menu structure and manages what happens when you click on menu items.
 * It also keeps track of the presentation to update menu items (like enabling/disabling
 * the Next button when you reach the last slide).
 * Refactored to use PresentationReader and PresentationWriter interfaces.
 */
public class MenuController extends MenuBar implements Observer {

    private final Frame parent;
    private transient final Presentation presentation;
    private MenuItem nextMenuItem;
    private MenuItem prevMenuItem;
    private MenuItem saveMenuItem;
    private MenuItem gotoMenuItem; // Added for consistent reference

    // --- Factories/Readers/Writers (Instantiated here for simplicity) ---
    // In a larger app, these might be injected (Dependency Injection)
    // Mark as transient to satisfy SpotBugs SE_BAD_FIELD_STORE
    private transient final PresentationReader xmlReader = new XMLPresentationReader();
    private transient final PresentationWriter xmlWriter = new XMLPresentationWriter();
    // ---

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
        // === File Menu ===
        Menu fileMenu = new Menu("File");

        // --- Open ---
        MenuItem openItem = new MenuItem("Open", new MenuShortcut('O'));
        openItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("XML Files", "xml");
                fileChooser.setFileFilter(filter);

                try { // Set preferred directory
                    String userHome = System.getProperty("user.home");
                    File desktop = new File(userHome, "Desktop");
                    if (desktop.isDirectory()) {
                        fileChooser.setCurrentDirectory(desktop);
                    }
                } catch (Exception ex) {
                    System.err.println("Could not set file chooser dir: " + ex.getMessage());
                }

                int returnVal = fileChooser.showOpenDialog(parent);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    loadFile(fileChooser.getSelectedFile()); // Delegate to helper method
                }
            }
        });
        fileMenu.add(openItem);

        // --- New ---
        MenuItem newItem = new MenuItem("New", new MenuShortcut('N'));
        newItem.addActionListener(e -> {
            presentation.clear();
            parent.repaint(); // Repaint to show empty state
            updateMenuState(); // Update menus immediately after clear
        });
        fileMenu.add(newItem);

        // --- Save ---
        saveMenuItem = new MenuItem("Save", new MenuShortcut('S'));
        saveMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (presentation.getSize() <= 0) return; // Nothing to save

                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("XML Files", "xml");
                fileChooser.setFileFilter(filter);
                fileChooser.setSelectedFile(new File(presentation.getTitle() + ".xml")); // Suggest name

                int returnVal = fileChooser.showSaveDialog(parent);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    saveFile(fileChooser.getSelectedFile()); // Delegate to helper method
                }
            }
        });
        fileMenu.add(saveMenuItem);

        // --- Separator and Exit ---
        fileMenu.addSeparator();
        MenuItem exitItem = new MenuItem("Exit", new MenuShortcut('E'));
        exitItem.addActionListener(e -> presentation.exit(0)); // Still using Presentation's exit
        fileMenu.add(exitItem);

        add(fileMenu); // Add File menu to the menu bar

        // === View Menu ===
        Menu viewMenu = new Menu("View");

        // --- Next ---
        nextMenuItem = new MenuItem("Next", new MenuShortcut('N', true)); // Ctrl+N
        nextMenuItem.addActionListener(e -> presentation.nextSlide());
        viewMenu.add(nextMenuItem);

        // --- Previous ---
        prevMenuItem = new MenuItem("Prev", new MenuShortcut('P', true)); // Ctrl+P
        prevMenuItem.addActionListener(e -> presentation.prevSlide());
        viewMenu.add(prevMenuItem);

        // --- Go To ---
        gotoMenuItem = new MenuItem("Go to", new MenuShortcut('G'));
        gotoMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String pageNumberStr = JOptionPane.showInputDialog("Go to slide number:");
                // User cancelled
                if (pageNumberStr == null) return;

                try {
                    int pageNumber = Integer.parseInt(pageNumberStr.trim());
                    // Validate input range
                    if (pageNumber >= 1 && pageNumber <= presentation.getSize()) {
                        presentation.setSlideNumber(pageNumber - 1); // Adjust to 0-based index
                    } else {
                        JOptionPane.showMessageDialog(parent,
                                "Invalid slide number: " + pageNumber + "\nMust be between 1 and " + presentation.getSize(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(parent, "Please enter a valid number.",
                            "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        viewMenu.add(gotoMenuItem);

        add(viewMenu); // Add View menu to the menu bar

        // === Help Menu ===
        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About", new MenuShortcut('A'));
        aboutItem.addActionListener(e -> AboutBox.show(parent));
        helpMenu.add(aboutItem);

        setHelpMenu(helpMenu); // Set the dedicated Help menu

        // --- Initial State ---
        updateMenuState(); // Set initial enabled/disabled state
    }

    // --- Helper method for loading ---
    private void loadFile(File file) {
        presentation.clear(); // Clear existing presentation
        try {
            if (!file.exists()) {
                JOptionPane.showMessageDialog(parent,
                        "File not found:\n" + file.getAbsolutePath(),
                        "Load Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            xmlReader.load(presentation, file.getAbsolutePath());
            presentation.setSlideNumber(0); // Go to first slide after load
            // Update frame title after loading new presentation
            parent.setTitle("Jabberpoint - " + presentation.getTitle());
        } catch (IOException exc) {
            System.err.println("ERROR loading file: " + exc.getMessage());
            JOptionPane.showMessageDialog(parent,
                    "Could not load presentation:\n" + exc.getMessage(),
                    "Load Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            parent.repaint(); // Repaint regardless of success/failure
            updateMenuState(); // Update menus after load attempt
        }
    }

    // --- Helper method for saving ---
    private void saveFile(File file) {
        try {
            String path = file.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".xml")) {
                path += ".xml";
            }
            xmlWriter.save(presentation, path);
            //provide user feedback on successful save
            JOptionPane.showMessageDialog(parent, "Presentation saved to:\n" + path,
                                       "Save Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException exc) {
            System.err.println("ERROR saving file: " + exc.getMessage());
            JOptionPane.showMessageDialog(parent, "Could not save presentation:\n" + exc.getMessage(),
                    "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Updates the enabled state of menu items based on the current presentation state.
     */
    private void updateMenuState() {
        int slideNumber = presentation.getSlideNumber();
        int slideCount = presentation.getSize();

        boolean hasSlides = slideCount > 0;
        boolean canGoNext = hasSlides && (slideNumber < slideCount - 1);
        boolean canGoPrev = hasSlides && (slideNumber > 0);

        // Enable/disable menu items
        nextMenuItem.setEnabled(canGoNext);
        prevMenuItem.setEnabled(canGoPrev);
        saveMenuItem.setEnabled(hasSlides);
        gotoMenuItem.setEnabled(hasSlides); // Can only go to if there are slides

        // Debugging output (optional)
        System.out.printf("Menu State Update: Slide %d/%d, Next:%b, Prev:%b, Save:%b, GoTo:%b%n",
                slideNumber + (hasSlides ? 1 : 0), slideCount, canGoNext, canGoPrev, hasSlides, hasSlides);
    }

    /**
     * Called by the Presentation when its state changes (e.g., slide number changes).
     */
    @Override
    public void update() {
        updateMenuState();
    }
}