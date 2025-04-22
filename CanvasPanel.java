import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.io.*;
import java.util.Random;
import javax.swing.*;

/**
 * 2D CanvasPanel
 *
 * @author Zachary Lorch
 * @author Jimmy Chen
 * @author Steven Weller
 * @version 12.12.2024
 */
public class CanvasPanel extends JPanel
{
    private static final int START_SCREEN = 0;
    private static final int IN_GAME = 1;
    
    private static final int ROW_COUNT = 21;
    private static final int COLUMN_COUNT = 19;
    private static final int TILE_SIZE = 32;
    private static final int BOARD_WIDTH = COLUMN_COUNT * TILE_SIZE;
    private static final int BOARD_HEIGHT = ROW_COUNT * TILE_SIZE;
    
    // Current game state 
    private static int gameState = START_SCREEN; 
    
    // For animation, increases by 1 in each frame.
    private static int frameNumber;
    private static Timer renderLoop;
    
    private static BufferedImage[] blueWallImage;
    private static BufferedImage[] whiteWallImage;
    private static BufferedImage[] redWallImage;
    
    private static BufferedImage[] pacmanUpImages = new BufferedImage[3];
    private static BufferedImage[] pacmanDownImages = new BufferedImage[3];
    private static BufferedImage[] pacmanLeftImages = new BufferedImage[3];
    private static BufferedImage[] pacmanRightImages = new BufferedImage[3];
    
    private static ArrayList<Shape2D> wallList;
    private static ArrayList<Shape2D> pelletList;
    private static ArrayList<Shape2D> ghostList;
    
    private static Sprite2D blueGhostSprite;
    private static Sprite2D orangeGhostSprite;
    private static Sprite2D pinkGhostSprite;
    private static Sprite2D redGhostSprite;
    private static Sprite2D pacmanSprite;
    
    private static char moveDirection;
    private static boolean startAudioPlayed;
    private static boolean sirenLoopStarted;
    private static boolean gameOver;
    
    private static AudioPlayer audioPlayer;
    private static AudioPlayer sirenPlayer;
    
    // X = wall, O = skip, P = pac man, ' ' = food, s = superfood
    // Ghosts: b = blue, o = orange, p = pink, r = red
    private static String[] tileMap = 
    {
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X                 X",
        "X XX X XXXXX X XX X",
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX",
        "OOOX X  bpo  X XOOO",
        "XXXX X XXrXX X XXXX",
        "O                 O",
        "XXXX X XXXXX X XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXXXX X XXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X  X     P     X  X",
        "XX X X XXXXX X X XX",
        "X    X   X   X    X",
        "X XXXXXX X XXXXXX X",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX" 
    };
      
    /**
     * Initializes a new CanvasPanel object.
     */
    public CanvasPanel()
    {
        this.ghostList = new ArrayList<Shape2D>();
        this.wallList = new ArrayList<Shape2D>();
        this.pelletList = new ArrayList<Shape2D>();
        
        loadSprites();
        ghostList.add(blueGhostSprite);
        ghostList.add(orangeGhostSprite);
        ghostList.add(pinkGhostSprite);
        ghostList.add(redGhostSprite);
        
        loadMap();
        
        this.moveDirection = 'L';
        this.startAudioPlayed = false;
        this.sirenLoopStarted = false;
        this.gameOver = false;
        
        this.audioPlayer = new AudioPlayer();
        this.sirenPlayer = new AudioPlayer();
        
        // Callback for keyboard events
        this.setFocusable(true);
        this.addKeyListener(new myActionListener());
        
        // Create a render loop
        // Create a Swing Timer that will tick 30 times a second
        // At each tick the ActionListener that was registered via the lambda expression will be invoked
        renderLoop = new Timer(44, (ActionEvent ev) -> {frameNumber++; Simulate(); repaint();}); // lambda expression for ActionListener implements actionPerformed
        renderLoop.start();
    }
    
