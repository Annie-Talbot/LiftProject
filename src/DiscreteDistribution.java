import java.util.ArrayList;
import java.util.Random;
/**
 * An instance of this class can be used to replicate a discrete probability distribution.
 * This distribution can then be used alongside the {@link Random} class to select a value
 * using the probabilities given from the constructor. 
 * The constructor-given distribution does not contain actual probabilities, but the 
 * numerator for the fraction representing the probability.
 * 
 * @author Annie Talbot
 *
 */
public class DiscreteDistribution {
	/**
	 * The list of values to choose from, each value is repeated a certain amount of times in order to 
	 * replicate the probability specified for it to occur.
	 */
	private int[] distribution;
	/**
	 * The Random object used to generate values.
	 */
	Random r;
	
	/**
	 * The constructor that sets up the distribution array to have the correct probabilities.
	 * 
	 * @param finalDistribution 	The list that holds the probability for each floor to be selected
	 */
	public DiscreteDistribution(ArrayList<Integer> finalDistribution) {
		// Get size of the distribution
		int size = 0;
		for (int floorNum = 0; floorNum < finalDistribution.size(); floorNum++) 
		{
			size += finalDistribution.get(floorNum);
		}
		distribution = new int[size];
		// Give the distribution list the correct probabilities for each floor
		int counter = 0;
		for (int floorNum = 0; floorNum < finalDistribution.size(); floorNum++) 
		{
			for (int i = 0; i < finalDistribution.get(floorNum); i++) 
			{
				distribution[counter] = floorNum;
				counter++;
			}
		}
		// Initialises the Random object
		r = new Random();
	}
	
	/**
	 * A constructor for a simple distribution in which each floor has the same probability for 
	 * being selected.
	 * @param noFloors		The number of floors spread across the distribution
	 */
	public DiscreteDistribution(int noFloors) {
		this.distribution = new int[noFloors];
		for (int i = 0; i < noFloors; i++)
		{
			this.distribution[i] = i;
		}
		r = new Random();
	}
	
	/**
	 * A public function for using this discrete distribution to select the next floor and return it.
	 * @return 			The randomly selected floor.
	 */
	public int getNextValue() {
		return (int) distribution[r.nextInt(distribution.length)];
	}
}
