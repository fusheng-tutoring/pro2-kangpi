import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class Demon extends EnermyEntity {
    private final Image DEMON_LEFT = new Image("res/demon/demonLeft.png");
    private final Image DEMON_RIGHT = new Image("res/demon/demonRight.png");
    private final Image DEMON_INVINCIBLE_LEFT = new Image("res/demon/demoninvincibleLeft.PNG");
    private final Image DEMON_INVINCIBLE_RIGHT = new Image("res/demon/demoninvincibleRight.PNG");
    private static final double ATTACK_RANGE = 150;
    private Fire fire;
    private Random random = new Random();

    public Demon(double x, double y, String filename) {
        super(x, y, filename);
        if (moveDirection == LEFT) {
            setCurrentImage(DEMON_LEFT);
        } else {
            setCurrentImage(DEMON_RIGHT);
        }
        this.setHealthPoints(MAX_HEALTH_POINT);
        setAggressive(random.nextBoolean());
    }

    public static int getMaxHealthPoint() {
        return MAX_HEALTH_POINT;
    }

    public void draw(ArrayList<Entity> entities, ArrayList<Sinkhole> sinkholes,
                       Player player, Point topLeft, Point bottomRight) {
        if (isAggressive()) {
            move();
            if (moveDirection == LEFT) {
                if (isInVincible()) {
                    setCurrentImage(DEMON_INVINCIBLE_LEFT);
                } else {
                    setCurrentImage(DEMON_LEFT);
                }
            } else {
                if(isInVincible()){
                    setCurrentImage(DEMON_INVINCIBLE_RIGHT);
                } else {
                    setCurrentImage(DEMON_RIGHT);
                }
            }
        } else {
            setCurrentImage(DEMON_RIGHT);
        }
        if(checkBlockCollision(entities,sinkholes) || isOutOfBound(topLeft,bottomRight,this)){//this
            moveBack();
        }
        if(checkDemonCollision(player) && !isDead()) {
            fire = new Fire(getX() + getImage().getWidth()/2, getY() + getImage().getHeight()/2,
                    player.getX() + player.currentImage.getWidth()/2,player.getY()+player.currentImage.getHeight()/2);
        }
        if(fire != null && checkDemonCollision(player)){
            fire.draw(player);
        }
        if(isInVincible()){
            InVincibleFrame ++;
        }
        if(getInVincibleFrame()/(REFRESH_RATE/1000) > INVINCIBLE_TIME) {
            setInVincible(false);
            setInVincibleFrame(0);
        }
        if(!isDead()){
            getImage().drawFromTopLeft(getX(),getY());
            renderHealthPoints();
        }
    }

    public boolean isOutOfBound(Point topLeft, Point bottomRight, Demon enermyEntity) {
        return (enermyEntity.getY() > bottomRight.y) || (enermyEntity.getY() < topLeft.y)
                || (enermyEntity.getX() < topLeft.x) || (enermyEntity.getX() > bottomRight.x);
    }
    public boolean checkDemonCollision(Player player){
        Rectangle playerBox = player.getBoundingBox();
        if(this.getAttackBox().intersects(playerBox)){
            return true;
        }
        return false;
    }
    public Rectangle getAttackBox() {
        Point topLeft = new Point(getX()+getImage().getWidth()/2-ATTACK_RANGE/2,getY()+getImage().getHeight()/2);
        return new Rectangle(topLeft, ATTACK_RANGE, ATTACK_RANGE);
    }
}

