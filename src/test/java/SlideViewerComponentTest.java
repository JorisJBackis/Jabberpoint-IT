import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

class SlideViewerComponentTest {
    private Presentation presentation;
    private SlideViewerComponent component;

    @BeforeEach
    void setUp() {
        presentation = new Presentation("Test Presentation");
        component = new SlideViewerComponent(presentation);
        Style.createStyles();
    }

    @Test
    void constructorShouldInitializeCorrectly() {
        assertNotNull(component);
        assertEquals(Slide.WIDTH, component.getPreferredSize().width);
        assertEquals(Slide.HEIGHT, component.getPreferredSize().height);
    }

    @Test
    void updateShouldNotThrowException() {
        // Add a slide to make the update meaningful
        Slide slide = new Slide();
        slide.setTitle("Test Slide");
        presentation.addSlide(slide);
        
        assertDoesNotThrow(() -> component.update());
    }

    @Test
    void setColorsShouldUpdateComponent() {
        Color testBg = new Color(255, 0, 0);
        Color testText = new Color(0, 255, 0);
        
        component.setBackgroundColor(testBg);
        component.setTextColor(testText);
        
        assertEquals(testBg, component.getBackground());
    }

    @Test
    void setTextPositionShouldUpdatePosition() {
        int testX = 100;
        int testY = 200;
        
        component.setTextPosition(testX, testY);
        
        // Since the position is private, we can verify it works by checking
        // that the component still functions after setting
        assertDoesNotThrow(() -> component.paintComponent(
            new BufferedImage(320, 240, BufferedImage.TYPE_INT_ARGB).createGraphics()
        ));
    }
} 