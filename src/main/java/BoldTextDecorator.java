import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute; // Still needed for AttributedString
import java.awt.font.TextLayout;
import java.awt.image.ImageObserver;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A Concrete Decorator that makes the text of a decorated TextItem appear bold.
 * This version applies a derived BOLD Font object to the AttributedString.
 */
public class BoldTextDecorator extends SlideItemDecorator {

    public BoldTextDecorator(SlideItem decoratedItem) {
        super(decoratedItem);
    }

    /**
     * Draws the decorated SlideItem. If it's a TextItem, it creates a bold
     * version of the style's font and applies it via AttributedString
     * before drawing the text using TextLayout.
     */
    @Override
    public void draw(int x, int y, float scale, Graphics g, Style style, ImageObserver observer) {
        // Ensure we are decorating a TextItem
        if (!(decoratedItem instanceof TextItem)) {
            super.draw(x, y, scale, g, style, observer); // Just draw non-text items normally
            return;
        }

        TextItem textItem = (TextItem) decoratedItem;
        String text = textItem.getText();

        if (text == null || text.length() == 0) {
            return; // Nothing to draw if text is empty
        }

        // --- Create AttributedString with a BOLD Font ---
        AttributedString attrStr = new AttributedString(text);

        // 1. Get the base font specified by the style
        Font baseFont = style.getFont(scale);

        // 2. Create a BOLD version of that font
        //    Using deriveFont with Font.BOLD is generally reliable.
        Font boldFont = baseFont.deriveFont(baseFont.getStyle() | Font.BOLD);

        // 3. Apply the *derived bold font* to the entire string
        attrStr.addAttribute(TextAttribute.FONT, boldFont, 0, text.length());

        // (Optional but good practice) Apply the style's color as a foreground attribute
        // This ensures the color is part of the AttributedString's definition.
        attrStr.addAttribute(TextAttribute.FOREGROUND, style.color, 0, text.length());
        // --- End Attribute Modification ---


        // --- Replicate TextLayout logic to draw the AttributedString ---
        Graphics2D g2d = (Graphics2D) g;
        FontRenderContext frc = g2d.getFontRenderContext();
        LineBreakMeasurer measurer = new LineBreakMeasurer(attrStr.getIterator(), frc);
        float wrappingWidth = (Slide.WIDTH - style.indent) * scale;
        Point pen = new Point(x + (int)(style.indent * scale),
                y + (int)(style.leading * scale));

        // Color is now set via TextAttribute.FOREGROUND, but setting it
        // on g2d is still a good fallback or default if the attribute wasn't set.
        g2d.setColor(style.color);

        while (measurer.getPosition() < text.length()) {
            TextLayout layout = measurer.nextLayout(wrappingWidth);
            pen.y += layout.getAscent();
            // TextLayout draws using the attributes (like font, color) in the AttributedString
            layout.draw(g2d, pen.x, pen.y);
            pen.y += layout.getDescent();
        }
        // --- End TextLayout Logic ---
    }


    /**
     * Gets the bounding box.
     * NOTE: Still using simple delegation. Accurate calculation remains complex.
     */
    @Override
    public Rectangle getBoundingBox(Graphics g, ImageObserver observer, float scale, Style style) {
        // Simple delegation.
        return decoratedItem.getBoundingBox(g, observer, scale, style);
    }

    /**
     * Provides a string representation, indicating it's a BoldDecorator.
     */
    @Override
    public String toString() {
        return "BoldDecorator[" + decoratedItem.toString() + "]";
    }
}