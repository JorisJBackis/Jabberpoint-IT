import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.event.KeyEvent;
import java.awt.Component;

import static org.junit.jupiter.api.Assertions.*;

class KeyControllerTest {
    private TestPresentation presentation;
    private KeyController controller;
    private KeyEvent keyEvent;

    // Custom Presentation class that doesn't actually exit
    private static class TestPresentation extends Presentation {
        private boolean exitCalled = false;

        public TestPresentation(String title) {
            super(title);
        }

        @Override
        public void exit(int n) {
            exitCalled = true;
        }

        public boolean wasExitCalled() {
            return exitCalled;
        }
    }

    @BeforeEach
    void setUp() {
        presentation = new TestPresentation("Test Presentation");
        controller = new KeyController(presentation);
        
        // Add some slides for testing navigation
        Slide slide1 = new Slide();
        slide1.setTitle("Slide 1");
        Slide slide2 = new Slide();
        slide2.setTitle("Slide 2");
        presentation.addSlide(slide1);
        presentation.addSlide(slide2);
        
        // Start at first slide
        presentation.setSlideNumber(0);
    }

    @Test
    void nextSlideKeyShouldAdvanceSlide() {
        // Test PageDown key
        keyEvent = new KeyEvent(new Component(){}, KeyEvent.KEY_PRESSED, 
            System.currentTimeMillis(), 0, KeyEvent.VK_PAGE_DOWN, KeyEvent.CHAR_UNDEFINED);
        controller.keyPressed(keyEvent);
        assertEquals(1, presentation.getSlideNumber());

        // Test Enter key
        presentation.setSlideNumber(0);
        keyEvent = new KeyEvent(new Component(){}, KeyEvent.KEY_PRESSED, 
            System.currentTimeMillis(), 0, KeyEvent.VK_ENTER, KeyEvent.CHAR_UNDEFINED);
        controller.keyPressed(keyEvent);
        assertEquals(1, presentation.getSlideNumber());
    }

    @Test
    void previousSlideKeyShouldGoBack() {
        presentation.setSlideNumber(1);
        keyEvent = new KeyEvent(new Component(){}, KeyEvent.KEY_PRESSED, 
            System.currentTimeMillis(), 0, KeyEvent.VK_PAGE_UP, KeyEvent.CHAR_UNDEFINED);
        controller.keyPressed(keyEvent);
        assertEquals(0, presentation.getSlideNumber());
    }

    @Test
    void quitKeyShouldCallExit() {
        keyEvent = new KeyEvent(new Component(){}, KeyEvent.KEY_PRESSED, 
            System.currentTimeMillis(), 0, KeyEvent.VK_Q, 'q');
        controller.keyPressed(keyEvent);
        assertTrue(presentation.wasExitCalled());
    }

    @Test
    void nextSlideShortcutKeyShouldAdvanceSlide() {
        // Test Ctrl+N
        keyEvent = new KeyEvent(new Component(){}, KeyEvent.KEY_PRESSED, 
            System.currentTimeMillis(), KeyEvent.CTRL_DOWN_MASK, KeyEvent.VK_N, 'n');
        controller.keyPressed(keyEvent);
        assertEquals(1, presentation.getSlideNumber(), "Ctrl+N should advance to the next slide");
    }

    @Test
    void previousSlideShortcutKeyShouldGoBack() {
        presentation.setSlideNumber(1); // Start on the second slide
        // Test Ctrl+P
        keyEvent = new KeyEvent(new Component(){}, KeyEvent.KEY_PRESSED, 
            System.currentTimeMillis(), KeyEvent.CTRL_DOWN_MASK, KeyEvent.VK_P, 'p');
        controller.keyPressed(keyEvent);
        assertEquals(0, presentation.getSlideNumber(), "Ctrl+P should go back to the previous slide");
    }
} 