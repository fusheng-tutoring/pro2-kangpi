import bagel.*;
import bagel.util.Point;
import bagel.util.Rectangle;

import java.util.ArrayList;

/**
 * This class is the abstract class for Wall and sinkhole
 */
public class Entity {
    private final Image image;
    private final double HALF = 2.0;
    private boolean isAppear; //justify whether sinkhole appear
    private double x;
    private double y;
    /**
     * Entities' constructor
     * @param x
     * @param y
     * @param filename
     */
    public Entity(double x, double y, String filename) {
        this.x = x;
        this.y = y;
        this.isAppear = true;
        image = new Image(filename);
    }
    /**
     * Update the entity on screen
     */
    public void update() {
        if (isAppear) {
            image.drawFromTopLeft(this.x, this.y);
        }
    }
    /**
     * Make image box for each entity
     * @return Rectangle
     */
    public Rectangle getBoundingBox() {
        return image.getBoundingBoxAt(
                new Point(this.getX() + image.getWidth() / HALF,
                        this.getY() + image.getHeight() / HALF));
    }
    /*Getter and setter*/
    public Image getCurrentImage() {return this.image;}
    public double getX() {return x;}
    public double getY() {return y;}
}

