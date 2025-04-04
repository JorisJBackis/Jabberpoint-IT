import org.w3c.dom.*;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;


/**
 * XMLAccessor, reads and writes XML files using Factories for SlideItem creation
 *
 * @author Ian F. Darwin, ian@darwinsys.com, Gert Florijn, Sylvia Stuurman
 * @version 1.7 2025/XX/XX // Added Factory Pattern integration
 */

public class XMLAccessor extends Accessor {

    /**
     * Default API to use.
     */
    protected static final String DEFAULT_API_TO_USE = "dom";

    /**
     * namen van xml tags of attributen
     */
    protected static final String SHOWTITLE = "showtitle";
    protected static final String SLIDETITLE = "title";
    protected static final String SLIDE = "slide";
    protected static final String ITEM = "item";
    protected static final String LEVEL = "level";
    protected static final String KIND = "kind";
    protected static final String TEXT = "text";
    protected static final String IMAGE = "image";
    // Example attributes for decorators (add these to your DTD/XML if using)
    protected static final String BOLD = "bold";
    protected static final String UNDERLINE = "underline";


    /**
     * tekst van messages
     */
    protected static final String PCE = "Parser Configuration Exception";
    protected static final String UNKNOWNTYPE = "Unknown Element type";
    protected static final String NFE = "Number Format Exception";

    // --- Factory Instances ---
    // Create instances of the concrete factories.
    // These could also be injected via a constructor if using Dependency Injection.
    private final SlideItemFactory textItemFactory = new TextItemFactory();
    private final SlideItemFactory bitmapItemFactory = new BitmapItemFactory();
    // --- End Factory Instances ---


    private String getTitle(Element element, String tagName) {
        NodeList titles = element.getElementsByTagName(tagName);
        if (titles.getLength() >= 1) { // Check if the element exists
            return titles.item(0).getTextContent();
        }
        System.err.println("Warning: Could not find title element <" + tagName + "> in XML.");
        return "Untitled"; // Return a default title
    }

