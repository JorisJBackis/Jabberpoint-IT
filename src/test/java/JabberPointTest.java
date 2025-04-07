import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.awt.*;
import static org.junit.jupiter.api.Assertions.*;

class JabberPointTest {
    @Test
    void mainShouldInitializeStyles() {
        // Call main with empty args
        // Note: Calling main directly can have side effects (like starting GUI thread).
        // Ideally, test Style.createStyles() directly if possible, but for coverage
        // of main's call path, this might be necessary, though fragile.
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

    // Removed mainShouldCreateFrame test as it launches GUI and is unsuitable for CI

    // Removed mainShouldLoadDemoPresentation test as it launches GUI and is unsuitable for CI
} 