import javax.swing.*;
import java.io.IOException;

/**
 * This is the main class that starts the JabberPoint presentation app.
 * It's like the front door to the application - everything begins here.
 */
public class JabberPoint {
    protected static final String IOERR = "IO Error: ";
    protected static final String JABERR = "Jabberpoint Error ";

    /**
     * This is where the program starts running. When you launch the app,
     * this method gets called first. It sets up everything needed for
     * the presentation to work.
     */
    public static void main(String[] argv) {
        Style.createStyles(); // First we create the styles for the presentation

        SwingUtilities.invokeLater(() ->
        {
            Presentation presentation = new Presentation("Demo Presentation");
            // We create the main window that shows the slides
            new SlideViewerFrame(JABTITLE() + " - " + presentation.getTitle(), presentation);
            try {
                // We load the demo presentation that comes built-in
                Accessor.getDemoAccessor().loadFile(presentation, "");
                presentation.setSlideNumber(0);

                // Print diagnostic information
                System.out.println("Loaded presentation with " + presentation.getSize() + " slides");
                System.out.println("Current slide: " + (presentation.getSlideNumber() + 1));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, IOERR + ex, JABERR,
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * This gives us the title for the application window.
     * It's just a simple helper method to keep the code clean.
     */
    private static String JABTITLE() {
        return "Jabberpoint - Demo";
    }
}