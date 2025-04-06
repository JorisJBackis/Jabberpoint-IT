import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.ImageObserver;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit tests for the abstract SlideItemDecorator class.
 * Uses a simple stub (DummySlideItem) to test delegation.
 */
class SlideItemDecoratorTest {

    // Simple stub/dummy SlideItem for testing delegation
    static class DummySlideItem extends SlideItem {
        boolean drawCalled = false;
        boolean boundingBoxCalled = false;
        Rectangle dummyBounds = new Rectangle(10, 20, 30, 40);

        public DummySlideItem(int level) { super(level); }

        @Override
        public Rectangle getBoundingBox(Graphics g, ImageObserver observer, float scale, Style style) {
            boundingBoxCalled = true;
            return dummyBounds; // Return predictable bounds
        }
        @Override
        public void draw(int x, int y, float scale, Graphics g, Style style, ImageObserver observer) {
            drawCalled = true;
        }
        // Reset flags for verification
        public void resetFlags() { drawCalled = false; boundingBoxCalled = false; }
    }

    private DummySlideItem dummyItem;
    private SlideItemDecorator concreteDecorator; // Use an anonymous inner class

    @BeforeEach
    void setUp() {
        dummyItem = new DummySlideItem(3);
        // Create an anonymous concrete instance to test the abstract class logic
        concreteDecorator = new SlideItemDecorator(dummyItem) {
            // No behavior override needed for testing base class delegation
        };
        // Ensure styles exist if methods under test use them
        try { Style.createStyles(); } catch (ExceptionInInitializerError e) {}
    }

    @Test
    @DisplayName("Constructor should set level from decorated item")
    void constructorShouldSetLevelFromDecoratedItem() {
        assertEquals(dummyItem.getLevel(), concreteDecorator.getLevel(), "Decorator level should match decorated item's level");
        assertEquals(3, concreteDecorator.getLevel());
    }

    @Test
    @DisplayName("getLevel should return correct level")
    void getLevelShouldReturnCorrectLevel() {
        assertEquals(3, concreteDecorator.getLevel());
    }


    @Test
    @DisplayName("getBoundingBox should delegate to decorated item")
    void getBoundingBoxShouldDelegate() {
        Style style = Style.getStyle(3);
        assertNotNull(style);
        dummyItem.resetFlags(); // Reset flag before call

        Rectangle bounds = concreteDecorator.getBoundingBox(null, null, 1.0f, style);

        assertTrue(dummyItem.boundingBoxCalled, "Decorated item's getBoundingBox should have been called");
        assertEquals(dummyItem.dummyBounds, bounds, "Should return the bounds from decorated item");
    }

    @Test
    @DisplayName("draw should delegate to decorated item")
    void drawShouldDelegate() {
        Style style = Style.getStyle(3);
        assertNotNull(style);
        dummyItem.resetFlags(); // Reset flag before call

        concreteDecorator.draw(5, 15, 1.0f, null, style, null);

        assertTrue(dummyItem.drawCalled, "Decorated item's draw should have been called");
    }
}