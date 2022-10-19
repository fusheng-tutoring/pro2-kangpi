import bagel.*;
import bagel.util.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Skeleton Code for SWEN20003 Project 2, Semester 2, 2022
 *
 * Please enter your name below
 * @author Kang Pi
 */

public class ShadowDimension extends AbstractGame{
    /*Position Settings*/
    private final static int WINDOW_WIDTH = 1024;
    private final static int WINDOW_HEIGHT = 768;
    private final static int MIDDLE_SHORT_X = 360;
    private final static int MIDDLE_SHORT_Y = 400;
    private final static int TITLE_X = 260;
    private final static int TITLE_Y = 250;
    private final static int INSTRUCT_X = 350;
    private final static int INSTRUCT_Y = 350;

    private final static int HINT_Y = 480;
    private final static int WIN_X = 280;

    private final static int OFFSET_X = 90;
    private final static int OFFSET_Y = 190;
    /*Define Image*/
    private final Image BACKGROUND_IMAGE = new Image("res/background0.png");
    private final Image BACKGROUND_IMAGE_1 = new Image("res/background1.png");

    private final String treeAddress = "res/tree.png";
    private final String demonRightAddress = "res/demon/demonRight.png";
    private final String navecRightAddress = "res/navec/navecRight.png";
    private final String sinkholeAddress = "res/sinkhole.png";
    private final String wallAddress = "res/wall.png";
    private final String FILE_NAME = "res/level0.csv";
    private final String LEVEL1_FILE_NAME = "res/level1.csv";
    /*Define Font */
    private final int NORMAL_MESSAGE_FONT_SIZE = 75;
    private final int INSTRUCTION_MESSAGE_FONT_SIZE = 40;
    private final Font MEG = new Font("res/frostbite.ttf", INSTRUCTION_MESSAGE_FONT_SIZE);
    private final Font FONT = new Font("res/frostbite.ttf", NORMAL_MESSAGE_FONT_SIZE);
    /*Define String*/
    private final static String GAME_TITLE = "SHADOW DIMENSION";
    private final static String BEGIN_MESSAGE = "PRESS SPACE TO STAR";
    private final static String HINT_MESSAGE = "USE ARROW KEYS TO FIND GATE";
    private final static String INSTRUCT_MESSAGE0 = "PRESS SPACE TO START";
    private final static String INSTRUCT_MESSAGE1 = "PRESS A TO ATTACK";
    private final static String INSTRUCT_MESSAGE2 = "DEFEAT NAVEC TO WIN";
    private final static String COMPLETE_MESSAGE = "LEVEL_COMPLETE";
    private final static String WIN_MESSAGE = "CONGRATULATIONS";
    private final static String LOSE_MESSAGE = "GAME OVER";
    /*Static Ints*/
    private final static double STEP_SIZE = 2;
    private final static double TWICE = 2.0;
    private final static int NO_HEALTH = 0;
    /*Game entities*/
    private ArrayList<Sinkhole> sinkholes = new ArrayList<>();
    private ArrayList<Entity> walls = new ArrayList<>();
    private ArrayList<Entity> trees = new ArrayList<>();
    private ArrayList<Demon> demons = new ArrayList<>();
    private ArrayList<Navec> navec = new ArrayList<Navec>();

    private ArrayList<TimescaleControl> timescaleControl = new ArrayList<>();

