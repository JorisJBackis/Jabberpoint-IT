import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TextItemFactoryTest {
    @Test
    void createSlideItemShouldReturnTextItem() {
        SlideItemFactory factory = new TextItemFactory();
        SlideItem item = factory.createSlideItem(1, "test text");
        assertNotNull(item);
        assertTrue(item instanceof TextItem, "Factory should create a TextItem");
        assertEquals(1, item.getLevel());
        assertEquals("test text", ((TextItem) item).getText());
    }
}