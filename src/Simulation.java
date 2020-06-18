import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Simulation object - The constructor takes an amount of floors in the building, people to be spawned
 * and a probability distribution for spawning people across those floors. The set up for the simulation
 * is then complete and the simulation can be used to run any of the different simulations (mechanical,
 * advanced or optimum) with this set up.
 * 
 * @author Annie Talbot
 * @version 1.3.1
 */
public class Simulation 
{
	/**
	 * Array of floors representing the starting state of the building with every person on their start
	 * floor.
	 */
	Floor[] initFloors;
	/**
	 * Array of people representing the starting state of the people for the simulation
	 */
	Person[] initPeople;
	/**
	 * Lift object used to hold the starting state of the lift
	 */
	Lift initLift;
	
	/**
	 * Constructor for a simulation that initiates the lift, the building (floors), spawns the people, 
	 * and places the people onto their starting floor. 
	 * 
	 * @param noFloors		the amount of floors that the building should have
	 * @param noPeople		the amount of people that should be spawned for the simulation
	 * @param pDist			a probability distribution used to determine the likelihood of a person
	 * 						spawning on each floor
	 */
	Simulation(int noFloors, int noPeople, DiscreteDistribution pDist)
	{
		initLift = new Lift(noFloors, 10);
		initFloors = instantiateFloors(noFloors);
		initPeople = generatePeople(noPeople, initFloors, pDist);
	}

	/**
	 * A private function that instantiates the floor array (building) and every floor it contains
	 * @param noFloors		The number of floors the building should contain, and thus the size of
	 * 						the floors array
	 * @return the fully initiated array of floors (building)
	 */
	private Floor[] instantiateFloors(int noFloors) 
	{
		Floor[] floors = new Floor[noFloors];
		for (int i = 0; i < noFloors; i ++)
		{
			floors[i] = new Floor(i);
		}
		return floors;
	}
	
	/**
	 * A private function that generates the an array of newly created people, using a probability
	 * distribution to determine the start floor and a random generator to determine the destination 
	 * floor. Each person is then placed on their respective floors for the beginning of a simulation.
	 * 
	 * @param noPeople		The number of people to be spawned into the building
	 * @param floors		The Floor array that represents the building, where each person must be 
	 * 						placed
	 * @param pDist			The probability distribution that is used to specify the likelihood of 
	 * 						each floor becoming the next person's start floor
	 * @return people		The fully initiated array of people
	 */
	private Person[] generatePeople(int noPeople, Floor[] floors, DiscreteDistribution pDist)
	{
		Person[] people = new Person[noPeople];
		Random r = new Random();
		int id = 0;
		for (int i = 0; i < noPeople; i++)
		{
			int startFloor = pDist.getNextValue();
			int endFloor = startFloor;
			while (endFloor == startFloor)
			{
				endFloor = r.nextInt(floors.length);
			}
			Person p = new Person(id, startFloor, endFloor);
			id++;
			people[i] = p;
			floors[startFloor].addPerson(p);
		}
		return people;
	}
	
	/** Runs a simulation using mechanical lift control - the lift moves all the way up and down the building,
	 * stopping to pick up/ drop off people and only changing direction if the lift reaches the top or 
	 * bottom floor. The calculated wait times for each person in the simulation are then saved to file and the
	 * route taken returned.
	 * 
	 * @return		The Route that contains the path taken by the lift during the simulation
	 */
	public Route runMechanicalSystem()
	{
		Person[] people = deepcopyPeople(initPeople);
		Floor[] floors = deepcopyFloors(initFloors, people);
		Lift lift = deepcopyLift(initLift, people);

		Route route = new Route(LiftEntryDecision.DirectionDependent);
		while (!isEveryoneDelivered(people))
		{
			// Move people onto the lift
			floors[lift.getCurrentFloor()].movePeopleOntoLift(lift, LiftEntryDecision.DirectionDependent);
			
			// Calculate which floor is next
			int nextFloor;
			if (lift.isGoingUp())
			{
				nextFloor = floors.length - 1;
				for (int i = lift.getCurrentFloor(); i < floors.length; i ++)
				{
					if (floors[i].isCallingUp() || lift.isCallingFloor(i))
					{
						nextFloor = i;
						break;
					}
				}
			}
			else
			{
				nextFloor = 0;
				for (int i = lift.getCurrentFloor(); i >= 0; i --)
				{
					if (floors[i].isCallingDown() || lift.isCallingFloor(i))
					{
						nextFloor = i;
						break;
					}
				}
			}
			
			// Move lift
			lift.move(floors[lift.getCurrentFloor()], nextFloor);
			route.addToPath(nextFloor);
		}
		// Calculate route wait times
		route.setTotalWaitTimes(people);
		// Save to file
		saveResults(floors.length, people, "mechanical");
		return route;
	}
	
