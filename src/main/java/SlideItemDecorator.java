import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;

/**
 * Abstract base class for Decorators that add responsibilities to SlideItems.
 * It conforms to the SlideItem interface and wraps another SlideItem.
 */
public abstract class SlideItemDecorator extends SlideItem {

    // The SlideItem being wrapped by this decorator.
    protected SlideItem decoratedItem;

    /**
     * Constructor for the SlideItemDecorator.
     * @param decoratedItem The SlideItem to be decorated.
     */
    public SlideItemDecorator(SlideItem decoratedItem) {
        // A decorator logically has the same level as the item it decorates.
        super(decoratedItem.getLevel());
        this.decoratedItem = decoratedItem;
    }

    /**
     * Delegates the getBoundingBox call to the decorated SlideItem.
     * Concrete decorators might override this if the decoration affects the size.
     */
    @Override
    public Rectangle getBoundingBox(Graphics g, ImageObserver observer, float scale, Style style) {
        // Basic delegation: assume decoration doesn't change bounds (may need override)
        return decoratedItem.getBoundingBox(g, observer, scale, style);
    }

    /**
     * Delegates the draw call to the decorated SlideItem.
     * Concrete decorators will override this to add their specific behavior
     * before or after delegation.
     */
    @Override
    public void draw(int x, int y, float scale, Graphics g, Style style, ImageObserver observer) {
        // Basic delegation: draw the wrapped item.
        decoratedItem.draw(x, y, scale, g, style, observer);
    }

    // Optional: You might want to delegate other SlideItem methods if they exist
    // For example, if SlideItem had a 'getText()' or 'getName()', you might add:
    // public String getText() { return (decoratedItem instanceof TextItem) ? ((TextItem)decoratedItem).getText() : null; }
    // public String getName() { return (decoratedItem instanceof BitmapItem) ? ((BitmapItem)decoratedItem).getName() : null; }
}