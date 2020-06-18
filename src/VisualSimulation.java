import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import javax.swing.JFrame;

/**
 * Visual Simulation Object - This is the driver used to display to the user a simulation that
 * has been run. The constructor uses the starting state of the simulation (the people, floors
 * and lift) and the route that the lift took during the simulation to create a visual 
 * representation of how the simulation happened. This object runs a continuous loop to render
 * and change each object.
 * 
 * @author Annie Talbot
 */
public class VisualSimulation extends Canvas implements Runnable
{
	/**
	 * The dimensions of the window to be created.
	 */
	public static final int WIDTH = 1000, HEIGHT = 1000;
	/**
	 * The handler that will control every object in the simulation.
	 */
	private Handler handler;
	/**
	 * Whether the simulation is currently running or not.
	 */
	private boolean running = false;
	/**
	 * The window to hold the visualisation of the simulation.
	 */
	private JFrame frame;
	/**
	 * The thread to run the simulation using.
	 */
	private Thread thread;
	
	/**
	 * Constructor for the visual simulation that creates all the objects and sets off the display.
	 * 
	 * @param simType		The algorithm used to calculate the route to be taken by the lift.
	 * @param route			The route the lift must take
	 * @param people		The people that are involved in the simulation
	 * @param floors		The building that the lift must traverse through
	 * @param lift			The lift that will be used during the simulation
	 * @param noFloors		The height of the building
	 */
	VisualSimulation(String simType, Route route, Person[] people, Floor[] floors, Lift lift, int noFloors)
	{
		// Set up frame
		frame = new JFrame(simType + " Algorithm Simulation");
		frame.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		frame.setMaximumSize(new Dimension(WIDTH, HEIGHT));
		frame.setMinimumSize(new Dimension(WIDTH, HEIGHT));
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// Set position on computer screen
		if (simType == "Advanced")
		{
			setLocationToTopRight(frame);
		}
		else
		{
			frame.setLocation(0, 0);
		}
		frame.setResizable(false);
		frame.add(this);
		frame.setVisible(true);
		// Set distance between each floor on the display
		int renderDistance = HEIGHT / noFloors;
		handler = new Handler(this, people, floors, lift, frame, route, renderDistance);
		// Start simulation
		this.start();
    }

	/**
	 * Function called to begin the simulation control loop
	 */
    public synchronized void start() 
    {
        running = true;
        thread = new Thread(this);
        thread.start();
    }
    /**
     * Function that drives all movements and rendering of the simulation
     */
    public void run() 
    {
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int frames = 0;
		while(running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {
				tick();
				delta--;
			}
			if (running) render();
			frames++;
			
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				System.out.println("FPS: " + frames);
				frames = 0;
			}
		}
		stop();
    }
    /**
     * Function called to stop the simulation from running
     */
    public synchronized void stop() 
    {
		try {
			thread.join();
			running = false;
		}catch (Exception e) {
			e.printStackTrace();
		}
    }
    /**
     * Function called to move/change every object in the simulation
     */
    private void tick() 
    {
        handler.tick();
    }
    /**
     * Function called to draw all object onto the display window
     */
    private void render() 
    {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) 
		{
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		g.setColor(Color.PINK);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		g.setColor(Color.BLACK);
		g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
		
		handler.render(g);
		
		g.dispose();
		bs.show();
    }
    /**
     * Function called to calculate the coordinates needed to place this display window on the top right of 
     * the computer screen, and puts the window onto those coordinates.
     * @param frame			The display window to be moved
     */
    static void setLocationToTopRight(JFrame frame) {
        GraphicsConfiguration config = frame.getGraphicsConfiguration();
        Rectangle bounds = config.getBounds();
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(config);

        int x = bounds.x + bounds.width - insets.right - frame.getWidth();
        int y = bounds.y + insets.top;
        frame.setLocation(x, y);
    }
}
