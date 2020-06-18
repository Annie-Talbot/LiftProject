import java.util.LinkedList;
/**
 * Route object - Holds the path that represents the route that the a lift object has taken (the
 * floor numbers of every floor he lift went to, in the order they were reached) to deliver
 * everyone in the simulation. This also holds a status for if the route is complete (everyone has
 * been delivered), the sum of the wait times of each person in the simulation and the method used
 * to move people into the lift. This object can then be used to visualise the simulation in 
 * another window.
 * 
 * @author Annie Talbot
 */
public class Route 
{
	/**
	 * Whether the route is complete/finished because everyone in the simulation has been 
	 * delivered.
	 */
	private boolean completeRoute = false;
	/**
	 * The sum total of each delivered person in the simulation's wait time.
	 */
	private int totalWaitTimes;
	/**
	 * The list of floors the lift travelled to in the order in which they were reached.
	 */
	private LinkedList<Integer> path = new LinkedList<Integer>();
	/**
	 * The method used to move people into the lift (direction dependent or independent)
	 */
	private LiftEntryDecision pickUpType;
	/**
	 * Constructor for the route where there is no pre-existing path. Only the method used to 
	 * move people onto the lift is specified.
	 * @param pickUpType		Method used to move people onto the lift
	 */
	Route(LiftEntryDecision pickUpType)
	{
		this.pickUpType = pickUpType;
		path.add(0);
	}
	/**
	 * Constructor for the route where some of a pre-existing path has already been created. This 
	 * using the variables given as parameters to set up the route.
	 * 
	 * @param pickUpType		Method used to move people onto the lift
	 * @param peopleWaitTotal	The sum total of each delivered person in the simulation's wait time.
	 * @param path				The list of floors the lift travelled to in the order in which they were reached.
	 */
	Route(LiftEntryDecision pickUpType, int peopleWaitTotal, LinkedList<Integer> path)
	{
		this.pickUpType = pickUpType;
		this.totalWaitTimes = peopleWaitTotal;
		this.setPath(path);
	}
	/**
	 * Getter for the sum total of each delivered person in the simulation's wait time.
	 * @return		The sum total
	 */
	public int getTotalWaitTimes() {
		return totalWaitTimes;
	}
	/**
	 * Setter for the total wait times that calculates the total using the list of people used in the simulation.
	 * This also sets the route as complete/finished if every person has been delivered.
	 * 
	 * @param people		The people whose wait times are to be summed.
	 */
	public void setTotalWaitTimes(Person[] people) {
		setCompleteRoute(true);
		int total = 0;
		for (Person p : people)
		{
			if (p.isDelivered())
			{
				total += p.getWaitTime();
			}
			else {setCompleteRoute(false);}
		}
		this.totalWaitTimes = total;
	}
	/**
	 * Setter for the sum total of every person's wait time. This just uses the value given and set the 
	 * total to that value.
	 * 
	 * @param waitValue			The new value for the total wait times.
	 */
	public void setTotalWaitTimes(int waitValue) {
		this.totalWaitTimes = waitValue;
	}
	/**
	 * Getter for the current route taken by the lift.
	 * @return
	 */
	public LinkedList<Integer> getPath() {
		return path;
	}
	/**
	 * Setter for the route taken by the lift.
	 * 
	 * @param path		Linked list containing all the floors travelled to by the lift in the order they 
	 * were reached.
	 */
	public void setPath(LinkedList<Integer> path) {
		this.path = path;
	}
	/**
	 * Add's a floor number to the path taken by the lift.
	 * @param nextFloor			The floor number to be added
	 */
	public void addToPath(int nextFloor)
	{
		path.add(nextFloor);
	}
	/**
	 * Removes the last added floor number from the list representing the path taken by the lift.
	 */
	public void removeLastFloor()
	{
		path.removeLast();
	}
	/**
	 * Getter for the floor number that the lift reached on a certain move, given as a parameter.
	 * @param index				The number of the move.
	 * @return					The floor number the lift reached on that move
	 */
	public int getPathValue(int index)
	{
		return path.get(index);
	}
	/**
	 * Getter for the amount of moves the lift has made to a different floor
	 * @return			The number of moves
	 */
	public int getPathSize()
	{
		return path.size();
	}
	/**
	 * Creates a copy of this route object
	 * @return The cloned route
	 */
	public Route clone()
	{
		Route clonedRoute = new Route(this.pickUpType, totalWaitTimes,(LinkedList<Integer>) path.clone());
		clonedRoute.setCompleteRoute(isCompleteRoute());
		return clonedRoute;
	}
	/**
	 * Getter for whether this route has a complete/finished route
	 * @return			True = complete route, False = not everyone has been delivered
	 */
	public boolean isCompleteRoute() {
		return completeRoute;
	}
	/**
	 * Setter for whether this route is complete or not.
	 * @param completeRoute				True = is complete, False = not complete
	 */
	public void setCompleteRoute(boolean completeRoute) {
		this.completeRoute = completeRoute;
	}
	/**
	 * Getter for the method used to move people onto the lift during this simulation.
	 * @return			The method used (direction dependent/independent)
	 */
	public LiftEntryDecision getPickUpType()
	{
		return this.pickUpType;
	}
	/**
	 * Creates a readable string containing all route data that can then be printed to the user.
	 * @return outString		The string will all information
	 */
	public String print()
	{
		String outString = "The path taken: [  ";
		for(int i : this.getPath())
		{
			outString += ", " + i;
		}
		outString += "  ]. Total Wait Time: " + this.getTotalWaitTimes();
		return outString;
	}

}