	/** Runs the simulation using the Optimum lift control system. This is not applicable in the real world but
	 * can be used to find the optimum route that the lift could have taken for the purposes of this simulation. 
	 * The algorithm uses recursion to test every possible (sensible) path that the lift could take, and then 
	 * picks the best option. The wait time of each person in the simulation are then saved to file.
	 * 
	 * @return The best (optimum) route that the lift could take for this simulation
	 */
	public Route runOptimumSystem()
	{
		int maxTime = runMechanicalSystem().getTotalWaitTimes();
		Person[] people = deepcopyPeople(initPeople);
		Floor[] floors = deepcopyFloors(initFloors, people);
		Lift lift = deepcopyLift(initLift, people);
		
		floors[lift.getCurrentFloor()].movePeopleOntoLift(lift, LiftEntryDecision.DirectionIndependent);
		// Call recursive function
		Route finalRoute = calculateOptimumRoute(floors, people, lift, new Route(LiftEntryDecision.DirectionIndependent), maxTime);
		// Write results to file
		saveResults(floors.length, people, "optimum");
		return finalRoute;
	}
	
	/**
	 * Recursion algorithm that finds the best path (minimises combined wait time of the people) from the current route (passed
	 * as a parameter) by comparing every option for the next floor to go to.
	 * 
	 * @param startFloors		The initial state of the building
	 * @param startPeople		The initial state of the people
	 * @param startLift			The initial state of the lift
	 * @param startRoute		The initial state of the current route
	 * @param mechTime			The combined total wait times of the mechanical system
	 * @return			The Route that contains the best complete path from the initial route given as a parameter
	 */
	private Route calculateOptimumRoute(Floor[] startFloors, Person[] startPeople, Lift startLift, Route startRoute, int mechTime)
	{
		Route currentRoute = startRoute.clone();
		Route bestRoute = startRoute.clone();
		// If the path is abnormally long then something has gone wrong, so exit this recursion
		if (startRoute.getPathSize() > startPeople.length * 2)
		{
			bestRoute.setCompleteRoute(false);
			System.err.println("Incorrect route path created.");
			return bestRoute;
		}
		for (int i = 0; i < startFloors.length; i++)
		{
			// Make copy of lift, floors and people so overall outcome is not affected
			Person[] testPeople = deepcopyPeople(startPeople);
			Lift testLift = deepcopyLift(startLift, testPeople);
			Floor[] testFloors = deepcopyFloors(startFloors, testPeople);

			// move people onto lift
			testFloors[testLift.getCurrentFloor()].movePeopleOntoLift(testLift, LiftEntryDecision.DirectionIndependent);
			// Check if this floor is being called
			if ((testFloors[i].isCallingDown() || testFloors[i].isCallingUp() || testLift.isCallingFloor(i)) && testLift.getCurrentFloor() != i)
			{
				// Move lift
				testLift.move(testFloors[testLift.getCurrentFloor()], i);
				currentRoute.addToPath(i);
				// Update route status
				currentRoute.setTotalWaitTimes(testPeople);
				if (currentRoute.getTotalWaitTimes() <= mechTime)	// If this route is less than the mechanical result
				{
					// check if the lift has finished, else get the rest of the optimum route for the current path (recursion)
					if (!isEveryoneDelivered(testPeople))
					{
						currentRoute = calculateOptimumRoute(testFloors, testPeople, testLift, currentRoute, mechTime);
					}
					
					// Update best route
					if (currentRoute.isCompleteRoute())
					{
						if (bestRoute.isCompleteRoute())
						{
							if (currentRoute.getTotalWaitTimes() < bestRoute.getTotalWaitTimes())
							{
								bestRoute = currentRoute.clone();
							}
						}
						else
						{
							bestRoute = currentRoute.clone();
						}
					}
				}
				// Reset current route
				for (int j = currentRoute.getPathSize(); j > startRoute.getPathSize(); j --)
				{
					currentRoute.removeLastFloor();
					currentRoute.setCompleteRoute(false);
				}

			}
		}
		// Run best path found
		for (int i = startRoute.getPathSize(); i < bestRoute.getPathSize(); i++)
		{
			if (bestRoute.getPathValue(i) > startLift.getCurrentFloor())
			{
				startLift.setGoingUp(true);
			}
			else
			{
				startLift.setGoingUp(false);
			}
			startFloors[startLift.getCurrentFloor()].movePeopleOntoLift(startLift, LiftEntryDecision.DirectionIndependent);
			startLift.move(startFloors[startLift.getCurrentFloor()], bestRoute.getPathValue(i));
		}
		return bestRoute;
	}
	/**
	 * Advanced lift movement algorithm - divides the building into 3 section; top, middle and bottom. When the lift is in either
	 * top/bottom and capacity is not reached, everyone who can be delivered in the section is delivered before the lift moves on.
	 * In the middle section, the lift will move in the direction it is already travelling unless there is no one to pick up or
	 * drop off in that direction. If the capacity is likely to be full, the lift will only travel somewhere it can drop someone
	 * off.
	 * 
	 * @return		The route that the lift took during this simulation
	 */
	public Route runAdvancedSystem() 
	{
		Person[] people = deepcopyPeople(initPeople);
		Floor[] floors = deepcopyFloors(initFloors, people);
		Lift lift = deepcopyLift(initLift, people);
		
		// The percentage of building that counts as 'bottom' or 'top' section.
		int floorPercentile = 20;
		int floorBounds = (int) floors.length * floorPercentile/ 100;
		
		floors[lift.getCurrentFloor()].movePeopleOntoLift(lift, LiftEntryDecision.DirectionDependent);
		Route route = new Route(LiftEntryDecision.DirectionDependent);
		while (!isEveryoneDelivered(people))
		{
			int nextFloor = -1;
			if (lift.getCurrentFloor() <= floorBounds) // If lift is in bottom section
			{
				// Find highest floor (in bottom section) that someone is requesting to move down/drop off
				for (int i = floorBounds; i >= 0; i--)
				{
					if (floors[i].isCallingDown())
					{
						lift.setGoingUp(false);
						nextFloor = i;
						break;
					}
				}
				// If nobody in the bottom section is going down
				if (nextFloor == -1)
				{
					// Find lowest floor with a request to move upwards/drop off
					for (int i = 0; i < floors.length; i++)
					{
						if (floors[i].isCallingUp() || lift.isCallingFloor(i))
						{
							lift.setGoingUp(true);
							nextFloor = i;
							break;
						}
					}
				}
				// If nobody needs to move upwards
				if (nextFloor == -1)
				{
					// Find highest floor with someone requesting to move downwards
					for (int i = floors.length - 1; i >= 0; i--)
					{
						if (floors[i].isCallingDown() || lift.isCallingFloor(i))
						{
							lift.setGoingUp(false);
							nextFloor = i;
							break;
						}
					}
				}
				// Next floor now is selected
				if (lift.getNumDifferentCalls() >= lift.getCapacity() - 2 && !lift.isCallingFloor(nextFloor)) // If lift capacity is likely to have been reached.
				{
					if (!lift.isGoingUp())
					{
						// Find highest drop off location in bottom section of the building
						for (int i = floorBounds; i >= 0; i--)
						{
							if (lift.isCallingFloor(i))
							{
								nextFloor = i;
								break;
							}
						}
					}
					if (nextFloor == -1) // If lift is going upwards or there is nobody to drop off in the bottom section
					{
						// Find lowest drop off location for whole building
						for (int i = 0; i < floors.length; i++)
						{
							if (lift.isCallingFloor(i))
							{
								lift.setGoingUp(true);
								nextFloor = i;
								break;
							}
						}
					}
				}
				// Move lift
				lift.move(floors[lift.getCurrentFloor()], nextFloor);
				route.addToPath(nextFloor);
				floors[lift.getCurrentFloor()].movePeopleOntoLift(lift, LiftEntryDecision.DirectionDependent);
			}
			else if(lift.getCurrentFloor() >= floors.length - floorBounds - 1)	// If lift is in top section
			{
				// Find lowest floors with a request to move upwards/drop off in top section
				for (int i = floors.length - floorBounds - 1; i < floors.length; i++)
				{
					if (floors[i].isCallingUp())
					{
						lift.setGoingUp(true);
						nextFloor = i;
						break;
					}
				}
				
				if (nextFloor == -1) // If nobody is moving upwards
				{
					// Find highest floor with somebody moving downwards
					for (int i = floors.length - 1; i >= 0; i--)
					{
						if (floors[i].isCallingDown() || lift.isCallingFloor(i))
						{
							lift.setGoingUp(false);
							nextFloor = i;
							break;
						}
						
					}
				}
				if (nextFloor == -1) // If nobody is moving downwards
				{
					// Find lowest floor that has somebody moving upwards/drop off
					for (int i = 0; i < floors.length; i++)
					{
						if (floors[i].isCallingUp() || lift.isCallingFloor(i))
						{
							lift.setGoingUp(true);
							nextFloor = i;
							break;
						}
					}
				}
				// Next floors is definitely selected now
				if (lift.getNumDifferentCalls() >= lift.getCapacity() - 2 && !lift.isCallingFloor(nextFloor)) // If lift capacity is likely to have been reached.
				{
					if (lift.isGoingUp())
					{
						// Find lowest drop off location in top section of the building
						for (int i = floors.length - floorBounds - 1; i < floors.length; i++)
						{
							if (lift.isCallingFloor(i))
							{
								nextFloor = i;
								break;
							}
						}
					}
					if (nextFloor == -1) // If lift is going downwards or there is nobody to drop off in the top section
					{
						// Find highest drop off location for whole building
						for (int i = floors.length - 1; i >= 0; i--)
						{
							if (lift.isCallingFloor(i))
							{
								lift.setGoingUp(false);
								nextFloor = i;
								break;
							}
						}
					}
				}
				lift.move(floors[lift.getCurrentFloor()], nextFloor);
				route.addToPath(nextFloor);
				floors[lift.getCurrentFloor()].movePeopleOntoLift(lift, LiftEntryDecision.DirectionDependent);
			}
			else // Lift is in the middle
			{
				if (lift.isGoingUp())
				{
					// If lift capacity is likely to have been reached
					if (!(lift.getNumDifferentCalls() >= lift.getCapacity() - 2))
					{
						//Check that no floors just below have people wanting to go upwards
						for (int i = lift.getCurrentFloor() - floorBounds; i < lift.getCurrentFloor(); i++)
						{
							if (floors[i].isCallingUp())
							{
								nextFloor = i;
								break;
							}
						}
					}
					if (nextFloor == -1) // If no floors just below have someone waiting to go upwards
					{
						// Find closest floor above with a request for upwards movement/drop off
						for (int i = lift.getCurrentFloor(); i < floors.length; i++)
						{
							if (floors[i].isCallingUp() || lift.isCallingFloor(i))
							{
								nextFloor = i;
								break;
							}
						}
					
						if (nextFloor == -1) // If no floors above want to move upwards
						{
							// Find highest floor with a request to move downwards
							for (int i = floors.length - 1; i >= 0; i--)
							{
								if (floors[i].isCallingDown() || lift.isCallingFloor(i))
								{
									lift.setGoingUp(false);
									nextFloor = i;
									break;
								}
							}
						}
					}
					if (nextFloor == -1)	// If no floors above want to move downwards/upwards
					{
						// Run algorithm again with the lift starting off moving downwards.
						lift.setGoingUp(false);
					}
					else
					{
						// Move lift
						lift.move(floors[lift.getCurrentFloor()], nextFloor);
						route.addToPath(nextFloor);
						floors[lift.getCurrentFloor()].movePeopleOntoLift(lift, LiftEntryDecision.DirectionDependent);
					}
				}
				else 
				{
					// If lift capacity is likely to have been reached
					if (!(lift.getNumDifferentCalls() >= lift.getCapacity() - 2))
					{
						//Check that no floors just below have people wanting to go upwards
						for (int i = lift.getCurrentFloor() + floorBounds; i > lift.getCurrentFloor(); i--)
						{
							if (floors[i].isCallingDown())
							{
								nextFloor = i;
								break;
							}
						}
					}
					if (nextFloor == -1) // If no floors just below have someone waiting to go downwards
					{
						// Find closest floor below with a request for downwards movement/drop off
						for (int i = lift.getCurrentFloor(); i >=0; i--)
						{
							if (floors[i].isCallingDown() || lift.isCallingFloor(i))
							{
								nextFloor = i;
								break;
							}
						}
					
						if (nextFloor == -1) // If no floors above want to move downwards
						{
							// Find lowest floor with a request to move upwards
							for (int i = floors.length - 1; i >= 0; i--)
							{
								if (floors[i].isCallingUp() || lift.isCallingFloor(i))
								{
									lift.setGoingUp(true);
									nextFloor = i;
									break;
								}
							}
						}
					}
					if (nextFloor == -1)	// If no floors below want to move downwards/upwards
					{
						// Run algorithm again with the lift starting off moving upwards.
						lift.setGoingUp(true);
					}
					else
					{
						// Move lift
						lift.move(floors[lift.getCurrentFloor()], nextFloor);
						route.addToPath(nextFloor);
						floors[lift.getCurrentFloor()].movePeopleOntoLift(lift, LiftEntryDecision.DirectionDependent);

					}
				}
			}
		
		}
		route.setTotalWaitTimes(people);
		saveResults(floors.length, people, "advanced");
		return route;
	}
	
