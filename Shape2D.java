import java.awt.Graphics;
import java.awt.Color;

/**
 * Superclass Shape2D
 *
 * @author Zachary Lorch
 * @author Jimmy Chen
 * @author Steven Weller
 * @version 12.12.2024
 */
public abstract class Shape2D
{
    public final static int WHITE = 5;
    
    // RGB color table.
    public static final Color[] COLORS = {
            //         R     G    B
            new Color(255,   0,   0),  // Red     0
            new Color(  0, 255,   0),  // Green   1
            new Color(  0,   0, 255),  // Blue    2
            new Color(  0,   0,   0),  // Black   3
            new Color(128, 128, 128),  // Grey    4
            new Color(255, 255, 255),  // White   5
            new Color(255, 255,   0),  // Yellow  6
            new Color(  0, 255, 255),  // Cyan    7
            new Color(255,   0, 255),  // Magenta 8 
            new Color(165,  42,  42),  // Brown   9
            new Color(255,  38,  38),
            new Color(255, 168,  38),
            new Color(212, 255,  38),
            new Color( 82, 255,  38),
            new Color( 38, 255, 125),
            new Color( 38, 255, 255),
            new Color( 38, 125, 255),
            new Color( 82,  38, 255),
            new Color(212,  38, 255),
            new Color(255,  38, 168),
        };
    
    private Color fillColor;
    private int fillColorIndex;

    // The x and y positions of the shape.
    private int xPosition;
    private int yPosition;
    
    // The x and y speeds of the shape.
    private int xSpeed;
    private int ySpeed;
    
    /**
     * Constructor for objects of class Shape2D.
     * 
     * @param fillColorIndex
     *      The index of the color of the shape in the COLORS array.
     * @param xPosition
     *      The x-position of the shape.
     * @param yPosition
     *      The y-position of the shape.
     */
    public Shape2D(int fillColorIndex, int xPosition, int yPosition)
    {
        this.fillColorIndex = fillColorIndex;
        this.fillColor      = COLORS[this.fillColorIndex];
        
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }
    
    /**
     * Gets the current x-position of the shape.
     * 
     * @return
     *      The current x-position of the shape.
     */
    public int getXPosition()
    {
        return xPosition;
    }
    
    /**
     * Gets the current y-position of the shape.
     * 
     * @return
     *      The current y-position of the shape.
     */
    public int getYPosition()
    {
        return yPosition;
    }
    
    /**
     * Gets the current speed for the x-axis.
     * 
     * @return
     *      The current speed for the x-axis.
     */
    public int getXSpeed()
    {
        return xSpeed;
    }
    
    /**
     * Gets the current speed for the y-axis.
     * 
     * @return
     *      The current speed for the y-axis.
     */
    public int getYSpeed()
    {
        return ySpeed;
    }
    
    /**
     * Gets the current fill color of the shape.
     * 
     * @return
     *      The current fill color of the shape.
     */
    public Color getFillColor()
    {
        return fillColor;
    }
    
    /**
     * Sets the fill color for the shape.
     * 
     * @param colorIndex
     *      The index of the color in the COLORS array.
     */
    public void setFillColor(int colorIndex)
    {
        this.fillColor = COLORS[colorIndex];
    }
    
    /**
     * Sets the coordinates of the shape.
     * 
     * @param xPosition
     *      The new x position for the shape.
     * @param yPosition
     *      The new y position for the shape.
     */
    public void setPosition(int xPosition, int yPosition)
    {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }
    
    /**
     * Sets the x and y speeds for the shape.
     * 
     * @param xSpeed
     *      The speed of the shape on the x-axis.
     * @param ySpeed
     *      The speed of the shape on the y-axis.
     */
    public void setSpeed(int xSpeed, int ySpeed)
    {
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
    }
    
    /**
     * Moves the shape by an amount (x distance, y distance).
     * 
     * @param xDelta
     *      The amount to translate along the x-axis.
     * @param yDelta
     *      The amount to translate along the y-axis.
     */
    public void Move(int xDelta, int yDelta)
    {
        this.xPosition += xDelta;
        this.yPosition += yDelta;
    }
    
    /**
     * Render the shape for both filled and outlined according to the states.
     * 
     * @param g
     *      The graphics context.
     */
    public abstract void Draw(Graphics g);
}
