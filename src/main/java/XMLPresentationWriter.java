import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Vector; // Assuming Slide still uses Vector

/**
 * Concrete implementation of PresentationWriter for saving presentations to XML files.
 * Handles unwrapping decorators to save correct attributes.
 * Implements SRP by focusing only on writing XML.
 */
public class XMLPresentationWriter implements PresentationWriter {

    // --- Copied Constants ---
    protected static final String SHOWTITLE = "showtitle";
    protected static final String SLIDETITLE = "title";
    protected static final String SLIDE = "slide";
    protected static final String ITEM = "item";
    protected static final String LEVEL = "level";
    protected static final String KIND = "kind";
    protected static final String TEXT = "text";
    protected static final String IMAGE = "image";
    protected static final String BOLD = "bold";
    protected static final String UNDERLINE = "underline";
    // --- End Copied Constants ---

    @Override
    public void save(Presentation presentation, String filename) throws IOException {
        // --- Logic copied from XMLAccessor.saveFile ---
        PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.UTF_8));
        out.println("<?xml version=\"1.0\"?>");
        out.println("<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">");
        out.println("<presentation>");
        out.print("<showtitle>");
        out.print(presentation.getTitle());
        out.println("</showtitle>");
        for (int slideNumber = 0; slideNumber < presentation.getSize(); slideNumber++) {
            Slide slide = presentation.getSlide(slideNumber);
            out.println("<slide>");
            out.println("<title>" + slide.getTitle() + "</title>");
            Vector<SlideItem> slideItems = slide.getSlideItems(); // Use clone returned by getSlideItems()
            for (int itemNumber = 0; itemNumber < slideItems.size(); itemNumber++) {
                SlideItem slideItem = slideItems.elementAt(itemNumber); // Get item from cloned list

                SlideItem itemToSave = slideItem;
                boolean isBold = false;
                boolean isUnderlined = false;

                // Correctly unwrap decorators
                while (itemToSave instanceof SlideItemDecorator) {
                    if (itemToSave instanceof UnderlineTextDecorator) {
                        isUnderlined = true;
                    } else if (itemToSave instanceof BoldTextDecorator) {
                        isBold = true;
                    }
                    // Add else if for other decorators...
                    itemToSave = ((SlideItemDecorator) itemToSave).decoratedItem;
                }

                out.print("<item kind=");
                if (itemToSave instanceof TextItem) {
                    out.print("\"" + TEXT + "\" level=\"" + slideItem.getLevel() + "\""); // Use original decorator level
                    if (isBold) out.print(" " + BOLD + "=\"true\"");
                    if (isUnderlined) out.print(" " + UNDERLINE + "=\"true\"");
                    out.print(">");
                    out.print(((TextItem) itemToSave).getText()); // Get text from base item
                }
                else if (itemToSave instanceof BitmapItem) {
                    out.print("\"" + IMAGE + "\" level=\"" + slideItem.getLevel() + "\""); // Use original decorator level
                    out.print(">");
                    out.print(((BitmapItem) itemToSave).getName()); // Get name from base item
                }
                else {
                    System.out.println("Ignoring unknown item type during save: " + slideItem);
                    continue; // Skip this item
                }
                out.println("</item>");
            }
            out.println("</slide>");
        }
        out.println("</presentation>");
        out.close();
        // --- End copied logic ---
    }
}