import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;


/**
 * <p>De klasse voor een Bitmap item</p>
 * <p>Bitmap items have the responsibility to draw themselves.</p>
 *
 * @author Ian F. Darwin, ian@darwinsys.com, Gert Florijn, Sylvia Stuurman
 * @version 1.6 2014/05/16 Sylvia Stuurman
 */

public class BitmapItem extends SlideItem {
    protected static final String FILE = "File ";
    protected static final String NOTFOUND = " not found";
    private final String imageName;
    private BufferedImage bufferedImage;

    // level is equal to item-level; name is the name of the file with the Image
    public BitmapItem(int level, String name) {
        super(level);
        imageName = name;

        // First try to load from current directory (relative path)
        try {
            File imageFile = new File(imageName);
            if (imageFile.exists()) {
                bufferedImage = ImageIO.read(imageFile);
            } else {
                // If not found, try in the project's base directory
                File projectBaseFile = new File("resources", imageName);
                if (projectBaseFile.exists()) {
                    bufferedImage = ImageIO.read(projectBaseFile);
                } else {
                    // If still not found, try an alternative location
                    File alternativeFile = new File("src/main/resources", imageName);
                    if (alternativeFile.exists()) {
                        bufferedImage = ImageIO.read(alternativeFile);
                    } else {
                        System.err.println("Tried multiple paths, but couldn't find: " + imageName);
                        System.err.println("Searched in: ., ./resources, ./src/main/resources");
                        throw new IOException("Image file not found in any standard location");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println(FILE + imageName + NOTFOUND + ": " + e.getMessage());
        }
    }

    // An empty bitmap-item
    public BitmapItem() {
        this(0, null);
    }

    // give the filename of the image
    public String getName() {
        return imageName;
    }

    // give the bounding box of the image
    public Rectangle getBoundingBox(Graphics g, ImageObserver observer, float scale, Style myStyle) {
        if (bufferedImage == null) {
            int x = 0;
            int width = (int) (myStyle.indent * scale);
            int height = (int) (myStyle.leading * scale);
            return new Rectangle(x, 0, width, height);
        }
        return new Rectangle((int) (myStyle.indent * scale), 0,
                (int) (bufferedImage.getWidth(observer) * scale),
                ((int) (myStyle.leading * scale)) + 
                (int) (bufferedImage.getHeight(observer) * scale));
    }

    // draw the image
    public void draw(int x, int y, float scale, Graphics g, Style myStyle, ImageObserver observer) {
        if (bufferedImage == null) {
            g.setColor(java.awt.Color.RED);
            g.drawString("Image not found: " + imageName, x + (int) (myStyle.indent * scale), 
                y + (int) (myStyle.leading * scale));
            return;
        }
        int width = x + (int) (myStyle.indent * scale);
        int height = y + (int) (myStyle.leading * scale);
        g.drawImage(bufferedImage, width, height,(int) (bufferedImage.getWidth(observer)*scale),
                (int) (bufferedImage.getHeight(observer)*scale), observer);
    }

    public String toString() {
        return "BitmapItem[" + getLevel() + "," + imageName + "]";
    }
} 