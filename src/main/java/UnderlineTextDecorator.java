import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.ImageObserver;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A Concrete Decorator that adds underlining.
 * This version cooperates better when stacked with BoldTextDecorator.
 */
public class UnderlineTextDecorator extends SlideItemDecorator {

    public UnderlineTextDecorator(SlideItem decoratedItem) {
        super(decoratedItem);
    }

    @Override
    public void draw(int x, int y, float scale, Graphics g, Style style, ImageObserver observer) {

        // --- Find the innermost item and check for decorators ---
        SlideItem innermostItem = this.decoratedItem;
        boolean needsBold = false;
        while (innermostItem instanceof SlideItemDecorator) {
            if (innermostItem instanceof BoldTextDecorator) {
                needsBold = true;
            }
            // Add checks for other decorators if needed
            innermostItem = ((SlideItemDecorator) innermostItem).decoratedItem;
        }
        // --- End unwrapping ---

        // Ensure the innermost item is actually a TextItem
        if (!(innermostItem instanceof TextItem)) {
            // If not text, delegate drawing down the original chain
            // This ensures non-text items or improperly stacked items are still drawn.
            decoratedItem.draw(x, y, scale, g, style, observer);
            return;
        }

        TextItem textItem = (TextItem) innermostItem;
        String text = textItem.getText();

        if (text == null || text.length() == 0) {
            return; // Nothing to draw
        }

        // --- Create AttributedString applying ALL necessary attributes ---
        AttributedString attrStr = new AttributedString(text);

        // 1. Get the base font from the style
        Font baseFont = style.getFont(scale);
        Font finalFont = baseFont; // Start with base font

        // 2. Apply bold if necessary
        if (needsBold) {
            finalFont = baseFont.deriveFont(baseFont.getStyle() | Font.BOLD);
        }
        // Apply the final calculated font (might be plain or bold)
        attrStr.addAttribute(TextAttribute.FONT, finalFont, 0, text.length());

        // 3. Apply underline (this decorator's main job)
        attrStr.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON, 0, text.length());

        // 4. Apply color
        attrStr.addAttribute(TextAttribute.FOREGROUND, style.color, 0, text.length());
        // --- End Attribute Combination ---


        // --- Replicate TextLayout logic to draw the combined AttributedString ---
        Graphics2D g2d = (Graphics2D) g;
        FontRenderContext frc = g2d.getFontRenderContext();
        LineBreakMeasurer measurer = new LineBreakMeasurer(attrStr.getIterator(), frc);
        float wrappingWidth = (Slide.WIDTH - style.indent) * scale;
        Point pen = new Point(x + (int)(style.indent * scale),
                y + (int)(style.leading * scale));

        g2d.setColor(style.color); // Set color as fallback

        while (measurer.getPosition() < text.length()) {
            TextLayout layout = measurer.nextLayout(wrappingWidth);
            pen.y += layout.getAscent();
            layout.draw(g2d, pen.x, pen.y);
            pen.y += layout.getDescent();
        }
        // --- End TextLayout Logic ---
    }

    /**
     * Gets the bounding box. Still using simple delegation.
     */
    @Override
    public Rectangle getBoundingBox(Graphics g, ImageObserver observer, float scale, Style style) {
        // Simple delegation.
        return decoratedItem.getBoundingBox(g, observer, scale, style);
    }

    @Override
    public String toString() {
        return "UnderlineDecorator[" + decoratedItem.toString() + "]";
    }
}