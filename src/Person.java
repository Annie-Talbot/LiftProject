/**
 * Person object - An abstraction for a person involved in the simulation. The constructor gives
 * this object a start and end destination floor and a unique person ID.
 * 
 * @author Annie Talbot
 *
 */
public class Person
{
	/**
	 * Unique ID for the person this object represents.
	 */
	private int personId;
	/*
	 * The floor this person starts at.
	 */
	private int startFloor;
	/**
	 * The floor this person want to go to.
	 */
	private int endFloor;
	/**
	 * The time (in lift movements) that it took for the lift to deliver this person.
	 */
	private int waitTime;
	/**
	 * Whether this person is inside the lift or not
	 */
	private boolean onLift = false;
	/**
	 * Whether this person has been delivered to their end destination yet.
	 */
	private boolean delivered = false;
	/**
	 * Constructor for this Person class that assigns this object it's ID, start and end destinations.
	 * @param id			Unique ID that represents this person
	 * @param startFloor	The floor this person will start at
	 * @param endFloor		The floor this person must get to
	 */
	Person(int id, int startFloor, int endFloor)
	{
		this.startFloor = startFloor;
		this.endFloor = endFloor;
		this.personId = id;
	}
	/**
	 * Getter for the end destination of this person
	 * @return		The floor number
	 */
	public int getEndFloor() {
		return endFloor;
	}
	/**
	 * Setter for whether this person is delivered or not.
	 * @param delivered			True = the person has been delivered, False = they have not been delivered
	 */
	public void setDelivered(boolean delivered) {
		this.onLift = false;
		this.delivered = delivered;
	}
	/**
	 * Getter for whether this person has been delivered
	 * @return		True = yes they have been delivered, False = no they have not.
	 */
	public boolean isDelivered()
	{
		return delivered;
	}
	/**
	 * Setter for this person's wait time - calculates how many additional movements the lift has made before 
	 * delivering this person.
	 * @param noMovements		The amount of movements the lift has made since the beginning of the simulation
	 */
	public void setWaitTime(int noMovements) {
		waitTime = noMovements - (Math.abs(startFloor - endFloor) + 1);
	}
	/**
	 * Direct setter for this person's wait time. The value parsed into the function is what the wait time is 
	 * set to.
	 * @param waitTime		The value to set this person wait time to
	 */
	public void cloneWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}
	/**
	 * Getter for the amount of time this person waited to be delivered.
	 * @return			The amount of time waited
	 */
	public int getWaitTime() {
		return waitTime;
	}
	/**
	 * Getter for this person's starting floor.
	 * @return		The floor number this person started at
	 */
	public int getStartFloor() {
		return startFloor;
	}
	/**
	 * Getter for this person ID value
	 * @return		The ID number
	 */
	public int getPersonId()
	{
		return personId;
	}
	/**
	 * Makes a complete copy of this person object
	 * @return		The copied person
	 */
	public Person clone()
	{
		Person p = new Person(personId, startFloor, endFloor);
		p.setDelivered(this.delivered);
		p.cloneWaitTime(this.waitTime);
		p.setOnLift(this.onLift);
		return p;
	}
	/**
	 * Getter for whether this person is currently on the lift or not
	 * @return		True = on the lift, False = not on the lift
	 */
	public boolean isOnLift() {
		return onLift;
	}
	/**
	 * Setter for whether this person is on the lift or not
	 * @param onLift		True = set this person to be on the lift, 
	 * 						False = set this person to not be on the lift
	 */
	public void setOnLift(boolean onLift) {
		this.onLift = onLift;
	}
}
