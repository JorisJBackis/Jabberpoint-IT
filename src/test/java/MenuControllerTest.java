import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static org.junit.jupiter.api.Assertions.*;

class MenuControllerTest {
    private MenuController controller;
    private Presentation presentation;
    private Frame frame;

    @BeforeEach
    void setUp() {
        presentation = new Presentation("Test Presentation");
        frame = new Frame();
        controller = new MenuController(frame, presentation);
    }

    @Test
    void setupMenusShouldCreateAllMenuItems() {
        // The menu structure is created in the constructor
        MenuBar menuBar = controller;
        assertNotNull(menuBar);
        
        // Check File menu
        Menu fileMenu = menuBar.getMenu(0);
        assertEquals("File", fileMenu.getLabel());
        assertEquals(5, fileMenu.getItemCount()); // Open, New, Save, separator, Exit
        
        // Check View menu
        Menu viewMenu = menuBar.getMenu(1);
        assertEquals("View", viewMenu.getLabel());
        assertEquals(3, viewMenu.getItemCount()); // Next, Previous, Go to
        
        // Check Help menu
        Menu helpMenu = menuBar.getHelpMenu();
        assertEquals("Help", helpMenu.getLabel());
        assertEquals(1, helpMenu.getItemCount()); // About
    }

    @Test
    void updateShouldUpdateMenuState() {
        // Add two slides to enable navigation
        Slide slide1 = new Slide();
        Slide slide2 = new Slide();
        presentation.addSlide(slide1);
        presentation.addSlide(slide2);
        
        // Initially at first slide
        controller.update();
        assertTrue(controller.getMenu(1).getItem(0).isEnabled()); // Next should be enabled
        assertFalse(controller.getMenu(1).getItem(1).isEnabled()); // Previous should be disabled
        
        // Move to second slide
        presentation.setSlideNumber(1);
        controller.update();
        assertFalse(controller.getMenu(1).getItem(0).isEnabled()); // Next should be disabled
        assertTrue(controller.getMenu(1).getItem(1).isEnabled()); // Previous should be enabled
    }

    @Test
    void menuItemsShouldHaveCorrectShortcuts() {
        MenuBar menuBar = controller;
        
        // Check File menu shortcuts
        Menu fileMenu = menuBar.getMenu(0);
        MenuItem openItem = fileMenu.getItem(0);
        MenuItem newItem = fileMenu.getItem(1);
        MenuItem saveItem = fileMenu.getItem(2);
        MenuItem exitItem = fileMenu.getItem(4); // Skip separator
        
        assertNotNull(openItem.getShortcut());
        assertEquals('O', openItem.getShortcut().getKey());
        
        assertNotNull(newItem.getShortcut());
        assertEquals('N', newItem.getShortcut().getKey());
        
        assertNotNull(saveItem.getShortcut());
        assertEquals('S', saveItem.getShortcut().getKey());
        
        assertNotNull(exitItem.getShortcut());
        assertEquals('E', exitItem.getShortcut().getKey());
        
        // Check View menu shortcuts
        Menu viewMenu = menuBar.getMenu(1);
        MenuItem nextItem = viewMenu.getItem(0);
        MenuItem prevItem = viewMenu.getItem(1);
        MenuItem gotoItem = viewMenu.getItem(2);
        
        assertNotNull(nextItem.getShortcut());
        assertTrue(nextItem.getShortcut().usesShiftModifier());
        
        assertNotNull(prevItem.getShortcut());
        assertTrue(prevItem.getShortcut().usesShiftModifier());
        
        assertNotNull(gotoItem.getShortcut());
        assertEquals('G', gotoItem.getShortcut().getKey());
        
        // Check Help menu shortcut
        Menu helpMenu = menuBar.getHelpMenu();
        MenuItem aboutItem = helpMenu.getItem(0);
        assertNotNull(aboutItem.getShortcut());
        assertEquals('A', aboutItem.getShortcut().getKey());
    }

    // Helper to get ActionListener for a specific menu item
    private ActionListener getActionListener(MenuItem item) {
        if (item != null && item.getActionListeners().length > 0) {
            return item.getActionListeners()[0];
        }
        return null;
    }
} 