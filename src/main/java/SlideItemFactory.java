/**
 * Interface for factories that create SlideItem objects.
 * This follows the Factory Method pattern principle.
 */
public interface SlideItemFactory {
    /**
     * Creates a SlideItem.
     *
     * @param level The indentation level of the item.
     * @param data  The data needed to create the item (e.g., text content or image filename).
     * @return The created SlideItem.
     */
    SlideItem createSlideItem(int level, String data);
}
