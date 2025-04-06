import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicBoolean;


class PresentationTest {

    private Presentation presentation;
    private Slide slide1;
    private Slide slide2;

    // Simple Mock Observer to check if notifyObservers is called
    class TestObserver implements Observer {
        boolean updated = false;
        @Override
        public void update() {
            updated = true;
        }
        public boolean wasUpdated() { return updated; }
        public void reset() { updated = false; }
    }

    @BeforeEach
    void setUp() {
        presentation = new Presentation("Test Presentation");
        slide1 = new Slide();
        slide1.setTitle("Slide 1 Title");
        slide2 = new Slide();
        slide2.setTitle("Slide 2 Title");
        // Ensure styles are created if needed by Presentation logic indirectly
        // If Style.createStyles() is not accessible, this might need adjustment
        // or tests might fail if Style assumes initialization.
        try {
            Style.createStyles(); // Assuming this is accessible and needed
        } catch (ExceptionInInitializerError e) {
            System.err.println("Warning: Could not initialize Styles in test setup. " + e);
            // Continue tests, but they might fail if Style logic is crucial
        }
    }

    @Test
    @DisplayName("Initial state should be correct")
    void testInitialState() {
        assertEquals("Test Presentation", presentation.getTitle(), "Initial title should match constructor");
        assertEquals(0, presentation.getSize(), "Initial presentation should have 0 slides");
        assertEquals(0, presentation.getSlideNumber(), "Initial slide number should be 0");
        assertNull(presentation.getCurrentSlide(), "Current slide should be null initially");
    }

    @Test
    @DisplayName("Adding slides should increase size")
    void testAddSlide() {
        presentation.addSlide(slide1);
        assertEquals(1, presentation.getSize(), "Size should be 1 after adding one slide");
        presentation.addSlide(slide2);
        assertEquals(2, presentation.getSize(), "Size should be 2 after adding two slides");
    }

    @Test
    @DisplayName("Getting slides by number should work")
    void testGetSlide() {
        presentation.addSlide(slide1);
        presentation.addSlide(slide2);
        assertSame(slide1, presentation.getSlide(0), "getSlide(0) should return the first slide");
        assertSame(slide2, presentation.getSlide(1), "getSlide(1) should return the second slide");
    }

    @Test
    @DisplayName("Getting slide with invalid index should throw exception")
    void testGetSlideOutOfBounds() {
        presentation.addSlide(slide1);
        assertThrows(IndexOutOfBoundsException.class, () -> presentation.getSlide(-1), "Getting slide -1 should throw IndexOutOfBoundsException");
        assertThrows(IndexOutOfBoundsException.class, () -> presentation.getSlide(1), "Getting slide 1 (when size is 1) should throw IndexOutOfBoundsException");
    }

    @Test
    @DisplayName("Setting and getting title should work")
    void testSetTitle() {
        presentation.setTitle("New Title");
        assertEquals("New Title", presentation.getTitle(), "getTitle should return the new title");
    }

    @Test
    @DisplayName("Setting slide number should update current slide and notify observers")
    void testSetSlideNumber() {
        TestObserver observer = new TestObserver();
        presentation.addObserver(observer);
        presentation.addSlide(slide1);
        presentation.addSlide(slide2);

        presentation.setSlideNumber(1);
        assertEquals(1, presentation.getSlideNumber(), "Slide number should be updated to 1");
        assertSame(slide2, presentation.getCurrentSlide(), "Current slide should be slide 2");
        assertTrue(observer.wasUpdated(), "Observer should be notified on setSlideNumber");

        observer.reset();
        presentation.setSlideNumber(0);
        assertEquals(0, presentation.getSlideNumber(), "Slide number should be updated to 0");
        assertSame(slide1, presentation.getCurrentSlide(), "Current slide should be slide 1");
        assertTrue(observer.wasUpdated(), "Observer should be notified on setSlideNumber");
    }

    @Test
    @DisplayName("Setting invalid slide number should not change state")
    void testSetInvalidSlideNumber() {
        TestObserver observer = new TestObserver();
        presentation.addObserver(observer);
        presentation.addSlide(slide1);
        presentation.setSlideNumber(0); // Start at 0

        observer.reset();
        presentation.setSlideNumber(-1); // Try setting invalid number
        assertEquals(0, presentation.getSlideNumber(), "Slide number should remain 0 after setting to -1");
        assertFalse(observer.wasUpdated(), "Observer should not be notified for invalid slide number -1");

        observer.reset();
        presentation.setSlideNumber(1); // Try setting invalid number (out of bounds)
        assertEquals(0, presentation.getSlideNumber(), "Slide number should remain 0 after setting to 1");
        assertFalse(observer.wasUpdated(), "Observer should not be notified for invalid slide number 1");
    }


    @Test
    @DisplayName("Next slide should advance correctly and notify observers")
    void testNextSlide() {
        TestObserver observer = new TestObserver();
        presentation.addObserver(observer);
        presentation.addSlide(slide1);
        presentation.addSlide(slide2);
        presentation.setSlideNumber(0); // Start at 0

        observer.reset();
        presentation.nextSlide();
        assertEquals(1, presentation.getSlideNumber(), "Slide number should advance to 1");
        assertTrue(observer.wasUpdated(), "Observer should be notified on nextSlide");

        observer.reset();
        presentation.nextSlide(); // Try to go past the end
        assertEquals(1, presentation.getSlideNumber(), "Slide number should stay at 1 (last slide)");
        assertFalse(observer.wasUpdated(), "Observer should not be notified when already at last slide");
    }

