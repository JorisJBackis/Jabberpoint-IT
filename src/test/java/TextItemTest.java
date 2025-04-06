import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the TextItem class.
 */
class TextItemTest {

    @BeforeEach
    void setUp() {
        // Ensure styles are available if any tested method relies on them indirectly
        try { Style.createStyles(); } catch (ExceptionInInitializerError e) {
            System.err.println("Warning: Could not initialize Styles in TextItemTest setup.");
        }
    }

    @Test
    @DisplayName("Default constructor should create item with level 0 and default text")
    void testDefaultConstructor() {
        TextItem item = new TextItem();
        assertEquals(0, item.getLevel(), "Default level should be 0");
        // Check against the known default text constant if accessible, otherwise the literal string
        assertEquals("No Text Given", item.getText(), "Default text should be 'No Text Given'");
    }

    @Test
    @DisplayName("Constructor with level and text should set properties correctly")
    void testLevelAndTextConstructor() {
        String testString = "This is my test text.";
        TextItem item = new TextItem(3, testString);
        assertEquals(3, item.getLevel(), "Level should be set by constructor");
        assertEquals(testString, item.getText(), "Text should be set by constructor");
    }

    @Test
    @DisplayName("getText should return empty string when constructed with null")
    void testGetTextWithNullInput() {
        TextItem item = new TextItem(1, null);
        // The current implementation returns "" if internal text is null
        assertEquals("", item.getText(), "getText should return empty string if internal text is null");
    }

    @Test
    @DisplayName("getText should return empty string when constructed with empty string")
    void testGetTextWithEmptyInput() {
        TextItem item = new TextItem(1, "");
        assertEquals("", item.getText(), "getText should return empty string for empty constructor string");
    }


    @Test
    @DisplayName("getLevel should return the correct level")
    void testGetLevel() {
        TextItem item0 = new TextItem(0, "Level 0");
        TextItem item5 = new TextItem(5, "Level 5"); // Use a level potentially out of Style bounds
        assertEquals(0, item0.getLevel());
        assertEquals(5, item5.getLevel());
    }

    @Test
    @DisplayName("toString should return correct format")
    void testToStringFormat() {
        TextItem item = new TextItem(2, "Some Content");
        assertEquals("TextItem[2,Some Content]", item.toString());
    }

    @Test
    @DisplayName("toString should handle empty text")
    void testToStringEmptyText() {
        TextItem item = new TextItem(1, "");
        assertEquals("TextItem[1,]", item.toString());
    }

    @Test
    @DisplayName("toString should handle default constructor text")
    void testToStringDefaultConstructor() {
        TextItem item = new TextItem();
        assertEquals("TextItem[0,No Text Given]", item.toString());
    }
}