import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * VisualFloor object - inherits abilities from the Floor object but with added ability to be rendered
 * onto a window (JFrame). This can then be used to visualise the results of a simulation.
 * 
 * @author Annie Talbot
 * @version 1.3.1
 */
public class VisualFloor extends Floor {
	/**
	 * The x position of this floor on the display.
	 */
	private int x;
	/**
	 * The y position of this floor on the display.
	 */
	private int y;
	/**
	 * The distance between each floor on the display.
	 */
	private int renderDistance; 
	/**
	 * The image used to represent people waiting on this floor.
	 */
	private BufferedImage person;
	/**
	 * The lift used during the visualisation of a simulation.
	 */
	private VisualLift lift = new VisualLift(null, 6, 10, 70, null);
	/**
	 * Constructor for the Floor that sets up the distance between each floor and loads in the image
	 * to be used for the people waiting for the lift.
	 * 
	 * @param floorNum			Identifies which floor in the building this is
	 * @param renderDistance	The distance between each floor on the window
	 */
	VisualFloor(int floorNum, int renderDistance) {
		super(floorNum);
		this.renderDistance = renderDistance;
		setCallingUp(false);
		setCallingDown(false);
		try {
			BufferedImage image = ImageIO.read(new FileInputStream("res/person1.png"));
			Image tmp = image.getScaledInstance(2 *renderDistance/7, renderDistance - 25, Image.SCALE_SMOOTH);
			person = new BufferedImage(2 *renderDistance/7, renderDistance - 25, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = person.createGraphics();
			g2.drawImage(tmp, 0, 0, null);
			g2.dispose();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds the lift to this Floor so that people may be moves from the floor to the lift.
	 * @param lift		The lift in the simulation
	 */
	public void addLift(VisualLift lift)
	{
		this.lift = lift;
	}
	/**
	 * Controls how this floor is rendered onto the display window.
	 * @param g			Graphics component used to draw onto the display window
	 */
	public void render(Graphics g)
	{
		// Decides position on the display window
		int place = 1000 - ((this.getFloorNum()+1) * renderDistance);
		// Displays the floor number and amount of people currently waiting on this floor
		g.drawString("Floor " + this.getFloorNum() + ": ", 200, place + 20);
		if (this.getAmountPeopleWaiting() != 0)
		{
			g.drawImage(person, 200, place + 30, null);
			g.drawString(" X " + Integer.toString(this.getAmountPeopleWaiting()), 220, place + 60);
		}
	}
	/**
	 * Called by the handler, this function controls the floor moving people onto the lift
	 * @param pickUpType		The method used to move people onto the lift (direction dependent or independent)
	 */
	public void tick(LiftEntryDecision pickUpType)
	{
		this.movePeopleOntoLift(lift, pickUpType);
	}
}
