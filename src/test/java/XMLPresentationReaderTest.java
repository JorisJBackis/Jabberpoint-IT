import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class XMLPresentationReaderTest {
    private XMLPresentationReader reader;
    private Presentation presentation;

    @BeforeEach
    void setUp() {
        reader = new XMLPresentationReader();
        presentation = new Presentation("Test Presentation");
    }

    @Test
    void loadShouldHandleNonExistentFile() {
        assertThrows(IOException.class, () -> 
            reader.load(presentation, "nonexistent.xml"));
    }

    @Test
    void loadShouldHandleEmptyFile(@TempDir Path tempDir) throws IOException {
        File emptyFile = tempDir.resolve("empty.xml").toFile();
        emptyFile.createNewFile();
        
        assertThrows(IOException.class, () -> 
            reader.load(presentation, emptyFile.getAbsolutePath()));
    }

    @Test
    void loadShouldHandleValidXML(@TempDir Path tempDir) throws IOException {
        File xmlFile = tempDir.resolve("test.xml").toFile();
        try (FileWriter writer = new FileWriter(xmlFile)) {
            writer.write("<?xml version=\"1.0\"?>\n" +
                    "<presentation>\n" +
                    "<showtitle>Test Show</showtitle>\n" +
                    "<slide>\n" +
                    "<title>Test Slide</title>\n" +
                    "<item kind=\"text\" level=\"1\">Test Item</item>\n" +
                    "</slide>\n" +
                    "</presentation>");
        }

        assertDoesNotThrow(() -> reader.load(presentation, xmlFile.getAbsolutePath()));
        assertEquals("Test Show", presentation.getTitle());
        assertEquals(1, presentation.getSize());
    }

    @Test
    void loadShouldHandleInvalidXML(@TempDir Path tempDir) throws IOException {
        File xmlFile = tempDir.resolve("invalid.xml").toFile();
        try (FileWriter writer = new FileWriter(xmlFile)) {
            writer.write("<?xml version=\"1.0\"?>\n<invalid>");
        }

        assertThrows(IOException.class, () -> 
            reader.load(presentation, xmlFile.getAbsolutePath()));
    }
} 