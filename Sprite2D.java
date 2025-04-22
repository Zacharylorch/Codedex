import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

/**
 * Subclass Sprite2D.
 *
 * @author Zachary Lorch
 * @author Jimmy Chen
 * @author Steven Weller
 * @version 12.12.2024
 */
public class Sprite2D extends Shape2D
{
    private BufferedImage[] imageFrames;
    private int frame;
    private int width;
    private int height;
    private char moveDirection;
    
    /**
     * Constructor for objects of class Sprite2D
     * 
     * @param xPosition
     *      The x-position of the sprite.
     * @param yPosition
     *      The y-position of the sprite.
     * @param imageFrames
     *      The image frames for the sprite.
     */
    public Sprite2D(int xPosition, int yPosition, BufferedImage[] imageFrames)
    {
        super(0, xPosition, yPosition);
        
        this.imageFrames = new BufferedImage[imageFrames.length];
        
        for (int i = 0; i < imageFrames.length; i++)
        {
            this.imageFrames[i] = imageFrames[i];
        }
        
        this.frame = 0;
        
        this.width = 32;
        this.height = 32;
    }
    
    /**
     * Constructor for objects of class Sprite2D
     * 
     * @param xPosition
     *      The x-position of the sprite.
     * @param yPosition
     *      The y-position of the sprite.
     * @param imageFrames
     *      The image frames for the sprite.
     * @param width
     *      The width of the sprite in pixels.
     * @param height
     *      The height of the sprite in pixels.
     */
    public Sprite2D(int xPosition, int yPosition, BufferedImage[] imageFrames, int width, int height)
    {
        super(0, xPosition, yPosition);
        
        this.imageFrames = new BufferedImage[imageFrames.length];
        
        for (int i = 0; i < imageFrames.length; i++)
        {
            this.imageFrames[i] = imageFrames[i];
        }
        
        this.frame = 0;
        
        this.width = width;
        this.height = height;
    }
    
    public char getMoveDirection()
    {
        return moveDirection;
    }
    
    public void setMoveDirection(char moveDirection)
    {
        this.moveDirection = moveDirection;
    }
    
    /**
     * Gets the height of the sprite.
     * 
     * @return
     *      The height of the sprite.
     */
    public int getHeight()
    {
        return height;
    }
    
    /**
     * Gets the width of the sprite.
     * 
     * @return
     *      The width of the sprite.
     */
    public int getWidth()
    {
        return width;
    }
    
    /**
     * Sets the image frames for the sprite.
     * 
     * @param imageFrames
     *      The image frames for the sprite.
     */
    public void setImageFrames(BufferedImage[] imageFrames)
    {
        this.imageFrames = imageFrames;
    }
    
    /**
     * Sets the size of the sprite.
     * 
     * @param width
     *      The width of the sprite in pixels.
     * @param height
     *      The height of the sprite in pixels.
     */
    public void setSize(int width, int height)
    {
        this.width = width;
        this.height = height;
    }
    
    @Override
    public void Draw(Graphics g)
    {
        // Draw the next Sprite frame.
        g.drawImage(imageFrames[frame], getXPosition(), getYPosition(), width, height, null);
        frame++;
        
        if (frame == imageFrames.length)
        {
            frame = 0;
        }
    }
}
