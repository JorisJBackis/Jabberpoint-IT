import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BitmapItemTest {
    @Test
    void constructorWithNullNameShouldHandleGracefully() {
        // Expect no exception, but image/name should be default/invalid
        BitmapItem item = assertDoesNotThrow(() -> new BitmapItem(1, null));
        assertNotNull(item);
        assertEquals("INVALID_NULL_NAME", item.getName()); // Check placeholder name
        assertEquals(1, item.getLevel());
    }

    @Test
    void getNameShouldReturnCorrectName() {
        String expectedName = "JabberPoint.jpg";
        BitmapItem item = new BitmapItem(0, expectedName);
        assertEquals(expectedName, item.getName());
    }

    @Test
    void testToStringFormat() {
        BitmapItem item = new BitmapItem(3, "image.png");
        assertEquals("BitmapItem[3,image.png]", item.toString());
    }

}