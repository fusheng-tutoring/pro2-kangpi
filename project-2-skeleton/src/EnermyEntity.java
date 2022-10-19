import bagel.*;
import bagel.util.Colour;
import bagel.util.Rectangle;
import bagel.util.Point;

import java.util.ArrayList;
import java.util.Random;

public class EnermyEntity implements Movable{

    private static Image image;
    private final double HALF = 2.0;
    private double x;
    private double y;
    private final double MIN_SPEED = 0.2;
    private final double MAX_SPEED = 0.7;
    private double moveSize = MIN_SPEED + new Random().nextDouble() * (MAX_SPEED - MIN_SPEED);
    private double initialSpeed;
    private final int ORANGE_HEALTH = 65;
    private final static int RED_HEALTH = 35;
    private final static Colour GREEN = new Colour(0, 0.8, 0.2);
    private final static Colour ORANGE = new Colour(0.9, 0.6, 0);
    private final static Colour RED = new Colour(1, 0, 0);
    private final DrawOptions COLOUR = new DrawOptions();
    public final int UP = 0;
    public final int DOWN = 1;
    public final int LEFT = 2;
    public final int RIGHT = 3;
    public final static int MOVE_DIRECTION = 4;
    public int moveDirection = new Random().nextInt(MOVE_DIRECTION);

    public final static int MAX_HEALTH_POINT = 40;
    /*Invincible time*/
    protected final static double INVINCIBLE_TIME = 3000;
    protected final static double REFRESH_RATE = 60;
    private Image currentImage;

    private double healthPoints;
    private boolean isInVincible;
    protected double InVincibleFrame;
    private final static int FONT_SIZE = 15;
    protected final static Font FONT = new Font("res/frostbit.ttf", FONT_SIZE);

    private boolean isAggressive;

    public EnermyEntity(double x, double y, String filename) {
        this.x = x;
        this.y = y;
        image = new Image(filename);
        this.InVincibleFrame = 0;
        this.isInVincible = false;
        this.initialSpeed = moveSize;
    }


    public void LoseHealthPoints(int damagePoint) {
        this.healthPoints -= damagePoint;
    }

    public boolean isDead() {
        return healthPoints <= 0;
    }
    @Override
    public void move() {
        if(moveDirection == UP) {
            cor_move(0,-moveSize);
        }else if(moveDirection == DOWN) {
            cor_move(0,moveSize);
        }else if(moveDirection == LEFT) {
            cor_move(-moveSize, 0);
        }else{
            cor_move(moveSize, 0);
        }
    }
    public void cor_move(double xMove, double yMove) {
        this.setX(this.getX() + xMove);
        this.setY(this.getY() + yMove);
    }

    public void moveBack() {
        if(moveDirection == UP) {
            moveDirection = DOWN;
        } else if (moveDirection == DOWN) {
            moveDirection = UP;
        } else if (moveDirection == LEFT) {
            moveDirection = RIGHT;
        } else {
            moveDirection = LEFT;
        }
    }
    public void renderHealthPoints() {
        double percentageHP = ((double) healthPoints / MAX_HEALTH_POINT*100);
        setHealthPoints(percentageHP);
        FONT.drawString(Math.round(percentageHP)+"%", getX(),getY()-6,getCOLOUR());
    }

    public Rectangle getBoundingBox() {
        return image.getBoundingBoxAt(
                new Point(this.getX() + image.getWidth() / HALF,
                        this.getY() + image.getHeight() / HALF));
    }

    public boolean checkBlockCollision(ArrayList<Tree> trees, ArrayList<Sinkhole> sinkholes) {
        Rectangle enemyBox = this.getBoundingBox();
        {
            for (Entity tree : trees) {
                Rectangle entityBox = tree.getBoundingBox();
                if (enemyBox.intersects(entityBox)) {
                    return true;
                }
            }
            for (Sinkhole sinkhole : sinkholes) {
                Rectangle sinkholeBoundingBox = sinkhole.getBoundingBox();
                if (enemyBox.intersects(sinkholeBoundingBox) && sinkhole.getIsAppear()) {
                    return true;
                }
            }
                return false;
            }
        }
    public boolean isOutOfBound(Point topLeft, Point bottomRight, Player enermyEntity) {
        return (enermyEntity.getY() > bottomRight.y) || (enermyEntity.getY() < topLeft.y)
                || (enermyEntity.getX() < topLeft.x) || (enermyEntity.getX() > bottomRight.x);
    }

    public void setEnemyHealthColour(double percentageHp) {
        if (percentageHp < RED_HEALTH) {
            COLOUR.setBlendColour(RED);
        } else if (percentageHp <= ORANGE_HEALTH) {
            COLOUR.setBlendColour(ORANGE);
        } else {
            COLOUR.setBlendColour(GREEN);
        }
    }

    public double getInVincibleFrame() {
        return InVincibleFrame;
    }

    public void setInVincibleFrame(double inVincibleFrame) {
        InVincibleFrame = inVincibleFrame;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getX() {
        return x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getY() {
        return y;
    }

    public boolean isInVincible() {
        return isInVincible;
    }

    public void setInVincible(boolean inVincible) {
        isInVincible = inVincible;
    }

    public DrawOptions getCOLOUR() {
        return COLOUR;
    }

    public Image getImage() {
        return currentImage;
    }

    public void setCurrentImage(Image currentImage) {
        this.currentImage = currentImage;
    }
    public void setHealthPoints(double healthPoints){
        this.healthPoints = healthPoints;
    }

    public double getHealthPoints() {
        return healthPoints;
    }

    public double getInitialSpeed() {
        return initialSpeed;
    }

    public double getMoveSize() {
        return moveSize;
    }

    public void setMoveSize(double moveSize) {
        this.moveSize = moveSize;
    }

    public boolean isAggressive() {
        return isAggressive;
    }


    public void setAggressive(boolean aggressive) {
        isAggressive = aggressive;
    }

}




