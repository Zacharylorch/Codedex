import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 2D Frame for 2D Graphics
 * 
 * @author Zachary Lorch
 * @author Jimmy Chen
 * @author Steven Weller
 * @version 12.12.2024
 */
public class CanvasFrame 
{
    private JFrame frame;       // the actual frame(window) we'll be showing
    private CanvasPanel canvas; // the canvas we'll be drawing
    
    /**
     * Creates a new CanvasFrame object.
     */
    public CanvasFrame()
    {
        frame = new JFrame("Pac-Man"); //make the JFrame, and set the window bar title 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        
        canvas = new CanvasPanel();  // CanvasPanel extends a JPanel
        
        // Use the canvasPanel size & borders to define window size
        canvas.setPreferredSize(new Dimension(canvas.getBoardWidth(), canvas.getBoardHeight()));
        frame.getContentPane().add(canvas); //put the canvas (JPanel) in the frame

        frame.pack();                       //make everything the preferred size
        frame.setVisible(true);             //show the frame
    }
}
