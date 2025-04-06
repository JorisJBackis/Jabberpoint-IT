import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit tests for BoldTextDecorator.
 */
class BoldTextDecoratorTest {

    // Use the same DummySlideItem from SlideItemDecoratorTest or create one here
    static class DummySlideItem extends SlideItem {
        boolean drawCalled = false;
        public DummySlideItem(int level) { super(level); }
        @Override public Rectangle getBoundingBox(Graphics g, ImageObserver observer, float scale, Style style) { return new Rectangle(); }
        @Override public void draw(int x, int y, float scale, Graphics g, Style style, ImageObserver observer) { drawCalled = true; }
        public void resetFlags() { drawCalled = false; }
    }


    private TextItem textItem;
    private BitmapItem bitmapItem; // Test handling of non-text items
    private BoldTextDecorator textDecorator;
    private BoldTextDecorator bitmapDecorator;
    private BoldTextDecorator nullTextDecorator;

    @BeforeEach
    void setUp() {
        textItem = new TextItem(1, "Test Bold");
        // Use a name that likely won't load an image to test non-text path
        bitmapItem = new BitmapItem(2, "nonexistent_for_test.jpg");
        textDecorator = new BoldTextDecorator(textItem);
        bitmapDecorator = new BoldTextDecorator(bitmapItem);
        nullTextDecorator = new BoldTextDecorator(new TextItem(0, null)); // Decorator with null text item

        try { Style.createStyles(); } catch (ExceptionInInitializerError e) {}
    }

    @Test
    @DisplayName("Constructor should set correct level")
    void constructorShouldSetLevel() {
        assertEquals(1, textDecorator.getLevel());
        assertEquals(2, bitmapDecorator.getLevel());
        assertEquals(0, nullTextDecorator.getLevel());
    }

    @Test
    @DisplayName("toString should indicate bold decoration")
    void toStringShouldIndicateDecoration() {
        assertTrue(textDecorator.toString().startsWith("BoldDecorator["));
        assertTrue(textDecorator.toString().contains(textItem.toString()));
    }

    @Test
    @DisplayName("draw should not crash when decorating TextItem")
    void drawWithTextItemShouldNotCrash() {
        Style style = Style.getStyle(textDecorator.getLevel());
        assertNotNull(style);
        // Needs a Graphics object to avoid NullPointerException in draw's TextLayout logic
        BufferedImage dummyImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D dummyGraphics = dummyImg.createGraphics();
        assertDoesNotThrow(() -> textDecorator.draw(0, 0, 1.0f, dummyGraphics, style, null));
        dummyGraphics.dispose();
    }

    @Test
    @DisplayName("draw should delegate when decorating non-TextItem")
    void drawWithNonTextItemShouldDelegate() {
        Style style = Style.getStyle(bitmapDecorator.getLevel());
        assertNotNull(style);
        // Use a DummySlideItem to track if its draw method is called
        DummySlideItem dummyBitmap = new DummySlideItem(2);
        BoldTextDecorator decoratorOnDummy = new BoldTextDecorator(dummyBitmap);

        BufferedImage dummyImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D dummyGraphics = dummyImg.createGraphics();
        dummyBitmap.resetFlags();

        assertDoesNotThrow(() -> decoratorOnDummy.draw(0, 0, 1.0f, dummyGraphics, style, null));
        assertTrue(dummyBitmap.drawCalled, "Decorated non-text item's draw should be called");

        dummyGraphics.dispose();
    }

    @Test
    @DisplayName("draw should handle null text in decorated TextItem")
    void drawWithNullTextItemShouldNotCrash() {
        Style style = Style.getStyle(nullTextDecorator.getLevel());
        assertNotNull(style);
        BufferedImage dummyImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D dummyGraphics = dummyImg.createGraphics();
        assertDoesNotThrow(() -> nullTextDecorator.draw(0, 0, 1.0f, dummyGraphics, style, null));
        dummyGraphics.dispose();
    }

}