    @Test
    @DisplayName("Next slide on empty presentation should do nothing")
    void testNextSlideEmpty() {
        TestObserver observer = new TestObserver();
        presentation.addObserver(observer);

        presentation.nextSlide();
        assertEquals(0, presentation.getSlideNumber(), "Slide number should remain 0");
        assertFalse(observer.wasUpdated(), "Observer should not be notified");
    }


    @Test
    @DisplayName("Previous slide should go back correctly and notify observers")
    void testPrevSlide() {
        TestObserver observer = new TestObserver();
        presentation.addObserver(observer);
        presentation.addSlide(slide1);
        presentation.addSlide(slide2);
        presentation.setSlideNumber(1); // Start at 1

        observer.reset();
        presentation.prevSlide();
        assertEquals(0, presentation.getSlideNumber(), "Slide number should go back to 0");
        assertTrue(observer.wasUpdated(), "Observer should be notified on prevSlide");

        observer.reset();
        presentation.prevSlide(); // Try to go before the start
        assertEquals(0, presentation.getSlideNumber(), "Slide number should stay at 0 (first slide)");
        assertFalse(observer.wasUpdated(), "Observer should not be notified when already at first slide");
    }

    @Test
    @DisplayName("Previous slide on empty presentation should do nothing")
    void testPrevSlideEmpty() {
        TestObserver observer = new TestObserver();
        presentation.addObserver(observer);

        presentation.prevSlide();
        assertEquals(0, presentation.getSlideNumber(), "Slide number should remain 0");
        assertFalse(observer.wasUpdated(), "Observer should not be notified");
    }

    @Test
    @DisplayName("Clear should remove all slides, reset number, and notify observers")
    void testClear() {
        TestObserver observer = new TestObserver();
        presentation.addObserver(observer);
        presentation.addSlide(slide1);
        presentation.addSlide(slide2);
        presentation.setSlideNumber(1);

        observer.reset();
        presentation.clear();
        assertEquals(0, presentation.getSize(), "Size should be 0 after clear");
        assertEquals(0, presentation.getSlideNumber(), "Slide number should be reset to 0 after clear");
        assertNull(presentation.getCurrentSlide(), "Current slide should be null after clear");
        assertTrue(observer.wasUpdated(), "Observer should be notified on clear");
    }

    @Test
    @DisplayName("Getting current slide should return correct slide based on number")
    void testGetCurrentSlide() {
        assertNull(presentation.getCurrentSlide(), "Current slide is null when empty");
        presentation.addSlide(slide1);
        presentation.addSlide(slide2);

        presentation.setSlideNumber(0);
        assertSame(slide1, presentation.getCurrentSlide(), "Current slide should be slide1 when number is 0");

        presentation.setSlideNumber(1);
        assertSame(slide2, presentation.getCurrentSlide(), "Current slide should be slide2 when number is 1");
    }

    @Test
    @DisplayName("Observer management should work")
    void testObserverManagement() {
        AtomicBoolean notified = new AtomicBoolean(false);
        Observer obs = () -> notified.set(true);

        presentation.addObserver(obs);
        presentation.addSlide(slide1); // Should not notify
        assertFalse(notified.get(), "Observer should not be notified just by adding slide");

        presentation.setSlideNumber(0); // Should notify
        assertTrue(notified.get(), "Observer should be notified by setSlideNumber");

        notified.set(false); // Reset flag
        presentation.removeObserver(obs);
        // Need to allow nextSlide to potentially change state
        presentation.addSlide(slide2);
        if (presentation.getSlideNumber() < presentation.getSize() - 1) {
            presentation.nextSlide(); // This should not notify removed observer
        }
        assertFalse(notified.get(), "Removed observer should not be notified");
    }

    @Test
    @DisplayName("isFirstSlide should return correct boolean")
    void testIsFirstSlide() {
        assertTrue(presentation.isFirstSlide(), "Should be first slide when empty (index 0)");
        presentation.addSlide(slide1);
        assertTrue(presentation.isFirstSlide(), "Should be first slide with one slide at index 0");
        presentation.addSlide(slide2);
        assertTrue(presentation.isFirstSlide(), "Should be first slide with two slides at index 0");
        presentation.setSlideNumber(1);
        assertFalse(presentation.isFirstSlide(), "Should not be first slide at index 1");
    }

    @Test
    @DisplayName("isLastSlide should return correct boolean")
    void testIsLastSlide() {
        assertFalse(presentation.isLastSlide(), "Should not be last slide when empty");
        presentation.addSlide(slide1);
        assertTrue(presentation.isLastSlide(), "Should be last slide with one slide at index 0");
        presentation.addSlide(slide2);
        assertFalse(presentation.isLastSlide(), "Should not be last slide with two slides at index 0");
        presentation.setSlideNumber(1);
        assertTrue(presentation.isLastSlide(), "Should be last slide with two slides at index 1");
    }
}