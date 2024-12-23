/**
 * @auther @MahanNoosh
 * Jun 2022-2023
 */

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Font;

/**
 * The main class
 * Makes the game window
 * Makes 2 tanks
 * Updates the game
 * Checks for win
 */
public class TankGame extends JFrame {
    public static boolean running;
    public static final int CELL_SIZE = 70;
    public static double lastShoot1 = 0, lastShoot2 = 0;
    public final double FIRE_RATE = 500_000_000;
    public final int MAX_BOUNCE = 10;
    public boolean gameOver = false;
    public String winner = "";


    /**
     * Set the title of the game window
     * Set the default close operation
     * Set window size
     * Center the game window on the screen
     * Disable window resizing
     * Make the game window visible
     */
    public TankGame() {
        setTitle("Tank Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //setExtendedState(JFrame.MAXIMIZED_BOTH);
        final int WIN_WIDTH = 5;
        final int WIN_HEIGHT = 3;
        final int WIN_SCALE = 5;
        setSize(WIN_WIDTH * WIN_SCALE * CELL_SIZE, WIN_HEIGHT * WIN_SCALE * CELL_SIZE);
        setLocationRelativeTo(null);
        setResizable(false);
        final Dimension SIZE = getSize();
        final int MAX_WINDOW_WIDTH = SIZE.width;
        final int MAX_WINDOW_HEIGHT = SIZE.height;
        add(new GamePanel(MAX_WINDOW_WIDTH, MAX_WINDOW_HEIGHT));
        setVisible(true);
    }

    /**
     * Game panel
     * Does operations in game
     * Does graphical stuff
     */
    private class GamePanel extends JPanel implements KeyListener {
        int FIELD_WIDTH;
        int FIELD_HEIGHT;
        final int DIS_FROM_WIN = 10;
        private boolean[] keysPlayer1;                        // Array to track keys pressed for player 1
        private boolean[] keysPlayer2;                        // Array to track keys pressed for player 2
        private Sprite player1;                               // Player 1 sprite object
        private Sprite player2;                               // Player 2 sprite object
        ArrayList<Bullet> bullets1 = new ArrayList<>();
        ArrayList<Bullet> bullets2 = new ArrayList<>();
        private Map map;                                      // Map object

        /**
         * Makes 2 tanks
         * Create the map
         * Set the panel focusable
         * Request focus for key events
         * Add key listener to handle key events
         */
        public GamePanel(int MAX_WINDOW_WIDTH, int MAX_WINDOW_HEIGHT) {
            FIELD_WIDTH = MAX_WINDOW_WIDTH - DIS_FROM_WIN;
            FIELD_HEIGHT = MAX_WINDOW_HEIGHT - DIS_FROM_WIN;              // Initialize the keys array for player 2
            keysPlayer1 = new boolean[256];                   // Initialize the keys array for player 1
            keysPlayer2 = new boolean[256];
            player1 = new Sprite(200, MAX_WINDOW_HEIGHT - 200, 50, 50, "RedTank.png", FIELD_WIDTH, FIELD_HEIGHT); // Create player 1 sprite object
            player2 = new Sprite(MAX_WINDOW_WIDTH-200, 200, 50, 50, "BlueTank.png", FIELD_WIDTH, FIELD_HEIGHT);// Create player 2 sprite object
            map = new Map(Maps.map1, CELL_SIZE);
            setFocusable(true);
            requestFocus();
            addKeyListener(this);
        }

        /**
         * Updates the game
         * Checks which key has pressed continuously
         * Call the function corresponding to key pressed
         */
        public void update() {
            if (keysPlayer1[KeyEvent.VK_D]) {
                player1.setRotation(player1.getRotation() + player1.dl());
            } else if (keysPlayer1[KeyEvent.VK_A]) {
                player1.setRotation(player1.getRotation() - player1.dr());
            }

            if (keysPlayer1[KeyEvent.VK_W]) {
                player1.setPervY(player1.getY());
                player1.setPervX(player1.getX());
                player1.setX(player1.getX() + player1.dx());
                player1.setY(player1.getY() - player1.dy());
            } else if (keysPlayer1[KeyEvent.VK_S]) {
                player1.setPervY(player1.getY());
                player1.setPervX(player1.getX());
                player1.setX(player1.getX() - player1.dx());
                player1.setY(player1.getY() + player1.dy());
            }
            if (keysPlayer2[KeyEvent.VK_C]){
                if(System.nanoTime()>= lastShoot1+FIRE_RATE) {
                    bullets1.add(new Bullet(player1.getX() + (player1.getWidth() / 2) - 5, player1.getY() + (player1.getHeight() / 2) - 5, 10, 10, player1.dx(), player1.dy(), Color.RED));
                    lastShoot1 = System.nanoTime();
                }
            }


            if (keysPlayer2[KeyEvent.VK_L]) {
                player2.setRotation(player2.getRotation() + player1.dl());                        // Set player 2 rotation to left
            } else if (keysPlayer2[KeyEvent.VK_J]) {
                player2.setRotation(player2.getRotation() - player1.dr());                        // Set player 2 rotation to right
            }
            if (keysPlayer2[KeyEvent.VK_I]) {                                                     // Move player 2 forward
                player2.setPervY(player2.getY());
                player2.setPervX(player2.getX());
                player2.setX(player2.getX() + player2.dx());
                player2.setY(player2.getY() - player2.dy());
            } else if (keysPlayer2[KeyEvent.VK_K]) {                                              // Move player 2 backward
                player2.setPervY(player2.getY());
                player2.setPervX(player2.getX());
                player2.setX(player2.getX() - player2.dx());
                player2.setY(player2.getY() + player2.dy());
            }
            if (keysPlayer2[KeyEvent.VK_PERIOD]){                                                // Shoot a bullet from player 2
                if(System.nanoTime()>= lastShoot2+FIRE_RATE) {
                    bullets2.add(new Bullet(player2.getX() + (player2.getWidth() / 2) - 5, player2.getY() + (player2.getHeight() / 2) - 5, 10, 10, player2.dx(), player2.dy(), Color.BLUE));
                    lastShoot2 = System.nanoTime();
                }
            }
        }

        /**
         * Does the graphical operations
         * Draw the map
         * Makes 2 tanks
         * Update players logic
         * Render players on the screen
         * Update bullets
         * Render bullets
         * Display "Game over" and the player who won on screen
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.drawRect(DIS_FROM_WIN / 2, DIS_FROM_WIN / 2, getWidth() - 10, getHeight() - 10);
            map.draw(g);                                       // Draw the map
            player1.update();                                  // Update player 1 logic
            player1.render(g);                                 // Render player 1 on the screen
            if(player1.tanksHitBox.intersects(player2.tanksHitBox)){
                player1.setX(player1.getPervX());
                player1.setY(player1.getPervY());
            }
            player2.update();                                  // Update player 2 logic
            player2.render(g);                                 // Render player 2 on the screen

            /*
                Calls logical and graphical methods of each bullets of player 2
             */
            Iterator<Bullet> iterator1 = bullets1.iterator();
            while (iterator1.hasNext()) {
                Bullet bullet1 = iterator1.next();
                if (bullet1 != null) {
                    bullet1.update();
                    bullet1.render(g);
                    if (bullet1.isHittingOpponent(player2)) {
                        iterator1.remove(); // Use the iterators remove method
                        player2.setRotation(0);
                        player2.setImage("Explosion.png");
                        winner = "Red";
                        gameOver = true;
                    }
                    if (bullet1.getBounces() > MAX_BOUNCE || bullet1.getX() < 0 || bullet1.getX() > FIELD_WIDTH || bullet1.getY() < 0 || bullet1.getY() > FIELD_HEIGHT) {
                        iterator1.remove(); // Use the iterators remove method
                    }
                }
            }

            /*
                Calls logical and graphical methods of each bullets of player 2
             */
            Iterator<Bullet> iterator2 = bullets2.iterator();
            while (iterator2.hasNext()) {
                Bullet bullet2 = iterator2.next();
                if (bullet2 != null) {
                    bullet2.update();
                    bullet2.render(g);
                    if (bullet2.isHittingOpponent(player1)) {
                        iterator2.remove(); // Use the iterators remove method
                        player1.setRotation(0);
                        player1.setImage("Explosion.png");
                        winner = "Blue";
                        gameOver = true;
                    }
                    if (bullet2.getBounces() > MAX_BOUNCE || bullet2.getX() < 0 || bullet2.getX() > FIELD_WIDTH || bullet2.getY() < 0 || bullet2.getY() > FIELD_HEIGHT) {
                        iterator2.remove(); // Use the iterators remove method
                    }
                }
            }

            /*
                Does game over screen function
             */
            if (gameOver){
                int MAX_WINDOW_WIDTH = FIELD_WIDTH + DIS_FROM_WIN;
                int MAX_WINDOW_HEIGHT = FIELD_HEIGHT + DIS_FROM_WIN;
                g.setColor(new Color(1f,1f,1f,.35f ));
                g.fillRect(0,0, MAX_WINDOW_WIDTH, MAX_WINDOW_HEIGHT);
                g.setColor(Color.RED);
                if (winner == "Blue"){
                    g.setColor(Color.BLUE);
                }
                g.setFont(new Font("Arial", Font.BOLD, 48));
                FontMetrics fm = g.getFontMetrics();
                String gameOverMsg = "Game Over\n"+winner+" won!";
                int msgHeight = fm.getHeight();
                int y = (MAX_WINDOW_HEIGHT - (3*msgHeight)) / 2;
                for (String line : gameOverMsg.split("\n")) {
                    int msgWidth = fm.stringWidth(line);
                    int x = (MAX_WINDOW_WIDTH - msgWidth) / 2;
                    g.drawString(line, x, y += g.getFontMetrics().getHeight());
                }
                running = false;
            }
            repaint();                                         // Trigger repaint to update the panel
        }

        /**
         * Set the corresponding key as pressed for players
         */
        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            if (keyCode >= 0 && keyCode < 256) {
                keysPlayer1[keyCode] = true;                    // Set the corresponding key as pressed for player 1
                keysPlayer2[keyCode] = true;                    // Set the corresponding key as pressed for player 2
            }
        }

