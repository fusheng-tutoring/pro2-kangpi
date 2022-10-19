import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;
import java.util.ArrayList;

public class Navec extends EnermyEntity {
    private final Image NAVEC_LEFT = new Image("res/navec/navecLeft.png");
    private final Image NAVEC_RIGHT = new Image("res/navec/navecRight.png");
    private final Image NAVEC_INVINCIBLE_LEFT = new Image("res/navec/navecinvincibleLeft.png");
    private final Image NAVEC_INVINCIBLE_RIGHT = new Image("res/navec/navecinvincibleRight.png");
    private final static double NAVEC_MAX_HEALTH = 80;
    private final static double NAVEC_ATTACK_RANGE = 200;
    private NavecFire navecFire;

    public Navec(double x, double y, String filename) {
        super(x, y, filename);
        if (moveDirection == LEFT) {
            setCurrentImage(NAVEC_LEFT);
        } else {
            setCurrentImage(NAVEC_RIGHT);
        }
        setHealthPoints(NAVEC_ATTACK_RANGE);
    }

    public static double getNavecMaxHealth() {
        return NAVEC_MAX_HEALTH;
    }

    public void update(ArrayList<Tree> trees, ArrayList<Sinkhole> sinkholes, Player player, Point topLeft, Point bottomRight) {
        move();
        if (moveDirection == LEFT) {
            if (this.isInVincible()) {
                setCurrentImage(NAVEC_INVINCIBLE_LEFT);
            } else {
                setCurrentImage(NAVEC_LEFT);
            }
        } else {
            if (this.isInVincible()) {
                setCurrentImage(NAVEC_INVINCIBLE_RIGHT);
            } else {
                setCurrentImage(NAVEC_RIGHT);
            }
        }
        if(checkBlockCollision(trees,sinkholes) || isOutOfBound(topLeft,bottomRight,this)){
            moveBack();
        }
        if (checkDemonCollision(player) && !isDead()) {
            Fire Fire = new Fire(getX() + getImage().getWidth() / 2, getY() + getImage().getHeight() / 2,
                    player.getX() + player.getImage().getWidth() / 2, player.getY() + player.getImage().getHeight() / 2);
        }
        if(navecFire != null &&checkDemonCollision(player) && ! isDead()) {
            navecFire.draw(player);
        }
        if(isInVincible()) {
            InVincibleFrame ++;
        }
        if(InVincibleFrame/(REFRESH_RATE/1000) > INVINCIBLE_TIME) {
            setInVincible(false);
            InVincibleFrame = 0;
        }
        if(!isDead()) {
            getImage().drawFromTopLeft(getX(),getY());
            renderHealthPoints();
        }
    }



    public boolean isOutOfBound(Point topLeft, Point bottomRight, Navec enermyEntity) {
        return (enermyEntity.getY() > bottomRight.y) || (enermyEntity.getY() < topLeft.y)
                || (enermyEntity.getX() < topLeft.x) || (enermyEntity.getX() > bottomRight.x);
    }

    public void renderHealthPoint() {
        double percentageHP = (getHealthPoints() / getNavecMaxHealth())*100;
        setHealthPoints(percentageHP);
        FONT.drawString(Math.round(percentageHP)+ "%",getX(),getY(),getCOLOUR());
    }

    public boolean checkDemonCollision(Player player) {
        Rectangle playerBox = player.getBoundingBox();
        if(this.getAttackBox().intersects(playerBox)) {
            return true;
        }
        return false;
    }

    public Rectangle getAttackBox() {
        Point topLeft = new Point(getX()+getImage().getWidth()/2-NAVEC_ATTACK_RANGE/2,
                getY()+getImage().getHeight()/2);
        return new Rectangle(topLeft,NAVEC_ATTACK_RANGE,NAVEC_ATTACK_RANGE);
    }

}