	/**
	 * A private function to test if every person in the simulation is delivered
	 * @param people		The people to test
	 * @return				True = everyone has been delivered,
	 * 						False = at least one person has not been delivered
	 */
	private boolean isEveryoneDelivered(Person[] people)
	{
		boolean allDelivered = true;
		for (Person p : people)
		{
			if (!p.isDelivered())
			{
				allDelivered = false;
				break;
			}
		}
		return allDelivered;
	}
	/**
	 * Debugging function that prints every person in the list given's start and end floor.
	 * @param people		The list of people
	 */
	private void printPeopleStatus(Person[] people)
	{
		String outString = "[  ";
		for (Person p : people)
		{
			outString += "(" + p.getStartFloor() + ", "+ p.getEndFloor() + ") ";
		}
		outString += "  ]";
		System.out.println(outString);
	}
	
	/**
	 * Creates a new instance of an array of floors, equivalent to the floor array entered as a parameter
	 * 
	 * @param orgFloors			The floor array state to be copied
	 * @param people			The already cloned people to be put into new floors
	 * @return		The new instance of floor array
	 */
	private Floor[] deepcopyFloors(Floor[] orgFloors, Person[] people)
	{
		Floor[] copiedFloors = new Floor[orgFloors.length];
		for (int i = 0; i < orgFloors.length; i++)
		{
			copiedFloors[i] = orgFloors[i].clone(people);
		}
		return copiedFloors;
	}

