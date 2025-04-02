import java.io.IOException;

/**
 * This class helps us load and save presentations.
 * Think of it as a helper that knows how to read and write presentation data.
 * If you want to create your own type of loader, you'll need to extend this class
 * and implement the loadFile and saveFile methods with your own code.
 */
public abstract class Accessor {
    public static final String DEMO_NAME = "Demonstration presentation";
    public static final String DEFAULT_EXTENSION = ".xml";

    /**
     * The basic constructor - it doesn't do much, but we need it.
     */
    public Accessor() {
    }

    /**
     * This gives you the accessor for loading the built-in demo presentation.
     * It's a handy way to get a sample presentation without loading a file.
     */
    public static Accessor getDemoAccessor() {
        return new DemoPresentation();
    }

    /**
     * This method loads a presentation from a file.
     * You'll need to write this method in your subclass to explain exactly
     * how to read your specific file format.
     */
    public abstract void loadFile(Presentation p, String fn) throws IOException;

    /**
     * This method saves a presentation to a file.
     * You'll need to write this method in your subclass to explain exactly
     * how to write your specific file format.
     */
    public abstract void saveFile(Presentation p, String fn) throws IOException;
}
