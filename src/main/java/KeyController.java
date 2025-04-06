import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.InputEvent;

/**
 * This class handles all keyboard input for the presentation.
 * It watches for key presses and helps you navigate through the slides
 * using the keyboard instead of the mouse.
 */
public class KeyController extends KeyAdapter {
    private final Presentation presentation;

    /**
     * We create a new KeyController for a specific presentation.
     * This connects the keyboard controls to that presentation.
     */
    public KeyController(Presentation p) {
        presentation = p;
    }

    /**
     * This gets called whenever you press a key on the keyboard.
     * It checks which key you pressed and takes the appropriate action:
     * - PageDown/Down/Enter/+ moves to the next slide
     * - PageUp/Up/- moves to the previous slide
     * - q or Q exits the program
     */
    public void keyPressed(KeyEvent keyEvent) {
        int beforeSlide = presentation.getSlideNumber();
        boolean wasLastSlide = presentation.isLastSlide();

        // Check for Ctrl+N shortcut for next slide
        if ((keyEvent.getKeyCode() == KeyEvent.VK_N) && 
            ((keyEvent.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0)) {
            System.out.println("KeyController: Next slide requested using Ctrl+N. Current: " +
                    (beforeSlide + 1) + " of " + presentation.getSize());
            presentation.nextSlide();
            System.out.println("KeyController: After nextSlide(). Now at: " +
                    (presentation.getSlideNumber() + 1));
            return;
        }
        
        // Check for Ctrl+P shortcut for previous slide
        if ((keyEvent.getKeyCode() == KeyEvent.VK_P) && 
            ((keyEvent.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0)) {
            System.out.println("KeyController: Previous slide requested using Ctrl+P. Current: " +
                    (beforeSlide + 1) + " of " + presentation.getSize());
            presentation.prevSlide();
            System.out.println("KeyController: After prevSlide(). Now at: " +
                    (presentation.getSlideNumber() + 1));
            return;
        }
        
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.VK_PAGE_DOWN:
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_ENTER:
            case '+':
                System.out.println("KeyController: Next slide requested. Current: " +
                        (beforeSlide + 1) + " of " + presentation.getSize() +
                        " | Last slide? " + wasLastSlide);
                presentation.nextSlide();
                System.out.println("KeyController: After nextSlide(). Now at: " +
                        (presentation.getSlideNumber() + 1));
                break;
            case KeyEvent.VK_PAGE_UP:
            case KeyEvent.VK_UP:
            case '-':
                System.out.println("KeyController: Previous slide requested. Current: " +
                        (beforeSlide + 1) + " of " + presentation.getSize() +
                        " | First slide? " + presentation.isFirstSlide());
                presentation.prevSlide();
                System.out.println("KeyController: After prevSlide(). Now at: " +
                        (presentation.getSlideNumber() + 1));
                break;
            case 'q':
            case 'Q':
                System.exit(0);
                break;
            default:
                break;
        }
    }
}