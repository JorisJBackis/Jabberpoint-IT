/**
 * This class creates a built-in demo presentation.
 * It's a handy way to see JabberPoint in action without needing to load a file.
 * The presentation shows off different features like slide levels and decorators.
 */
class DemoPresentation extends Accessor {

    public void loadFile(Presentation presentation, String unusedFilename) {
        presentation.setTitle("Demo Presentation");
        Slide slide;
        slide = new Slide();
        slide.setTitle("JabberPoint");
        slide.append(1, "The Java Presentation Tool");
        slide.append(2, "Copyright (c) 1996-2000: Ian Darwin");
        slide.append(2, "Copyright (c) 2000-now:");
        slide.append(2, "Gert Florijn andn Sylvia Stuurman");
        slide.append(4, "Starting JabberPoint without a filename");
        slide.append(4, "shows this presentation");
        slide.append(1, "Navigate:");
        slide.append(3, "Next slide: PgDn or Enter");
        slide.append(3, "Previous slide: PgUp or up-arrow");
        slide.append(3, "Quit: q or Q");
        presentation.addSlide(slide);

        slide = new Slide();
        slide.setTitle("Demonstration of levels and stijlen");
        slide.append(1, "Level 1");
        slide.append(2, "Level 2");
        slide.append(1, "Again level 1");
        slide.append(1, "Level 1 has style number 1");
        slide.append(2, "Level 2 has style number  2");
        slide.append(3, "This is how level 3 looks like");
        slide.append(4, "And this is level 4");
        presentation.addSlide(slide);

        slide = new Slide();
        slide.setTitle("Demonstration with Bold");
        slide.append(1, "This text demonstrates bold styling.");
        slide.append(2, "Using the BoldTextDecorator class.");
        slide.append(3, "Which follows the Decorator pattern.");
        presentation.addSlide(slide);

        slide = new Slide();
        slide.setTitle("Decorator Pattern Demo");

        // Original item
        slide.append(1, "This is standard level 1 text.");

        // Create a TextItem
        TextItem boldItem = new TextItem(2, "This level 2 text is BOLD.");
        // Decorate it with BoldTextDecorator
        SlideItem decoratedBold = new BoldTextDecorator(boldItem);
        // Add the decorated item to the slide
        slide.append(decoratedBold);

        // Create another TextItem
        TextItem underlineItem = new TextItem(2, "This level 2 text is UNDERLINED.");
        // Decorate it with UnderlineTextDecorator
        SlideItem decoratedUnderline = new UnderlineTextDecorator(underlineItem);
        // Add the decorated item
        slide.append(decoratedUnderline);

        // Demonstrate chaining decorators: Bold AND Underlined
        TextItem boldAndUnderlineItem = new TextItem(3, "This level 3 text is BOLD and UNDERLINED.");
        // Wrap with Bold first
        SlideItem decoratedWithBold = new BoldTextDecorator(boldAndUnderlineItem);
        // Wrap the already-bold item with Underline
        SlideItem decoratedWithBoth = new UnderlineTextDecorator(decoratedWithBold);
        // Add the doubly-decorated item
        slide.append(decoratedWithBoth);


        presentation.addSlide(slide);

        slide = new Slide();
        slide.setTitle("The third slide");
        slide.append(1, "To open a new presentation,");
        slide.append(2, "use File->Open from the menu.");
        slide.append(1, " ");
        slide.append(1, "This is the end of the presentation.");
        slide.append(new BitmapItem(1, "JabberPoint.jpg"));
        presentation.addSlide(slide);

        // Add one more slide to test navigation to the very last slide
        slide = new Slide();
        slide.setTitle("Final Slide - Debugging");
        slide.append(1, "This is the final slide.");
        slide.append(2, "If you can see this, navigation to the last slide works!");
        slide.append(1, " ");
        presentation.addSlide(slide);
    }

    public void saveFile(Presentation presentation, String unusedFilename) {
        throw new IllegalStateException("Save As->Demo! called");
    }
}
