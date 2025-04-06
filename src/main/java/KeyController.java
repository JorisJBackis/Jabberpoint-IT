import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.InputEvent;

public class KeyController extends KeyAdapter{

    private Presentation presentation;

    public KeyController(Presentation p) {
        presentation = p;
    }

    public void keyPressed(KeyEvent keyEvent) {
        // Check for Ctrl+N shortcut for next slide
        if ((keyEvent.getKeyCode() == KeyEvent.VK_N) && 
            ((keyEvent.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0)) {
            presentation.nextSlide();
            return;
        }
        
        // Check for Ctrl+P shortcut for previous slide
        if ((keyEvent.getKeyCode() == KeyEvent.VK_P) && 
            ((keyEvent.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0)) {
            presentation.prevSlide();
            return;
        }
        
        switch(keyEvent.getKeyCode()) {
            case KeyEvent.VK_PAGE_DOWN:
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_ENTER:
            case '+':
                presentation.nextSlide();
                break;
            case KeyEvent.VK_PAGE_UP:
            case KeyEvent.VK_UP:
            case '-':
                presentation.prevSlide();
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