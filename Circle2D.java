import java.awt.Graphics;

/**
 * Subclass Circle2D
 *
 * @author Zachary Lorch
 * @author Jimmy Chen
 * @author Steven Weller
 * @version 12.12.2024
 */
public class Circle2D extends Shape2D
{
    // The diameter of the circle.
    private int diameter;
    
    /**
     * Constructor for objects of class Circle2D.
     * 
     * @param fillColorIndex
     *      The index of the color of the circle in the COLORS array.
     * @param xPosition
     *      The x-position of the circle.
     * @param yPosition
     *      The y-position of the circle.
     * @param diameter
     *      The diameter of the circle.
     */
    public Circle2D(int fillColorIndex, int xPosition, int yPosition, int diameter)
    {
        super(fillColorIndex, xPosition, yPosition);
        
        this.diameter = diameter;
    }
    
    /**
     * Gets the diameter of the circle.
     * 
     * @return
     *      The diameter of the circle.
     */
    public int getDiameter()
    {
        return diameter;
    }
    
    @Override
    public void Draw(Graphics g)
    {
        g.setColor(getFillColor());
        g.fillOval(getXPosition(), getYPosition(), diameter, diameter);
    }
}
