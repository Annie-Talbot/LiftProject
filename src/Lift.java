import java.util.ArrayList;
import java.util.LinkedList;
/**
 * Lift object - Part of the simulation, this object represents a lift to be placed in a building
 * that can pick up, move between floors and drop off people.
 * @author theb4
 *
 */
public class Lift 
{
	/**
	 * The highest floor that the lift can reach.
	 */
	private int topFloor;
	/**
	 * The buttons inside the lift that show where people inside the lift want to go.
	 */
	private boolean[] buttons;
	/**
	 * The list of people inside the lift.
	 */
	private LinkedList<Person> occupants = new LinkedList<Person>();
	/**
	 * The current floor that the lift is on.
	 */
	private int currentFloor = 0;
	/**
	 * The number of movements the lift has made since the beginning of the simulation. Each floor the lift
	 * travels through appends 1 and every time the lift stops another 1 is appended.
	 */
	private int noMovements = 0;
	/**
	 * Keeps track of the direction that the lift is travelling. True = upwards, False = downwards.
	 */
	private boolean goingUp = true;
	/**
	 * The total amount of people that can fit in the lift.
	 */
	private int capacity;
	/**
	 * The current number of people in the lift.
	 */
	private int noPeopleInLift = 0;
	
	/**
	 * Constructor for the lift that decides how many floors it can travel up and the amount of people that
	 * can fit in lift.
	 * 
	 * @param noFloors			The number of floors the lift can traverse (the size of the building)
	 * @param capacity			The max capacity of this lift
	 */
	Lift(int noFloors, int capacity)
	{
		this.topFloor = noFloors - 1;
		this.capacity = capacity;
		this.buttons = new boolean[noFloors];
	}
	
	/**
	 * Moves the lift to the next floor, delivers anyone in the lift who has reached their destination.
	 * 
	 * @param prevFloor		The floor that the lift was on before the movement
	 * @param nextFloor		The floor that the lift is travelling to
	 */
	public void move(Floor prevFloor, int nextFloor)
	{
		// add movements, plus 1 for time taken to stop
		noMovements += Math.abs(nextFloor - currentFloor) + 1;
		
		// move lift
		currentFloor = nextFloor;
		
		//drop off people in lift
		deliverPeople();
		
		// update direction
		updateDirection();
		
		prevFloor.updateButtons();
	}
	/**
	 * If any person in the lift's destination floor is the floor that the lift is currently at, they will
	 * be removed from the lift, their wait time calculated and they will be set as delivered.
	 */
	private void deliverPeople() 
	{
		ArrayList<Person> peopleToRemove = new ArrayList<Person>();
		for (int i = 0; i < occupants.size(); i++)
		{
			Person p = occupants.get(i);
			if (p.getEndFloor() == currentFloor)
			{
				peopleToRemove.add(p);
			}
		}
		for (Person p : peopleToRemove)
		{
			p.setWaitTime(noMovements);
			p.setDelivered(true);
			noPeopleInLift--;
			occupants.remove(p);
		}
		
		buttons[currentFloor] = false;
	}
	/**
	 * Changes the direction of the lift if it at the top or the bottom of the building
	 */
	private void updateDirection()
	{
		if (currentFloor == topFloor) {setGoingUp(false);}
		else if (currentFloor == 0) {setGoingUp(true);}
	}
	/**
	 * Getter for the floor that the lift is currently on.
	 * @return
	 */
	public int getCurrentFloor() {
		return currentFloor;
	}
	/**
	 * Sets the lift's position in the building to a new floor number.
	 * @param newFloor		The floor that the lift is on
	 */
	public void setCurrentFloor(int newFloor) {
		this.currentFloor= newFloor;
	}
	/**
	 * Represents the act of someone getting in the lift.
	 * @param p		The person that is getting into the lift.
	 */
	public void addPerson(Person p)
	{
		buttons[p.getEndFloor()] = true;
		p.setOnLift(true);
		occupants.add(p);
		noPeopleInLift++;

	}
	/**
	 * Getter for the direction the lift is travelling.
	 * @return
	 */
	public boolean isGoingUp() {
		return goingUp;
	}
	/**
	 * Changes the direction that the lift is travelling
	 * @param goingUp		The new direction (True = up, False = down)
	 */
	public void setGoingUp(boolean goingUp) {
		this.goingUp = goingUp;
	}
	/**
	 * Getter for whether a certain button in the lift has been pressed (if a person in the lift
	 * is travelling to the specified floor).
	 * @param floorNum			The floor that the button represents
	 * @return			True = the button has been pressed, False = it hasn't
	 */
	public boolean isCallingFloor(int floorNum)
	{
		return buttons[floorNum];
	}
	/**
	 * Checks if the lift is at full capacity (as if a person is looking into the lift to see if 
	 * they can enter), returns whether they can enter or not.
	 * @return			True = not full, False = is full.
	 */
	public boolean isLiftFull()
	{
		if (noPeopleInLift >= capacity)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	/**
	 * Getter for the amount of movements the lift has made.
	 * @return			The number of movements
	 */
	public int getNoMovements()
	{
		return noMovements;
	}
	/**
	 * Setter for the number of movements the lift has made.
	 * @param newVal		The value to change the number of movements to
	 */
	public void setNoMovements(int newVal)
	{
		this.noMovements = newVal;
	}
	/**
	 * Setter for the number of people in the lift currently.
	 * @param noPeople		The new number of people in the lift
	 */
	public void setNoPeopleInLift(int noPeople)
	{
		this.noPeopleInLift = noPeople;
	}
	/**
	 * Creates a copy of this lift
	 * @return		the copied lift
	 */
	public Lift clone()
	{
		Lift clonedLift = new Lift(topFloor + 1, capacity);
		clonedLift.buttons = buttons.clone();
		clonedLift.setNoMovements(noMovements);
		clonedLift.setGoingUp(isGoingUp());
		clonedLift.setCurrentFloor(currentFloor);
		return clonedLift;
	}
	/**
	 * Getter for the list of people in the lift.
	 * @return		The list of people
	 */
	public LinkedList<Person> getOccupants()
	{
		return occupants;
	}
	/**
	 * Getter for the amount of buttons currently pressed in the lift.
	 * @return		The number of buttons pressed inside the lift
	 */
	public int getNumDifferentCalls()
	{
		int total = 0;
		for (boolean button : buttons)
		{
			if (button)
			{
				total ++;
			}
		}
		return total;
	}
	/**
	 * Getter for the maximum capacity of the lift
	 * @return		The max capacity of the lift
	 */
	public int getCapacity()
	{
		return capacity;
	}
	/**
	 * Getter for the number of people in the lift currently.
	 * @return		The number of people in the lift currently
	 */
	public int getNoPeopleOnLift()
	{
		return noPeopleInLift;
	}
	/**
	 * Setter for the list of buttons in the lift.
	 * @param buttons			The list values to change this lift's buttons to
	 */
	public void setButtons(boolean[] buttons)
	{
		this.buttons = buttons;
	}
}
