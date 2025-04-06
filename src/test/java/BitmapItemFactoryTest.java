import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BitmapItemFactoryTest {
    @Test
    void createSlideItemShouldReturnBitmapItem() {
        SlideItemFactory factory = new BitmapItemFactory();
        // Use a known image or expect potential loading errors logged to console
        SlideItem item = factory.createSlideItem(2, "JabberPoint.jpg");
        assertNotNull(item);
        assertTrue(item instanceof BitmapItem, "Factory should create a BitmapItem");
        assertEquals(2, item.getLevel());
        assertEquals("JabberPoint.jpg", ((BitmapItem) item).getName());
    }
}