    /**
     * Simulates the game.
     */
    private void Simulate()
    {   
        if (gameState == IN_GAME)
        {
            if (!startAudioPlayed)
            {
                playAudioWithDelay(AudioPlayer.THEME, 5);
                startAudioPlayed = true;
            }
            else if (startAudioPlayed && !sirenLoopStarted)
            {
                loopSirenAudio();
                sirenLoopStarted = true;
            }

            updateGhostMovement();
        
            // Switch directions.
            switch (moveDirection)
            {
                case 'U':
                    if (pacmanSprite.getXPosition() % TILE_SIZE == 0 && pacmanSprite.getYPosition() % TILE_SIZE == 0)
                    {
                        pacmanSprite.setSpeed(0, -8);
                    }
                    pacmanSprite.setImageFrames(pacmanUpImages);
                    break;
                case 'D':
                    if (pacmanSprite.getXPosition() % TILE_SIZE == 0 && pacmanSprite.getYPosition() % TILE_SIZE == 0)
                    {
                        pacmanSprite.setSpeed(0, 8);
                    }
                    pacmanSprite.setImageFrames(pacmanDownImages);
                    break;
                case 'L':
                    if (pacmanSprite.getXPosition() % TILE_SIZE == 0 && pacmanSprite.getYPosition() % TILE_SIZE == 0)
                    {
                        pacmanSprite.setSpeed(-8, 0);
                    }
                    pacmanSprite.setImageFrames(pacmanLeftImages);
                    break;
                case 'R':
                    if (pacmanSprite.getXPosition() % TILE_SIZE == 0 && pacmanSprite.getYPosition() % TILE_SIZE == 0)
                    {
                        pacmanSprite.setSpeed(8, 0);
                    }
                    pacmanSprite.setImageFrames(pacmanRightImages);
                    break;
            }
            pacmanSprite.Move(pacmanSprite.getXSpeed(), pacmanSprite.getYSpeed());
            
            checkGameConditions();
        }
    }

