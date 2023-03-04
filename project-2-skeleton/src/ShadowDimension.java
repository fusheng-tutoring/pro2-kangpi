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
    /*Define Image*/
    private final Image BACKGROUND_IMAGE = new Image("res/background0.png");
    private final Image BACKGROUND_IMAGE_1 = new Image("res/background1.png");
    private final String treeAddress = "res/tree.png";
    private final String demonRightAddress = "res/demon/demonRight.png";
    private final String navecRightAddress = "res/navec/navecRight.png";
    private final String sinkholeAddress = "res/sinkhole.png";
    private final String wallAddress = "res/wall.png";
    private final String LEVEL0_FILE_NAME = "res/level0.csv";
    private final String LEVEL1_FILE_NAME = "res/level1.csv";
    /* Font Settings */
    private final int NORMAL_MESSAGE_FONT_SIZE = 75;
    private final int INSTRUCTION_MESSAGE_FONT_SIZE = 40;
    private final Font MEG = new Font("res/frostbite.ttf", INSTRUCTION_MESSAGE_FONT_SIZE);
    private final Font FONT = new Font("res/frostbite.ttf", NORMAL_MESSAGE_FONT_SIZE);
    /* Define String */
    private final static String GAME_TITLE = "SHADOW DIMENSION";
    private final static String BEGIN_MESSAGE = "PRESS SPACE TO STAR";
    private final static String HINT_MESSAGE = "USE ARROW KEYS TO FIND GATE";
    private final static String INSTRUCT_MESSAGE0 = "PRESS SPACE TO START";
    private final static String INSTRUCT_MESSAGE1 = "PRESS A TO ATTACK";
    private final static String INSTRUCT_MESSAGE2 = "DEFEAT NAVEC TO WIN";
    private final static String COMPLETE_MESSAGE = "LEVEL COMPLETE";
    private final static String WIN_MESSAGE = "CONGRATULATIONS";
    private final static String LOSE_MESSAGE = "GAME OVER";
    /* Position Settings */
    private final static int WINDOW_WIDTH = 1024;
    private final static int WINDOW_HEIGHT = 768;
    private final static int MIDDLE_SHORT_X = 360;
    private final static int MIDDLE_SHORT_Y = 400;
    private final static int TITLE_X = 260;
    private final static int TITLE_Y = 250;
    private final int WON_X = 950;
    private final int WON_Y = 670;
    private final static int INSTRUCT_X = 350;
    private final static int INSTRUCT_Y = 350;

    private final static int HINT_Y = 480;
    private final static int WIN_X = 280;

    private final static int OFFSET_X_FIRST = 90;
    private final static int OFFSET_X_SECOND = 70;
    private final static int OFFSET_Y = 190;
    /* Static Ints*/
    private final static double STEP_SIZE = 2;
    private final static double TWICE = 2.0;
    private final static int NO_HEALTH = 0;
    private final double REFRESH_RATE = 60;
    private final double LEVEL_UP_TIME = 3000;
    private final int NO_FRAME = 0;
    private final static int LEVEL_0 = 0;
    private final static int LEVEL_1 = 1;
    /* Game entities */
    private ArrayList<Sinkhole> sinkholes = new ArrayList<>();
    private ArrayList<Wall> walls = new ArrayList<>();
    private ArrayList<Tree> trees = new ArrayList<>();
    private ArrayList<Demon> demons = new ArrayList<>();
    private Navec navec;
    /* Variables */
    private TimescaleControl timescaleControl;
    private boolean isGameFinish;
    private boolean isGameInProcess;
    private boolean isGameWin;
    private boolean buffering;
    private int levelUpFrame;
    private Point bottomRight;
    private Point topLeft;
    private Player player;
    private int level;
    /**
     * Check window size
     */
    ShadowDimension(){
        super(WINDOW_WIDTH, WINDOW_HEIGHT, GAME_TITLE);
        isGameWin = false;
        isGameInProcess = false;
        isGameFinish = false;
        buffering = false;
        levelUpFrame = NO_FRAME;
        level = LEVEL_0;
        readCSV(LEVEL0_FILE_NAME);
    }
    /**
     * The entry point for the program.
     */
    public static void main(String[] args) {
        ShadowDimension game = new ShadowDimension();
        game.run();
    }
    /**
     * Method used to read file and create objects (You can change this
     * method as you wish).
     */
    private void readCSV(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String text = null;
            while ((text = br.readLine()) != null) {
                String[] position = text.split(","); // Split text in level0.csv through comma
                if (position[0].equals("Fae")) {
                    player = new Player(Double.parseDouble(position[1]), Double.parseDouble(position[2]));
                } else if (position[0].equals("Navec")) {
                    navec = new Navec(Double.parseDouble(position[1]), Double.parseDouble(position[2]), navecRightAddress);
                } else if (position[0].equals("Wall")) {
                    walls.add(new Wall(Double.parseDouble(position[1]), Double.parseDouble(position[2]), wallAddress));
                } else if (position[0].equals("Sinkhole")) {
                    sinkholes.add(new Sinkhole(Double.parseDouble(position[1]), Double.parseDouble(position[2]), sinkholeAddress));
                } else if (position[0].equals("Tree")) {
                    trees.add(new Tree(Double.parseDouble(position[1]), Double.parseDouble(position[2]), treeAddress));
                } else if (position[0].equals("BottomRight")) {
                    bottomRight = new Point(Integer.parseInt(position[1]), Integer.parseInt(position[2]));
                } else if (position[0].equals("TopLeft")) {
                    topLeft = new Point(Integer.parseInt(position[1]), Integer.parseInt(position[2]));
                } else if (position[0].equals("Demon")) {
                    demons.add(new Demon(Double.parseDouble(position[1]), Double.parseDouble(position[2]), demonRightAddress));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Performs a state update.
     * allows the game to exit when the escape key is pressed.
     */
    @Override
    protected void update(Input input) {
        screenUpdate();
        inputUpdate(input);
        gameStatusUpdate();
        playerUpdate(input);
    }
    /**
     * Update inputs commands
     * @param input
     */
    void inputUpdate(Input input){
        if (input.wasPressed(Keys.SPACE)) {
            isGameInProcess = true;
        }
        if (input.wasPressed(Keys.ESCAPE)) Window.close();
        if (input.wasPressed((Keys.W))) isGameWin = true;
        if (input.wasPressed((Keys.P))) {
            buffering = true;
            isGameInProcess = false;
        }
        if (input.wasPressed(Keys.L)) timescaleControl.increaseTimeScale(demons,navec);
        if (input.wasPressed(Keys.K)) timescaleControl.decreaseTimeScale(demons,navec);
    }
    /**
     * Update game status
     */
    void gameStatusUpdate() {
        if (buffering) levelUpFrame ++; //3 seconds
        if ((levelUpFrame / (REFRESH_RATE / 1000)) >= LEVEL_UP_TIME) {
            level = LEVEL_1;
            buffering = false;
            levelUpFrame = NO_FRAME;
        }
        if(isGameInProcess && !isGameFinish && !isGameWin) {
            if(level == LEVEL_0) {
                BACKGROUND_IMAGE.draw(Window.getWidth() / TWICE, Window.getHeight() / TWICE);
                wallUpdate();
                sinkholeUpdate();
                if (level == LEVEL_0 && player.getX() >= WON_X && player.getY() >= WON_Y) {
                    buffering = true;
                    isGameInProcess = false;
                    readCSV(LEVEL1_FILE_NAME);
                }
            }
            if(level == LEVEL_1) {
                BACKGROUND_IMAGE_1.draw(Window.getWidth() / TWICE, Window.getHeight() / TWICE);
                treeUpdate();
                demonUpdate();
                sinkholeUpdate();
                if (navec != null) {
                    navec.update(trees, sinkholes, player, topLeft, bottomRight);
                    if (navec.isDead()) isGameWin = true;
                }
            }
        }
    }
    /**
     * Update Walls
     */
    void wallUpdate(){
        for(Entity wall: walls) {
            wall.update();
        }
    }
    /**
     * Update Sinkholes
     */
    void sinkholeUpdate(){
        for(Sinkhole sinkhole: sinkholes) {
            if(sinkhole.getIsAppear()) {
                sinkhole.update();
            }
        }
    }
    /**
     * Update Trees
     */
    void treeUpdate() {
        for(Entity tree: trees) {
            tree.update();
        }
    }
    /**
     * Update Demons
     */
    void demonUpdate(){
        for(Demon demon: demons) {
            if(!demon.isDead()){
                demon.update(trees, sinkholes, player, topLeft, bottomRight);
            }
        }
    }
    /**
     * Fae Update
     */
    private void playerUpdate(Input input){
        if (isGameInProcess && !isGameFinish && !isGameWin){
            player.update(input, walls, sinkholes, trees, demons, navec, topLeft, bottomRight, level);
        }
        if (player.isDead()) isGameFinish = true;
        if (player.getX() >= bottomRight.x) {
            player.setX(player.getX() - STEP_SIZE);
        } else if(player.getX() <= topLeft.x){
            player.setX(player.getX() + STEP_SIZE);
        }else if(player.getY() <= topLeft.y){
            player.setY(player.getY()+ STEP_SIZE);
        }else if(player.getY() >= bottomRight.y) {
            player.setY(player.getY() - STEP_SIZE);
        }
    }
    /**
     * Update screen status
     */
    private void screenUpdate() {
        if (!isGameInProcess && level == 0 && !buffering) {
            FONT.drawString(GAME_TITLE,TITLE_X,TITLE_Y);
            MEG.drawString(BEGIN_MESSAGE,TITLE_X + OFFSET_X_FIRST,TITLE_Y + OFFSET_Y);
            MEG.drawString(HINT_MESSAGE,TITLE_X + OFFSET_X_SECOND,HINT_Y);
        }

        if(buffering){
            FONT.drawString(COMPLETE_MESSAGE, WIN_X, MIDDLE_SHORT_Y);
        }

        if (!isGameInProcess && level == 1 && !buffering) {
            FONT.drawString(INSTRUCT_MESSAGE0, TITLE_X, TITLE_Y);
            MEG.drawString(INSTRUCT_MESSAGE1,TITLE_X + OFFSET_X_FIRST,TITLE_Y + OFFSET_Y);
            MEG.drawString(INSTRUCT_MESSAGE2,TITLE_X + OFFSET_X_SECOND,HINT_Y);
        }

        if(player.getHealthPoint() <= NO_HEALTH && isGameFinish) {
            FONT.drawString(LOSE_MESSAGE, MIDDLE_SHORT_X, MIDDLE_SHORT_Y);
        }

        if(player.getHealthPoint() > NO_HEALTH && isGameWin){
            FONT.drawString(WIN_MESSAGE, WIN_X, MIDDLE_SHORT_Y);
        }
    }
}