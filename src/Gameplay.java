import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import javax.swing.Timer;
import javax.swing.JPanel;
import java.io.*;

public class Gameplay extends JPanel implements KeyListener, 
ActionListener, MouseListener, MouseMotionListener{
               
        // game ID
        private static final long serialVersionUID = 1L;
                
        // define map files
        private final String LEVEL1 = "mapTxt/level1.txt";
        private final String LEVEL5 = "mapTxt/level5.txt";
        private final String MAP_NO_SOLUTION = "mapTxt/mapNoSolution.txt";                
        private final String SAVED_MAP = "mapTxt/savedMap.txt";                
        private final String GENERATED_MAP_1 = "mapTxt/geneMap1.txt";
        private final String GENERATED_MAP_2 = "mapTxt/geneMap2.txt";
        private final String GENERATED_MAP_3 = "mapTxt/geneMap3.txt";
        
        // define map state files
        private String MAP_STATES_FILE = "mapTxt/printWriterOutput.txt";        
        private String MOVES_FILE = "mapTxt/movesToLoad.txt";
                      
        // generate a map with defined number of cars
        private int CAR_GENERATE_NUMBER = 10;         
        private int mapNumber = 0;        
        // initial image tool
        Toolkit t = Toolkit.getDefaultToolkit();          
        // background img
        Image backgroundImg = t.getImage("img/background_2.png");
        // player car is fix to a red car
        Image car0Img   = t.getImage("img/car0.png");
        // other cars have multiple images 
        Image car1_1Img = t.getImage("img/car1_1.png");
        Image car1_2Img = t.getImage("img/car1_2.png");
        Image car1_3Img = t.getImage("img/car1_3.png");
        Image car1_4Img = t.getImage("img/car1_4.png");
        Image car2_1Img = t.getImage("img/car2_1.png");
        Image car2_2Img = t.getImage("img/car2_2.png");
        Image car2_3Img = t.getImage("img/car2_3.png");
        Image car2_4Img = t.getImage("img/car2_4.png");        
        Image car3_1Img = t.getImage("img/car3_1.png");
        Image car3_2Img = t.getImage("img/car3_2.png");
        Image car4_1Img = t.getImage("img/car4_1.png");
        Image car4_2Img = t.getImage("img/car4_2.png");        
        // random number to randomize car images         
        private Random rand = new Random();
        // randImg range from 1 to 4
        int randImg = rand.nextInt(4) + 1;               
        // current using map, used to reset  
        private String curMap;       
        // AI perform speed, 5 is normal to eyes 
        private int AI_SPEED = 20000;                 
        // set true for AI, set false for human player
        private boolean AI_MODE = false;
        // show moves if true, otherwise false
        private boolean SHOW_MOVES = true; 
        // show help infomation if true, otherwise false
        private boolean SHOW_HELP = false;
        // generate a map if true, otherwise false
        private boolean GENERATE_MAP = false;
        
        // car with same width
        private final int CAR_WIDTH = 48;
        // small car with length of 2 gird
        private final int S_CAR_LENGTH = 98;
        // big car with length of 3 gird
        private final int B_CAR_LENGTH = 148;        
        // one move pixel distance
        private final int MOVEMENT = 50; 
        
        // transorm pixel value to grid number 
        private final int ONE = 1;
        private final int TWO = 51;
        private final int THREE = 101;
        private final int FOUR = 151;
        private final int FIVE = 201;
        private final int SIX = 251;
                
        /*
         * CAR0 
         * type 1 means small car can go up&down
         * type 2 means small car can go left&right
         * type 3 means big car can go up&down
         * type 4 means big car can go left&right
         */
        private final int CAR0 = 0;
        private final int CAR1 = 1;
        private final int CAR2 = 2;
        private final int CAR3 = 3;
        private final int CAR4 = 4;        
        // put all cars in the map 
        ArrayList<Car> cars; 
        // max number of map state can save        
        private int MAP_ROW_NUMBER = 1000000;
        // car numbers 
        private int MAP_COL_NUMBER = 11;
        // state of a map 
        private int[][] mapState;        
        
        // load moves from file if true, otherwise false
        private boolean LOAD_MOVES = false;
        // data structure for load moves
        private int[][] MOVES_TO_LOAD;        
        // count number of moves need to be loaded
        private int  MOVES_TO_LOAD_COUNT = 0;
        // count total moves
        private int totalMoves = 0;        
        // true while playing the game, false if win the game
        private boolean play = true;
        // set game timer and delay
        private Timer timer;
        private int delay = 1;   
        
        
        public Gameplay() {              
                cars = new ArrayList<Car>();                
                curMap = LEVEL5; // set current map to be loaded
                loadMap(cars, curMap);
                MOVES_TO_LOAD = loadMovesFile(MOVES_FILE);                
                                
                mapState = new int[MAP_ROW_NUMBER][MAP_COL_NUMBER*3];
                //initMapState(cars, mapState);
                //saveMapState(cars, mapState);
                //writeMapState(cars, mapState);

                addMouseListener(this);
                addKeyListener(this);
                setFocusable(true);
                setFocusTraversalKeysEnabled(false);
                timer = new Timer(delay, this);
                timer.start();
                
        }
        
        
        /**
         * inititate map state by set values all to -1
         * 
         * @param cars 
         * @param mapState
         */
        public void initMapState(ArrayList<Car> cars, int[][] mapState) {
                int rows = mapState.length;
                int cols = mapState[0].length;
                for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < cols; j++) {
                                mapState[i][j] = -1;
                        }
                }
        }
                        
        
        /**
         * update coordinates of cars in map state 
         * 
         * @param cars
         * @param mapState
         */
        public void saveMapState(ArrayList<Car> cars, int[][] mapState) {
                int rows = mapState.length;
                //int cols = mapState[0].length;
                for (int i = 0; i < rows; i++) {
                        if (mapState[i][0] == -1) {
                                int j = 0;
                                for (int k = 0; k < cars.size(); k++) {
                                        int type = cars.get(k).getType();
                                        int carX = (cars.get(k).getPosX() - 1)
                                                        / 50 + 1;
                                        int carY = (cars.get(k).getPosY() - 1) 
                                                        / 50 + 1;                                        
                                        mapState[i][j] = type;
                                        mapState[i][j+1] = carX;
                                        mapState[i][j+2] = carY;
                                        //System.out.println(i + " " + j +  " " + k );
                                        //System.out.println(carX + " " + carY);
                                        j += 3;                                         
                                }
                                return;
                        } else {
                                continue;
                        }           
                }
        }
        
        
        /**
         * 
         * write map state to file and print it 
         * 
         * @param cars
         * @param mapState
         */
        public void writeMapState(ArrayList<Car> cars, int[][] mapState) {
                System.out.println("Print current map state: ");
                int rows = mapState.length;
                int cols = mapState[0].length;
                for (int i = 0; i < rows; i++) {                                                
                        if (mapState[i][0] != -1) {
                                continue;                                                           
                        }                        
                        String line = "";
                        for (int j = 0; j < cols; j++) {
                                line += mapState[i-1][j] + " ";
                        }
                        System.out.println(line);
                        int stateNumber = i - 1;
                        System.out.println("This is " + stateNumber 
                                        + "th map state \n");
                        try {                                 
                                PrintWriter pw = new PrintWriter
                                                (new FileOutputStream(
                                                new File(MAP_STATES_FILE), 
                                                true /* append = true */));                                 
                                pw.append(line);
                                pw.append("\n");
                                pw.close();
                        } catch (IOException e) {  
                                
                        }
                        return;                  
                }
        }
        
        
       

        
        @Override
        public void keyPressed(KeyEvent e) {   
                // restart the game by set each car to orinigal place
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        if (!play) {
                                play = true;
                                loadMap(cars, curMap);
                                repaint();
                                totalMoves = 0;
                        }
                }
                
                // reset the game to inital map state
                if (e.getKeyCode() == KeyEvent.VK_R) {
                        loadMap(cars, curMap);
                        repaint();
                        totalMoves = 0;
                }
                
                // load saved game
                if (e.getKeyCode() == KeyEvent.VK_L) {
                        loadMap(cars, SAVED_MAP);
                        repaint();
                        totalMoves = 0;
                }
                
                // save current state to map file                 
                if (e.getKeyCode() == KeyEvent.VK_S) {
                        saveMap(cars, SAVED_MAP);
                        System.out.println("Gmae saved to " + SAVED_MAP);
                        repaint();                        
                }
                
                // press right arrow key to load next map
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        if (mapNumber > 5) {
                                mapNumber -= 6;
                        } 
                        if (mapNumber % 6 == 0) {
                                curMap = LEVEL1;
                        } else if (mapNumber % 6 == 1) {
                                curMap = LEVEL5;
                        } else if (mapNumber % 6 == 2) {
                                curMap = MAP_NO_SOLUTION;
                        } else if (mapNumber % 6 == 3) {
                                curMap = GENERATED_MAP_1;
                        } else if (mapNumber % 6 == 4) {
                                curMap = GENERATED_MAP_2;
                        } else if (mapNumber % 6 == 5) {
                                curMap = GENERATED_MAP_3;
                        }                                                      
                        loadMap(cars, curMap);
                        repaint();
                        mapNumber++;
                        totalMoves = 0;
                        System.out.println(curMap + " loaded ");
                }
                             
                
                // press KEY_M to turn on or off showing moves
                if (e.getKeyCode() == KeyEvent.VK_M) {
                        if (SHOW_MOVES) {
                                SHOW_MOVES = false;
                        } else {
                                SHOW_MOVES = true;
                        }
                }
                
                // press KEY_H to turn on or off showing help message
                if (e.getKeyCode() == KeyEvent.VK_H) {
                        if (SHOW_HELP) {
                                SHOW_HELP = false;
                        } else {
                                SHOW_HELP = true;
                        }
                }
                
                // press KEY_G to generate a map
                if (e.getKeyCode() == KeyEvent.VK_G) {
                        if (GENERATE_MAP) {
                                GENERATE_MAP = false;
                        } else {
                                GENERATE_MAP = true;
                        }
                }
                
                // press KEY_SPACE to load moves
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        if (LOAD_MOVES) {
                                LOAD_MOVES = false;
                        } else {
                                LOAD_MOVES = true;
                        }
                }
                
                // press KEY_A to switch AI mode
                if (e.getKeyCode() == KeyEvent.VK_A) {
                        if (!play) {
                                play = true;
                        } 
                        
                        if (AI_MODE) {
                                AI_MODE = false; 
                        } else {
                                AI_MODE = true;
                        }
                }
                
                // press UP arrow key to make AI and load moves 5 times faster
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                        if (delay <= 125) {
                                delay *= 5;        
                        }                  
                        timer.setDelay(delay);
                        System.out.println("delay = " + timer.getDelay());
                }
                
                // press UP arrow key to make AI and load moves 5 times slower
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        if (delay >= 5) {
                                delay /= 5;        
                        }
                        timer.setDelay(delay);
                        System.out.println("delay = " + timer.getDelay());
                }                        
        }
        

        @Override
        public void mouseClicked(MouseEvent e) {
                // BUTTON1 menas left click 
                if (e.getButton() == MouseEvent.BUTTON1 ) {
                        // e.getX() returns X axle coordinate of mouse
                        // e.getY() returns Y axle coordinate of mouse
                        if (play) {
                                LeftClickCar(cars, e.getX(), e.getY());          
                        }
                                                                                             
                }
                // BUTTON3 means right click                                                      
                if (e.getButton() == MouseEvent.BUTTON3) {
                        if (play) {
                                RightClickCar(cars, e.getX(), e.getY());           
                        }
                                                                  
                }
        }
        
                
        /**
         * initiate game and load a map
         * 
         * @param cars
         * @param filename the map file to be loaded
         */
        public void loadMap(ArrayList<Car> cars, String filename) {
                cars.clear();
                Scanner sc = null;               
                try{
                        sc = new Scanner(new FileReader(filename));                       
                        while(sc.hasNextLine()){
                                String line = sc.nextLine();
                                String[] input = line.split("\\s+");
                                int type =  Integer.parseInt(input[0]);
                                int posX =  (Integer.parseInt(input[1])-1)
                                                * 50 + 1;
                                int posY =  (Integer.parseInt(input[2])-1)
                                                * 50 + 1;                                                                
                                addCar(cars, type, posX, posY);                                
                        }
                }
                catch(FileNotFoundException e)
                {
                        System.err.println("File not found.");
                }
                finally{
                        if (sc != null){
                                sc.close();
                        }
                }               
        }
        
             
        /**
         * save current state to a file
         * 
         * @param cars
         * @param filename the file to store current state map
         */
        public void saveMap(ArrayList<Car> cars, String filename) {  
                BufferedWriter writer = null;
                try {
                        writer = new BufferedWriter(new FileWriter(filename));
                        for (int i = 0; i < cars.size(); i++) {
                                int type = cars.get(i).getType();
                                int carX = (cars.get(i).getPosX()-1) / 50 + 1;
                                int carY = (cars.get(i).getPosY()-1) / 50 + 1;
                                String carInfo = type + " " + carX + " "+carY;
                                writer.write(carInfo);
                                writer.newLine();
                        }    
                }
                catch(IOException e) {
                        e.printStackTrace();
                }
                finally {
                        try {
                                writer.close();
                        } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        }
                }                                                                                                                          
        }
        
                   
        /**
         * generate a map with one player car and other cars
         * 
         * @param carNumber the nubmer of other cars
         */
        public void generateMap(int carNumber) {
                Random rand = new Random();
                int type;
                int randX;
                int randY;                
                System.out.println("Generating map, please wait...");
                // 10000000 is the limitation of map generated
                for (int i = 0; i < 100000000; i ++) { 
                        // map grid to verify whether map valid
                        int[][] map= new int[6][6];
                        for (int m = 0;m < 6; m++) {
                                for (int n = 0; n < 6; n++) {
                                        map[m][n] = 0;
                                }
                        }
                        // set player car to the map grid
                        map[1][2] = 1;
                        map [2][2] = 1;
                        // randMap to store map generated then print valid map
                        String randMap = "0 2 3\n";
                        for (int j = 0; j < carNumber; j++) {
                                type = rand.nextInt(4) + 1;
                                randX = rand.nextInt(6);
                                randY = rand.nextInt(6);                                                                                                                                
                                if (type == 1) {
                                        // check if type 1 car is valid in grid
                                        if (randY > 4 
                                                || map[randX][randY] == 1 
                                                || map[randX][randY+1] == 1) {
                                                break;
                                        }
                                        map[randX][randY] = 1;
                                        map[randX][randY+1] = 1;
                                } else if (type == 2) {
                                        //check if type 2 car is valid in grid
                                        if (randY == 2 
                                                || randX > 4 
                                                || map[randX][randY] == 1 
                                                || map[randX+1][randY] == 1) {
                                                break;
                                        }                                        
                                        map[randX][randY] = 1;
                                        map[randX+1][randY] = 1;
                                } else if (type == 3) {      
                                        //check if type 3 car is valid in grid
                                        if (randY > 3 
                                                || map[randX][randY] == 1 
                                                || map[randX][randY+1] == 1 
                                                || map[randX][randY+2] == 1) {
                                                break;
                                        }
                                        if (randY == 0) {
                                                if (map[randX][randY+3] == 1 
                                                    || map[randX][randY+4]==1 
                                                    || map[randX][randY+5]==1){
                                                        break;
                                                }
                                                
                                        } else if (randY == 1) {
                                                if (map[randX][randY+3] == 1 
                                                    || map[randX][randY+4]==1){
                                                        break;
                                                }
                                                
                                        } else if (randY == 2) {
                                                if (map[randX][randY+3] == 1) {
                                                        break;
                                                }                                                
                                        }                                                                                  
                                        map[randX][randY] = 1;
                                        map[randX][randY+1] = 1;
                                        map[randX][randY+2] = 1;
                                } else {
                                        //check if type 4 car is valid in grid                                        
                                        if (randY == 2 || randX > 3 
                                            || map[randX][randY] == 1 
                                            || map[randX+1][randY] == 1 
                                            || map[randX+2][randY] == 1) {
                                                break;
                                        }
                                        map[randX][randY] = 1;
                                        map[randX+1][randY] = 1;
                                        map[randX+2][randY] = 1;
                                }
                                randX++;
                                randY++;
                                randMap += type + " " + randX 
                                                + " " + randY + " \n";                                
                                if (j == carNumber-1) {
                                        boolean found = true;
                                        // if 6 grid in a row all taken
                                        // then map is invalid
                                        for (int row = 0; row < 6; row++) {
                                                int count = 0;
                                                for (int col=0;col<6;col++) {
                                                        if (map[row][col]==1){
                                                                count++;
                                                        } 
                                                        if (count == 6) {
                                                                found = false;
                                                        }
                                                } 
                                        }
                                        // if 6 grid in a column all taken
                                        // then map is invalid
                                        for (int col = 0; col < 6; col++) {
                                                int count = 0;
                                                for (int row=0;row<6;row++){
                                                        if (map[row][col]==1){
                                                                count++;
                                                        } 
                                                        if (count == 6) {
                                                                found = false;
                                                        } 
                                                } 
                                        } 
                                        // if player row has 2 grid empty
                                        // then map is invalid 
                                        if (map[3][2] == 0 && map[4][2] == 0) {
                                                found = false;
                                        }
                                        // print map if valid, otherwise generate again  
                                        if (found) {
                                                System.out.println();
                                                System.out.println(
                                                        "Map generated: ");
                                                System.out.println(randMap);
                                                return;
                                        } else {
                                                break;
                                        }                                                                                                                      
                                }
                                
                        }                                                
                }
                System.out.println("Cannot generate a valid map, "
                                + "please try again.");
                return ;
        }
        
        /**
         * 
         * add cars from map file to cars
         * 
         */
        
        /**
         * add cars from map file to cars
         * 
         * @param cars
         * @param type
         * @param posX
         * @param posY
         */
        public void addCar(ArrayList<Car> cars, int type, int posX, int posY){
                switch (type) {
                case 0: cars.add(new Car(type, posX, posY, 
                                S_CAR_LENGTH, CAR_WIDTH));
                break;
                
                case 1: cars.add(new Car(type, posX, posY, 
                                CAR_WIDTH, S_CAR_LENGTH));
                break;
                
                case 2: cars.add(new Car(type, posX, posY, 
                                S_CAR_LENGTH, CAR_WIDTH));
                break;
                
                case 3: cars.add(new Car(type, posX, posY, 
                                CAR_WIDTH, B_CAR_LENGTH));
                break;
                
                case 4: cars.add(new Car(type, posX, posY, 
                                B_CAR_LENGTH, CAR_WIDTH));
                break;
                
                default: break;
                }
        }
      
        
        /**
         * paint all cars to screen
         * 
         * @param cars
         * @param g
         */
        public void paintCars(ArrayList<Car> cars, Graphics g) {                
                for (int i = 0; i < cars.size(); i++) {
                        int type = cars.get(i).getType();
                        int carX = cars.get(i).getPosX();
                        int carY = cars.get(i).getPosY();
                        if (type == 0) {                                
                                g.drawImage(car0Img, carX, carY, this);
                                //g.setColor(Color.red);                                
                                //g.fillRect(carX, carY, SCARLENGTH, CARWIDTH);
                        } else if (type == 1) {
                                Image car1Img = null;
                                switch (randImg) {
                                case(1): car1Img = car1_1Img;
                                break;
                                case(2): car1Img = car1_2Img;
                                break;
                                case(3): car1Img = car1_3Img;
                                break;
                                case(4): car1Img = car1_4Img;
                                break;
                                default: break;
                                }
                                g.drawImage(car1Img, carX, carY, this);
                                //g.setColor(Color.yellow);
                                //g.fillRect(carX, carY, CARWIDTH, SCARLENGTH);
                        } else if (type == 2) {
                                Image car2Img = null;
                                switch (randImg) {
                                case(1): car2Img = car2_1Img;
                                break;
                                case(2): car2Img = car2_2Img;
                                break;
                                case(3): car2Img = car2_3Img;
                                break;
                                case(4): car2Img = car2_4Img;
                                break;
                                default: break;
                                }
                                g.drawImage(car2Img, carX, carY, this);
                                //g.setColor(Color.yellow);
                                //g.fillRect(carX, carY, SCARLENGTH, CARWIDTH);
                        } else if (type == 3) {
                                Image car3Img = null;
                                switch (randImg % 2) {
                                case(0): car3Img = car3_1Img;
                                break;
                                case(1): car3Img = car3_2Img;
                                break;                              
                                default: break;
                                }
                                g.drawImage(car3Img, carX, carY, this);
                                //g.setColor(Color.blue);
                                //g.fillRect(carX, carY, CARWIDTH, BCARLENGTH);
                        } else if (type == 4) {
                                Image car4Img = null;
                                switch (randImg % 2) {
                                case(0): car4Img = car4_1Img;
                                break;
                                case(1): car4Img = car4_2Img;
                                break;                              
                                default: break;
                                }
                                g.drawImage(car4Img, carX, carY, this);
                                //g.setColor(Color.blue);
                                //g.fillRect(carX, carY, BCARLENGTH, CARWIDTH);
                        } else {
                                System.err.println("Wrong car type");
                        }
                        //randImg++;
                }
        }        

        
        /**
         * paint the game frame 
         * 
         */
        public void paint(Graphics g) {
                // draw background image
                g.drawImage(backgroundImg, 1, 1, 300, 300, this);
                // draw all cars in the map
                paintCars(cars, g);                 
                // generate a map if GENERATE_MAP is true
                if (GENERATE_MAP == true) {
                        generateMap(CAR_GENERATE_NUMBER);
                }              
                // if player wins, show messages
                if (cars.get(0).getPosX() >= 201) {
                        play = false;
                        g.setColor(Color.white);
                        g.setFont(new Font("serif", Font.BOLD, 30));
                        g.drawString("You Won! ", 90, 120);
                        
                        g.setFont(new Font("serif", Font.BOLD, 25));
                        g.drawString("Press Enter to restart", 35, 160);
                        g.setColor(Color.black);
                        g.drawString(String.valueOf(totalMoves), 215, 30);
                }                   
                // show total moves if SHOW_MOVES is true
                if (SHOW_MOVES == true) {
                        g.setFont(new Font("serif", Font.BOLD, 25));                        
                        g.setColor(Color.black);
                        g.drawString(String.valueOf(totalMoves), 215, 30);
                }
                // load moves if LOAD_MOVES is true
                if (LOAD_MOVES) {
                        loadMoves();
                }
                // start AI mode if AI_MODE is true
                if (AI_MODE) {
                        AIplay();        
                }
                // dispose graphics                
                g.dispose();                
        }
  
        /**
         * use AI algorithm to solve the puzzle 
         * 
         */
        public void AIplay() {
                if (!play) {           
                        return;
                        //AIMODE = false; 
                }                
                Random rand = new Random();                                        
                for (int i = 0; i < AI_SPEED; i++) {
                        int randX = rand.nextInt(299) + 1;
                        int randY = rand.nextInt(329) + 1;                        
                        if (randX % 2 == 0) {                                
                                LeftClickCar(cars, randX, randY);
                        } else {
                                //moves++;
                                RightClickCar(cars, randX, randY);
                        }
                        //every time check if can move player car to end  
                        for (int j = 0; j < 4; j++) {
                                int playerX = cars.get(0).getPosX();
                                int playerY = cars.get(0).getPosY();
                                if (checkCollide(playerX+20+MOVEMENT,
                                                playerY+20)) {
                                        cars.get(0).setPosX(playerX+MOVEMENT);
                                        //saveMapState(cars, mapState);
                                        //writeMapState(cars, mapState);
                                }
                        }                                                
                        // if game won, stop game
                        if (cars.get(0).getPosX() >= 201) {
                                System.out.println("AI won the game with "
                                                + "total moves: "+totalMoves);
                                play = false;
                                return;                                
                        } 
                }
        }
        
        /**
         * 
         * @param movesFile
         * @return an array with moves in it
         */
        public int[][] loadMovesFile(String movesFile) {                
                int[][] moves = new int[MAP_ROW_NUMBER][MAP_COL_NUMBER*3];
                Scanner sc = null;               
                try{
                        sc = new Scanner(new FileReader(movesFile));
                        int row = 0;
                        while(sc.hasNextLine()){
                                String line = sc.nextLine();                                
                                String[] input = line.split("\\s+");
                                //System.out.println(line);
                                int carNumbers = input.length / 3;                                
                                for (int col = 0; col < carNumbers; col++) {                                        
                                        int carType =  Integer.parseInt(
                                                        input[col*3]);
                                        int carPosX =  Integer.parseInt(
                                                        input[col*3+1]);
                                        int carPosY =  Integer.parseInt(
                                                        input[col*3+2]);                                        
                                        moves[row][col*3] = carType;
                                        moves[row][col*3+1] = carPosX;
                                        moves[row][col*3+2] = carPosY;                                                                                
                                }
                                row++;                                                                                                                           
                        }
                        MOVES_TO_LOAD_COUNT = row;
                }
                catch(FileNotFoundException e)
                {
                        System.err.println("File not found.");
                }
                finally{
                        if (sc != null){
                                sc.close();
                        }
                }
                return moves;                
        }
        
        /**
         * load moves from the specified file
         * 
         */
        public void loadMoves() {
                for (int i = totalMoves+1; i < MOVES_TO_LOAD.length+1; i++) {
                        if (i >= MOVES_TO_LOAD_COUNT) {
                                LOAD_MOVES = false;
                                //play = false;
                                return;
                        }
                        if (!play) {
                                return;
                        }
                        //System.out.println(MOVES_TO_LOAD);
                        for (int j = 0; j < MOVES_TO_LOAD[0].length; j++) {                                
                                if(MOVES_TO_LOAD[i][j]!=MOVES_TO_LOAD[i-1][j]){
                                        int carNum = j / 3;
                                        int X = MOVES_TO_LOAD[i-1][carNum*3+1];
                                        int Y = MOVES_TO_LOAD[i-1][carNum*3+2];                                        
                                        int posX = (X - 1) * 50 + 1;
                                        int posY = (Y - 1) * 50 + 1;
                                        int dir = MOVES_TO_LOAD[i][j] 
                                                       - MOVES_TO_LOAD[i-1][j];
                                        if (j % 3 == 1) {                                                
                                                cars.get(carNum).setPosX(
                                                         posX + dir*MOVEMENT);
                                                totalMoves++;                                                                                               
                                                //saveMapState(cars, mapState);
                                                //writeMapState(cars, mapState);
                                        } else {                                               
                                                cars.get(carNum).setPosY(
                                                          posY + dir*MOVEMENT);
                                                totalMoves++;                                                                                              
                                                //saveMapState(cars, mapState);
                                                //writeMapState(cars, mapState);
                                        }                                                                           
                                        return;
                                }
                        }
                }                
        }
        
        
        @Override
        public void actionPerformed(ActionEvent arg0) {
                timer.start();                
                repaint();                
        }
                
                
        /**
         * check if mouse are in area of a car
         * 
         * @param mouseX
         * @param mouseY
         * @param carX
         * @param carY
         * @param w
         * @param h
         * @return 
         */
        public boolean checkPos(int mouseX, int mouseY, 
                        int carX, int carY, int w, int h) {
                if ((mouseX >= carX && mouseX <= carX + w) 
                                && (mouseY >= carY && mouseY <= carY + h) ) {
                        return true;
                } else {                        
                        return false;
                }
        }

           
        /**
         * check collides if move a car
         * nextX and nextY is the new position of the car
         * @param nextX
         * @param nextY
         * @return
         */
        public boolean checkCollide(int nextX, int nextY) {
                for (int i = 0; i < cars.size(); i++) {
                        int carX = cars.get(i).getPosX();
                        int carY = cars.get(i).getPosY();
                        int w = cars.get(i).getWidth();
                        int h = cars.get(i).getLength();
                        if (checkPos(nextX, nextY, carX, carY, w, h)) {
                                return false;
                        }                
                }                
                return true;
        }

               
        /**
         *  after left clicked the car, move the car up or left if not collide
         * @param cars
         * @param mouseX
         * @param mouseY
         */
        public void LeftClickCar(ArrayList<Car> cars, int mouseX, int mouseY) {
                for (int i = 0; i < cars.size(); i++) {
                        int carX = cars.get(i).getPosX();
                        int carY = cars.get(i).getPosY();
                        int w = cars.get(i).getWidth();
                        int h = cars.get(i).getLength();                                                
                        if (checkPos(mouseX, mouseY, carX, carY, w, h)) {
                                if (w < h) {
                                        if (checkCollide(carX + 20, carY - 20)) {
                                                if (carY - MOVEMENT >= ONE) {                                                        
                                                        cars.get(i).setPosY(
                                                             carY - MOVEMENT);
                                                        totalMoves++;
                                                        int posX = carX / 50 + 1;
                                                        int posY = carY / 50 + 1;                 
                                                        //saveMapState(cars,
                                                        //mapState);
                                                        //writeMapState(cars,
                                                        //mapState);
                                                }                                                                                              
                                        }   
                                } else {
                                        if (checkCollide(carX - 20, carY + 20)) {
                                                if (carX - MOVEMENT >= ONE) {                                                        
                                                        cars.get(i).setPosX(
                                                             carX - MOVEMENT);
                                                        totalMoves++;
                                                        int posX = carX / 50 + 1;
                                                        int posY = carY / 50 + 1;                                                        
                                                        //saveMapState(cars, 
                                                        //mapState);
                                                        //writeMapState(cars, 
                                                        //mapState);
                                                }                                                                                                                                                
                                        }
                                }
                        }
                }
        }
        
       
        /**
         * after right clicked the car, move the car dwon or right if not collide
         * 
         * @param cars
         * @param mouseX
         * @param mouseY
         */
        public void RightClickCar(ArrayList<Car> cars, int mouseX, int mouseY){
                for (int i = 0; i < cars.size(); i++) {
                        int type = cars.get(i).getType();
                        int carX = cars.get(i).getPosX();
                        int carY = cars.get(i).getPosY();
                        int w = cars.get(i).getWidth();
                        int h = cars.get(i).getLength();                        
                        if (checkPos(mouseX, mouseY, carX, carY, w, h)) {
                                if (w < h) {
                                        if (type == CAR1 && checkCollide(
                                                        carX + 20,carY + 120)){
                                                if (carY + MOVEMENT <= FIVE) {
                                                        cars.get(i).setPosY(
                                                             carY + MOVEMENT);
                                                        totalMoves++;
                                                        int posX = carX/50+1;
                                                        int posY = carY/50+1;                                                        
                                                        //saveMapState(cars, mapState);
                                                        //writeMapState(cars, mapState);
                                                }
                                        }                                        
                                        if (type == CAR3 && checkCollide(
                                                        carX + 20, carY + 170)) { 
                                                if (carY + MOVEMENT <= FOUR) {
                                                        cars.get(i).setPosY(
                                                             carY + MOVEMENT);
                                                        totalMoves++;
                                                        int posX = carX / 50 + 1;
                                                        int posY = carY / 50 + 1;
                                                        //System.out.println("Car (" + posX + ", " + posY + ") " + "move down");
                                                        //saveMapState(cars, mapState);
                                                        //writeMapState(cars, mapState);
                                                }
                                        }
                                        
                                } else {                                        
                                        if ((type == CAR0 || type == CAR2) && checkCollide(carX + 120, carY + 20)) {   
                                                if (carX + MOVEMENT <= FIVE) {
                                                        cars.get(i).setPosX(carX + MOVEMENT);
                                                        totalMoves++;
                                                        int posX = carX / 50 + 1;
                                                        int posY = carY / 50 + 1;
                                                        //System.out.println("Car (" + posX + ", " + posY + ") " + "move right");
                                                        //saveMapState(cars, mapState);
                                                        //writeMapState(cars, mapState);
                                                }                                                                                              
                                        }                                
                                        if (type == CAR4 && checkCollide(carX + 170, carY + 20)) {                                                
                                                if (carX + MOVEMENT <= FOUR) {
                                                        cars.get(i).setPosX(carX + MOVEMENT);
                                                        totalMoves++;
                                                        int posX = carX / 50 + 1;
                                                        int posY = carY / 50 + 1;
                                                        //System.out.println("Car (" + posX + ", " + posY + ") " + "move right");
                                                        //saveMapState(cars, mapState);
                                                        //writeMapState(cars, mapState);
                                                }
                                        }
                                } 
                        }
                }
        }
        
        
        @Override
        public void keyReleased(KeyEvent e) {}

        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
        public void mouseDragged(MouseEvent arg0) {}

        @Override
        public void mouseMoved(MouseEvent arg0) {}
        
        @Override
        public void mouseEntered(MouseEvent arg0) {}

        @Override
        public void mouseExited(MouseEvent arg0) {}

        @Override
        public void mousePressed(MouseEvent arg0) {}
        
        @Override
        public void mouseReleased(MouseEvent arg0) {}        

}