    // This method is called by renderloop
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        if (gameState == START_SCREEN)
        {
            drawStartScreen(g);
        }
        else if (gameState == IN_GAME)
        {
            drawGameScreen(g);
        }
    }
    
    /**
     * Draws the start screen for the game.
     * 
     * @param g
     *      The graphics context.
     */
    private void drawStartScreen(Graphics g)
    {
        try
        {
            g.drawImage(ImageIO.read(new File("./sprites/Pacman_Start_Screen.png")), 0, 0, Color.BLACK, null);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 25));
        g.drawString("Press ENTER To Start", 180, 475);
    }
    
    /**
     * Draws the game screen.
     * 
     * @param g
     *      The graphics context.
     */
    private void drawGameScreen(Graphics g)
    {
        for (Shape2D shape : wallList)
        {
            shape.Draw(g);
        }
        
        for (Shape2D pellet : pelletList)
        {
            pellet.Draw(g);
        }
        
        for (Shape2D ghost : ghostList)
        {
            ghost.Draw(g);
        }
        
        pacmanSprite.Draw(g);
    }
    
    /**
     * Adds walls outside of the game screen to restrict off-screen sprite movement
     */
    private void addOutOfBoundWalls()
    {
        // Left out of bounds.
        wallList.add(new Sprite2D(-TILE_SIZE, 256, blueWallImage));
        wallList.add(new Sprite2D(-TILE_SIZE, 320, blueWallImage));
        wallList.add(new Sprite2D((-TILE_SIZE * 2), 256, blueWallImage));
        wallList.add(new Sprite2D((-TILE_SIZE * 2), 320, blueWallImage));
        
        // Right out of bounds.
        wallList.add(new Sprite2D(BOARD_WIDTH + TILE_SIZE, 256, blueWallImage));
        wallList.add(new Sprite2D(BOARD_WIDTH + TILE_SIZE, 320, blueWallImage));
        wallList.add(new Sprite2D(BOARD_WIDTH + (TILE_SIZE * 2), 256, blueWallImage));
        wallList.add(new Sprite2D(BOARD_WIDTH + (TILE_SIZE * 2), 320, blueWallImage));
    }
    
    /**
     * Gets the board's width.
     * 
     * @return
     *      The board's width.
     */
    public int getBoardWidth()
    {
        return BOARD_WIDTH;
    }
    
    /**
     * Gets the board's height.
     * 
     * @return
     *      The board's height.
     */
    public int getBoardHeight()
    {
        return BOARD_HEIGHT;
    }
    
    /**
     * Gets the tile size.
     * 
     * @return
     *      The tile size.
     */
    public int getTileSize()
    {
        return TILE_SIZE;
    }
    
    /**
     * Detects a collision between two sprites.
     * 
     * @param s1
     *      The first Shape2D object.
     * @param s2
     *      The second Shape2D object.
     * 
     * @return
     *      True if there is a collision between the shapes. Otherwise, false.
     */
    private boolean collision(Shape2D s1, Shape2D s2)
    {
        return s1.getXPosition() < s2.getXPosition() + TILE_SIZE &&
               s1.getXPosition() + TILE_SIZE  > s2.getXPosition() &&
               s1.getYPosition() < s2.getYPosition() + TILE_SIZE &&
               s1.getYPosition() + TILE_SIZE  > s2.getYPosition();
    }
    
    /**
     * Detects a collision between pacman and a pellet.
     * 
     * @param s1
     *      The pacman object.
     * @param s2
     *      The pellet object.
     * 
     * @return
     *      True if there is a collision between pacman and the pellet. Otherwise, false.
     */
    private boolean pelletCollision(Sprite2D s1, Circle2D s2)
    {
        return s1.getXPosition() < s2.getXPosition() + s2.getDiameter() &&
               s1.getXPosition() + s1.getWidth()  > s2.getXPosition() &&
               s1.getYPosition() < s2.getYPosition() + s2.getDiameter() &&
               s1.getYPosition() + s1.getHeight()  > s2.getYPosition();
    }
    
    /**
     * Loads the sprites for the game.
     */
    private void loadSprites()
    {
        blueWallImage = new BufferedImage[1];
        whiteWallImage = new BufferedImage[1];
        redWallImage = new BufferedImage[1];
        
        BufferedImage[] blueGhostImage = new BufferedImage[1];
        BufferedImage[] orangeGhostImage = new BufferedImage[1];
        BufferedImage[] pinkGhostImage = new BufferedImage[1];
        BufferedImage[] redGhostImage = new BufferedImage[1];
        
        try
        {
            blueWallImage[0] = ImageIO.read(new File("./sprites/blueWall.png"));
            whiteWallImage[0] = ImageIO.read(new File("./sprites/whiteWall.png"));
            redWallImage[0] = ImageIO.read(new File("./sprites/redWall.png"));
            
            blueGhostImage[0] = ImageIO.read(new File("./sprites/blueGhost.png"));
            orangeGhostImage[0] = ImageIO.read(new File("./sprites/orangeGhost.png"));
            pinkGhostImage[0] = ImageIO.read(new File("./sprites/pinkGhost.png"));
            redGhostImage[0] = ImageIO.read(new File("./sprites/redGhost.png"));
            
            pacmanUpImages[0] = ImageIO.read(new File("./sprites/pacmanUp1.png"));
            pacmanUpImages[1] = ImageIO.read(new File("./sprites/pacmanUp2.png"));
            pacmanUpImages[2] = ImageIO.read(new File("./sprites/pacmanUp3.png"));
            
            pacmanDownImages[0] = ImageIO.read(new File("./sprites/pacmanDown1.png"));
            pacmanDownImages[1] = ImageIO.read(new File("./sprites/pacmanDown2.png"));
            pacmanDownImages[2] = ImageIO.read(new File("./sprites/pacmanDown3.png"));
            
            pacmanLeftImages[0] = ImageIO.read(new File("./sprites/pacmanLeft1.png"));
            pacmanLeftImages[1] = ImageIO.read(new File("./sprites/pacmanLeft2.png"));
            pacmanLeftImages[2] = ImageIO.read(new File("./sprites/pacmanLeft3.png"));
            
            pacmanRightImages[0] = ImageIO.read(new File("./sprites/pacmanRight1.png"));
            pacmanRightImages[1] = ImageIO.read(new File("./sprites/pacmanRight2.png"));
            pacmanRightImages[2] = ImageIO.read(new File("./sprites/pacmanRight3.png"));
        }
        catch (IOException ie)
        {
            ie.printStackTrace();
        }
        
        this.blueGhostSprite = new Sprite2D(0, 0, blueGhostImage);
        this.orangeGhostSprite = new Sprite2D(0, 0, orangeGhostImage);
        this.pinkGhostSprite = new Sprite2D(0, 0, pinkGhostImage);
        this.redGhostSprite = new Sprite2D(0, 0, redGhostImage);
        
        this.pacmanSprite = new Sprite2D(0, 0, pacmanLeftImages);
    }
    
    /**
     * Loads the map for the game.
     */
    private void loadMap()
    {
        for (int r = 0; r < ROW_COUNT; r++)
        {
            for (int c = 0; c < COLUMN_COUNT; c++)
            {
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);
                
                int xPosition = c * TILE_SIZE;
                int yPosition = r * TILE_SIZE;
                
                // Walls.
                if (tileMapChar == 'X')
                {
                    wallList.add(new Sprite2D(xPosition, yPosition, blueWallImage)); 
                }
                else if (tileMapChar == 'b')
                {
                    blueGhostSprite.setPosition(xPosition, yPosition);
                }
                else if (tileMapChar == 'o')
                {
                    orangeGhostSprite.setPosition(xPosition, yPosition);
                }
                else if (tileMapChar == 'p')
                {
                    pinkGhostSprite.setPosition(xPosition, yPosition);
                }
                else if (tileMapChar == 'r')
                {
                    redGhostSprite.setPosition(xPosition, yPosition);
                }
                else if (tileMapChar == 'P')
                {
                    pacmanSprite.setPosition(xPosition, yPosition);
                }
                else if (tileMapChar == ' ')
                {
                    pelletList.add(new Circle2D(Shape2D.WHITE, xPosition + 12, yPosition + 12, 4));
                }
            }
            
            addOutOfBoundWalls();
        }
        
        repaint();
    }
    
    /**
     * Loads an updated map if the game is won.
     */
    private void loadWinMap()
    {
        wallList.removeAll(wallList);
        ghostList.removeAll(ghostList);
        
        for (int r = 0; r < ROW_COUNT; r++)
        {
            for (int c = 0; c < COLUMN_COUNT; c++)
            {
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);
                
                int xPosition = c * TILE_SIZE;
                int yPosition = r * TILE_SIZE;
                
                if (tileMapChar == 'X')
                {
                    wallList.add(new Sprite2D(xPosition, yPosition, whiteWallImage)); 
                }
            }
        }
    }
    
    /**
     * Loads an updated map if the game is lost.
     */
    private void loadLoseMap()
    {
        wallList.removeAll(wallList);
        pelletList.removeAll(pelletList);
        
        for (int r = 0; r < ROW_COUNT; r++)
        {
            for (int c = 0; c < COLUMN_COUNT; c++)
            {
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);
                
                int xPosition = c * TILE_SIZE;
                int yPosition = r * TILE_SIZE;
                
                if (tileMapChar == 'X')
                {
                    wallList.add(new Sprite2D(xPosition, yPosition, redWallImage)); 
                }
            }
        }
    }
    
    /**
     * Sets the move direction for pacman.
     * 
     * @param direction
     *      The new direction of pacman.
     */
    public static void setMoveDirection(char direction)
    {
        if (direction == 'U' || direction == 'D' || direction == 'L' || direction == 'R')
        {
            moveDirection = direction;
        }
    }
    
    /**
     * Gets a random direction for a ghost to move in.
     * 
     * @param ghost
     *      The ghost to get the random direction for.
     *      
     * @return
     *      The direction for the ghost to move in.
     */
    private char getRandomDirection(Sprite2D ghost)
    {
        char[] directions = {'U', 'D', 'L', 'R'};
        Random random = new Random();
        char newDirection;
        
        do 
        {
            newDirection = directions[random.nextInt(directions.length)];
    
            // Calculate potential new position.
            int newX = ghost.getXPosition();
            int newY = ghost.getYPosition();
    
            switch (newDirection) 
            {
                case 'U':
                    if (ghost.getMoveDirection() != 'D' && ghost.getXPosition() % TILE_SIZE == 0 && ghost.getYPosition() % TILE_SIZE == 0)
                    {
                        newY -= TILE_SIZE;
                    } 
                    break;
                case 'D': 
                    if (ghost.getMoveDirection() != 'U' && ghost.getXPosition() % TILE_SIZE == 0 && ghost.getYPosition() % TILE_SIZE == 0)
                    {
                        newY += TILE_SIZE;
                    } 
                    break;
                case 'L': 
                    if (ghost.getMoveDirection() != 'R' && ghost.getXPosition() % TILE_SIZE == 0 && ghost.getYPosition() % TILE_SIZE == 0)
                    {
                        newX -= TILE_SIZE;
                    } 
                    break;
                case 'R': 
                    if (ghost.getMoveDirection() != 'L' && ghost.getXPosition() % TILE_SIZE == 0 && ghost.getYPosition() % TILE_SIZE == 0)
                    {
                        newX += TILE_SIZE; 
                    }
                    break;
            }
    
            // Check if the new position collides with walls.
            boolean isValid = true;
            for (Shape2D wall : wallList) 
            {
                if (collision(new Rectangle2D(0, newX, newY, TILE_SIZE, TILE_SIZE), wall)) 
                {
                    isValid = false;
                    break;
                }
            }
    
            if (isValid) 
            {
                return newDirection;
            }
        } 
        while (true);
    }
    
    /**
     * Updates the movement for the ghosts.
     */
    private void updateGhostMovement()
    {
        for (Shape2D ghost : ghostList)
        {
            Sprite2D sprite = (Sprite2D) ghost;
            Random random = new Random();
            int randomFrameNumber = (random.nextInt(15) + 5) *  4;
            
            if (frameNumber % randomFrameNumber == 0)
            {
                sprite.setMoveDirection(getRandomDirection(sprite));                
            }
            
            // Move the ghost in its current direction
            switch (sprite.getMoveDirection()) 
            {
                case 'U': 
                    sprite.setSpeed(0, -4); 
                    break;
                case 'D': 
                    sprite.setSpeed(0, 4); 
                    break;
                case 'L': 
                    sprite.setSpeed(-4, 0); 
                    break;
                case 'R': 
                    sprite.setSpeed(4, 0); 
                    break;
            }
            
            sprite.Move(sprite.getXSpeed(), sprite.getYSpeed());
            
            // If collision with a wall occurs, backtrack and pick a new direction
            for (Shape2D wall : wallList) 
            {
                if (collision(sprite, wall)) 
                {
                    sprite.Move(-sprite.getXSpeed(), -sprite.getYSpeed());
                    sprite.setMoveDirection(getRandomDirection(sprite));
                    break;
                }
            }
        }
    }
    
    /**
     * Plays a specified audio.
     * 
     * @param audioIndex
     *      The index of the audio to be played in the audio player.
     */
    private void playAudio(int audioIndex) 
    {
        audioPlayer.setFile(audioIndex);
        audioPlayer.play();
    }
    
    /**
     * Plays a specified audio on a loop.
     * 
     * @param audioIndex
     *      The index of the audio to be played in the audio player.
     */
    private void loopAudio(int audioIndex)
    {
        audioPlayer.setFile(audioIndex);
        audioPlayer.play();
        audioPlayer.loop();
    }
    
    /**
     * Plays the start theme with a delay.
     * 
     * @param delayInSeconds
     *      The delay in seconds to pause the program for.
     */
    private void playAudioWithDelay(int audioIndex, int delayInSeconds)
    {
        int delay = delayInSeconds * 1000;
        
        Thread audioThread = new Thread(() ->
        {
            playAudio(audioIndex);
            
            try
            {
                Thread.sleep(delay);
            }
            catch (InterruptedException ie)
            {
                ie.printStackTrace();
            }
        });
        
        audioThread.start();
        
        try
        {
            audioThread.join();
        }
        catch (InterruptedException ie)
        {
            ie.printStackTrace();
        }
    }
    
    /**
     * Plays the looped siren audio.
     */
    private void loopSirenAudio()
    {
        sirenPlayer.setFile(AudioPlayer.SIREN);
        sirenPlayer.play();
        sirenPlayer.loop();
    }
    
    /**
     * Stops the looped siren audio.
     */
    private void stopSirenAudio()
    {
        sirenPlayer.stop();
    }
    
    /**
     * Checks the game conditions.
     */
    private void checkGameConditions()
    {
        // Check if Pacman exits window.
        if (pacmanSprite.getXPosition() < -TILE_SIZE)
        {
            pacmanSprite.setPosition((COLUMN_COUNT + 1) * TILE_SIZE, pacmanSprite.getYPosition());
        }
        else if (pacmanSprite.getXPosition() > (COLUMN_COUNT + 1) * TILE_SIZE)
        {
            pacmanSprite.setPosition(-TILE_SIZE, pacmanSprite.getYPosition());
        }
        
        //Check if ghost exit window.
        for (Shape2D ghost : ghostList)
        {
            if (ghost.getXPosition() < -TILE_SIZE)
            {
                ghost.setPosition(((COLUMN_COUNT + 1) * TILE_SIZE), ghost.getYPosition());
            }
            else if (ghost.getXPosition() > ((COLUMN_COUNT + 1) * TILE_SIZE))
            {
                ghost.setPosition(-TILE_SIZE, ghost.getYPosition());
            }
        }
        
        // Pellet collision check.
        Iterator<Shape2D> pelletIterator = pelletList.iterator();
        while (pelletIterator.hasNext())
        {
            Circle2D pellet = (Circle2D) pelletIterator.next();
            
            if (pelletCollision(pacmanSprite, pellet))
            {
                pelletIterator.remove();
                playAudio(AudioPlayer.WAKA);
            }
        }
        
        // Super pellet collision check.
        Iterator<Shape2D> superPelletIterator = pelletList.iterator();
        while (superPelletIterator.hasNext())
        {
            Circle2D pellet = (Circle2D) superPelletIterator.next();
            
            if (pelletCollision(pacmanSprite, pellet))
            {
                superPelletIterator.remove();
                playAudio(AudioPlayer.WAKA);
            }
        }
        
        // Wall collision check.
        for (Shape2D wall : wallList)
        {
            if (collision(pacmanSprite, wall))
            {
                pacmanSprite.Move(-pacmanSprite.getXSpeed(), -pacmanSprite.getYSpeed());
                break;
            }
        }
        
        // Game win condition.
        if (pelletList.isEmpty())
        {
            win();
        }
        
        // Game loss condition.
        for (Shape2D ghost : ghostList)
        {
            if (collision(pacmanSprite, ghost))
            {
                loss();
            }
        }
    }
    
    /**
     * Initiates loss sequence.
     */
    private void loss()
    {
        renderLoop.stop();
        loadLoseMap();
        stopSirenAudio();
        gameOver = true;
        playAudio(AudioPlayer.DEATH);
    }
    
    /**
     * Initiates win sequence.
     */
    private void win()
    {
        renderLoop.stop();
        loadWinMap();
        stopSirenAudio();
        gameOver = true;
        playAudio(AudioPlayer.WIN);
    }
    
    /**
     * Restarts the game. 
     */
    private void restart()
    {
        gameState = START_SCREEN; // Go back to the start screen
        frameNumber = 0;
        startAudioPlayed = false;
        sirenLoopStarted = false;
        gameOver = false;
    
        // Reset game objects
        ghostList.clear();
        wallList.clear();
        pelletList.clear();
    
        // Reload sprites and map
        loadSprites();
        ghostList.add(blueGhostSprite);
        ghostList.add(orangeGhostSprite);
        ghostList.add(pinkGhostSprite);
        ghostList.add(redGhostSprite);
        loadMap();
    
        // Stop any ongoing audio
        stopSirenAudio();
        renderLoop.start(); // Restart render loop if stopped
    }
    
    /**
     * Nested Class myActionListener
     */
    public class myActionListener extends KeyAdapter 
    {
        @Override
        public void keyPressed(KeyEvent e)
        {
            if (e.getKeyCode() == KeyEvent.VK_ENTER)
            {
                gameState = IN_GAME;
                renderLoop.start();
            }
            else if (gameState == IN_GAME)
            {
                switch (e.getKeyCode())
                {
                    case KeyEvent.VK_UP:
                        CanvasPanel.setMoveDirection('U');
                        break;
                    case KeyEvent.VK_DOWN:
                        CanvasPanel.setMoveDirection('D');
                        break;
                    case KeyEvent.VK_LEFT:
                        CanvasPanel.setMoveDirection('L');
                        break;
                    case KeyEvent.VK_RIGHT:
                        CanvasPanel.setMoveDirection('R');
                        break;
                    case KeyEvent.VK_ENTER:
                        win();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        if (gameOver == true)
                        {
                            restart();
                        }
                        break;
                    default:
                        break;
                }
            }
            repaint();
        }
    }
}
