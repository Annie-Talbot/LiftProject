import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
/**
 * Visual Lift Object - inherits from the Lift object but with added abilities to display the lift
 * onto a display window. The object can then be used to display the results of a simulation to 
 * a user.
 * 
 * @author Annie Talbot
 */
public class VisualLift extends Lift{
	/**
	 * The x position of the lift on the display window.
	 */
	private int x = 100;
	/**
	 * The y position of the lift on the display window.
	 */
	private int y = 950;
	/**
	 * The distance between each floor on the display window.
	 */
	private int renderDistance;
	/**
	 * Whether the lift is currently moving between floors.
	 */
	private boolean moving = true;
	/**
	 * The image used to represent the lift on the display window.
	 */
	private BufferedImage liftImage;
	/**
	 * The route between floors that the lift must take.
	 */
	private Route route;
	/**
	 * The number of the move that the lift is currently making (out of all the moves defined by the route).
	 */
	private int routeIndex = 1;
	/**
	 * The building that the lift is working on.
	 */
	private VisualFloor[] floors;
	/**
	 * Constructor for the lift that instantiates all attributes and loads in the image to be used for the lift.
	 * 
	 * @param floors					The building the lift is to run on
	 * @param noFloors					The size of this building ( number of floors )
	 * @param capacity					The maximum amount of people allowed on this lift
	 * @param renderDistance			The distance between each floor on the display window
	 * @param route						The route that this lift must replicate
	 */
	VisualLift(VisualFloor[] floors, int noFloors, int capacity, int renderDistance, Route route) {
		super(noFloors, capacity);
		this.renderDistance = renderDistance;
		this.setButtons(new boolean[noFloors]);
		this.route = route;
		this .floors = floors;
		try {
			BufferedImage image = ImageIO.read(new FileInputStream("res/lift2.png"));
			Image tmp = image.getScaledInstance(4 *renderDistance/7, renderDistance, Image.SCALE_SMOOTH);
			liftImage = new BufferedImage(4 *renderDistance/7, renderDistance, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = liftImage.createGraphics();
			g2.drawImage(tmp, 0, 0, null);
			g2.dispose();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.y = 950 - renderDistance;
		setGoingUp(true);
	}
	/**
	 * Getter for whether the lift is currently moving between floors.
	 * @return			True = is moving, False = not moving
	 */
	public boolean isMoving() {
		return moving;
	}
	/**
	 * Setter for whether the lift is currently moving between floors.
	 * @param moving			True = the lift is moving, False = the lift is stopped
	 */
	public void setMoving(boolean moving) {
		this.moving = moving;
	}
	/**
	 * Controls how the lift is rendered onto the display window.
	 * @param g			Graphics component used to draw onto the display window
	 */
	public void render(Graphics g)
	{
		// The image representing the lift
		g.drawImage(liftImage, x, y, null);
		//	The current amount of people in the lift.
		g.drawString(Integer.toString(this.getNoPeopleOnLift()) + " / " + Integer.toString(this.getCapacity()), 35, y + 35);
	}
	/**
	 * Called by the handler, this function controls when and where the lift moves to.
	 */
	public void tick()
	{
		if (moving)
		{
			if (this.isGoingUp())
			{
				y --;
			}
			else
			{
				y++;
			}
			// If the lift's current destination floor has been reached
			if (y == 1000 - ((route.getPathValue(routeIndex)+1) * renderDistance) + 5)
			{
				// Stop and run the non-visual function that moves the lift
				moving = false;
				this.move(floors[route.getPathValue(routeIndex - 1)], route.getPathValue(routeIndex));
			}
		}
		else
		{
			// Select the next destination for the lift
			routeIndex++;
			if (routeIndex < route.getPathSize())
			{
				if (route.getPathValue(routeIndex) > this.getCurrentFloor())
				{
					this.setGoingUp(true);
				}
				else
				{
					this.setGoingUp(false);
				}
				moving = true;
			}
		}
	}
	/**
	 * Getter for the number of moves the lift has made (of the moves specified by the route).
	 * @return			The number of the current move.
	 */
	public int getRouteIndex()
	{
		return routeIndex;
	}
	/**
	 * The size of the route this lift is to take (the amount of moves that will be made during this simulation).
	 * @return			The number of moves
	 */
	public int getRouteSize()
	{
		return route.getPathSize();
	}
}