        /**
         * Set the corresponding key as released for players
         */
        @Override
        public void keyReleased(KeyEvent e) {
            int keyCode = e.getKeyCode();
            if (keyCode >= 0 && keyCode < 256) {
                keysPlayer1[keyCode] = false;                   // Set the corresponding key as released for player 1
                keysPlayer2[keyCode] = false;                   // Set the corresponding key as released for player 2
            }
        }


        @Override
        public void keyTyped(KeyEvent e) {
            // Not used in this game
        }
    }

    /**
     * Load the map
     * Create a new instance of the game window
     * Start the game
     */
    public static void main(String[] args) {
        Maps.ReadMap.loadMap();
        TankGame game = new TankGame();
        game.startGame();
    }

    /**
     * Refresh class
     * set FPS to 60
     * Operate and refresh 60 times every second
     */
    public void startGame() {
        running = true;


        while (running) {
            long startTime = System.nanoTime();


            GamePanel gamePanel = (GamePanel) getContentPane().getComponent(0);
            gamePanel.update();                                  // Update the game logic
            gamePanel.repaint();                                // Repaint the game panel


            long endTime = System.nanoTime();
            long deltaTime = (endTime - startTime) / 1_000_000;


            if (deltaTime < 16) {
                try {
                    Thread.sleep(16 - deltaTime);                 // Maintain a constant frame rate of approximately 60 FPS
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

/**
 * The tank sprites
 * Makes 2 tank object
 * Does tank functions and logic check
 */
class Sprite {
    private int x;
    private int y;
    private int width;
    private int height;
    private Image image;
    private double rotation; // Rotation angle in degrees
    private int FIELD_WIDTH;
    private int FIELD_HEIGHT;
    private int speed = 5; // Variable to store the time of the last update
    private int rotationSpeed = 8;
    private double SCALE = 0.6;
    private int pervY;
    private int pervX;
    public  Rectangle tanksHitBox =  new Rectangle(x+1, y+1, width-3, height-3);

    public Sprite(int x, int y, int width, int height, String imagePath, int fieldWidth, int fieldHeight) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = loadImage(imagePath);
        this.rotation = 0.0;
        this.FIELD_WIDTH = fieldWidth;
        this.FIELD_HEIGHT = fieldHeight;
    }

    /**
     * load tank images
     * @param imagePath
     * @return tank image
     */
    private Image loadImage(String imagePath) {
        ImageIcon icon = new ImageIcon(imagePath);
        return icon.getImage();
    }

    /**
     * Update the tanks
     * Mainly checks the logic of tanks
     */
    public void update() {
        int x = this.getX();
        int y = this.getY();
        int width = this.getWidth();
        int height = this.getHeight();

        if (x < 0) {
            this.setX(0);
            this.setY(pervY);
        } else if (x + width > FIELD_WIDTH) {
            this.setX(FIELD_WIDTH - width);
            this.setY(pervY);
        }

        if (y < 0) {
            this.setY(0);
            this.setX(this.pervX);
        } else if (y + height > FIELD_HEIGHT) {
            this.setY(FIELD_HEIGHT - height);
            this.setX(this.pervX);
        }
        if(this.isObstacle(this.tanksHitBox)) {                              // prevent tanks from passing the obstacles
            this.setX(this.pervX);
            this.setY(this.pervY);
        }
    }


    public void render(Graphics g) {
        // Rotate the graphics context based on the sprite's rotation
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.rotate(Math.toRadians(rotation), x + width / 2, y + height / 2);
        this.tanksHitBox = new Rectangle(x+1, y+1, width-3, height-3);
        g2d.drawImage(image, x, y, width, height, null);
    }


    public int getX() {
        return x;
    }


    public void setX(int x) {
        this.x = x;
    }


    public int getY() {
        return y;
    }


    public void setY(int y) {
        this.y = y;
    }


    public double getRotation() {
        return rotation;
    }


    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    /**
     * @return how many pixels should tanks move on x depending on rotation of the tanks
     */
    public int dx() {
        return (int) Math.round(Math.sin(Math.toRadians(rotation + 180)) * speed * SCALE);

    }

    /**
     * @return how many pixels should tanks move on y depending on rotation of the tanks
     */
    public int dy() {
        return (int) Math.round(Math.cos(Math.toRadians(rotation + 180)) * speed * SCALE);

    }

    /**
     * @return speed of rotation to right
     */
    public int dr() {
        return (int) (rotationSpeed * SCALE);
    }

    /**
     * @return speed of rotation to left
     */
    public int dl() {
        return (int) (rotationSpeed * SCALE);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setPervY(int pervY){
        this.pervY = pervY;
    }

    public void setPervX(int pervX){
        this.pervX = pervX;
    }

    public int getPervX(){
        return this.pervX;
    }

    public int getPervY(){
        return this.pervY;
    }

    public Rectangle getTankHitBox(){
        return tanksHitBox;
    }

    public void setImage(String image){
        this.image = loadImage(image);
    }

    /**
     * @param object
     * @return yes if object have collision with map obstacle, no if not
     */
    public static boolean isObstacle(Rectangle object){
        for (int row = 0; row < Map.Height; row++) {
            for (int col = 0; col < Map.Width; col++) {
                int cellX = col * Map.cellSize - 1;
                int cellY = row * Map.cellSize - 1;

                if (Map.data[row][col] == 1) {
                    Rectangle obstacle = new Rectangle(cellX, cellY, Map.cellSize+1, Map.cellSize+1);
                    if (object != null && object.intersects(obstacle)){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

/**
 * Map class
 * loads the map and does the graphics
 */
class Map {
    public static int[][] data;   // 2D array to store map data
    public static int Width;      // Width of the map
    public static int Height;     // Height of the map
    public static int cellSize;      // Size of each cell in pixels



    public Map(int[][] mapData, int cellSize) {
        this.data = mapData;
        this.Width = mapData[0].length;
        this.Height = mapData.length;
        this.cellSize = cellSize;
    }

    /**
     * Reads the map and makes an obstacle when the corresponding number in mab data is 0 and ground for 1
     * @param g
     */
    public void draw(Graphics g) {
        ImageIcon wall = new ImageIcon("wall.jpeg");
        ImageIcon ground = new ImageIcon("Ground.jpeg");
        for (int row = 0; row < Height; row++) {
            for (int col = 0; col < Width; col++) {
                int cellX = col * cellSize;
                int cellY = row * cellSize;

                if (data[row][col] == 1) {

                    g.drawImage(wall.getImage(), cellX, cellY, cellSize, cellSize, null);
                    //g.setColor(Color.GRAY);
                    //g.fillRect(cellX, cellY, cellSize, cellSize);

                } else {
                    g.drawImage(ground.getImage(), cellX, cellY, cellSize, cellSize, null);
                    //g.setColor(Color.WHITE);
                    //g.fillRect(cellX, cellY, cellSize, cellSize);
                }
            }
        }
    }
}

/**
 * Bullet class
 * Makes bullet for both tanks
 * Does bullet functions and logic check
 */
class Bullet{

    private int x;
    private int y;
    private int width;
    private int height;
    private Rectangle bullet;
    private int dx;
    private int dy;
    private Color color;
    private double lastMove;
    private final int BULLET_SPEED = 10_000_000;
    private int bounces = 0;
    public static Rectangle obFinder1, obFinder2;


    public Bullet(int x, int y, int width, int height, int dx, int dy, Color color){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.dx = dx;
        this.dy = dy;
        this.color = color;
    }

    /**
     * Does the graphical stuff for bullets
     * @param g
     */
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        this.bullet = new Rectangle(this.x, this.y, this.width, this.height);
        g2d.setColor(color);
        g2d.fillRect(this.x, this.y, this.width, this.height);
    }

    /**
     * Functions the movement of bullets(i.e. moving and bouncing)
     */
    public void update() {
        if(TankGame.running && System.nanoTime()>= lastMove + BULLET_SPEED) {
            if(Sprite.isObstacle(bullet)) {
                this.bounces++;
                if (intersectSide() == 1) {
                    this.dy = -this.dy;
                } else if (intersectSide() == 2) {
                    this.dx = -this.dx;
                } else if (intersectSide() == 3){
                    this.dx = -this.dx;
                    this.dy = -this.dy;
                }
            }
            this.setX(this.getX() + this.dx);
            this.setY(this.getY() - this.dy);
            lastMove = System.nanoTime();
        }
    }
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    public int getBounces(){
        return bounces;
    }

    /**
     * finds what side each bullet hit the obstacle to calculate the bounce
     * @return 1 for top and bottom, 2 for right and left, 3 for corners
     */
    public int intersectSide(){
        for(int i=1; i < this.getHeight() - 1; i++){
            obFinder1 = new Rectangle(this.getX(),this.getY()+i, 1,1);
            obFinder2 = new Rectangle(this.getX() + this.getWidth(), this.getY() + i, 1, 1);
            if(Sprite.isObstacle(this.obFinder1) || Sprite.isObstacle(this.obFinder2)) {
                return 2;
            } else {
                obFinder1 = new Rectangle(this.getX() + i,this.getY(), 1,1);
                obFinder2 = new Rectangle(this.getX()+ i, this.getY() + this.getWidth(), 1, 1);
                if(Sprite.isObstacle(this.obFinder1) || Sprite.isObstacle(this.obFinder2)) {
                    return 1;
                }
            }
        }
        return 3;
    }
    /**
     * Checks if each bullet hit the other tank
     * @return true if yes, no if not
     */
    public boolean isHittingOpponent(Sprite tank){
        if (bullet.intersects(tank.getTankHitBox())){
            return true;
        } else {
            return false;
        }
    }

}
