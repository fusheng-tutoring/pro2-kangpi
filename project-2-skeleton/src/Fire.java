import bagel.DrawOptions;
import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

public class Fire {
    public static int MAX_HEALTH_POINT = 40;
    private DrawOptions rotation;
    private Image currentImage;
    private int damagePoint;
    private double x;
    private double y;
    private double playX;
    private double playY;

    private boolean isInvincibleState;


    public Fire(double x, double y, double playX, double playY) {
        this.x = x;
        this.y = y;
        this.playX = playX;
        this.playY = playY;
        this.currentImage = new Image("res/demon/demonFire.png");
        this.damagePoint = 10;
    }
    public void draw(Player player) {
        if(getPlayX() <= getX() && getPlayY() <= getY()){
            getCurrentImage().drawFromTopLeft(getX()-getCurrentImage().getWidth()/2 - 40,
                    getY()-getCurrentImage().getHeight()/2-40);
        } else if (getPlayX() <= getX() && getPlayY() > getY()) {
            rotation = new DrawOptions();
            rotation.setRotation(Math.PI/2);
            getCurrentImage().drawFromTopLeft(getX()-currentImage.getWidth()/2-40,
                    getY()-getCurrentImage().getHeight()/2,rotation);
        } else if (getX() > getX() && getY() <= getY()) {
            rotation = new DrawOptions();
            rotation.setRotation(Math.PI*3/2);
            getCurrentImage().drawFromTopLeft(getX()-getCurrentImage().getWidth()/2,
                    getY()-getCurrentImage().getHeight()/2-40,rotation);
        } else if (getPlayX() >  getX() && getY() > getY()) {
            rotation = new DrawOptions();
            rotation.setRotation(Math.PI);
            getCurrentImage().drawFromTopLeft(getX()-getCurrentImage().getWidth()/2,
                    getY()-getCurrentImage().getHeight()/2-40,rotation);
        }
        if (checkPlayerCollision(player) && ! isInvincibleState(player)) {
            player.decreaseHealth(getDamagePoint());
            setInvincibleState(true);
            printMessage(player);
        }
    }
    public boolean checkPlayerCollision(Player player) {
        Rectangle playerBox = player.getBoundingBox();
        if(playerBox.intersects(this.getBoundingBox())) {
            return true;
        }
        return  false;
    }

    private Rectangle getBoundingBox() {
        return currentImage.getBoundingBoxAt(
                new Point(this.getX() + currentImage.getWidth() / 2.0,
                        this.getY() + currentImage.getHeight() / 2.0));
    }

    public void printMessage(Player player) {
        System.out.println("Demon inflicts 10 damage points on Fae. Fae's current health: "
                + player.getHealthPoint() + "/" + getMaxHealthPoint());
    }

    public double getPlayX() {
        return playX;
    }

    public void setPlayX(double playX) {
        this.playX = playX;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }
    public double getY() {
        return y;
    }

    public void setY(double y){
        this.y = y;
    }

    public double getPlayY() {
        return playY;
    }

    public void setPlayY(double playY) {
        this.playY = playY;
    }
    public int getDamagePoint() {
        return damagePoint;
    }

    public void setDamagePoint(int damagePoint) {
        this.damagePoint = damagePoint;
    }

    public Image getCurrentImage() {
        return currentImage;
    }

    public void setCurrentImage(Image currentImage) {
        this.currentImage = currentImage;
    }
    public boolean isInvincibleState(Player player) {
        return isInvincibleState;
    }

    public void setInvincibleState(boolean invincibleState) {
        isInvincibleState = invincibleState;
    }

    public int getMaxHealthPoint() {
        return MAX_HEALTH_POINT;
    }
}
