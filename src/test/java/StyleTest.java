import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class StyleTest {

    // Styles are static, create them once for all tests in this class
    @BeforeAll
    static void setupStyles() {
        Style.createStyles();
    }

    @Test
    @DisplayName("GetStyle should return correct style for level 0")
    void testGetStyleLevel0() {
        Style style = Style.getStyle(0);
        assertNotNull(style, "Style for level 0 should not be null");
        // Example check based on known initialization values in Style.createStyles()
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
    @DisplayName("GetStyle for negative level should throw ArrayIndexOutOfBoundsException")
    void testGetStyleNegativeLevel() {
        // The current implementation directly accesses styles[level] without checking < 0
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            Style.getStyle(-1);
        }, "Requesting style for level -1 should throw ArrayIndexOutOfBoundsException");

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            Style.getStyle(-5);
        }, "Requesting style for level -5 should throw ArrayIndexOutOfBoundsException");
    }

    @Test
    @DisplayName("toString should return a non-empty string representation")
    void testToString() {
        Style style = Style.getStyle(2);
        String styleString = style.toString();
        assertNotNull(styleString, "toString() should not return null");
        assertFalse(styleString.isEmpty(), "toString() should return a non-empty string");
        // Optional: More specific check if format is stable
        assertTrue(styleString.contains("50") && styleString.contains("black") && styleString.contains("36"),
                "toString() should contain indent, color, and font size");
    }

    // Note: Testing getFont() requires a Graphics context or assumptions about rendering,
    // which goes beyond typical unit testing. Testing the style properties themselves is sufficient here.
}