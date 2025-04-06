import java.io.IOException; // Keep import, even if not directly used, for interface compliance

/**
 * Concrete implementation of PresentationReader for the built-in demo presentation.
 * Implements SRP by focusing only on creating demo content.
 */
public class DemoPresentationReader implements PresentationReader {

    @Override
    public void load(Presentation presentation, String source) throws IOException {
        // source is unused for the demo reader

        presentation.setTitle("Demo Presentation with Decorators"); // Updated Title
        Slide slide;

        // Slide 1: Original Content
        slide = new Slide();
        slide.setTitle("JabberPoint");
        slide.append(1, "The Java Presentation Tool");
        slide.append(2, "Copyright (c) 1996-2000: Ian Darwin");
        slide.append(2, "Copyright (c) 2000-now:");
        slide.append(2, "Gert Florijn and Sylvia Stuurman"); // Corrected typo
        slide.append(4, "Starting JabberPoint without a filename");
        slide.append(4, "shows this presentation");
        slide.append(1, "Navigate:");
        slide.append(3, "Next slide: PgDn or Enter");
        slide.append(3, "Previous slide: PgUp or up-arrow");
        slide.append(3, "Quit: q or Q");
        presentation.addSlide(slide);

        // Slide 2: Original Content
        slide = new Slide();
        slide.setTitle("Demonstration of levels and styles");
        slide.append(1, "Level 1");
        slide.append(2, "Level 2");
        slide.append(1, "Again level 1");
        slide.append(1, "Level 1 has style number 1");
        slide.append(2, "Level 2 has style number 2"); // Corrected typo
        slide.append(3, "This is how level 3 looks like");
        slide.append(4, "And this is level 4");
        presentation.addSlide(slide);

        // Slide 3: Decorator Demo
        slide = new Slide();
        slide.setTitle("Decorator Pattern Demo");
        slide.append(1, "This is standard level 1 text.");
        TextItem boldItem = new TextItem(2, "This level 2 text is BOLD.");
        SlideItem decoratedBold = new BoldTextDecorator(boldItem);
        slide.append(decoratedBold);
        TextItem underlineItem = new TextItem(2, "This level 2 text is UNDERLINED.");
        SlideItem decoratedUnderline = new UnderlineTextDecorator(underlineItem);
        slide.append(decoratedUnderline);
        TextItem boldAndUnderlineItem = new TextItem(3, "This level 3 text is BOLD and UNDERLINED.");
        SlideItem decoratedWithBold = new BoldTextDecorator(boldAndUnderlineItem);
        SlideItem decoratedWithBoth = new UnderlineTextDecorator(decoratedWithBold);
        slide.append(decoratedWithBoth);
        presentation.addSlide(slide);

        // Slide 4: Original End Slide
        slide = new Slide();
        slide.setTitle("The fourth and final slide"); // Renamed title for clarity
        slide.append(1, "To open a new presentation,");
        slide.append(2, "use File->Open from the menu.");
        slide.append(1, " ");
        slide.append(1, "This is the end of the presentation.");
        slide.append(new BitmapItem(1, "JabberPoint.jpg"));
        presentation.addSlide(slide);
    }
}