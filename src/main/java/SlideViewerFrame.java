import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * This is the main window that shows the presentation.
 * It's like a frame around the slides, with menus and controls.
 * Its job is to organize all the different parts of the display.
 */
public class SlideViewerFrame extends JFrame {
    public final static int WIDTH = 1200;
    public final static int HEIGHT = 800;
    private static final long serialVersionUID = 3227L;
    private static final String JABTITLE = "Jabberpoint - Observer Pattern Demo";

    /**
     * We create a new window to show the presentation.
     * This sets up the window with the right size and title,
     * and connects it to the presentation we want to show.
     */
    public SlideViewerFrame(String title, Presentation presentation) {
        super(title);
        // We create the component that actually shows the slides
        SlideViewerComponent slideViewerComponent = new SlideViewerComponent(presentation);
        // We connect the presentation to its view
        presentation.setShowView(slideViewerComponent);
        setupWindow(slideViewerComponent, presentation);
    }

    /**
     * This sets up all the details of the window.
     * It handles what happens when you close the window,
     * adds the keyboard controls, and creates all the menus.
     */
    public void setupWindow(SlideViewerComponent slideViewerComponent, Presentation presentation) {
        setTitle(JABTITLE + " - " + presentation.getTitle());
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        getContentPane().add(slideViewerComponent);
        addKeyListener(new KeyController(presentation));
        // We add the menu controller that handles all the menus
        setMenuBar(new MenuController(this, presentation));
        setSize(new Dimension(WIDTH, HEIGHT));
        setVisible(true);
    }
}