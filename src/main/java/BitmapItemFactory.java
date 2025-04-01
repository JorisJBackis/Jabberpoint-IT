/**
 * A concrete factory implementing SlideItemFactory to create BitmapItem objects.
 */
public class BitmapItemFactory implements SlideItemFactory {

    /**
     * Default constructor.
     */
    public BitmapItemFactory() {
        // No specific initialization needed for this factory.
    }

    /**
     * Creates a BitmapItem using the provided level and data string (as image name).
     *
     * @param level         The indentation level for the BitmapItem.
     * @param imageNameData The filename of the image for the BitmapItem.
     * @return A new BitmapItem.
     */
    @Override
    public SlideItem createSlideItem(int level, String imageNameData) {
        // The data string is interpreted as the image filename here.
        return new BitmapItem(level, imageNameData);
    }
}
