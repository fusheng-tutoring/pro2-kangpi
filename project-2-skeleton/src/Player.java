import bagel.*;
import bagel.util.Point;
import bagel.util.Colour;
import java.util.ArrayList;
import bagel.util.Rectangle;
/**
 * This class is made for player object
 */
public class Player {
    // Initial const data
    private final int HEALTH_FONT_SIZE = 30;
    public Image currentImage;
    private final Font HEALTH_FONT = new Font("res/frostbite.ttf",HEALTH_FONT_SIZE);
    private final Image faeToRight = new Image("res/fae/faeRight.png");
    private final Image faeToLeft = new Image("res/fae/faeLeft.png");
    private final Image attackLeft = new Image("res/fae/faeAttackLeft.png");
    private final Image attackRight = new Image("res/fae/faeAttackRight.png");

    private final static Colour ORANGE = new Colour(0.9,0.6,0);
    private final static Colour GREEN = new Colour(0,0.8,0.2);
    private final static Colour RED = new Colour(1,0,0);
    private final DrawOptions COLOUR = new DrawOptions();
    private final static int ORANGE_HEALTH = 65;
    private final static int RED_HEALTH = 35;
    private final int MAX_HEALTH_POINT = 100;
    private final static int STEP_SIZE = 2;// The length of the player's footsteps per movement
    private final static int HEALTH_X = 10;// The position of health message
    private final static int HEALTH_Y = 25;
    private final int ATTACK_POINT = 30;
    private final int PERCENTAGE = 100;
    private final double HALF = 2.0;
    private final int NO_MOVE = 0;
    private final int WON_X = 950;
    private final int WON_Y = 670;
    private final int DEATH = 0;
    private final double INVINCIBLE_TIME = 3000;
    private final double COLL_DOWN_TIME = 2000;
    private final double ATTACK_TIME = 1000;
    private final double REFRESH_RATE = 60;
    private final int damagePoint = 20;
    private double attackFrame;
    private double coolDownFrame;
    private double invincibleFrame;
    private boolean attackState;
    private boolean coolDownState;
    private boolean invincibleState;

    private boolean faceRight;

    /*Set up Image object*/
    private Image CurrentImage; // Get player's image with correct direction

    private int healthPoint;
    /*Set up Player object*/
    private double cur_x;
    private double cur_y;
    private double xold;
    private double yold;
    /**
     * Making Player constructor
     */
    public Player(double x,double y) {
        this.healthPoint = MAX_HEALTH_POINT;
        this.CurrentImage = faeToRight;
        this.cur_x = x;
        this.cur_y = y;
        this.xold = x;
        this.yold = y;
        this.coolDownState = false;
        this.attackState = false;
        this.invincibleState = false;
        this.attackFrame = 0;
        this.coolDownFrame = 0;
        this.invincibleFrame = 0;
        this.faceRight = true;
    }
    /**
     * Update player drawing
     * @param input
     * @param walls
     * @param sinkholes
     */
    public void draw(Input input,ArrayList<Entity> walls,ArrayList<Sinkhole> sinkholes,ArrayList<Entity> entities,ArrayList<Demon> demons,Navec navec,Point topLeft,Point bottomRight) {
        boolean stop = checkCollisions(walls,sinkholes);
        if (input.isDown(Keys.UP) && !stop) {
            move(NO_MOVE, -STEP_SIZE);
            xold = cur_x;
            yold = cur_y + STEP_SIZE;
        } else if (input.isDown(Keys.DOWN) && !stop) {
            move(NO_MOVE, STEP_SIZE);
            xold = cur_x;
            yold = cur_y - STEP_SIZE;
        } else if (input.isDown(Keys.LEFT) && !stop) {
            CurrentImage = faeToLeft;
            move(-STEP_SIZE, NO_MOVE);
            xold = cur_x + STEP_SIZE;
            yold = cur_y;
        } else if (input.isDown(Keys.RIGHT) && !stop) {
            CurrentImage = faeToRight;
            move(STEP_SIZE, NO_MOVE);
            xold = cur_x - STEP_SIZE;
            yold = cur_y;
        }
        CurrentImage.drawFromTopLeft(cur_x, cur_y); // Drawing image in every frame
        if (input.wasPressed(Keys.A) && !coolDownState) {
            attackState = true;
            if (faceRight) {
                setCurrentImage(attackRight);
            } else {
                setCurrentImage(attackLeft);
            }
        }
        if (attackState) {
            checkDemonCollisions(demons, navec);
            attackFrame++;
        }
        if (attackFrame / (REFRESH_RATE / 1000) > ATTACK_TIME) {
            attackState = false;
            coolDownState = true;
            attackFrame = 0;
            if (faceRight) {
                setCurrentImage(faeToRight);
            } else {
                setCurrentImage(faeToLeft);
            }
        }
        if (coolDownState) {
            coolDownFrame++;
        }
        if (coolDownFrame / (REFRESH_RATE / 1000) > COLL_DOWN_TIME) {
            coolDownState = true;
            coolDownFrame = 0;
            setCurrentImage(faeToRight);
            if (input.wasPressed(Keys.A) && !coolDownState) {
                attackState = true;
            }
        }
        if (invincibleState) {
            invincibleFrame ++;
        }
        if (invincibleFrame / (REFRESH_RATE / 1000) > INVINCIBLE_TIME) {
            invincibleState = false;
            coolDownState = true;
            invincibleFrame = 0;
        }
        if (checkCollisions(entities,sinkholes) || navec.isOutOfBound(topLeft,bottomRight,this)){
            moveBack();
        }
        currentImage.drawFromTopLeft(this.getX(),this.getY());
        drawHealth();
    }

