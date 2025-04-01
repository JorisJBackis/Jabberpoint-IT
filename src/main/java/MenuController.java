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

public class MenuController extends MenuBar implements Observer {

    private Frame parent;
    private Presentation presentation;
    private MenuItem nextMenuItem;
    private MenuItem prevMenuItem;
    private MenuItem saveMenuItem;

    public MenuController(Frame frame, Presentation pres) {
        parent = frame;
        presentation = pres;
        presentation.addObserver(this);
        setupMenus();
    }

    private void setupMenus() {
        Menu fileMenu = new Menu("File");
        MenuItem openItem = new MenuItem("Open", new MenuShortcut('O'));
        openItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "XML Files", "xml");
                fileChooser.setFileFilter(filter);
                int returnVal = fileChooser.showOpenDialog(parent);
                
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    presentation.clear();
                    try {
                        File file = fileChooser.getSelectedFile();
                        new XMLAccessor().loadFile(presentation, file.getAbsolutePath());
                        presentation.setSlideNumber(0);
                    } catch (IOException exc) {
                        JOptionPane.showMessageDialog(parent, "IO Exception: " + exc, 
                            "Load Error", JOptionPane.ERROR_MESSAGE);
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
                try {
                    new XMLAccessor().saveFile(presentation, "dump.xml");
                } catch (IOException exc) {
                    JOptionPane.showMessageDialog(parent, "IO Exception: " + exc, 
                        "Save Error", JOptionPane.ERROR_MESSAGE);
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
        nextMenuItem = new MenuItem("Next", new MenuShortcut('>'));
        nextMenuItem.addActionListener(e -> presentation.nextSlide());
        viewMenu.add(nextMenuItem);

        prevMenuItem = new MenuItem("Prev", new MenuShortcut('<'));
        prevMenuItem.addActionListener(e -> presentation.prevSlide());
        viewMenu.add(prevMenuItem);

        MenuItem gotoItem = new MenuItem("Go to", new MenuShortcut('G'));
        gotoItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String pageNumberStr = JOptionPane.showInputDialog("Page number?");
                try {
                    int pageNumber = Integer.parseInt(pageNumberStr);
                    presentation.setSlideNumber(pageNumber - 1);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(parent, "Invalid number: " + pageNumberStr);
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

    private void updateMenuState() {
        int slideNumber = presentation.getSlideNumber();
        int slideCount = presentation.getSize();
        nextMenuItem.setEnabled(slideNumber < slideCount - 1);
        prevMenuItem.setEnabled(slideNumber > 0);
        saveMenuItem.setEnabled(slideCount > 0);
    }

    @Override
    public void update() {
        updateMenuState();
    }
}