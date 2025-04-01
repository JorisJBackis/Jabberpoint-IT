/**
 * A concrete factory implementing SlideItemFactory to create TextItem objects.
 */
public class TextItemFactory implements SlideItemFactory {

    /**
     * Default constructor.
     */
    public TextItemFactory() {
        // No specific initialization needed for this factory.
    }

    /**
     * Creates a TextItem using the provided level and data string.
     *
     * @param level The indentation level for the TextItem.
     * @param textData The string content for the TextItem.
     * @return A new TextItem.
     */
    @Override
    public SlideItem createSlideItem(int level, String textData) {
        return new TextItem(level, textData);
    }
}
