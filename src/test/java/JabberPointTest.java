import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.awt.*;
import static org.junit.jupiter.api.Assertions.*;

class JabberPointTest {
    @Test
    void mainShouldInitializeStyles() {
        // Call main with empty args
        assertDoesNotThrow(() -> JabberPoint.main(new String[]{}));
        
        // Verify styles were initialized by checking a few style properties
        Style style0 = Style.getStyle(0);
        assertNotNull(style0);
        assertEquals(0, style0.indent);
        assertEquals(Color.red, style0.color);
        assertEquals(48, style0.fontSize);
        
        Style style4 = Style.getStyle(4);
        assertNotNull(style4);
        assertEquals(90, style4.indent);
        assertEquals(Color.black, style4.color);
        assertEquals(24, style4.fontSize);
    }

    @Test
    void mainShouldCreateFrame() {
        // Create a new thread to run the main method
        Thread thread = new Thread(() -> {
            try {
                JabberPoint.main(new String[]{"test.xml"});
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        // Start the thread and wait a bit for the frame to be created
        thread.start();
        try {
            Thread.sleep(1000); // Wait for frame creation
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Check if any frame with "Jabberpoint" in the title exists
        boolean frameFound = false;
        Frame[] frames = Frame.getFrames();
        for (Frame frame : frames) {
            if (frame.getTitle() != null && frame.getTitle().contains("Jabberpoint")) {
                frameFound = true;
                frame.dispose(); // Clean up
                break;
            }
        }
        
        assertTrue(frameFound, "No Jabberpoint frame was created");
    }

    @Test
    void mainShouldLoadDemoPresentation() {
        // Call main with empty args
        assertDoesNotThrow(() -> JabberPoint.main(new String[]{}));
        
        // Verify demo presentation was loaded
        Frame[] frames = Frame.getFrames();
        if (frames.length > 0 && frames[0] instanceof SlideViewerFrame) {
            SlideViewerFrame frame = (SlideViewerFrame) frames[0];
            Component[] components = frame.getContentPane().getComponents();
            if (components.length > 0 && components[0] instanceof SlideViewerComponent) {
                SlideViewerComponent viewer = (SlideViewerComponent) components[0];
                // The viewer should have been updated with the demo presentation
                assertTrue(frame.getTitle().contains("Demo Presentation"));
            }
        }
    }
} 