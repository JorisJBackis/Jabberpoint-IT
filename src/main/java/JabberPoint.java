import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.io.IOException;

public class JabberPoint {
    protected static final String IOERR = "IO Error: ";
    protected static final String JABERR = "Jabberpoint Error ";

    // The logic is now self-contained in DemoPresentationReader.

    public static void main(String[] argv) {
        Style.createStyles(); // Initialize styles first

        SwingUtilities.invokeLater(() -> {
            Presentation presentation = new Presentation("Demo Presentation");
            // Create the main frame. The frame will wire in the view and controllers.
            new SlideViewerFrame(JABTITLE() + " - " + presentation.getTitle(), presentation);
            try {
                // OLD: Accessor.getDemoAccessor().loadFile(presentation, "");
                // NEW: Instantiate and use the specific reader
                PresentationReader reader = new DemoPresentationReader();
                reader.load(presentation, null); // Pass null or empty string for source
                presentation.setSlideNumber(0);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, IOERR + ex, JABERR,
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static String JABTITLE() {
        return "Jabberpoint - Demo";
    }
}