import java.awt.Graphics;
import javax.swing.JFrame;

/**
 * Handler object that controls all objects involved in the visualisation of a simulation.
 * @author Annie Talbot
 *
 */
public class Handler 
{
	/**
	 * The building of the visual simulation
	 */
	private VisualFloor[] floors;
	/**
	 * The lift of the visual simulation
	 */
	private VisualLift lift;
	/**
	 * The window in which the simulation will be displayed
	 */
	private JFrame frame;
	/**
	 * The visual simulation
	 */
	private VisualSimulation sim;
	/**
	 * The route that the lift should take
	 */
	private Route route;
	/**
	 * The list of people the lift must deliver
	 */
	private Person[] people;
 	/**
 	 * Constructor for the simulation handler that creates the window (JFrame) and instantiates all of the other attributes.
 	 * 
 	 * @param sim					The simulation object this will control
 	 * @param people				The list of people the simulation will deliver
 	 * @param floors				The building that the simulation should run on
 	 * @param lift					The lift that the simulation will use
 	 * @param frame					The window that the simulation will be displayed as
 	 * @param route					The path that the lift should follow
 	 * @param renderDistance		The distance between each floor in the window
 	 */
	Handler(VisualSimulation sim, Person[] people, Floor[] floors, Lift lift, JFrame frame, Route route, int renderDistance)
	{
		this.floors = new VisualFloor[floors.length];
		this.frame = frame;
		this.sim = sim;
		this.route = route;
		this.people = people;
		for (int i = 0; i < floors.length; i++)
		{
			this.floors[i] = new VisualFloor(floors[i].getFloorNum(), renderDistance);
		}
		this.lift = new VisualLift(this.floors, floors.length, lift.getCapacity(), renderDistance, route);
		
		for (VisualFloor f : this.floors)
		{
			f.addLift(this.lift);
		}
		
		for (Person p : people)
		{
			this.floors[p.getStartFloor()].addPerson(p);
		}
		this.floors[0].movePeopleOntoLift(this.lift, route.getPickUpType());
	}
	/** 
	 * Function that runs continually until the simulation is finished or closed, and moves all the elements of the simulation.
	 */
	public void tick()
	{
		if (frame.isDisplayable())
		{
			if (lift.getRouteIndex() < lift.getRouteSize())
			{
				if (lift.isMoving())
				{
					lift.tick();
				}
				else
				{
					lift.tick();
					floors[lift.getCurrentFloor()].tick(route.getPickUpType());
				}
			}
			else
			{
				frame.dispose();
				
			}
		}
		else
		{
			sim.stop();
		}
	}
	/** 
	 * Function that runs continually until the simulation is finished or closed, and renders all the elements of the simulation
	 * onto the window.
	 */
	public void render(Graphics g)
	{
		g.drawString("Route Taken: ", 690, 30);
		g.drawString(route.getPath().toString(), 700 - (4*route.getPath().toString().length())/2, 60);		
		g.drawString("No. People Delivered:", 650, 110);
		int deliveredPeople = 0;
		for (Person p : people)
		{
			if (p.isDelivered())
			{
				deliveredPeople++;
			}
		}
		g.drawString(deliveredPeople + " / " + people.length, 720, 140);
		g.drawString("Total Wait Time of Route:", 635, 170);
		g.drawString(Integer.toString(route.getTotalWaitTimes()), 730, 200);
		
		// Render lift and floors
		lift.render(g);
		for (VisualFloor f: floors)
		{
			f.render(g);
		}
		
	}
	
}
