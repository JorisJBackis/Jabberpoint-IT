import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.io.IOException;

public class JabberPoint {
    protected static final String IOERR = "IO Error: ";
    protected static final String JABERR = "Jabberpoint Error ";

    public static void main(String[] argv) {
        Style.createStyles(); // Initialize styles first
        
        SwingUtilities.invokeLater(() -> {
            Presentation presentation = new Presentation("Demo Presentation");
            // Create the main frame. The frame will wire in the view and controllers.
            new SlideViewerFrame(JABTITLE() + " - " + presentation.getTitle(), presentation);
            try {
                // Load the demo presentation using your Accessor implementation.
                Accessor.getDemoAccessor().loadFile(presentation, "");
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