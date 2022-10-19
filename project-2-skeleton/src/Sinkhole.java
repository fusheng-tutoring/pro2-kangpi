/**
 * This is made for rendering sinkhole object
 */
public class Sinkhole extends Entity {
    private boolean isAppear; //justify whether sinkhole appear
    /**
     * Sinkhole's contructor
     * @param x
     * @param y
     * @param filename
     */
    public Sinkhole(double x, double y, String filename) {
        super(x, y,filename);
        this.isAppear = true;
    }
    /*remove sinkhole after player step on it*/
    public void setIsAppear(boolean isAppear) {this.isAppear = isAppear;}
    public boolean getIsAppear() {return this.isAppear;}

    @Override
    public void draw() {
        if(isAppear);
        super.draw();
    }
}
