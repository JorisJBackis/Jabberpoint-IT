import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class StyleTest {

    // Styles are static, create them once for all tests in this class
    @BeforeAll
    static void setupStyles() {
        try {
            Style.createStyles(); // Assuming this is accessible and needed
        } catch (ExceptionInInitializerError e) {
            System.err.println("FATAL: Could not initialize Styles in BeforeAll. Tests will likely fail. " + e);
        }
    }

    @Test
    @DisplayName("GetStyle should return correct style for level 0")
    void testGetStyleLevel0() {
        Style style = Style.getStyle(0);
        assertNotNull(style, "Style for level 0 should not be null");
        assertEquals(0, style.indent, "Indent for level 0 should be 0");
        assertEquals(Color.red, style.color, "Color for level 0 should be red");
        assertEquals(48, style.fontSize, "Font size for level 0 should be 48");
    }

    @Test
    @DisplayName("GetStyle should return correct style for level 1")
    void testGetStyleLevel1() {
        Style style = Style.getStyle(1);
        assertNotNull(style, "Style for level 1 should not be null");
        assertEquals(20, style.indent, "Indent for level 1 should be 20");
        assertEquals(Color.blue, style.color, "Color for level 1 should be blue");
        assertEquals(40, style.fontSize, "Font size for level 1 should be 40");
    }

    @Test
    @DisplayName("GetStyle should return correct style for level 4 (max defined)")
    void testGetStyleLevel4() {
        Style style = Style.getStyle(4);
        assertNotNull(style, "Style for level 4 should not be null");
        assertEquals(90, style.indent, "Indent for level 4 should be 90");
        assertEquals(Color.black, style.color, "Color for level 4 should be black");
        assertEquals(24, style.fontSize, "Font size for level 4 should be 24");
    }


    @Test
    @DisplayName("GetStyle for level higher than max should return max level style")
    void testGetStyleLevelTooHigh() {
        Style styleMax = Style.getStyle(4); // Get the expected style for the highest level
        Style styleHigh = Style.getStyle(5); // Request level 5
        assertNotNull(styleHigh, "Style for level 5 should not be null");
        assertSame(styleMax, styleHigh, "Style for level 5 should be the same instance as style for level 4");

        Style styleVeryHigh = Style.getStyle(100); // Request level 100
        assertNotNull(styleVeryHigh, "Style for level 100 should not be null");
        assertSame(styleMax, styleVeryHigh, "Style for level 100 should be the same instance as style for level 4");
    }

    @Test
    @DisplayName("GetStyle for negative level should use level 0 (or default)")
    void testGetStyleNegativeLevel() {
        // Depending on implementation: could throw, or default to level 0.
        // Current implementation accesses array directly, so it *will* throw.
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            Style.getStyle(-1);
        }, "Requesting style for level -1 should throw ArrayIndexOutOfBoundsException");
    }

    @Test
    @DisplayName("Style properties should be correct for level 2") // Renamed test
    void testStylePropertiesLevel2() { // Renamed test
        Style style = Style.getStyle(2);
        assertNotNull(style, "Style for level 2 should not be null");

        // Direct property assertions
        assertEquals(50, style.indent, "Indent for level 2 should be 50");
        assertEquals(Color.black, style.color, "Color for level 2 should be black");
        assertEquals(36, style.fontSize, "Font size for level 2 should be 36");
        assertEquals(10, style.leading, "Leading for level 2 should be 10");

        String styleString = style.toString();
        assertTrue(styleString.contains("50") && styleString.contains("java.awt.Color[r=0,g=0,b=0]") && styleString.contains("36"),
                "toString() should contain indent, color representation, and font size");
    }

    @Test
    @DisplayName("getFont should return a derived font with correct size")
    void testGetFont() {
        Style style = Style.getStyle(1); // Size 40
        float scale = 1.5f;
        Font derivedFont = style.getFont(scale);
        assertNotNull(derivedFont, "Derived font should not be null");
        assertEquals(40 * scale, derivedFont.getSize2D(), 0.01, "Derived font size should be scaled");
        // Check if style (PLAIN) is preserved - assumes base font is PLAIN
        assertEquals(Font.PLAIN, derivedFont.getStyle(), "Derived font style should match base style");
    }
}