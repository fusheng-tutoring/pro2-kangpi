import bagel.Image;

public class NavecFire extends Fire{
    public NavecFire(double x,double y,double playerX,double playerY){
        super(x,y,playerX,playerY);
        setCurrentImage(new Image("res/navec/navecFire.png"));
        setDamagePoint(20);
    }
    public void printMessage(Player player) {
        System.out.println("Nevec inflicts 20 damage points on Fae. Fae's current health: "
                + player.getHealthPoint() + "/" + getMaxHealthPoint());
    }
}
