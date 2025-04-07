import org.junit.jupiter.api.Test;
import javax.swing.JFrame;
import static org.junit.jupiter.api.Assertions.*;

class AboutBoxTest {
    @Test
    void showShouldNotThrowException() {
        JFrame frame = new JFrame();
        assertDoesNotThrow(() -> AboutBox.show(frame));
    }

    @Test
    void showWithNullParentShouldNotThrowException() {
        assertDoesNotThrow(() -> AboutBox.show(null));
    }
} 