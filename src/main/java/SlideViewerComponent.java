import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JComponent;

public class SlideViewerComponent extends JComponent implements Observer {

    private Presentation presentation;
    private Font labelFont;
    private Color backgroundColor = Color.WHITE;
    private Color textColor = Color.BLACK;
    private int xPos = 50;
    private int yPos = 50;

    public SlideViewerComponent(Presentation pres) {
        this.presentation = pres;
        this.labelFont = new Font("Dialog", Font.BOLD, 10);
        setBackground(backgroundColor);
        presentation.addObserver(this);
    }

    // This update() method will be called by Presentation.notifyObservers()
    @Override
    public void update() {
        repaint();
        System.out.println("Updated to slide: " + (presentation.getSlideNumber() + 1));
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(Slide.WIDTH, Slide.HEIGHT);
    }

    @Override
    public void paintComponent(Graphics graphics) {
        graphics.setColor(backgroundColor);
        graphics.fillRect(0, 0, getSize().width, getSize().height);
        Slide currentSlide = presentation.getCurrentSlide();
        if (presentation.getSlideNumber() < 0 || currentSlide == null) {
            return;
        }
        graphics.setFont(labelFont);
        graphics.setColor(textColor);
        graphics.drawString("Slide " + (1 + presentation.getSlideNumber()) + " of " +
                presentation.getSize(), xPos, yPos);
        Rectangle area = new Rectangle(0, yPos, getWidth(), (getHeight() - yPos));
        currentSlide.draw(graphics, area, this);
    }

    // Setters for customization
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        setBackground(backgroundColor);
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public void setLabelFont(Font labelFont) {
        this.labelFont = labelFont;
    }

    public void setTextPosition(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }
}