    @Override
    public void loadFile(Presentation presentation, String filename) throws IOException {
        int slideNumber, itemNumber, max = 0, maxItems = 0;
        try {
            File xmlFile = new File(filename);
            if (!xmlFile.exists()) {
                System.err.println("XML file not found: " + xmlFile.getAbsolutePath());
                throw new IOException("Cannot find file: " + filename);
            }

            System.out.println("Loading XML file from: " + xmlFile.getAbsolutePath());

            // Create a factory with DTD validation disabled to avoid errors if DTD is missing
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // Set features to make parsing more lenient
            try {
                // Disable DTD validation
                factory.setValidating(false);
                // Disable external entity resolution (more secure too)
                factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
                factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
                factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            } catch (ParserConfigurationException e) {
                // If feature isn't supported, log and continue without it
                System.err.println("Warning: Some XML parser features aren't supported: " + e.getMessage());
                // Non-fatal, continue with default factory settings
            }

            DocumentBuilder builder = factory.newDocumentBuilder();

            // Add custom error handler that logs but doesn't fail on DTD errors
            builder.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException e) {
                    System.err.println("XML Parse Warning: " + e.getMessage());
                }

                @Override
                public void error(SAXParseException e) {
                    // For DTD-related errors, just log them but don't fail
                    if (e.getMessage().contains("DOCTYPE") || e.getMessage().contains("DTD")) {
                        System.err.println("XML DTD-related error (continuing): " + e.getMessage());
                    } else {
                        // Other errors might be structure problems, so log more prominently
                        System.err.println("XML Error (non-fatal): " + e.getMessage());
                    }
                }

                @Override
                public void fatalError(SAXParseException e) throws SAXException {
                    // Fatal errors we do need to throw - these are serious parsing problems
                    System.err.println("XML Fatal Error: " + e.getMessage());
                    throw e;
                }
            });

            Document document = builder.parse(xmlFile); // Create a DOM document
            Element doc = document.getDocumentElement();
            presentation.setTitle(getTitle(doc, SHOWTITLE));

            NodeList slides = doc.getElementsByTagName(SLIDE);
            max = slides.getLength();
            for (slideNumber = 0; slideNumber < max; slideNumber++) {
                Element xmlSlide = (Element) slides.item(slideNumber);
                Slide slide = new Slide();
                slide.setTitle(getTitle(xmlSlide, SLIDETITLE));
                presentation.addSlide(slide);

                NodeList slideItems = xmlSlide.getElementsByTagName(ITEM);
                maxItems = slideItems.getLength();
                for (itemNumber = 0; itemNumber < maxItems; itemNumber++) {
                    Element item = (Element) slideItems.item(itemNumber);
                    loadSlideItem(slide, item); // Delegate item loading
                }
            }
            System.out.println("Successfully loaded " + max + " slides from " + filename);
        } catch (IOException iox) {
            // Re-throw specific IOExceptions with more helpful messages
            System.err.println("IOException during file load: " + iox.getMessage());
            System.err.println("Make sure the XML file exists and is accessible");
            throw new IOException("Error loading file: " + filename + " - " + iox.getMessage(), iox);
        } catch (SAXException sax) {
            System.err.println("SAXException (XML parsing error): " + sax.getMessage());
            throw new IOException("XML parsing error in " + filename + ": " + sax.getMessage(), sax);
        } catch (ParserConfigurationException pcx) {
            System.err.println(PCE + ": " + pcx.getMessage());
            throw new IOException("Parser configuration error: " + pcx.getMessage(), pcx);
        }
    }

    // Method to load a single slide item using Factories
    protected void loadSlideItem(Slide slide, Element item) {
        int level = 1; // default
        NamedNodeMap attributes = item.getAttributes();
        Node levelNode = attributes.getNamedItem(LEVEL);
        if (levelNode != null) {
            String leveltext = levelNode.getTextContent();
            try {
                level = Integer.parseInt(leveltext);
            } catch (NumberFormatException x) {
                System.err.println(NFE + " for level attribute: " + leveltext);
                // Keep default level 1
            }
        } else {
            System.err.println("Warning: Missing level attribute for item, defaulting to 1.");
        }

        Node kindNode = attributes.getNamedItem(KIND);
        if (kindNode == null) {
            System.err.println("Error: Missing kind attribute for item. Skipping item.");
            return; // Cannot create item without knowing the kind
        }
        String type = kindNode.getTextContent();
        String data = item.getTextContent(); // The text content of the <item> tag

        SlideItem baseItem = null;

        // --- Use Factories to create the base SlideItem ---
        if (TEXT.equals(type)) {
            baseItem = textItemFactory.createSlideItem(level, data);

            // --- Optional: Check for Decorator attributes AFTER factory creation ---
            Node boldAttr = attributes.getNamedItem(BOLD);
            Node underlineAttr = attributes.getNamedItem(UNDERLINE);

            if (boldAttr != null && "true".equalsIgnoreCase(boldAttr.getTextContent())) {
                baseItem = new BoldTextDecorator(baseItem); // Decorate
            }
            if (underlineAttr != null && "true".equalsIgnoreCase(underlineAttr.getTextContent())) {
                baseItem = new UnderlineTextDecorator(baseItem); // Decorate (possibly already decorated)
            }
            // --- End Decorator Check ---

        } else if (IMAGE.equals(type)) {
            baseItem = bitmapItemFactory.createSlideItem(level, data);
            // Could add checks for image-specific decorators here (e.g., caption)
        }
        // --- End Factory Usage ---

        // Add the final item (potentially decorated) to the slide
        if (baseItem != null) {
            slide.append(baseItem);
        } else {
            // Unknown kind is now handled before factory call if kindNode is null,
            // but we could add an error here if baseItem remained null for other reasons.
            System.err.println(UNKNOWNTYPE + ": " + type + ". Skipping item.");
        }
    }

    @Override
    public void saveFile(Presentation presentation, String filename) throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(filename));
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
            Vector<SlideItem> slideItems = slide.getSlideItems();
            for (int itemNumber = 0; itemNumber < slideItems.size(); itemNumber++) {
                SlideItem slideItem = slideItems.elementAt(itemNumber);

                // --- Handle potential Decorators during save ---
                // Need to unwrap decorators to save the base item correctly
                SlideItem itemToSave = slideItem;
                boolean isBold = false;
                boolean isUnderlined = false;

                // Unwrap decorators (this order matters if combined)
                // Check for Underline first if it can wrap Bold
                if (itemToSave instanceof UnderlineTextDecorator) {
                    isUnderlined = true;
                    itemToSave = ((UnderlineTextDecorator) itemToSave).decoratedItem;
                }
                // Check for Bold
                if (itemToSave instanceof BoldTextDecorator) {
                    isBold = true;
                    itemToSave = ((BoldTextDecorator) itemToSave).decoratedItem;
                }
                // Add checks for other decorators here...

                // --- End Decorator Handling ---


                // --- Save based on the unwrapped item type ---
                out.print("<item kind=");
                if (itemToSave instanceof TextItem) {
                    out.print("\"" + TEXT + "\" level=\"" + slideItem.getLevel() + "\""); // Use original level
                    // Add decorator attributes if they were present
                    if (isBold) out.print(" " + BOLD + "=\"true\"");
                    if (isUnderlined) out.print(" " + UNDERLINE + "=\"true\"");
                    out.print(">");
                    // Make sure to get text from the *base* TextItem
                    out.print(((TextItem) itemToSave).getText());
                } else if (itemToSave instanceof BitmapItem) {
                    out.print("\"" + IMAGE + "\" level=\"" + slideItem.getLevel() + "\""); // Use original level
                    // Add image decorator attributes here if needed
                    out.print(">");
                    // Make sure to get name from the *base* BitmapItem
                    out.print(((BitmapItem) itemToSave).getName());
                } else {
                    System.out.println("Ignoring unknown item type during save: " + slideItem);
                    // Skip closing the item tag correctly if ignored? Need to handle this.
                    // Maybe print a placeholder or skip entirely. Let's skip for now.
                    continue; // Skip to next item
                }
                out.println("</item>");
            }
            out.println("</slide>");
        }
        out.println("</presentation>");
        out.close();
    }
}