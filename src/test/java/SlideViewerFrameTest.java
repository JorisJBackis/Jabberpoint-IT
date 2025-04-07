import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import static org.junit.jupiter.api.Assertions.*;

class SlideViewerFrameTest {
    private Presentation presentation;
    private SlideViewerFrame frame;

    @BeforeEach
    void setUp() {
        presentation = new Presentation("Test Presentation");
        frame = new SlideViewerFrame("Test Title", presentation);
    }

    @Test
    void constructorShouldInitializeFrame() {
        assertEquals("Jabberpoint - Observer Pattern Demo - Test Presentation", frame.getTitle());
        assertTrue(frame.isVisible());
        assertEquals(1200, frame.getSize().width);
        assertEquals(800, frame.getSize().height);
    }

    @Test
    void windowShouldHaveCorrectComponents() {
        Component[] components = frame.getContentPane().getComponents();
        assertEquals(1, components.length);
        assertTrue(components[0] instanceof SlideViewerComponent);
    }

    @Test
    void setupWindowShouldAddComponents() {
        // Verify components were added
        Component[] components = frame.getContentPane().getComponents();
        assertEquals(1, components.length);
        assertTrue(components[0] instanceof SlideViewerComponent);
        
        // Verify key listener was added
        assertTrue(frame.getKeyListeners().length > 0);
        
        // Verify menu bar was added
        assertNotNull(frame.getMenuBar());
    }

    @Test
    void frameShouldHaveWindowListener() {
        assertTrue(frame.getWindowListeners().length > 0);
    }
} 