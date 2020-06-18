import java.util.LinkedList;

/**
 * Floor object - The constructor assigns this floor a number and thus, a position within the simulation
 * building. The Floor can then be used to hold people waiting for the lift and to then move these people 
 * onto the lift.
 * @author Annie Talbot
 *
 */
public class Floor implements Cloneable{
	/**
	 * A number identifying where in the building this floor is.
	 */
	private int floorNum;
	/**
	 * Represents an up button on this floor, so when the button is pressed (and this variable is true) it
	 * means that at least one person on this floor is requesting for the lift to take them upwards.
	 */
	private boolean callingUp;
	/**
	 * Represents a down button on this floor, so when the button is pressed (and this variable is true) it
	 * means that at least one person on this floor is requesting for the lift to take them downwards.
	 */
	private boolean callingDown;
	/**
	 * A list that holds every person currently waiting on this floor for the lift.
	 */
	private LinkedList<Person> occupants = new LinkedList<Person>();
	/**
	 * The constructor ensures that the buttons are not active and assigns the object it's floor
	 * number.
	 * @param floorNum		The value to set this object's floor number to
	 */
	Floor(int floorNum){
		setCallingUp(false);
		setCallingDown(false);
		this.floorNum = floorNum;
	}
	/**
	 * This function moves one people on this floor waiting for the lift onto the lift (as long as the lift
	 * is not full). There are 2 types of movement: 1. "DirectionDependant" - this only moves the person 
	 * onto the lift if they are travelling in the same direction as the lift, 2. "DirectionIndependant" - 
	 * this moves the person onto the lift regardless of their travel direction.
	 * 
	 * @param lift			The lift to move people into.
	 * @param liftType		The way in which to select people that will be moved into the lift.
	 */
	public void movePeopleOntoLift(Lift lift, LiftEntryDecision liftType)
	{
		while(!lift.isLiftFull() && this.getAmountPeopleWaiting() != 0)
		{
			Person personToRemove = null;
			if (liftType == LiftEntryDecision.DirectionIndependent)
			{
				for (Person p : occupants)
				{
					personToRemove = p;
					break;
				}
			}
			else if (liftType == LiftEntryDecision.DirectionDependent)
			{
				for (Person p : occupants)
				{
					if (lift.isGoingUp())
					{
						if (p.getEndFloor() > floorNum)
						{
							personToRemove = p;
							break;
						}
					}
					else
					{
						if (p.getEndFloor() < floorNum)
						{
							personToRemove = p;
							break;
						}
					}
				}
			}
			if (personToRemove == null) 
			{
				break;
			}
			else
			{
				personToRemove.setOnLift(true);
				occupants.remove(personToRemove);
				lift.addPerson(personToRemove);
			}
		}
		this.turnButtonsOff();
	}
	
	/**
	 * Adds the specified person onto this floor and updates the floor's calling buttons
	 * @param p		The person to be added to the floor
	 */
	public void addPerson(Person p)
	{
		occupants.add(p);
		if (p.getEndFloor() < floorNum) {setCallingDown(true);}
		else if (p.getEndFloor() > floorNum) {setCallingUp(true);}
	}
	
	/**
	 * Getter for the calling up button of this floor
	 * @return		True = floor is calling upwards
	 * 				False = floor is not calling upwards
	 */
	public boolean isCallingUp() {
		return callingUp;
	}
	
	/**
	 * Setter for the calling up button for this lift
	 * @param		True = button on
	 * 				False = button off
	 */
	public void setCallingUp(boolean upButton) {
		this.callingUp = upButton;
	}
	
	/**
	 * Getter for the calling down button of this floor
	 * @return		True = floor is calling downwards
	 * 				False = floor is not calling downwards
	 */
	public boolean isCallingDown() {
		return callingDown;
	}
	
	/**
	 * Setter for the calling down button for this lift
	 * @param		True = button on
	 * 				False = button off
	 */
	public void setCallingDown(boolean downButton) {
		this.callingDown = downButton;
	}
	
	/**
	 * Getter for the number of people on this floor (the current size of the {@link Floor#occupants} list)
	 * @return		The number of people on this floor
	 */
	public int getAmountPeopleWaiting() {
		return occupants.size();
	}
	
	/**
	 * Sets both the up and down calling buttons to false
	 */
	public void turnButtonsOff()
	{
		setCallingUp(false);
		setCallingDown(false);
	}
	
	/**
	 * Sets the up and down calling buttons to the correct values by cycling through every person
	 * waiting on this lift.
	 */
	public void updateButtons()
	{
		callingUp = false;
		callingDown = false;
		for (Person p: occupants)
		{
			if (p.getEndFloor() < floorNum) {setCallingDown(true);}
			else if (p.getEndFloor() > floorNum) {setCallingUp(true);}
		}
	}
	
	/**
	 * Makes a copy of this floor and places any people that were on the un-copied floor, onto the copied floor.
	 * 
	 * @param people		The list of people from which any person who was on the un-copied floor should be moved
	 * 						to the new floor.
	 * @return				The cloned floor
	 */
	public Floor clone(Person[] people)
	{ 
		Floor clonedFloor = new Floor(floorNum); 
		clonedFloor.setCallingDown(callingDown);
		clonedFloor.setCallingUp(callingUp);
		for (Person p : people)   
		{
			if (p.getStartFloor() == floorNum && !p.isOnLift() && !p.isDelivered())
			{
				clonedFloor.addPerson(p);
			}
		}

		return clonedFloor; 
	}

	/**
	 * Getter for this floor object's identifying number (it's place within the building)
	 * 
	 * @return		The floor number of this object
	 */
	public int getFloorNum() {
		return floorNum;
	} 
}
