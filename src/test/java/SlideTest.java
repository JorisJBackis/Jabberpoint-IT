import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SlideTest {

    private Slide slide;

    @BeforeEach
    void setUp() {
        slide = new Slide();
        // Note: Style creation might be needed if tests indirectly depend on them (e.g., drawing)
        Style.createStyles();
    }

    @Test
    @DisplayName("Initial state should be empty")
    void testInitialState() {
        assertNull(slide.getTitle(), "Initial title should be null");
        assertEquals(0, slide.getSize(), "Initial slide should have 0 items");
        assertTrue(slide.getSlideItems().isEmpty(), "Initial slide items vector should be empty");
    }

    @Test
    @DisplayName("Setting and getting title should work")
    void testSetAndGetTitle() {
        slide.setTitle("My Slide Title");
        assertEquals("My Slide Title", slide.getTitle(), "getTitle should return the set title");
    }

    @Test
    @DisplayName("Appending different SlideItem types should increase size")
    void testAppendSlideItem() {
        assertEquals(0, slide.getSize(), "Size should be 0 initially");

        TextItem textItem = new TextItem(1, "Hello World");
        slide.append(textItem);
        assertEquals(1, slide.getSize(), "Size should be 1 after appending TextItem");
        assertSame(textItem, slide.getSlideItem(0), "getSlideItem(0) should return the TextItem");

        // Note: BitmapItem might print an error if the file doesn't exist,
        // but the test focuses on adding it to the slide structure.
        BitmapItem bitmapItem = new BitmapItem(2, "nonexistent.jpg");
        slide.append(bitmapItem);
        assertEquals(2, slide.getSize(), "Size should be 2 after appending BitmapItem");
        assertSame(bitmapItem, slide.getSlideItem(1), "getSlideItem(1) should return the BitmapItem");
    }

    @Test
    @DisplayName("Appending text using convenience method should work")
    void testAppendTextConvenience() {
        slide.append(1, "Level 1 Text");
        assertEquals(1, slide.getSize(), "Size should be 1 after append(level, text)");

        SlideItem item = slide.getSlideItem(0);
        assertInstanceOf(TextItem.class, item, "Appended item should be a TextItem");
        assertEquals(1, item.getLevel(), "TextItem level should be correct");
        assertEquals("Level 1 Text", ((TextItem) item).getText(), "TextItem text should be correct");
    }

    @Test
    @DisplayName("Getting slide item by index should work")
    void testGetSlideItem() {
        TextItem text1 = new TextItem(1, "First");
        TextItem text2 = new TextItem(2, "Second");
        slide.append(text1);
        slide.append(text2);

        assertSame(text1, slide.getSlideItem(0), "getSlideItem(0) should return first item");
        assertSame(text2, slide.getSlideItem(1), "getSlideItem(1) should return second item");
    }

    @Test
    @DisplayName("Getting slide item with invalid index should throw exception")
    void testGetSlideItemOutOfBounds() {
        TextItem text1 = new TextItem(1, "First");
        slide.append(text1);

        // Note: Vector throws ArrayIndexOutOfBoundsException for invalid index
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> slide.getSlideItem(-1), "Getting item -1 should throw");
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> slide.getSlideItem(1), "Getting item 1 (size is 1) should throw");
    }

    @Test
    @DisplayName("Getting all slide items should return the correct vector")
    void testGetSlideItems() {
        assertTrue(slide.getSlideItems().isEmpty(), "Initially, getSlideItems should be empty");

        TextItem text1 = new TextItem(1, "One");
        BitmapItem image1 = new BitmapItem(1, "img.png");
        slide.append(text1);
        slide.append(image1);

        java.util.Vector<SlideItem> items = slide.getSlideItems();
        assertEquals(2, items.size(), "getSlideItems should return vector of size 2");
        assertSame(text1, items.get(0), "First element in vector should be text1");
        assertSame(image1, items.get(1), "Second element in vector should be image1");
    }

    @Test
    @DisplayName("Getting size should reflect number of items")
    void testGetSize() {
        assertEquals(0, slide.getSize(), "Initial size is 0");
        slide.append(new TextItem(1, "Item 1"));
        assertEquals(1, slide.getSize(), "Size is 1 after one item");
        slide.append(new TextItem(2, "Item 2"));
        assertEquals(2, slide.getSize(), "Size is 2 after two items");
    }
}