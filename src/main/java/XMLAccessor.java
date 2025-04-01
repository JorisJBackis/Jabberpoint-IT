import java.util.Vector;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node; // Import Node if checking for decorator attributes later
import org.w3c.dom.NodeList;


/** XMLAccessor, reads and writes XML files using Factories for SlideItem creation
 * @author Ian F. Darwin, ian@darwinsys.com, Gert Florijn, Sylvia Stuurman
 * @version 1.1 2002/12/17 Gert Florijn
 * @version 1.2 2003/11/19 Sylvia Stuurman
 * @version 1.3 2004/08/17 Sylvia Stuurman
 * @version 1.4 2007/07/16 Sylvia Stuurman
 * @version 1.5 2010/03/03 Sylvia Stuurman
 * @version 1.6 2014/05/16 Sylvia Stuurman
 * @version 1.7 2025/XX/XX // Added Factory Pattern integration
 */

public class XMLAccessor extends Accessor {

	/** Default API to use. */
	protected static final String DEFAULT_API_TO_USE = "dom";

	/** namen van xml tags of attributen */
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


	/** tekst van messages */
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
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = builder.parse(new File(filename)); // Create a DOM document
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
		}
		catch (IOException iox) {
			// Re-throw specific IOExceptions or handle more gracefully
			System.err.println("IOException during file load: " + iox.getMessage());
			throw iox; // Or wrap in a custom exception
		}
		catch (SAXException sax) {
			System.err.println("SAXException (XML parsing error): " + sax.getMessage());
			// Consider wrapping and re-throwing
		}
		catch (ParserConfigurationException pcx) {
			System.err.println(PCE + ": " + pcx.getMessage());
			// Consider wrapping and re-throwing
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
			}
			catch(NumberFormatException x) {
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
		for (int slideNumber=0; slideNumber<presentation.getSize(); slideNumber++) {
			Slide slide = presentation.getSlide(slideNumber);
			out.println("<slide>");
			out.println("<title>" + slide.getTitle() + "</title>");
			Vector<SlideItem> slideItems = slide.getSlideItems();
			for (int itemNumber = 0; itemNumber<slideItems.size(); itemNumber++) {
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
					out.print( ((TextItem) itemToSave).getText());
				}
				else if (itemToSave instanceof BitmapItem) {
					out.print("\"" + IMAGE + "\" level=\"" + slideItem.getLevel() + "\""); // Use original level
					// Add image decorator attributes here if needed
					out.print(">");
					// Make sure to get name from the *base* BitmapItem
					out.print( ((BitmapItem) itemToSave).getName());
				}
				else {
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