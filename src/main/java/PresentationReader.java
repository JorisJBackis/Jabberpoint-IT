import java.io.IOException;

/**
 * Defines the contract for reading a presentation from a source.
 * Addresses DIP: High-level modules can depend on this abstraction.
 * Addresses SRP: Reading is a distinct responsibility.
 */
public interface PresentationReader {
    /**
     * Loads presentation data into the given Presentation object.
     *
     * @param presentation The presentation object to populate.
     * @param source       A string identifying the source (e.g., filename, identifier).
     * @throws IOException If an error occurs during reading.
     */
    void load(Presentation presentation, String source) throws IOException;
}