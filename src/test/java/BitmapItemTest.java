import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import static org.junit.jupiter.api.Assertions.*;

class BitmapItemTest {

    @BeforeEach
    void setUp() {
        // Ensure styles are available
        try { Style.createStyles(); } catch (ExceptionInInitializerError e) { /* ignore */ }
    }

    @Test
    void constructorWithNullNameShouldHandleGracefully() {
        // Expect no exception, but image/name should be default/invalid
        BitmapItem item = assertDoesNotThrow(() -> new BitmapItem(1, null));
        assertNotNull(item);
        assertEquals("INVALID_NULL_NAME", item.getName()); // Check placeholder name
        assertEquals(1, item.getLevel());
    }

    @Test
    void constructorWithNonExistentFileShouldHandleGracefully() {
        String nonExistentFileName = "non_existent_image_for_testing.png";
        // Expect no exception during construction, error logged to stderr
        BitmapItem item = assertDoesNotThrow(() -> new BitmapItem(2, nonExistentFileName));
        assertNotNull(item);
        assertEquals(nonExistentFileName, item.getName());
        assertEquals(2, item.getLevel());
        // Internal bufferedImage should be null
    }

    @Test
    void getNameShouldReturnCorrectName() {
        String expectedName = "JabberPoint.jpg"; // Assumes this exists
        BitmapItem item = assertDoesNotThrow(() -> new BitmapItem(0, expectedName));
        assertEquals(expectedName, item.getName());
    }

    @Test
    void testToStringFormat() {
        BitmapItem item = new BitmapItem(3, "image.png");
        assertEquals("BitmapItem[3,image.png]", item.toString());
    }
}