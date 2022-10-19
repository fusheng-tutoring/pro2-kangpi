import java.util.ArrayList;

public class TimescaleControl {
    private final static double MAX_COUNT = 3;
    private final static double MIN_COUNT = 3;
    private final static double CONTROL_SIZE = 0.5;
    private int count;
    public TimescaleControl(){
        this.count = 0;
    }
    public void increaseTimeScale(ArrayList<Demon> demons,Navec navec) {
        this.count ++;
        //check count whether is 3 to -3
        if(ckeckOutOfBound()) {
            setSpeed(demons,navec);
            System.out.println("Sped up, Speed: " + count);
        }
    }
    public void decreaseTimeScale(ArrayList<Demon> demons,Navec navec){
    if(ckeckOutOfBound()) {
        setSpeed(demons,navec);
        System.out.println("Sped up, Speed: " + count);
    }
    }
    public boolean ckeckOutOfBound(){
        if (count > 3){
            count = 3;
        } else if (count < -3) {
            count = -3;
        }
        return true;
    }
    public void setSpeed(ArrayList<Demon> demons,Navec navec){
        if(count == -3){
            navec.setMoveSize(navec.getInitialSpeed() * Math.pow(CONTROL_SIZE,3));
        }
        if(count == -2){
            navec.setMoveSize(navec.getInitialSpeed() * Math.pow(CONTROL_SIZE,2));
        }
        if(count == -1){
            navec.setMoveSize(navec.getInitialSpeed() * Math.pow(CONTROL_SIZE,1));
        }
        if(count == 0) {
            navec.setMoveSize(navec.getInitialSpeed());
        }
        if(count == 1) {
            navec.setMoveSize(navec.getInitialSpeed() * (1 + CONTROL_SIZE));
        }
        if(count == 2) {
            navec.setMoveSize(navec.getInitialSpeed() * Math.pow((1 + CONTROL_SIZE),2));
        }
        if(count == 3) {
            navec.setMoveSize(navec.getInitialSpeed() * Math.pow((1 + CONTROL_SIZE),3));
        }
    }
}
