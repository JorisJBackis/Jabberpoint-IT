import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

public class SlideViewerFrame extends JFrame {
	private static final long serialVersionUID = 3227L;
	private static final String JABTITLE = "Jabberpoint - Observer Pattern Demo";
	public final static int WIDTH = 1200;
	public final static int HEIGHT = 800;

	public SlideViewerFrame(String title, Presentation presentation) {
		super(title);
		// Create a SlideViewerComponent that registers itself as an observer
		SlideViewerComponent slideViewerComponent = new SlideViewerComponent(presentation);
		// Set the presentation's view
		presentation.setShowView(slideViewerComponent);
		setupWindow(slideViewerComponent, presentation);
	}

	// Setup the GUI
	public void setupWindow(SlideViewerComponent slideViewerComponent, Presentation presentation) {
		setTitle(JABTITLE + " - " + presentation.getTitle());
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		getContentPane().add(slideViewerComponent);
		addKeyListener(new KeyController(presentation));
		// The MenuController should disable the Next option when at the last slide.
		setMenuBar(new MenuController(this, presentation));
		setSize(new Dimension(WIDTH, HEIGHT));
		setVisible(true);
	}
}