    private boolean isGameFinish;
    private boolean isGameBegin;
    private boolean isGameWin;
    private boolean levelUp;
    private boolean hasLevelUp;
    private int levelUpFrame;
    private double REFRESH_RATE = 60;
    private double LEVEL_UP_TIME = 3000;
    private Point bottomRight;
    private Point topLeft;
    private Player player;
    private static String filename;
    /**
     * Check window size
     */
    ShadowDimension(String filename){
        super(WINDOW_WIDTH, WINDOW_HEIGHT, GAME_TITLE);
        this.filename = filename;
        readCSV(filename);
        isGameWin = false;
        isGameBegin = false;
        isGameFinish = false;
        levelUp = false;
        hasLevelUp = false;
        levelUpFrame = 0;
    }
    /**
     * The entry point for the program.
     */
    public static void main(String[] args) {
        ShadowDimension game = new ShadowDimension(filename);
        game.run();
    }
    /**
     * Method used to read file and create objects (You can change this
     * method as you wish).
     */
    private void readCSV(String filename){
        try(BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String text = null;
            while ((text = br.readLine()) != null) {
                String[] position = text.split(","); // Split text in level0.csv through comma
                if (position[0].equals("Player")) {
                    player = new Player(Double.parseDouble(position[1]), Double.parseDouble(position[2]));
                } else if (position[0].equals("Wall")) {
                    walls.add(new Wall(Double.parseDouble(position[1]), Double.parseDouble(position[2]), wallAddress));
                } else if (position[0].equals("Sinkhole")) {
                    sinkholes.add(new Sinkhole(Double.parseDouble(position[1]), Double.parseDouble(position[2]), sinkholeAddress));
                } else if (position[0].equals("BottomRight")) {
                    bottomRight = new Point(Integer.parseInt(position[1]), Integer.parseInt(position[2]));
                } else if (position[0].equals("TopLeft")) {
                    topLeft = new Point(Integer.parseInt(position[1]), Integer.parseInt(position[2]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try(BufferedReader br = new BufferedReader(new FileReader(LEVEL1_FILE_NAME))) {
            String text = null;
            while ((text = br.readLine()) != null) {
                String[] position = text.split(","); // Split text in level0.csv through comma
                if (position[0].equals("Player")) {
                    player = new Player(Double.parseDouble(position[1]), Double.parseDouble(position[2]));
                } else if(position[0].equals("Tree")) {
                    trees.add(new Tree(Double.parseDouble(position[1]), Double.parseDouble(position[2]),treeAddress));
                } else if (position[0].equals("Demon")) {
                    demons.add(new Demon(Double.parseDouble(position[1]), Double.parseDouble(position[2]), demonRightAddress));
                } else if (position[0].equals("Sinkhole")) {
                    sinkholes.add(new Sinkhole(Double.parseDouble(position[1]), Double.parseDouble(position[2]), sinkholeAddress));
                } else if(position[0].equals("Navec")){
                    navec.add(new Navec(Double.parseDouble(position[1]), Double.parseDouble(position[2]),navecRightAddress));
                }else if (position[0].equals("BottomRight")) {
                    bottomRight = new Point(Integer.parseInt(position[1]), Integer.parseInt(position[2]));
                } else if (position[0].equals("TopLeft")) {
                    topLeft = new Point(Integer.parseInt(position[1]), Integer.parseInt(position[2]));
                }
            }
        }catch(Exception e){
                e.printStackTrace();
            }
        }
        /**
     * Performs a state update.
     * allows the game to exit when the escape key is pressed.
     */
    @Override
    protected void update(Input input) {
        inputUpdate(input);
        fastPass(input);
        gameStatusUpdate(input);
        gameLevelUp(input);


    }


    /**
     * Update inputs commands
     * @param input
     */
    void inputUpdate(Input input){
        if(input.wasPressed(Keys.ESCAPE)){Window.close();}
    }

    void fastPass(Input input) {
        if (input.wasPressed((Keys.W))) {
            isGameWin = true;
        }
    }
    /**
     * Update game status
     * @param input
     */
    void gameStatusUpdate(Input input) {
        if (!isGameBegin) {
            drawStartScreen(input);
        } if (isGameFinish) {
            drawFinishScreen(player);
        } if (levelUp && !hasLevelUp) { //3 seconds
            levelUpFrame ++;
            drawFinishScreen(player);
        } if (levelUpFrame / (REFRESH_RATE / 1000) > LEVEL_UP_TIME || input.wasPressed(Keys.W)) {
            hasLevelUp = true;
            sinkholes = new ArrayList<>();
            readCSV(LEVEL1_FILE_NAME);
            isGameBegin = false;
            levelUpFrame = 0;
        } if (isGameWin) {
            drawFinishScreen(player);
        }
    }
    private void gameLevelUp(Input input) {
        if(isGameBegin && !isGameFinish && !isGameWin) {
            if(!levelUp && !hasLevelUp) {
                BACKGROUND_IMAGE.draw(Window.getWidth() / TWICE, Window.getHeight() / TWICE);
                wallUpdate();
                sinkholeUpdate();
                playerUpdate(input);
                if(player.isWon()){
                    levelUp = true;
                }
            }
            if(hasLevelUp) {
                BACKGROUND_IMAGE_1.draw(Window.getWidth() / TWICE, Window.getHeight() / TWICE);
                if (input.wasPressed(Keys.L)) {
                    timescaleControl.increaseTimeScale(demons,navec);
                } if (input.wasPressed(Keys.K)) {
                    timescaleControl.decreaseTimeScale(demons,navec);
                }
                treeUpdate();
                sinkholeUpdate();
                for(Demon demon: demons) {
                    if(! demon.isDead()){
                        demon.draw(trees,sinkholes,player,topLeft,bottomRight);
                    }
                }
                navec.draw();
                if(navec.isDead()){
                    isGameWin = true;
                }
                player.draw(input,walls,sinkholes,demons,entities,navec,topLeft,bottomRight);
            }
            if(player.isDead()) {
                isGameFinish = true;
            }
        }
    }
    /**
     * Update walls
     */
    void wallUpdate(){
        for(Entity wall: walls) {
            wall.draw();
        }
    }
    /**
     * Update sinkholes
     */
    void sinkholeUpdate(){
        for(Sinkhole sinkhole: sinkholes) {
            if(sinkhole.getIsAppear()) {
                sinkhole.draw();
            }
        }
    }

    void treeUpdate() {
        for(Entity tree: trees) {
            tree.draw();
        }
    }
    void demonUpdate(){
        for(Demon demon: demons) {
            if(demon.getIsAppear()) {
                demon.draw(entities,sinkholes,player,topLeft,bottomRight);
            }
        }
    }
    /**
     * Update player status
     * @param input
     */
    void playerUpdate(Input input){
//        player.draw(input,walls,sinkholes,entities,demons,navec,topLeft,bottomRight);
        player.drawHealth();
        /*handle collision with walls and sinkholes*/
        if (player.checkCollisions(walls, sinkholes)){
            player.moveBack();
        }
        /*Check window bound*/
        if(player.getX() >= bottomRight.x) {
            player.setX(player.getX() - STEP_SIZE);
        } else if(player.getX() <= topLeft.x){
            player.setX(player.getX() + STEP_SIZE);
        }else if(player.getY() <= topLeft.y){
            player.setY(player.getY()+ STEP_SIZE);
        }else if(player.getY() >= bottomRight.y) {
            player.setY(player.getY() - STEP_SIZE);
        }
        /*Check player status*/
        if(player.isWon()) { isGameWin = true;}
        if(player.isDead()) { isGameFinish = true;}
    }
    /**
     * Draw the start screen
     * @param input
     */
    private void drawStartScreen(Input input) {
        FONT.drawString(GAME_TITLE,TITLE_X,TITLE_Y);
        MEG.drawString(BEGIN_MESSAGE,TITLE_X + OFFSET_X,TITLE_Y + OFFSET_Y);
        MEG.drawString(HINT_MESSAGE,TITLE_X + OFFSET_X,HINT_Y);
        if(input.wasPressed(Keys.SPACE)) {
            isGameBegin = true;
        }
    }
    /**
     * Draw the finish screen
     * @param player
     */
    private void drawFinishScreen(Player player) {
        if(player.getHealthPoint() <= NO_HEALTH && isGameFinish) {
            FONT.drawString(LOSE_MESSAGE,MIDDLE_SHORT_X,MIDDLE_SHORT_Y);
        }
        if(player.getHealthPoint() > NO_HEALTH && isGameWin){
            FONT.drawString(COMPLETE_MESSAGE, WIN_X,MIDDLE_SHORT_Y);
        }
    }
}
