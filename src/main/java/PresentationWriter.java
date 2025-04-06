import java.io.IOException;

/**
 * Defines the contract for writing a presentation to a destination.
 * Addresses DIP: High-level modules can depend on this abstraction.
 * Addresses SRP: Writing is a distinct responsibility.
 */
public interface PresentationWriter {
    /**
     * Saves the given presentation object.
     *
     * @param presentation The presentation object to save.
     * @param destination  A string identifying the destination (e.g., filename).
     * @throws IOException If an error occurs during writing.
     */
    void save(Presentation presentation, String destination) throws IOException;
}