    /**
     * Draw health status
     */
    public void drawHealth(){
        double percentageHP = ((double)healthPoint / MAX_HEALTH_POINT) * PERCENTAGE;
        setHealthColour(percentageHP);
        HEALTH_FONT.drawString(Math.round(percentageHP) + "%", HEALTH_X, HEALTH_Y, COLOUR);
    }
    /**
     * Checking if player is overlay on the walls or sinkhole
     * @param walls
     * @param sinkholes
     * @return boolean
     */
    public boolean checkCollisions(ArrayList<Entity> walls, ArrayList<Sinkhole> sinkholes) {
        Rectangle playerBox = this.getBoundingBox();
        for (Entity wall: walls) {
            Rectangle wallBox = wall.getBoundingBox();
            if (playerBox.intersects(wallBox)) {
                return true;
            }
        }
        for (Sinkhole sinkhole: sinkholes) {
            Rectangle sinkholeBox = sinkhole.getBoundingBox();
            if (sinkholeBox.intersects((playerBox)) && sinkhole.getIsAppear()) {
                sinkhole.setIsAppear(false);
                decreaseHealth(ATTACK_POINT);
                System.out.println("Sinkhole inflicts 30 damage points on Fae." +
                        "Fae's current health: " + healthPoint + '/' + MAX_HEALTH_POINT);
            }
        }
        return false;
    }
    private void checkDemonCollisions(ArrayList<Demon>demons,Navec navec) {
        Rectangle playerBox = this.getBoundingBox();
        Rectangle navecBox = navec.getBoundingBox();
        if(playerBox.intersects(navecBox) && !navec.isInVincible()) {
            navec.LoseHealthPoints(damagePoint);
            navec.setInVincible(true);
            System.out.println("Fae inflicts 20 damage points on Demon. Demon's current health: " + navec.getHealthPoints() + '/' + Navec.getNavecMaxHealth());
        }
    }
    /* need check collision with demon*/
    /**
     * Setup health number
     * @param percentageHp
     */
    public void setHealthColour(double percentageHp) {
        if (percentageHp < RED_HEALTH) {
            COLOUR.setBlendColour(RED);
        } else if (percentageHp <= ORANGE_HEALTH) {
            COLOUR.setBlendColour(ORANGE);
        } else {
            COLOUR.setBlendColour(GREEN);
        }
    }
    /**
     * Player get attacked by sinkhole
     * @param decrease
     */
    public void decreaseHealth(double decrease){
        if (this.healthPoint - decrease < DEATH) {
            this.healthPoint = DEATH;
        } else {
            this.healthPoint -= decrease;
        }
    }
    /**
     * Moving player
     * @param xMove
     * @param yMove
     */
    public void move(double xMove, double yMove) {
        this.cur_x += xMove;
        this.cur_y += yMove;
    }
    /**
     * Let Player move back to original point when player has collision with walls or bounds
     */
    public void moveBack(){
        setX(xold);
        setY(yold);
    }
    /*Get player status*/
    public boolean isWon() {return this.cur_x >= WON_X && this.cur_y >= WON_Y;}
    public boolean isDead() {return healthPoint <= DEATH;}
    /**
     * Make image box for player
     * @return
     */
    public Rectangle getBoundingBox() {
        return CurrentImage.getBoundingBoxAt(
                new Point(getX() + CurrentImage.getWidth() / HALF,
                        getY() + CurrentImage.getHeight() / HALF));
    }
    /*Getter and Setter*/
    public int getHealthPoint() {return healthPoint;}
    public void setX(double x) {this.cur_x = x;}
    public double getX() {return cur_x;}
    public void setY(double y) {this.cur_y = y;}
    public double getY() {return cur_y;}

    public int getMAX_HEALTH_POINT() {
        return MAX_HEALTH_POINT;
    }

    public boolean getInvincibleState() {
        return invincibleState;
    }

    public void setInvincibleState(boolean invincibleState) {
        this.invincibleState = invincibleState;
    }

    public Image getcurrentImage() {
        return currentImage;
    }

    public void setCurrentImage(Image currentImage) {
        this.currentImage = currentImage;
    }

}
