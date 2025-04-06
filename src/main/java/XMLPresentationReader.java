import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import java.io.*;

/**
 * Concrete implementation of PresentationReader for loading presentations from XML files.
 * Uses SlideItem factories for creating items.
 * Implements SRP by focusing only on reading and parsing XML.
 */
public class XMLPresentationReader implements PresentationReader {

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
    protected static final String PCE = "Parser Configuration Exception";
    protected static final String UNKNOWNTYPE = "Unknown Element type";
    protected static final String NFE = "Number Format Exception";
    // --- End Copied Constants ---

    // --- Factory Instances ---
    private final SlideItemFactory textItemFactory = new TextItemFactory();
    private final SlideItemFactory bitmapItemFactory = new BitmapItemFactory();
    // --- End Factory Instances ---

    @Override
    public void load(Presentation presentation, String filename) throws IOException {
        // --- Logic copied from XMLAccessor.loadFile ---
        int slideNumber, itemNumber, max = 0, maxItems = 0;
        try {
            File xmlFile = new File(filename);
            if (!xmlFile.exists()) {
                System.err.println("XML file not found: " + xmlFile.getAbsolutePath());
                throw new IOException("Cannot find file: " + filename);
            }

            System.out.println("Loading XML file from: " + xmlFile.getAbsolutePath());

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            try {
                factory.setValidating(false);
                factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
                factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
                factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            } catch (ParserConfigurationException e) {
                System.err.println("Warning: Some XML parser features aren't supported: " + e.getMessage());
            }

            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new ErrorHandler() {
                @Override public void warning(SAXParseException e) { System.err.println("XML Parse Warning: " + e.getMessage()); }
                @Override public void error(SAXParseException e) {
                    if (e.getMessage().contains("DOCTYPE") || e.getMessage().contains("DTD")) {
                        System.err.println("XML DTD-related error (continuing): " + e.getMessage());
                    } else { System.err.println("XML Error (non-fatal): " + e.getMessage()); }
                }
                @Override public void fatalError(SAXParseException e) throws SAXException { System.err.println("XML Fatal Error: " + e.getMessage()); throw e; }
            });

            Document document = builder.parse(xmlFile);
            Element doc = document.getDocumentElement();
            presentation.setTitle(getTitle(doc, SHOWTITLE)); // Use helper method

            NodeList slides = doc.getElementsByTagName(SLIDE);
            max = slides.getLength();
            for (slideNumber = 0; slideNumber < max; slideNumber++) {
                Element xmlSlide = (Element) slides.item(slideNumber);
                Slide slide = new Slide();
                slide.setTitle(getTitle(xmlSlide, SLIDETITLE)); // Use helper method
                presentation.addSlide(slide);

                NodeList slideItems = xmlSlide.getElementsByTagName(ITEM);
                maxItems = slideItems.getLength();
                for (itemNumber = 0; itemNumber < maxItems; itemNumber++) {
                    Element item = (Element) slideItems.item(itemNumber);
                    loadSlideItem(slide, item); // Use helper method
                }
            }
            System.out.println("Successfully loaded " + max + " slides from " + filename);
        } catch (IOException iox) {
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
        // --- End copied logic ---
    }

    // --- Helper methods copied from XMLAccessor (now private) ---
    private String getTitle(Element element, String tagName) {
        NodeList titles = element.getElementsByTagName(tagName);
        if (titles.getLength() >= 1) {
            return titles.item(0).getTextContent();
        }
        System.err.println("Warning: Could not find title element <" + tagName + "> in XML.");
        return "Untitled";
    }

    private void loadSlideItem(Slide slide, Element item) {
        int level = 1;
        NamedNodeMap attributes = item.getAttributes();
        Node levelNode = attributes.getNamedItem(LEVEL);
        if (levelNode != null) {
            String leveltext = levelNode.getTextContent();
            try { level = Integer.parseInt(leveltext); }
            catch(NumberFormatException x) { System.err.println(NFE + " for level: " + leveltext); }
        } else { System.err.println("Warning: Missing level for item, default=1."); }

        Node kindNode = attributes.getNamedItem(KIND);
        if (kindNode == null) { System.err.println("Error: Missing kind. Skip item."); return; }
        String type = kindNode.getTextContent();
        String data = item.getTextContent();

        SlideItem baseItem = null;
        if (TEXT.equals(type)) {
            baseItem = textItemFactory.createSlideItem(level, data);
            Node boldAttr = attributes.getNamedItem(BOLD);
            Node underlineAttr = attributes.getNamedItem(UNDERLINE);
            if (boldAttr != null && "true".equalsIgnoreCase(boldAttr.getTextContent())) { baseItem = new BoldTextDecorator(baseItem); }
            if (underlineAttr != null && "true".equalsIgnoreCase(underlineAttr.getTextContent())) { baseItem = new UnderlineTextDecorator(baseItem); }
        } else if (IMAGE.equals(type)) {
            baseItem = bitmapItemFactory.createSlideItem(level, data);
        }

        if (baseItem != null) { slide.append(baseItem); }
        else { System.err.println(UNKNOWNTYPE + ": " + type + ". Skip item."); }
    }
    // --- End helper methods ---
}