	/**
	 * Creates a new instance of an array of people, equivalent to the people array entered as a parameter
	 * 
	 * @param orgPeople			The people array state to be copied
	 * @return		The new instance of people array
	 */
	private Person[] deepcopyPeople(Person[] orgPeople)
	{
		Person[] clonedPeople = new Person[orgPeople.length];
		
		for (int i = 0; i < orgPeople.length; i++)
		{
			clonedPeople[i] = orgPeople[i].clone();
		}
		
		return clonedPeople;
	}

	/**
	 * Creates a new instance of a lift, equivalent to the lift entered as a parameter
	 * 
	 * @param lift				The lift state to be copied
	 * @param people			The already cloned people to be put onto the lift (if they were on the 
	 * 							original lift)
	 * @return		The new instance of lift
	 */
	private Lift deepcopyLift(Lift lift, Person[] people)
	{
		Lift clonedLift = lift.clone();
		for (Person p : people)
		{
			if (p.isOnLift())
			{			
				clonedLift.addPerson(p);
			}
		}
		return clonedLift;
		
	}
	
	/**
	 * Writes all wait times of the simulation into a new file stored in a directory based upon
	 * 'SimulationData/noFloors/noPeople/liftContolSystemUsed/simulationNumber.txt'.
	 * 
	 * @param noFloors			The amount of floors in the simulation
	 * @param people			The people used in the simulation
	 * @param systemUsed		A string used to describe the lift control system e.g. 'mechanical',
	 * 							'advanced', or 'optimal'
	 */
	private void saveResults(int noFloors, Person[] people, String systemUsed)
	{
		// create string
		String writeStr = "";
		for (Person p : people)
        {
            writeStr +=  p.getWaitTime() + "\n";
        }
		
		try  
        {
            String fileName = "SimulationData/" + noFloors + "/" + people.length + "/" + systemUsed; 
            File f = new File(fileName);
            f.mkdirs();
            // find correct simulation number
            Integer simulationNum = 1;
            fileName += "/simulation" + simulationNum + ".txt";
            f = new File(fileName);
            while (f.exists()) 
            {
            	fileName = fileName.substring(0, fileName.length() - (4 + Integer.toString(simulationNum).length()));
            	simulationNum ++;
            	fileName += simulationNum + ".txt";
            	f = new File(fileName);
            }
            
            f.createNewFile(); // if file already exists will do nothing 
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
            writer.append(writeStr);
            writer.close();
        }
        catch(IOException e)  
        {  
        e.printStackTrace();  
        } 
	}
}
