import java.awt.Graphics;

/**
 * Subclass Rectangle2D
 *
 * @author Zachary Lorch
 * @author Jimmy Chen
 * @author Steven Weller
 * @version 12.12.2024
 */
public class Rectangle2D extends Shape2D
{
    // The width of the rectangle.
    private int width;
    
    // The height of the rectangle.
    private int height;
    
    /**
     * Constructor for objects of class Rectangle2D.
     * 
     * @param fillColorIndex
     *      The index of the color of the rectangle in the COLORS array.
     * @param xPosition
     *      The x-position of the rectangle.
     * @param yPosition
     *      The y-position of the rectangle.
     * @param width
     *      The width of the rectangle.
     * @param height
     *      The height of the rectangle.
     */
    public Rectangle2D(int fillColorIndex, int xPosition, int yPosition, int width, int height)
    {
        super(fillColorIndex, xPosition, yPosition);
        
        this.width = width;
        this.height = height;
    }
    
    /**
     * Gets the width of the rectangle.
     * 
     * @return
     *      The width of the rectangle.
     */
    public int getWidth()
    {
        return width;
    }
    
    /**
     * Gets the height of the rectangle.
     * 
     * @return
     *      The height of the rectangle.
     */
    public int getHeight()
    {
        return height;
    }
    
    @Override
    public void Draw(Graphics g)
    {
        g.setColor(getFillColor());
        g.fillRect(getXPosition(), getYPosition(), width, height);
    }
}
