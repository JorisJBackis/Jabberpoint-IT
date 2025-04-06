import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit tests for UnderlineTextDecorator.
 */
class UnderlineTextDecoratorTest {

    // Use the same DummySlideItem from SlideItemDecoratorTest or create one here
    static class DummySlideItem extends SlideItem {
        boolean drawCalled = false;
        boolean boundingBoxCalled = false; // Added for bounding box test
        Rectangle dummyBounds = new Rectangle(5,5,5,5); // Different dummy bounds

        public DummySlideItem(int level) { super(level); }
        @Override public Rectangle getBoundingBox(Graphics g, ImageObserver observer, float scale, Style style) { boundingBoxCalled = true; return dummyBounds; }
        @Override public void draw(int x, int y, float scale, Graphics g, Style style, ImageObserver observer) { drawCalled = true; }
        public void resetFlags() { drawCalled = false; boundingBoxCalled = false; }
    }

    private TextItem textItem;
    private BitmapItem bitmapItem;
    private UnderlineTextDecorator textDecorator;
    private UnderlineTextDecorator bitmapDecorator;
    private UnderlineTextDecorator nullTextDecorator;
    private UnderlineTextDecorator stackedDecorator; // For testing unwrapping

    @BeforeEach
    void setUp() {
        textItem = new TextItem(1, "Test Underline");
        bitmapItem = new BitmapItem(2, "nonexistent_for_test.jpg");
        textDecorator = new UnderlineTextDecorator(textItem);
        bitmapDecorator = new UnderlineTextDecorator(bitmapItem);
        nullTextDecorator = new UnderlineTextDecorator(new TextItem(0, null));

        // Stacking: Underline(Bold(Text))
        BoldTextDecorator boldInner = new BoldTextDecorator(new TextItem(3, "Stacked"));
        stackedDecorator = new UnderlineTextDecorator(boldInner);

        try { Style.createStyles(); } catch (ExceptionInInitializerError e) {}
    }

    @Test
    @DisplayName("Constructor should set correct level")
    void constructorShouldSetLevel() {
        assertEquals(1, textDecorator.getLevel());
        assertEquals(2, bitmapDecorator.getLevel());
        assertEquals(0, nullTextDecorator.getLevel());
        assertEquals(3, stackedDecorator.getLevel());
    }

    @Test
    @DisplayName("toString should indicate underline decoration")
    void toStringShouldIndicateDecoration() {
        assertTrue(textDecorator.toString().startsWith("UnderlineDecorator["));
        assertTrue(textDecorator.toString().contains(textItem.toString()));
        assertTrue(stackedDecorator.toString().startsWith("UnderlineDecorator[BoldDecorator["));
    }

    @Test
    @DisplayName("draw should not crash when decorating TextItem")
    void drawWithTextItemShouldNotCrash() {
        Style style = Style.getStyle(textDecorator.getLevel());
        assertNotNull(style);
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
        DummySlideItem dummyBitmap = new DummySlideItem(2);
        UnderlineTextDecorator decoratorOnDummy = new UnderlineTextDecorator(dummyBitmap);

        BufferedImage dummyImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D dummyGraphics = dummyImg.createGraphics();
        dummyBitmap.resetFlags();

        assertDoesNotThrow(() -> decoratorOnDummy.draw(0, 0, 1.0f, dummyGraphics, style, null));
        // Verify the *original* decorated item's draw was called eventually
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

    @Test
    @DisplayName("draw with stacked decorators should not crash")
    void drawWithStackedDecoratorsShouldNotCrash() {
        Style style = Style.getStyle(stackedDecorator.getLevel());
        assertNotNull(style);
        BufferedImage dummyImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D dummyGraphics = dummyImg.createGraphics();
        // Checks if the unwrapping and attribute combination logic runs
        assertDoesNotThrow(() -> stackedDecorator.draw(0, 0, 1.0f, dummyGraphics, style, null));
        dummyGraphics.dispose();
    }

    @Test
    @DisplayName("getBoundingBox should delegate to decorated item")
    void getBoundingBoxShouldDelegate() {
        Style style = Style.getStyle(1);
        assertNotNull(style);
        DummySlideItem dummyText = new DummySlideItem(1);
        UnderlineTextDecorator decoratorOnDummy = new UnderlineTextDecorator(dummyText);
        dummyText.resetFlags();

        Rectangle bounds = decoratorOnDummy.getBoundingBox(null, null, 1.0f, style);
        assertTrue(dummyText.boundingBoxCalled, "Decorated item's getBoundingBox should be called");
        assertEquals(dummyText.dummyBounds, bounds);
    }
}